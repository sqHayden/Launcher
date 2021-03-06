package com.idx.launcher.user.personal_center.address;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hyphenate.util.LatLng;
import com.idx.launcher.BaseActivity;
import com.idx.launcher.R;
import com.idx.launcher.data.JsonData;
import com.idx.launcher.data.JsonUtil;
import com.idx.launcher.service.Execute;
import com.idx.launcher.service.IService;
import com.idx.launcher.service.SpeakService;
import com.idx.launcher.service.listener.DataListener;
import com.idx.launcher.takeout.utils.Constant;
import com.idx.launcher.user.personal_center.InputTipsActivity;
import com.idx.launcher.user.personal_center.address.bean.AddAddress;
import com.idx.launcher.user.personal_center.address.bean.GetAdderssList;
import com.idx.launcher.user.personal_center.address.bean.UserInfoBean;
import com.idx.launcher.utils.BitmapUtils;
import com.idx.launcher.utils.RegexUtils;
import com.idx.launcher.utils.SharedPreferencesUtil;
import com.idx.launcher.videocall.utils.SpUtils;
import com.mor.sale.SaleClient;
import com.mor.sale.entity.AddressEntity;
import com.xiaomor.mor.app.common.utils.LogUtils;

import net.imoran.tv.sdk.network.callback.NetRequestCallback;
import net.imoran.tv.sdk.network.requestdata.FProtocol;

import java.util.List;

public class NewAddAddressActivity extends BaseActivity {

    private static final String TAG = "NewAddAddressActivity";
    private Bitmap mBitmap_toolbar;
    private EditText address_name; //输入姓名
    private EditText address_phone; //输入手机号码
    private TextView address_select; //选择地区
    private EditText address_detailed; //详细地址
    private Button address_save;
    private String name;        //收货人
    private String phone;       //手机号
    private String select;      //收货地址
    private String detailed ;    //详细地址
    private String labels = null; // 标签
    private String _id;
    private Intent intent;
    private ActionBar actionBar;
    private Toolbar toolbar;
    private TextView house;
    private TextView company;
    private EditText address_labels;
    private IService mIService;
    private String latitude;
    private String longitude;

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mIService = (IService) service;
            mIService.setDataListener((DataListener) NewAddAddressActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    private InputMethodManager inputManager;
    private String current_time;
    private SharedPreferencesUtil sharedPreferencesUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        window.setFlags(flag, flag);
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_new_add_address);
        initToolbar_newAdd();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindService(new Intent(NewAddAddressActivity.this, SpeakService.class), conn, BIND_AUTO_CREATE);

    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(conn);
        ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                NewAddAddressActivity.this.getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);

    }

    @Override
    public void onJsonReceived(String json) {
        super.onJsonReceived(json);
        JsonData jsonUtil = JsonUtil.createJsonData(json);
        String type = jsonUtil.getType();
        Log.d(TAG, "onJsonReceived: "+ type);
        if (type.equals("back")){
            back();
        }
        Intent intent = new Intent(Execute.ACTION_SUCCESS);
        sendBroadcast(intent);
    }

    private void back() {
        if (!TextUtils.isEmpty(SpUtils.get(getApplicationContext(),Constant.COMMIT_ORDER_ADDRESS,""))
                && SpUtils.get(getApplicationContext(), Constant.COMMIT_ORDER_ADDRESS,"").equals(Constant.COMMIT_ORDER_ADDRESS_VALUE)){
            finish();
            SpUtils.put(getApplicationContext(),Constant.COMMIT_ORDER_ADDRESS,"");
        }else if (!TextUtils.isEmpty(SpUtils.get(getApplicationContext(), Constant.TAKEOUT_SELLER,""))
                && (SpUtils.get(getApplicationContext(),Constant.TAKEOUT_SELLER,"").equals(Constant.TAKEOUT_SELLER_SELLER_VALUE)
                || SpUtils.get(getApplicationContext(),Constant.TAKEOUT_SELLER,"").equals(Constant.TAKEOUT_SELLER_CAR_VALUE))){
            //由购物车或商家跳转过来
            finish();
            SpUtils.put(getApplicationContext(),Constant.TAKEOUT_SELLER,"");
        }else {
            startActivity(new Intent(NewAddAddressActivity.this, AddressActivity.class));
            finish();
        }
    }

    private void initView() {
        address_name = findViewById(R.id.address_name);
        address_phone = findViewById(R.id.address_phone);
        address_select = findViewById(R.id.address_select);
        address_detailed = findViewById(R.id.address_detailed);
        address_save = findViewById(R.id.address_save);
        //家 公司 自定义
        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (!RegexUtils.isChinese(source.charAt(i))) {
                        return "";
                    }
                }
                return null;
            }
        };

        address_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    String temp = s.toString();
                    String tem = temp.substring(temp.length()-1, temp.length());
                    char[] temC = tem.toCharArray();
                    int mid = temC[0];
                    if(mid>=48&&mid<=57){//数字
                        return;
                    }
                    s.delete(temp.length()-1, temp.length());
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
        });
        RegexUtils.setEditTextInhibitInputSpeChat(address_detailed);

