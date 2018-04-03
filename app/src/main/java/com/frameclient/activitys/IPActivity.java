package com.frameclient.activitys;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.frameclient.utils.Constants;
import com.frameclient.utils.SharedPreferencesHelper;

public class IPActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ip);
        final EditText ip_base_url = (EditText) findViewById(R.id.ip_base_url);
        final EditText ip_address = (EditText) findViewById(R.id.ip_address);
        ip_base_url.setText(Constants.BASE_URL.replace("http://", "").replace("/", "").toString());
        ip_address.setText(Constants.IP_ADDRESS);
        findViewById(R.id.save_ip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(ip_base_url.getText().toString()) || TextUtils.isEmpty(ip_address.getText()
                        .toString())) {
                    Toast.makeText(IPActivity.this, "视频地址和服务地址不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                StringBuilder builder = new StringBuilder();
                builder.append("http://");
                builder.append(ip_base_url.getText().toString());
                //保存到全局
                Constants.BASE_URL = builder.toString();
                Constants.IP_ADDRESS = ip_address.getText().toString();
                //保存到缓存中
                SharedPreferencesHelper.putString(IPActivity.this, "IP_ADDRESS", Constants
                        .IP_ADDRESS);
                SharedPreferencesHelper.putString(IPActivity.this, "BASE_URL", Constants.BASE_URL);
                finish();
            }
        });

    }
}
