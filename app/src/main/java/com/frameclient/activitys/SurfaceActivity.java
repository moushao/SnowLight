package com.frameclient.activitys;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.frameclient.services.VideoService;
import com.frameclient.utils.ResourceItemInfo;
import com.frameclient.utils.SoftResource;

import java.util.ArrayList;
import java.util.List;

import retrofit2.http.POST;

/**
 * Created by MouShao on 2018/2/26.
 */

public class SurfaceActivity extends Activity {
    public static String TAG = "SurfaceActivity";
    private Context mContext;
    private SurfaceView view_sfv = null;
    private SurfaceHolder holder;
    private ProgressDialog proDialog;
    boolean start_decode = false;
    Bitmap bitmap = null;
    boolean hide = false; //界面是否隐藏
    private List<ResourceItemInfo> resList;
    private ImageView rightImage;
    private ImageView leftImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surface);
        init();
        registerBroadcast();
        initResList();
    }

    private void init() {
        mContext = this;
        view_sfv = (SurfaceView) findViewById(R.id.sf_test);
        rightImage = (ImageView) findViewById(R.id.iv_right);
        leftImage = (ImageView) findViewById(R.id.iv_left);
        rightImage.setOnClickListener(clickListener);
        leftImage.setOnClickListener(clickListener);
        holder = view_sfv.getHolder();
        if (SoftResource.camera_pos == 0) {
            leftImage.setVisibility(View.INVISIBLE);
        }
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_left:
                    findNewPos(SoftResource.camera_pos - 1);
                    break;
                case R.id.iv_right:
                    findNewPos(SoftResource.camera_pos + 1);
                    break;
            }
        }
    };

    private void findNewPos(int newPos) {
        if (newPos == 0) {
            leftImage.setVisibility(View.INVISIBLE);
        } else if (newPos == resList.size() - 1) {
            rightImage.setVisibility(View.INVISIBLE);
        } else {
            leftImage.setVisibility(View.VISIBLE);
            rightImage.setVisibility(View.VISIBLE);
        }

        jumpToNextCamera(newPos);
    }

    private void jumpToNextCamera(int newPos) {
        ResourceItemInfo rii = resList.get(newPos);
        SoftResource.cameraid = rii.id;
        SoftResource.stream = rii.stream_server;
        SoftResource.camera_pos = newPos;
        startVideoService(SoftResource.uid, SoftResource.cameraid, SoftResource.stream);
    }

    private void initResList() {
        resList = new ArrayList<>();
        for (int i = 0; i < SoftResource.getList().size(); i++) {
            ResourceItemInfo res = SoftResource.getList().get(i);
            if (res.type == SoftResource.type_camera) {
                resList.add(res);
            }
        }

        if (resList.size() > 0 && resList.get(0) != null) {
            //list_view.setSelection(37);
            startVideoService(SoftResource.uid, SoftResource.cameraid, SoftResource.stream);
        }
    }

    private void registerBroadcast() {
        if (SoftResource.bmp_queue.isEmpty() == false) {
            Log.e(TAG, "on create cleanr bmp_queue 3");
            SoftResource.bmp_queue.clear();
        }

        IntentFilter intentfilter = new IntentFilter();
        intentfilter.addAction("com.frameclient.videostart.rsp");
        intentfilter.addAction("com.frameclient.videoend.rsp");
        intentfilter.addAction("com.frameclient.videodata");
        intentfilter.addAction("com.frameclient.nodata");
        intentfilter.addAction("com.frameclient.socket.err");
        intentfilter.addAction("com.frameclient.video.err");
        intentfilter.addAction("com.frameclient.videoserver.dead");
        intentfilter.addAction("com.frameclient.video.firstframe");
        intentfilter.addAction("com.frameclient.video.pixel.change");
        intentfilter.addAction("com.frameclient.notify");
        intentfilter.addAction("com.frameclient.otherlogin.rsp");
        intentfilter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);

        registerReceiver(broadcastRec, intentfilter);
    }

    private void startVideoService(String uid, String cameraid, String stream) {

        stopService(new Intent(mContext, VideoService.class));

        Log.i(TAG, "starting videoService..... cameraId = " + cameraid + " uid = " + uid + " stream = " + stream);

        Intent it = new Intent(mContext, VideoService.class);

        Bundle bundle = new Bundle();
        bundle.putInt("opera", 0);
        bundle.putString("uid", uid);
        bundle.putString("stream", stream);
        bundle.putString("id", cameraid);
        it.putExtras(bundle);
        mContext.startService(it);

        proDialog = ProgressDialog.show(mContext, "连接中..", "请稍后....", true, true);
    }

    private BroadcastReceiver broadcastRec = new BroadcastReceiver() {
        @SuppressLint("NewApi")
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            if (intent.getAction().equals("com.frameclient.videostart.rsp")) {
                int err = intent.getIntExtra("error", 1);
                if (err == SoftResource.err_net_failure) {
                    Message message = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putInt("message", SoftResource.err_net_failure);
                    message.setData(bundle);
                    handler.sendMessage(message);
                }
            } else if (intent.getAction().equals("com.frameclient.videoend.rsp")) {

            } else if (intent.getAction().equals("com.frameclient.nodata")) {
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putInt("message", -2);
                message.setData(bundle);
                handler.sendMessage(message);
            } else if (intent.getAction().equals("com.frameclient.video.firstframe")) {
                if (proDialog != null) {
                    proDialog.dismiss();
                    proDialog = null;
                }

                Log.i(TAG, "---> get first frame and start process thread");
                //bitmapProcess = new Thread(new BitmapProcess());// 启动收听线程线程
                //bitmapProcess.start();
            } else if (intent.getAction().equals("com.frameclient.videodata")) {

                //Log.i(TAG,"get videodata");
                if (!hide) {
                    Message message = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putInt("message", 100);
                    message.setData(bundle);
                    handler.sendMessage(message);
                } else {
                    if (SoftResource.bmp_queue.isEmpty() == false) {
                        Log.e(TAG, "hide clear 1 frame bmp_queue 2");
                        SoftResource.bmp_queue.clear();
                        //bitmap = SoftResource.bmp_queue.poll();
                    }
                }

            } else if (intent.getAction().equals("com.frameclient.socket.err")) {
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putInt("message", -4);
                message.setData(bundle);
                handler.sendMessage(message);
            } else if (intent.getAction().equals("com.frameclient.video.err")) {
                int error = intent.getIntExtra("error", -1001);
                Log.e(TAG, "video error = " + error);
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putInt("message", error);
                message.setData(bundle);
                handler.sendMessage(message);
            } else if (intent.getAction().equals("com.frameclient.videoserver.dead")) {

                Log.v(TAG, "Recv the last video server dead msg");


                String uid = SoftResource.uid;
                String cameraid = SoftResource.cameraid;
                String stream = SoftResource.stream;


                Log.i(TAG, "starting videoservice..... cameraid = " + cameraid);

                Intent it = new Intent(mContext, VideoService.class);

                Bundle bundle = new Bundle();
                bundle.putInt("opera", 0);
                bundle.putString("uid", uid);
                bundle.putString("stream", stream);
                bundle.putString("id", cameraid);
                it.putExtras(bundle);

                startService(it);
            } else if (intent.getAction().equals("com.frameclient.video.pixel.change")) {
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putInt("message", 1105);
                message.setData(bundle);
                handler.sendMessage(message);
            } else if (intent.getAction().equals("com.frameclient.video.pixel.change")) {

            } else if (intent.getAction().equals("com.frameclient.otherlogin.rsp")) {
                int err = intent.getIntExtra("error", -100);

                Message message = new Message();
                Bundle bundle = new Bundle();
                if (err == 0) {
                    bundle.putInt("message", 500);
                } else {
                    bundle.putInt("message", 501);
                }

                message.setData(bundle);
                handler.sendMessage(message);
            } else if (intent.getAction().equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra("reason");
                if (reason != null) {
                    if (reason.equals("homekey")) {
                        // home key处理点
                        Log.i(TAG, "+++++++++++++ home key++++++++++++++");

                    } else if (reason.equals("recentapps")) {
                        // long home key处理点
                    }
                }
            }
        }
    };

    private void over(String str) {
        if (str != "") {
            Toast.makeText(mContext, str, Toast.LENGTH_SHORT).show();
        }
    }

    private void go_back(String str) {
        over(str);
    }

    Handler handler = new Handler() {
        @SuppressLint("NewApi")
        public void handleMessage(Message msg) {
            int message = msg.getData().getInt("message");

            switch (message) {
                case 0:
                    Log.i(TAG, "err 0");
                    go_back("超时无数据");
                    break;
                case 1:
                    Log.i(TAG, "err 1");
                    go_back("没登陆");
                    break;
                case 2:
                    Log.i(TAG, "err 2");
                    go_back("网络错误");
                    break;
                case 10:
                    Log.i(TAG, "err 10");
                    go_back("没初始化");
                    break;
                case 11:
                    Log.i(TAG, "err 11");
                    go_back("参数错误");
                    break;
                case 12:
                    Log.i(TAG, "err 12");
                    go_back("数据错误");
                    break;
                case 500:
                    if (proDialog != null) {
                        proDialog.dismiss();
                        proDialog = null;
                    }
                    proDialog = ProgressDialog.show(mContext, "视频请求中..", "请稍后....", true, true);
                    break;
                case 501:
                    go_back("登录错误");
                    break;
                case 1001:
                    Log.i(TAG, "err 1001");
                    go_back("数据库访问出错");
                    break;
                case 1101:
                    Log.i(TAG, "err 1101");
                    go_back("摄像机不存在");
                    break;
                case 1102:
                    Log.i(TAG, "err 1102");
                    go_back("没找到转发服务器");
                    break;
                case 1103:
                    Log.i(TAG, "err 1103");
                    go_back("设备未登陆");
                    break;
                case 1104:
                    Log.i(TAG, "err 1104");
                    go_back("设备流打开错误");
                    break;
                case 1105:
                    Log.i(TAG, "err 1105");
                    go_back("连接流媒体或转码服务器失败");
                    break;
                case 1106:
                    Log.i(TAG, "err 1106");
                    go_back("当前连接摄像头过多，请稍后重试");
                    break;
                case 100:
                    //  paintOver = false;

                    if (!SoftResource.bmp_queue.isEmpty()) {
                        //view_test_frame.setText("剩余："+SoftResource.bmp_queue.size()+" 帧数据");
                        //Log.e(TAG, "bmp_queue size = "+SoftResource.bmp_queue.size());
                        bitmap = SoftResource.bmp_queue.poll();
                        if (bitmap != null) {
                            Canvas c = holder.lockCanvas(holder.getSurfaceFrame());
                            c.drawBitmap(bitmap, null, holder.getSurfaceFrame(), new Paint());
                            holder.unlockCanvasAndPost(c);// 更新屏幕显示内容
                        }
                    }

                    //paintOver = true;

                    break;
                case 10000:

                case 10001: {
                    Log.v(TAG, "turn right event");

                    /*ResourceItemInfo rii = findNextCamrea(true);
                    if(rii != null)
                    {
                        jumpToNextCamrera(rii);
                    }*/

                    break;
                }
                case 10002: {
                    /*Log.v(TAG,"turn left event");

                    ResourceItemInfo rii = findNextCamrea(false);
                    if(rii != null)
                    {
                        jumpToNextCamrera(rii);
                    }*/

                    break;
                }
                case -2: {
                    Log.i(TAG, "err -2");
                    go_back("数据通道无数据");
                    break;
                }
                case -3: {
                    Log.i(TAG, "err -3");
                    go_back("连接流媒体失败");
                    break;
                }
                case -4: {
                    Log.i(TAG, "err -4");
                    go_back("本地连接已断开");
                    break;
                }
                default:
                    break;

            }
        }
    };

    @Override
    public void onPause() {
        Log.i(TAG, "--->onPause list.size() = " + SoftResource.getListSize());

        super.onPause();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        hide = true;
        stopService(new Intent(mContext, VideoService.class));
        mContext.unregisterReceiver(broadcastRec);
        SoftResource.bmp_queue.clear();
        try {
            view_sfv.getHolder().getSurface().release();
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onBackPressed();
    }
}
