package com.idx.jakku.service;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * Created by derik on 18-5-18.
 */

public class UDPServer {

    private static final String TAG = "UDPServer";
    private InetAddress toGroup;
    private InetAddress fromGroup;
    private int port;
    private MulticastSocket sendSocket = null;
    private MulticastSocket receiveSocket = null;
    private boolean enable = true;
    private UDPDataListener mListener;

    public UDPServer(String sendIp, String receiveIp, int port) {
        if (sendIp.equals(receiveIp)) {
            throw new IllegalArgumentException();
        }
        try {
            this.port = port;
            toGroup = InetAddress.getByName(sendIp);
            fromGroup = InetAddress.getByName(receiveIp);
            sendSocket = new MulticastSocket(port);
            receiveSocket = new MulticastSocket(port);
            sendSocket.setTimeToLive(1);
            sendSocket.joinGroup(toGroup);
            receiveSocket.setTimeToLive(1);
            receiveSocket.joinGroup(fromGroup);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setDataListener(UDPDataListener listener) {
        mListener = listener;
    }

    public void sendMsg(final String msg) {
        if (msg == null) {
            return;
        }
        new Thread() {
            @Override
            public void run() {
                try {
                    DatagramPacket packet;
                    byte data[] = msg.getBytes("utf-8");
                    packet = new DatagramPacket(data, data.length, toGroup, port);
                    sendSocket.send(packet);
                    Log.d(TAG, "run: send by udp, msg=" + msg);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void receive() {
        new Thread() {
            @Override
            public void run() {
                while (enable) {
                    try {
                        Log.d(TAG, "run: ready");
                        byte[] data = new byte[1024];
                        DatagramPacket packet = new DatagramPacket(data, data.length, fromGroup, port);
                        receiveSocket.receive(packet);
                        String ip = packet.getAddress().getHostAddress();
                        String message;
                        message = new String(packet.getData(), 0, packet.getLength(), "utf-8");
                        if (!message.isEmpty()) {
                            if (mListener != null) {
                                mListener.onReceived(ip, message);
                            }
                        }
                        Log.d(TAG, "run:from udp host=" + packet.getAddress().getHostAddress() + ",from udp port=" + packet.getPort());
                        Log.d(TAG, "receive message=" + message);
                    } catch (Exception e) {
                        e.printStackTrace();
                        receiveSocket.close();
                    }
                }
            }
        }.start();
    }

    public void startServer() {
        enable = true;
    }

    public void stopServer() {
        enable = false;
    }

}
