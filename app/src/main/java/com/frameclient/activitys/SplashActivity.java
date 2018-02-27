package com.frameclient.activitys;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.frameclient.services.NetWorkService;
import com.frameclient.utils.Constants;

public class SplashActivity extends Activity {


    //    private String username;
    //    private String password;
    //    private String ipaddr;

    private EditText view_username;
    private EditText view_password;
    private EditText view_ipaddr;
    private Button view_login;
    private CheckBox view_remember;
    private CheckBox view_autologin;
    private RelativeLayout login_layout;
    private RelativeLayout init_layout;


    private static final String TAG = "FrmaeDebug";
    private static final int MENU_EXIT = Menu.FIRST - 1;
    private static final int MENU_ABOUT = Menu.FIRST;
    /**
     * 判断是否本地有用户信息,如果有,则直接登录,如果没有,则显示登录界面
     */
    private boolean hasInfo = false;

    /**
     * 用来操作SharePreferences的标识
     */
    private final String SHARE_LOGIN_TAG = "SHARE_LOGIN_TAG";
    /**
     * 如果登录成功后,用于保存用户名到SharedPreferences,以便下次不再输入
     */
    private String SHARE_LOGIN_USERNAME = "LOGIN_USERNAME";
    /**
     * 如果登录成功后,用于保存PASSWORD到SharedPreferences,以便下次不再输入
     */
    private String SHARE_LOGIN_PASSWORD = "LOGIN_PASSWORD";
    /**
     * 如果登录成功后,用于保存PASSWORD到SharedPreferences,以便下次不再输入
     */
    private String SHARE_LOGIN_IP_ADDR = "LOGIN_IPADDR";

    /** 如果登陆失败,这个可以给用户确切的消息显示,true是网络连接失败,false是用户名和密码错误 */

    /**
     * 登录loading提示框
     */
    private ProgressDialog proDialog;

    /**
     * 登录后台通知更新UI线程,主要用于登录失败,通知UI线程更新界面
     */
    Handler loginHandler = new Handler() {
        public void handleMessage(Message msg) {
            int error = msg.getData().getInt("error");

            switch (error) {
                case 1:
                    if (proDialog != null) {
                        proDialog.dismiss();
                    }
                    Toast.makeText(SplashActivity.this, "没登录!", Toast.LENGTH_SHORT)
                            .show();
                    changeLayout();
                    break;
                case 2:
                    if (proDialog != null) {
                        proDialog.dismiss();
                    }
                    Toast.makeText(SplashActivity.this, "网络错误!", Toast.LENGTH_SHORT)
                            .show();
                    changeLayout();
                    break;
                case 10:
                    if (proDialog != null) {
                        proDialog.dismiss();
                    }
                    Toast.makeText(SplashActivity.this, "没初始化!", Toast.LENGTH_SHORT)
                            .show();
                    changeLayout();
                    break;
                case 11:
                    if (proDialog != null) {
                        proDialog.dismiss();
                    }
                    Toast.makeText(SplashActivity.this, "参数错误!", Toast.LENGTH_SHORT)
                            .show();
                    changeLayout();
                    break;
                case 12:
                    if (proDialog != null) {
                        proDialog.dismiss();
                    }
                    Toast.makeText(SplashActivity.this, "数据错误!", Toast.LENGTH_SHORT)
                            .show();
                    changeLayout();
                    break;
                case 1001:
                    if (proDialog != null) {
                        proDialog.dismiss();
                    }
                    Toast.makeText(SplashActivity.this, "数据库访问错误!",
                            Toast.LENGTH_SHORT).show();
                    changeLayout();
                    break;
                case 1101:
                    if (proDialog != null) {
                        proDialog.dismiss();
                    }
                    Toast.makeText(SplashActivity.this, "摄像机不存在!",
                            Toast.LENGTH_SHORT).show();
                    break;
                case 1102:
                    if (proDialog != null) {
                        proDialog.dismiss();
                    }
                    Toast.makeText(SplashActivity.this, "没找到转码服务器!",
                            Toast.LENGTH_SHORT).show();
                    changeLayout();
                    break;
                case 1103:
                    if (proDialog != null) {
                        proDialog.dismiss();
                    }
                    Toast.makeText(SplashActivity.this, "设备未登录!", Toast.LENGTH_SHORT)
                            .show();
                    break;
                case 1104:
                    if (proDialog != null) {
                        proDialog.dismiss();
                    }
                    Toast.makeText(SplashActivity.this, "设备流打开错误!",
                            Toast.LENGTH_SHORT).show();
                    break;
                case 1105:
                    if (proDialog != null) {
                        proDialog.dismiss();
                    }
                    Toast.makeText(SplashActivity.this, "连接流媒体或转码服务器失败!",
                            Toast.LENGTH_SHORT).show();
                    changeLayout();

                    break;
                case 0:
                    if (proDialog != null) {
                        proDialog.dismiss();
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //                                    Toast.makeText(SplashActivity.this, "登陆成功",
                                    // Toast.LENGTH_SHORT)
                                    //                                            .show();
                                    Intent intent = new Intent();
                                    intent.putExtra("isLogin", true);
                                    intent.setClass(SplashActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });

                        }
                    }, 1000);

