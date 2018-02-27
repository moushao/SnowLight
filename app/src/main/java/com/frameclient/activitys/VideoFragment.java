package com.frameclient.activitys;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
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
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.frameclient.services.VideoService;
import com.frameclient.utils.ResourceItemInfo;
import com.frameclient.utils.SoftResource;
import com.frameclient.widget.ZoomListenter;

/**
 * Created by Zhang on 2017/12/20.
 */

public class VideoFragment extends Fragment {

    private int cmd_type;
    private int cmd_status;

    private String TAG = "VideoFragment";

    public static MainActivity instance = null;

    boolean popInit = false;   //云台控制是否初始化
    boolean isShow = false;    //云台控制浮动窗口是否是显示状态
    boolean paintOver = false;  //一帧是否更新完毕
    boolean over = false;  //是否要返回上级页面
    boolean hide = false; //界面是否隐藏

    private Button view_cameralist = null;
    private Button view_remoteplay = null;
    private Button view_alarm = null;
    private Button view_localsave = null;
    private Button view_returnlist = null;
    private ImageView view_up = null;
    private ImageView view_down = null;
    private ImageView view_left = null;
    private ImageView view_right = null;
    private ImageView view_bigger = null;
    private ImageView view_smaller = null;
    private SurfaceView view_sfv = null;

    private SurfaceHolder holder;
    private ProgressDialog proDialog;
    private ZoomListenter zoomLister = null;

    LayoutInflater layoutInflater = null;
    View popview = null;
    PopupWindow popWindow = null;

    boolean start_decode = false;
    Bitmap bitmap = null;

    int width = 0;
    int height = 0;
    int need_scaler = 0;

    private void findViewById(View view)
    {
        view_cameralist = (Button)view.findViewById(R.id.btn_cameralist);
        view_remoteplay = (Button)view.findViewById(R.id.btn_remote_play);
        view_alarm = (Button)view.findViewById(R.id.btn_alarm);
        view_localsave = (Button)view.findViewById(R.id.btn_local_save);
        view_returnlist = (Button)view.findViewById(R.id.btn_return_list);
        view_sfv = (SurfaceView)view.findViewById(R.id.sf_test);

    }

    private void setListener()
    {
        view_cameralist.setOnClickListener(button_listerner);
        view_remoteplay.setOnClickListener(button_listerner);
        view_alarm.setOnClickListener(button_listerner);
        view_localsave.setOnClickListener(button_listerner);
        view_returnlist.setOnClickListener(button_listerner);
        view_sfv.setOnClickListener(sf_listener);
        //view_sfv.setOnTouchListener(zoomLister);
    }

    private View.OnTouchListener imageview_listerner = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    if(v.getId() == R.id.iv_bigger)
                    {
                        cmd_type = 20;
                        v.setBackgroundResource(R.drawable.bigb);
                    }
                    else if(v.getId() == R.id.iv_small)
                    {
                        cmd_type = 22;
                        v.setBackgroundResource(R.drawable.smallb);
                    }
                    else if(v.getId() == R.id.iv_up)
                    {
                        cmd_type = 0;
                        v.setBackgroundResource(R.drawable.upb);
                    }
                    else if(v.getId() == R.id.iv_down)
                    {
                        cmd_type = 2;
                        v.setBackgroundResource(R.drawable.downb);
                    }
                    else if(v.getId() == R.id.iv_left)
                    {
                        cmd_type = 4;
                        v.setBackgroundResource(R.drawable.leftb);
                    }
                    else if(v.getId() == R.id.iv_right)
                    {
                        cmd_type = 6;
                        v.setBackgroundResource(R.drawable.rightb);
                    }

