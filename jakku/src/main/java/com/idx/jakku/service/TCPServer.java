package com.idx.jakku.service;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by derik on 18-6-4.
 */

public class TCPServer implements Runnable {

    private static final String TAG = TCPServer.class.getSimpleName();
    private List<Socket> sockets = new ArrayList<>();
    private ServerSocket serverSocket;
    private boolean isClose = false;
    private Thread mThread;
    private CallBack mCallback;

    public interface CallBack {
        void onSuccess(String s);

        void onError(String error);
    }

    TCPServer(CallBack callBack) {
        mCallback = callBack;
    }

    public static TCPServer newInstance(CallBack callBack) {
        return new TCPServer(callBack);
    }

    @Override
    public void run() {
        try {
            while (true) {
                Log.d(TAG, "run: " + Thread.currentThread().getId());
                serverSocket = new ServerSocket(0);
                Socket socket = serverSocket.accept();
                sockets.add(socket);
                Log.d(TAG, "run: socket size=" + sockets.size());
                new Thread(new ServerThread(socket)).start();
                if (isClose) {
                    serverSocket.close();
                    break;
                }
                Thread.sleep(100);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startServer() {
        mThread = new Thread(this);
        mThread.start();
    }

    public void stopServer() {
        isClose = true;
        if (mThread != null) {
            mThread = null;
        }
    }

    public int getPort() {
        if (serverSocket != null) {
            return serverSocket.getLocalPort();
        }
        return -1;
    }

    public String getHost() {
        if (serverSocket != null) {
            return serverSocket.getInetAddress().getHostAddress();
        }
        return null;
    }

    public class ServerThread implements Runnable {
        private Socket socket = null;
        private BufferedReader bread = null;
        private BufferedWriter bwrite = null;

        ServerThread(Socket socket) throws IOException {
            this.socket = socket;
            bread = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
            bwrite = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "utf-8"));
        }

        @Override
        public void run() {
            try {
                while (!isClose) {
                    StringBuilder stringBuilder = new StringBuilder();
                    char[] buffer = new char[2048];
                    int length;
                    while ((length = bread.read(buffer)) != -1) {
                        stringBuilder.append(buffer, 0, length);
                        if (stringBuilder.toString().contains("#eof#")) {
                            break;
                        }
                    }

                    String result = stringBuilder.toString();
                    if (result.length() >= 5) {
                        result = result.substring(0, result.length() - 5);
                    }
                    Log.d(TAG, "run: receive length=" + result.length());

                    if (mCallback != null) {
                        mCallback.onSuccess(result);
                    }
                    String reply = "TCP Server reply, success.";
                    Log.d(TAG, "run: write length=" + reply);
                    bwrite.write(reply + "#eof#");
                    bwrite.flush();
                    Log.d(TAG, "run: go to sleep 500");
                    Thread.sleep(500);
                }

            } catch (Exception e) {
                e.printStackTrace();
                sockets.remove(socket);
                if (mCallback != null) {
                    mCallback.onError("tcp error");
                }
            }

        }
    }
}