//        address_detailed.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                try {
//                    String temp = s.toString();
//                    String tem = temp.substring(temp.length()-1, temp.length());
//                    char[] temC = tem.toCharArray();
//                    int mid = temC[0];
//                    if(mid>=48&&mid<=57){//数字
//                        return;
//                    }
//                    s.delete(temp.length()-1, temp.length());
//                } catch (Exception e) {
//                    // TODO: handle exception
//                }
//            }
//        });

        address_name.setFilters(new InputFilter[]{filter,new InputFilter.LengthFilter(10)});
        house = findViewById(R.id.house);
        company = findViewById(R.id.company);
        address_labels = findViewById(R.id.address_labels);
        inputManager = (InputMethodManager)address_labels.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        house.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                house.setTextColor(Color.RED);
                company.setTextColor(getResources().getColor(R.color.figure_experience_text_color));
                address_labels.setFocusable(false);
                address_labels.setFocusableInTouchMode(false);
                address_labels.requestFocus();
                inputManager.hideSoftInputFromWindow(address_labels.getWindowToken(),0);
                address_labels.setText("");
                labels = "家";

            }
        });

        company.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                house.setTextColor(getResources().getColor(R.color.figure_experience_text_color));
                company.setTextColor(Color.RED);
                address_labels.setFocusable(false);
                address_labels.setFocusableInTouchMode(false);
                address_labels.requestFocus();
                inputManager.hideSoftInputFromWindow(address_labels.getWindowToken(),0);
                address_labels.setText("");
                labels = "公司";


            }
        });

        address_labels.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                house.setTextColor(getResources().getColor(R.color.figure_experience_text_color));
                company.setTextColor(getResources().getColor(R.color.figure_experience_text_color));
                address_labels.setFocusable(true);
                address_labels.setFocusableInTouchMode(true);
                address_labels.requestFocus();
                address_labels.requestFocusFromTouch();
                address_labels.setTextColor(Color.RED);
                inputManager.showSoftInput(address_labels, 0);
                labels = address_labels.getText().toString().trim();
            }
        });

        //address_detailed.addTextChangedListener(new JumpTextWatcher());
        address_labels.addTextChangedListener(new AddressLabelsWatcher());
        intent = getIntent();
        final String id = intent.getStringExtra("id");

        if (id  != null){
            address_name.setText(intent.getStringExtra("name"));
            address_phone.setText(intent.getStringExtra("phone"));
            address_select.setText(intent.getStringExtra("select"));
            address_detailed.setText(intent.getStringExtra("detailed"));
            Log.d(TAG, "initView: " + id);
            actionBar.setTitle("编辑地址");
        }

        //进入地区选择界面
        address_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewAddAddressActivity.this, InputTipsActivity.class);
                intent.putExtra("data_select",address_select.getText().toString().trim());

                startActivityForResult(intent,1);
            }
        });

        //保存数据
        address_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(address_name.getText().toString().trim())){
                    Toast.makeText(NewAddAddressActivity.this, "收货人不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(address_phone.getText().toString().trim()) ){
                    Toast.makeText(NewAddAddressActivity.this, "手机号码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (address_select.getText().toString().trim().equals("点击选择地区")){
                    Toast.makeText(NewAddAddressActivity.this, "请选择收货地址", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (address_phone.getText().toString().trim().length() < 11) {
                    Toast.makeText(NewAddAddressActivity.this, "手机号码格式不正确", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!RegexUtils.checkMobile(address_phone.getText().toString().trim())) {
                    Toast.makeText(NewAddAddressActivity.this, "手机号码格式不正确", Toast.LENGTH_SHORT).show();
                    return;
                }

                AddAddress();

            }
        });
    }

    //增加地址
    public void AddAddress(){
        //当前时间戳
        current_time = String.valueOf(System.currentTimeMillis());
        sharedPreferencesUtil = new SharedPreferencesUtil(this);
        name =  address_name.getText().toString();
        phone =  address_phone.getText().toString();
        select =  address_select.getText().toString();
        detailed =  address_detailed.getText().toString();

        final AddAddress addAddress = new AddAddress();
        UserInfoBean userInfoBean = new UserInfoBean();
        userInfoBean.setConsignee(name);
        userInfoBean.setConsigneeMobile(phone);
        userInfoBean.setAddressRegion(select);
        userInfoBean.setAddressDetail(detailed);
        userInfoBean.setAddressAlias(labels);
        userInfoBean.setAddressCity(current_time);
        userInfoBean.setAddressDistrict("");
        userInfoBean.setAddressProvince("");
        userInfoBean.setAddressType("0");
        userInfoBean.setLatitude(latitude);
        userInfoBean.setLongitude(longitude);
        addAddress.setUserInfoBean(userInfoBean);
        addAddress.setCreateid(current_time);
        addAddress.setUuid(sharedPreferencesUtil.getUUID("uuid"));

        ApiRequestUtils_1.addAddress(this, addAddress, 0x04, new NetRequestCallback() {
            @Override
            public void success(int requestCode, String data) {
                Log.d(TAG, "success: "+ data);
                if (!TextUtils.isEmpty(SpUtils.get(getApplicationContext(),Constant.TAKEOUT_SELLER,""))){
                    if (SpUtils.get(getApplicationContext(),Constant.TAKEOUT_SELLER,"").equals(Constant.TAKEOUT_SELLER_SELLER_VALUE)) {
//                        Intent intent = new Intent(NewAddAddressActivity.this, TakeoutSellerActivity.class);
//                        SpUtils.put(getApplicationContext(), "phone", name + " " + phone);
//                        SpUtils.put(getApplicationContext(), "address", select + detailed);
//                    intent.putExtra("order_id",SpUtils.get(getApplicationContext(),"order_id",""));
//                        startActivity(intent);
                        SpUtils.put(getApplicationContext(), Constant.TAKEOUT_SELLER, "");
                        finish();
                    }else if (SpUtils.get(getApplicationContext(),Constant.TAKEOUT_SELLER,"").equals(Constant.TAKEOUT_SELLER_CAR_VALUE)){
//                        Intent intent = new Intent(NewAddAddressActivity.this, TakeoutCarActivity.class);
//                        startActivity(intent);
                        SpUtils.put(getApplicationContext(), Constant.TAKEOUT_SELLER, "");
                        finish();
                    }
                } else if (!TextUtils.isEmpty(SpUtils.get(getApplicationContext(),Constant.COMMIT_ORDER_ADDRESS,""))
                        && SpUtils.get(getApplicationContext(), Constant.COMMIT_ORDER_ADDRESS,"")
                        .equals(Constant.COMMIT_ORDER_ADDRESS_VALUE)){
                    saveUserInfo();
                }else {
                    Intent intent = new Intent(NewAddAddressActivity.this, AddressActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void mistake(int requestCode, FProtocol.NetDataProtocol.ResponseStatus status, String errorMessage) {}
        });
    }

    private List<DataBean> mDataBean = null;
    private void saveUserInfo() {
        final AddressEntity addressEntity = new AddressEntity();
        final GetAdderssList getAdderssList = new GetAdderssList();
        getAdderssList.setMo(sharedPreferencesUtil.getUUID("mobile"));//通过当前登录账号拿地址
        Log.d(TAG, "searchAddress: 当前手机号码");
        ApiRequestUtils_1.getAddressList(this, getAdderssList, 0x08, new NetRequestCallback() {
            @Override
            public void success(int requestCode, String data) {
                String json = data.substring(data.indexOf("data") + 6, data.indexOf("msg") - 2);
                Gson gson = new Gson();
                AllAddress allAddress = gson.fromJson(data, AllAddress.class);
                if (allAddress.getRet() != 200) {
                    //服务器无地址返回
                    Log.d(TAG, "success: 无地址返回");
                } else {
                    //有地址
                    mDataBean = allAddress.getData();
                    //addressEntity
                    if (!TextUtils.isEmpty(sharedPreferencesUtil.getUUID("uuid"))) {
                        if (mDataBean != null && mDataBean.size() > 0) {
                            addressEntity.setLoginPhone(mDataBean.get(mDataBean.size() - 1).getConsigneeMobile());
                            addressEntity.setNotifyPhone(mDataBean.get(mDataBean.size() - 1).getConsigneeMobile());
                            addressEntity.setName(mDataBean.get(mDataBean.size() - 1).getConsignee());
                            addressEntity.setLatitude((String) mDataBean.get(mDataBean.size() - 1).getLatitude());
                            addressEntity.setLongitude((String) mDataBean.get(mDataBean.size() - 1).getLongitude());
                            LogUtils.i(TAG, "地址：" + mDataBean.get(mDataBean.size() - 1).getAddressDistrict()
                                    + "--" + mDataBean.get(mDataBean.size() - 1).getAddressRegion()
                                    + "--" + mDataBean.get(mDataBean.size() - 1).getAddressDetail());
                            addressEntity.setAddress(mDataBean.get(mDataBean.size() - 1).getAddressDistrict()
                                    + mDataBean.get(mDataBean.size() - 1).getAddressRegion() + mDataBean.get(mDataBean.size() - 1).getAddressDetail());
                            addressEntity.setAddressJson(json.replace(" ", "").trim());
                            addressEntity.setUserid(sharedPreferencesUtil.getUUID("uuid"));
                        }
                        SaleClient.getInstance().updateUserInfo(addressEntity);
                        finish();
                        SpUtils.put(getApplicationContext(),Constant.COMMIT_ORDER_ADDRESS,"");
                    }
                }
            }

            @Override
            public void mistake ( int requestCode, FProtocol.
                    NetDataProtocol.ResponseStatus status, String errorMessage){
                Log.d(TAG, "fail: AllAddress " + requestCode);
            }
        });
    }

    private void initToolbar_newAdd() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setTitle("新增地址");
        actionBar.setDisplayHomeAsUpEnabled(true);
        mBitmap_toolbar = BitmapUtils.scaleBitmapFromResources(this,R.mipmap.path,
                70,70);
        actionBar.setHomeAsUpIndicator(new BitmapDrawable(mBitmap_toolbar));
    }



    private class AddressLabelsWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String str=s.toString();
            if (str.indexOf("\r")>=0 || str.indexOf("\n")>=0){//发现输入回车符或换行符
                address_labels.setText(str.replace("\r","").replace("\n",""));//去掉回车符和换行符
                address_labels.setSelection(address_labels.getText().length());
                //关闭输入法
                ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                        NewAddAddressActivity.this.getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
            }
            labels = address_labels.getText().toString().trim();

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                back();
                break;
            default:break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(NewAddAddressActivity.this,AddressActivity.class));
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 1:
                if (resultCode == 11) {
                    address_select.setText(data.getStringExtra("address"));
                    String latlng = data.getStringExtra("latlng");
                    Gson gson = new Gson();
                    LatLng point = gson.fromJson(latlng,new TypeToken<LatLng>(){}.getType());
                    latitude = String.valueOf(point.latitude);
                    longitude = String.valueOf(point.longitude);
                    Log.i(TAG, "onActivityResult: "+"经度:"+point.latitude+",维度："+point.longitude);
                }else if (requestCode == 12){
                    address_select.setText(data.getStringExtra("address"));
                }
                break;
        }
    }
}