                    break;
                case MotionEvent.ACTION_UP:
                    if(v.getId() == R.id.iv_bigger)
                    {
                        cmd_type = 21;
                        v.setBackgroundResource(R.drawable.big);
                    }
                    else if(v.getId() == R.id.iv_small)
                    {
                        cmd_type = 23;
                        v.setBackgroundResource(R.drawable.small);
                    }
                    else if(v.getId() == R.id.iv_up)
                    {
                        cmd_type = 1;
                        v.setBackgroundResource(R.drawable.up);
                    }
                    else if(v.getId() == R.id.iv_down)
                    {
                        cmd_type = 3;
                        v.setBackgroundResource(R.drawable.down);
                    }
                    else if(v.getId() == R.id.iv_left)
                    {
                        cmd_type = 5;
                        v.setBackgroundResource(R.drawable.left);
                    }
                    else if(v.getId() == R.id.iv_right)
                    {
                        cmd_type = 7;
                        v.setBackgroundResource(R.drawable.right);
                    }


                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
            }

            Intent it = new Intent("com.frameclient.control");
            it.putExtra("type", cmd_type);
            mActivity.sendBroadcast(it);
            return true;
        }
    };


    private View.OnClickListener button_listerner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.btn_return_list)
            {

            }
            else if(v.getId() == R.id.btn_alarm)
            {

            }
            else if(v.getId() == R.id.btn_remote_play)
            {

            }
            else if(v.getId() == R.id.btn_local_save)
            {

            }
            else if(v.getId() == R.id.btn_cameralist)
            {

                if (proDialog != null)
                {
                    proDialog.dismiss();
                }

                if(!start_decode)
                {
                    over("");
                }
                else
                {
                    over = true;

                    while(true)
                    {
                        if(paintOver)
                        {
                            over("");
                            break;
                        }
                        Log.i(TAG,"Wait the Canvas update the bitmap when destory video acitvity 02 ");
                    }
                }

            }
        }
    };

    private View.OnClickListener sf_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mActivity.stopService(new Intent(mActivity, VideoService.class));
                
            Intent itt = new Intent(getActivity(), SurfaceActivity.class);
            getActivity().startActivity(itt);
            
        }
    };

    Handler handler = new Handler() {
        @SuppressLint("NewApi")
        public void handleMessage(Message msg)
        {
            int message = msg.getData().getInt("message");

            switch(message)
            {
                case 0:
                    Log.i(TAG,"err 0");
                    go_back("超时无数据");
                    break;
                case 1:
                    Log.i(TAG,"err 1");
                    go_back("没登陆");
                    break;
                case 2:
                    Log.i(TAG,"err 2");
                    go_back("网络错误");
                    break;
                case 10:
                    Log.i(TAG,"err 10");
                    go_back("没初始化");
                    break;
                case 11:
                    Log.i(TAG,"err 11");
                    go_back("参数错误");
                    break;
                case 12:
                    Log.i(TAG,"err 12");
                    go_back("数据错误");
                    break;
                case 500:
                    if(proDialog != null)
                    {
                        proDialog.dismiss();
                        proDialog = null;
                    }
                    proDialog = ProgressDialog.show(mActivity, "视频请求中..","请稍后....", true, true);
                    break;
                case 501:
                    go_back("登录错误");
                    break;
                case 1001:
                    Log.i(TAG,"err 1001");
                    go_back("数据库访问出错");
                    break;
                case 1101:
                    Log.i(TAG,"err 1101");
                    go_back("摄像机不存在");
                    break;
                case 1102:
                    Log.i(TAG,"err 1102");
                    go_back("没找到转发服务器");
                    break;
                case 1103:
                    Log.i(TAG,"err 1103");
                    go_back("设备未登陆");
                    break;
                case 1104:
                    Log.i(TAG,"err 1104");
                    go_back("设备流打开错误");
                    break;
                case 1105:
                    Log.i(TAG,"err 1105");
                    go_back("连接流媒体或转码服务器失败");
                    break;
                case 1106:
                    Log.i(TAG,"err 1106");
                    go_back("当前连接摄像头过多，请稍后重试");
                    break;
                case 100:
                    if(!over)
                    {
                        paintOver = false;
                        if(!SoftResource.bmp_queue.isEmpty())
                        {
                            //view_test_frame.setText("剩余："+SoftResource.bmp_queue.size()+" 帧数据");
                            //Log.e(TAG, "bmp_queue size = "+SoftResource.bmp_queue.size());
                            bitmap = SoftResource.bmp_queue.poll();
                            if(bitmap != null)
                            {
                                Canvas c = holder.lockCanvas(holder.getSurfaceFrame());
                                c.drawBitmap(bitmap,null,holder.getSurfaceFrame(), new Paint());
                                holder.unlockCanvasAndPost(c);// 更新屏幕显示内容

                            }
                        }

                        paintOver = true;
                    }

                    break;
                case 10000:
//                {
//                    Log.v(TAG,"click event");
//
//                    if(!popInit)
//                    {
//                        layoutInflater = (LayoutInflater) (mActivity).getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                        popview = layoutInflater.inflate(R.layout.video_control, null);
//
//                        DisplayMetrics dm = new DisplayMetrics();
//                        //取得窗口属性
//                        mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
//                        //窗口的宽度
//                        int screenHeight = dm.heightPixels;
//                        //窗口高度
//                        int height = (int) ((int)screenHeight*0.32);
//
//                        Log.v(TAG,"pop height = "+height);
//                        popWindow = new PopupWindow(popview, FrameLayout.LayoutParams.MATCH_PARENT, height);
//                        popWindow.showAtLocation(findViewById(R.id.sf_test), Gravity.BOTTOM, 0, 0);
//
//                        view_up = (ImageView)popview.findViewById(R.id.iv_up);
//                        view_down = (ImageView)popview.findViewById(R.id.iv_down);
//                        view_left = (ImageView)popview.findViewById(R.id.iv_left);
//                        view_right = (ImageView)popview.findViewById(R.id.iv_right);
//                        view_bigger = (ImageView)popview.findViewById(R.id.iv_bigger);
//                        view_smaller = (ImageView)popview.findViewById(R.id.iv_small);
//
//                        view_up.setOnTouchListener(imageview_listerner);
//                        view_down.setOnTouchListener(imageview_listerner);
//                        view_left.setOnTouchListener(imageview_listerner);
//                        view_right.setOnTouchListener(imageview_listerner);
//                        view_bigger.setOnTouchListener(imageview_listerner);
//                        view_smaller.setOnTouchListener(imageview_listerner);
//
//                        popInit = true;
//                        isShow = true;
//                        return;
//                    }
//                    if(isShow)
//                    {
//                        popview.setVisibility(View.INVISIBLE);
//                        isShow = false;
//                    }
//                    else
//                    {
//
//                        popview.setVisibility(View.VISIBLE);
//                        isShow = true;
//                    }
//                    break;
//                }
                case 10001:
                {
                    Log.v(TAG,"turn right event");

                    ResourceItemInfo rii = findNextCamrea(true);
                    if(rii != null)
                    {
                        jumpToNextCamrera(rii);
                    }

                    break;
                }
                case 10002:
                {
                    Log.v(TAG,"turn left event");

                    ResourceItemInfo rii = findNextCamrea(false);
                    if(rii != null)
                    {
                        jumpToNextCamrera(rii);
                    }

                    break;
                }
                case -2:
                {
                    Log.i(TAG,"err -2");
                    go_back("数据通道无数据");
                    break;
                }
                case -3:
                {
                    Log.i(TAG,"err -3");
                    go_back("连接流媒体失败");
                    break;
                }
                case -4:
                {
                    Log.i(TAG,"err -4");
                    go_back("本地连接已断开");
                    break;
                }
                default:
                    break;

            }
        }
    };

    private ResourceItemInfo findNextCamrea(boolean next)
    {
        ResourceItemInfo rii = null;
        boolean find_camrea = false;
        int pos = SoftResource.camera_pos;

        while(true)
        {
            if(next)
            {
                pos++;
                if(pos > SoftResource.getAdapterListSize() -1 )
                {
                    pos = 0;
                }
            }
            else
            {
                pos--;
                if(pos < 0)
                {
                    pos = SoftResource.getAdapterListSize() -1;
                }
            }



            rii = (ResourceItemInfo)SoftResource.getAdapterElem(pos);
            if(rii.type == SoftResource.type_camera && rii.status == 2)
            {
                find_camrea = true;
                SoftResource.camera_pos = pos;
                break;
            }

            if(pos == SoftResource.camera_pos)
            {
                find_camrea = false;

                break;
            }
        }

        if(find_camrea)
        {
            return rii;
        }
        else
        {
            return null;
        }
    }

    private void jumpToNextCamrera(ResourceItemInfo rii)
    {
        over = true;

        Intent intent = new Intent("com.frameclient.videoclose");
        intent.putExtra("respons", 1);
        mActivity.sendBroadcast(intent);

        while(true)
        {

            if(paintOver)
            {
                Log.e(TAG,"cleanr bmp_queue 1");
                SoftResource.bmp_queue.clear();
                break;
            }

            Log.i(TAG,"Wait the Canvas update the bitmap when destory video acitvity 01");
        }

        SoftResource.cameraid = rii.id;
        SoftResource.stream = rii.stream_server;

    }

    private void over(String str)
    {

        if (proDialog != null)
        {
            proDialog.dismiss();
        }

        if(str != "")
        {
            Toast.makeText(mActivity, str,Toast.LENGTH_SHORT).show();
        }

//        VideoActivity.this.finish();
    }

    private void go_back(String str)
    {
        over(str);
    }

    private BroadcastReceiver broadcastRec = new BroadcastReceiver(){
        @SuppressLint("NewApi")
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            if(intent.getAction().equals("com.frameclient.videostart.rsp"))
            {
                int err = intent.getIntExtra("error", 1);
                if(err == SoftResource.err_net_failure)
                {
                    Message message = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putInt("message",SoftResource.err_net_failure);
                    message.setData(bundle);
                    handler.sendMessage(message);
                }
            }
            else if(intent.getAction().equals("com.frameclient.videoend.rsp"))
            {

            }
            else if(intent.getAction().equals("com.frameclient.nodata"))
            {
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putInt("message",-2);
                message.setData(bundle);
                handler.sendMessage(message);
            }
            else if(intent.getAction().equals("com.frameclient.video.firstframe"))
            {
                if (proDialog != null)
                {
                    proDialog.dismiss();
                    proDialog = null;
                }

                Log.i(TAG,"---> get first frame and start process thread");
                //bitmapProcess = new Thread(new BitmapProcess());// 启动收听线程线程
                //bitmapProcess.start();
            }
            else if(intent.getAction().equals("com.frameclient.videodata"))
            {

                //Log.i(TAG,"get videodata");
                if(hide == false)
                {
                    Message message = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putInt("message",100);
                    message.setData(bundle);
                    handler.sendMessage(message);
                }
                else
                {
                    if(SoftResource.bmp_queue.isEmpty() == false)
                    {
                        Log.e(TAG,"hide clear 1 frame bmp_queue 2");
                        //bitmap = SoftResource.bmp_queue.poll();
                    }
                }

                start_decode = true;

            }
            else if(intent.getAction().equals("com.frameclient.socket.err"))
            {
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putInt("message",-4);
                message.setData(bundle);
                handler.sendMessage(message);
            }
            else if(intent.getAction().equals("com.frameclient.video.err"))
            {
                int error = intent.getIntExtra("error", -1001);
                Log.e(TAG,"video error = "+error);
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putInt("message",error);
                message.setData(bundle);
                handler.sendMessage(message);
            }
            else if(intent.getAction().equals("com.frameclient.videoserver.dead"))
            {

                Log.v(TAG,"Recv the last video server dead msg");

                if (proDialog != null)
                {
                    proDialog.dismiss();
                }
                proDialog = ProgressDialog.show(context, "连接中..","请稍后....", true, true);

                String uid = SoftResource.uid;
                String cameraid = SoftResource.cameraid;
                String stream = SoftResource.stream;


                Log.i(TAG, "starting videoservice..... cameraid = "+cameraid);

                Intent it = new Intent(mActivity, VideoService.class);

                Bundle bundle = new Bundle();
                bundle.putInt("opera",0);
                bundle.putString("uid", uid);
                bundle.putString("stream", stream);
                bundle.putString("id", cameraid);
                it.putExtras(bundle);

                mActivity.startService(it);

                over = false;
                hide = false;
            }
            else if(intent.getAction().equals("com.frameclient.video.pixel.change"))
            {
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putInt("message",1105);
                message.setData(bundle);
                handler.sendMessage(message);
            }
            else if(intent.getAction().equals("com.frameclient.video.pixel.change"))
            {

            }
            else if (intent.getAction().equals("com.frameclient.otherlogin.rsp"))
            {
                int err = intent.getIntExtra("error", -100);

                Message message = new Message();
                Bundle bundle = new Bundle();
                if(err == 0)
                {


                    bundle.putInt("message", 500);
                }
                else
                {
                    bundle.putInt("message", 501);
                }

                message.setData(bundle);
                handler.sendMessage(message);
            }
            else if(intent.getAction().equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS))
            {
                String reason = intent.getStringExtra("reason");
                if (reason != null) {
                    if (reason.equals("homekey"))
                    {
                        // home key处理点
                        hide = true;
                        Log.i(TAG,"+++++++++++++ home key++++++++++++++");

                    }
                    else if (reason.equals("recentapps"))
                    {
                        // long home key处理点
                    }
                }
            }
        }
    };

    private MainActivity mActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (MainActivity) context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video, null);
        findViewById(view);
        return view;
    }



    @Override
    public void onActivityCreated( Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        zoomLister = new ZoomListenter(mActivity,handler);
        setListener();

        mActivity = (MainActivity) getActivity();
        instance = mActivity;

        if(SoftResource.bmp_queue.isEmpty() == false)
        {
            Log.e(TAG,"on create cleanr bmp_queue 3");
            SoftResource.bmp_queue.clear();
        }

        holder = view_sfv.getHolder();

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

        mActivity.registerReceiver(broadcastRec,intentfilter);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG,"--->onStart list.size() = "+SoftResource.getListSize());


        String uid = SoftResource.uid;
        String cameraid = SoftResource.cameraid;
        String stream = SoftResource.stream;

        Log.i(TAG, "starting videoservice..... cameraid = "+cameraid + " uid = "+uid+" stream = "+stream);