                    try {
                        unregisterReceiver(broadcastRec);
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                    break;
                case -1:
                    if (proDialog != null) {
                        proDialog.dismiss();
                    }
                    Toast.makeText(SplashActivity.this, "用户不存在!", Toast.LENGTH_SHORT)
                            .show();
                    view_username.setText("");
                    view_password.setText("");
                    clearShareUserInfo(false, true, true);
                    changeLayout();

                    break;
                case -2:
                    if (proDialog != null) {
                        proDialog.dismiss();
                    }
                    Toast.makeText(SplashActivity.this, "密码错误!", Toast.LENGTH_SHORT)
                            .show();
                    view_password.setText("");
                    clearShareUserInfo(false, false, true);
                    changeLayout();

                    break;
                case -3:
                    if (proDialog != null) {
                        proDialog.dismiss();
                    }
                    Toast.makeText(SplashActivity.this, "服务器无法连接!",
                            Toast.LENGTH_SHORT).show();
                    changeLayout();

                    break;
                case -4:
                    if (proDialog != null) {
                        proDialog.dismiss();
                    }
                    Toast.makeText(SplashActivity.this, "获取资源失败!",
                            Toast.LENGTH_SHORT).show();
                    changeLayout();
                    break;
                case -5:
                    if (proDialog != null) {
                        proDialog.dismiss();
                    }
                    Toast.makeText(SplashActivity.this, "IP地址为空!",
                            Toast.LENGTH_SHORT).show();
                    break;
                case -6:
                    if (proDialog != null) {
                        proDialog.dismiss();
                    }
                    Toast.makeText(SplashActivity.this, "用户名为空!", Toast.LENGTH_SHORT)
                            .show();
                    break;
                case -7:
                    if (proDialog != null) {
                        proDialog.dismiss();
                    }
                    Toast.makeText(SplashActivity.this, "密码为空!", Toast.LENGTH_SHORT)
                            .show();
                    break;
                case -8:
                    if (proDialog != null) {
                        proDialog.dismiss();
                    }
                    Toast.makeText(SplashActivity.this, "登陆超时!", Toast.LENGTH_SHORT)
                            .show();
                    changeLayout();
                    break;
                default:
                    break;

            }
        }
    };

    private void changeLayout() {
        init_layout.setVisibility(View.GONE);
        login_layout.setVisibility(View.VISIBLE);
    }

    private BroadcastReceiver broadcastRec = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub

            if (intent.getAction().equals("com.frameclient.login.rsp")) {
                int err = intent.getIntExtra("error", -100);

                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putInt("error", err);
                message.setData(bundle);
                loginHandler.sendMessage(message);
            } else if (intent.getAction().equals("com.frameclient.connection.rsp")) {
                int res = intent.getIntExtra("result", -100);
                if (res < 0) {
                    Message message = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putInt("error", -3);
                    message.setData(bundle);
                    loginHandler.sendMessage(message);
                }


            } else if (intent.getAction().equals("com.frameclient.logout.rsp")) {

                String uid = intent.getStringExtra("uid");
            } else if (intent.getAction().equals("com.frameclient.login.timeout")) {
                int result = intent.getIntExtra("result", -100);
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putInt("error", -8);
                message.setData(bundle);
                loginHandler.sendMessage(message);
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        findViewsById();
        setListener();
        getSharedData();
        registerBroadcastReceiver();

        //无用户信息显示登录界面，有用户信息直接登录
        if (hasInfo) {
            init_layout.setVisibility(View.VISIBLE);
            login();
        } else {
            setData(false);
            login_layout.setVisibility(View.VISIBLE);
        }
        //

        //Log.i(TAG, "starting newtworkservice.....");
        // Intent it = new Intent("com.frameclient.services.networkservice");
        //Intent it = new Intent(LoginActivity.this, NetWorkService.class);
        //startService(it);

    }

    //注册广播
    private void registerBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.frameclient.connection.rsp");
        intentFilter.addAction("com.frameclient.login.rsp");
        intentFilter.addAction("com.frameclient.logout.rsp");
        intentFilter.addAction("com.frameclient.login.timeout");
        Log.i(TAG, "registerReceiver.....");
        registerReceiver(broadcastRec, intentFilter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }


    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        super.onMenuItemSelected(featureId, item);
        switch (item.getItemId()) {
            case MENU_EXIT:
                finish();
                break;
            case MENU_ABOUT:
                alertAbout();
                break;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "-->onDestory");
        try {
            unregisterReceiver(broadcastRec);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == RESULT_OK) {
            Log.v(TAG, "onActivityResult : loginacitvity finish");
            finish();
        }
    }

    private void findViewsById() {
        view_username = (EditText) findViewById(R.id.username);
        view_password = (EditText) findViewById(R.id.password);
        view_ipaddr = (EditText) findViewById(R.id.ipaddr);
        view_login = (Button) findViewById(R.id.btn_login);
        view_remember = (CheckBox) findViewById(R.id.remember);
        view_autologin = (CheckBox) findViewById(R.id.autologin);
        login_layout = (RelativeLayout) findViewById(R.id.login_layout);
        init_layout = (RelativeLayout) findViewById(R.id.init_layout);
    }

    /**
     * 初始化界面
     *
     * @param isRememberMe
     */
    private void setData(boolean isRememberMe) {
        view_username.setText(Constants.LOGIN_NAME);
        view_password.setText(Constants.LOGIN_PWD);
        //view_ipaddr.setText(Constants.BASE_IP);
    }

    /**
     * 获取用户信息
     */
    private void getSharedData() {
        SharedPreferences share = getSharedPreferences(SHARE_LOGIN_TAG, 0);
        //String ip = share.getString(SHARE_LOGIN_IP_ADDR, "");
        String username = share.getString(SHARE_LOGIN_USERNAME, "");
        String password = share.getString(SHARE_LOGIN_PASSWORD, "");
        if (/*!TextUtils.isEmpty(ip) &&*/ !TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
            // Constants.BASE_IP = ip;
            Constants.LOGIN_NAME = username;
            Constants.LOGIN_PWD = password;
            hasInfo = true;
        }

        Log.d(this.toString(), "userName=" + username + " password=" + password
                + " IPAddr = ");
        share = null;

    }

    /**
     * 保存用户信息
     */
    private void saveSharePreferences(boolean saveIpaddr, boolean saveUserName,
                                      boolean savePassword) {
        SharedPreferences share = getSharedPreferences(SHARE_LOGIN_TAG, 0);
        if (saveUserName) {
            Log.d(this.toString(), "saveUserName="
                    + view_username.getText().toString());
            share.edit()
                    .putString(SHARE_LOGIN_USERNAME,
                            view_username.getText().toString()).commit();
        }
        if (savePassword) {
            share.edit()
                    .putString(SHARE_LOGIN_PASSWORD,
                            view_password.getText().toString()).commit();
        }
        if (saveIpaddr) {
            share.edit()
                    .putString(SHARE_LOGIN_IP_ADDR,
                            view_ipaddr.getText().toString()).commit();
        }
        share = null;
    }

    private void setListener() {
        view_login.setOnClickListener(submitListener);
        view_remember.setOnClickListener(rememberPasswordListener);
        view_autologin.setOnClickListener(autologinListener);
    }

    private void login() {
        //        proDialog = ProgressDialog.show(SplashActivity.this, "连接中..",
        //                "连接中..请稍后....", true, true);
        Thread loginThread = new Thread(new SplashActivity.LoginFailureHandler());
        loginThread.start();
    }

    /**
     * 登录Button Listener
     */
    private View.OnClickListener submitListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (checkData()) {
                saveSharePreferences(false, true, true);
                //            proDialog = ProgressDialog.show(SplashActivity.this, "连接中..",
                //                    "连接中..请稍后....", true, true);
                init_layout.setVisibility(View.VISIBLE);
                Thread loginThread = new Thread(new SplashActivity.LoginFailureHandler());
                loginThread.start();
            }

        }


    };

    /**
     * 点击登录的时验证数据是否合格
     */
    private boolean checkData() {
        //String ipaddr = view_ipaddr.getText().toString();
        String username = view_username.getText().toString();
        String password = view_password.getText().toString();
        /*if ("".equals(ipaddr)) {
            Message message = new Message();
            Bundle bundle = new Bundle();
            bundle.putInt("error", -5);
            message.setData(bundle);
            loginHandler.sendMessage(message);
            return false;
        } else*/
        if ("".equals(username)) {
            Message message = new Message();
            Bundle bundle = new Bundle();
            bundle.putInt("error", -6);
            message.setData(bundle);
            loginHandler.sendMessage(message);
            return false;
        } else if ("".equals(password)) {
            Message message = new Message();
            Bundle bundle = new Bundle();
            bundle.putInt("error", -7);
            message.setData(bundle);
            loginHandler.sendMessage(message);
            return false;
        }
        //Constants.BASE_IP = ipaddr;
        Constants.LOGIN_NAME = username;
        Constants.LOGIN_PWD = password;
        return true;
    }

    /**
     * 记住密码 checkbox Listener
     */
    private View.OnClickListener rememberPasswordListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (view_remember.isChecked()) {
                saveSharePreferences(false, true, true);
            }
        }
    };

    /**
     * 自动登录 checkbox Listener
     */
    private View.OnClickListener autologinListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (view_autologin.isChecked() && !view_remember.isChecked()) {
                saveSharePreferences(false, true, true);
            }
        }
    };

    /**
     * 弹出关于对话框
     */
    private void alertAbout() {
        new AlertDialog.Builder(SplashActivity.this)
                .setTitle(R.string.MENU_ABOUT)
                .setMessage(R.string.aboutInfo)
                .setPositiveButton(R.string.ok_label,
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialoginterface, int i) {
                            }
                        }).show();
    }

    /**
     * 清除密码
     */
    private void clearShareUserInfo(boolean ip, boolean username,
                                    boolean password) {
        SharedPreferences share = getSharedPreferences(SHARE_LOGIN_TAG, 0);
        if (ip) {
            share.edit().putString(SHARE_LOGIN_IP_ADDR, "").commit();
        }

        if (username) {
            share.edit().putString(SHARE_LOGIN_USERNAME, "").commit();
        }

        if (password) {
            share.edit().putString(SHARE_LOGIN_PASSWORD, "").commit();
        }

        share = null;
    }

    class LoginFailureHandler implements Runnable {
        @Override
        public void run() {
            //Constants.IP_ADDRESS = Constants.BASE_IP + ":6611";
            Intent it = new Intent(SplashActivity.this, NetWorkService.class);
            Bundle bundle = new Bundle();
            bundle.putInt("opera", 0);
            bundle.putString("ipaddr", Constants.IP_ADDRESS);
            bundle.putString("username", Constants.LOGIN_NAME);
            bundle.putString("password", Constants.LOGIN_PWD);
            it.putExtras(bundle);
            startService(it);


        }

    }

}