//        Toast.makeText(mActivity,str,
//                Toast.LENGTH_SHORT).show();

        Intent it = new Intent(mActivity, VideoService.class);

        Bundle bundle = new Bundle();
        bundle.putInt("opera",0);
        bundle.putString("uid", uid);
        bundle.putString("stream", stream);
        bundle.putString("id", cameraid);
        it.putExtras(bundle);

        mActivity.startService(it);

        if(proDialog != null)
        {
            proDialog.dismiss();
        }
        proDialog = ProgressDialog.show(mActivity, "连接中..","请稍后....", true, true);
        hide = false;
        over = false;
     
    }

    public void onNewRes(){

        mActivity.stopService(new Intent(mActivity, VideoService.class));

        String uid = SoftResource.uid;
        String cameraid = SoftResource.cameraid;
        String stream = SoftResource.stream;

        Log.i(TAG, "starting videoservice..... cameraid = "+cameraid + " uid = "+uid+" stream = "+stream);

//        Toast.makeText(mActivity,str,
//                Toast.LENGTH_SHORT).show();

        Intent it = new Intent(mActivity, VideoService.class);

        Bundle bundle = new Bundle();
        bundle.putInt("opera",0);
        bundle.putString("uid", uid);
        bundle.putString("stream", stream);
        bundle.putString("id", cameraid);
        it.putExtras(bundle);

        mActivity.startService(it);

        if(proDialog != null)
        {
            proDialog.dismiss();
        }

        proDialog = ProgressDialog.show(mActivity, "连接中..","请稍后....", true, true);


        hide = false;
        over = false;
    }

    @Override
    public void onResume() {
        super.onResume();
//        onNewRes();
    }

    @Override
    public void onPause() {
        Log.i(TAG,"--->onPause list.size() = "+SoftResource.getListSize());
        hide = true;
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.i(TAG,"--->onStop list.size() = "+SoftResource.getListSize());

        super.onStop();
    }

    @Override
    public void onDestroyView() {
        Log.i(TAG,"--->onDestroy list.size() = "+SoftResource.getListSize());
        Intent intent = new Intent("com.frameclient.videoclose");
        mActivity.sendBroadcast(intent);
        mActivity.unregisterReceiver(broadcastRec);

        try {

            view_sfv.getHolder().getSurface().release();
//            mActivity.unregisterReceiver(broadcastRec);
            if(popWindow != null)
            {
                popWindow.dismiss();
            }

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        super.onDestroyView();
    }


}
