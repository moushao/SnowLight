package com.frameclient.activitys;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.frameclient.utils.Constants;
import com.frameclient.utils.SharedPreferencesHelper;
import com.frameclient.utils.SoftResource;

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
                        .toString()))
                    return;
                StringBuilder builder = new StringBuilder();
                builder.append("http://");
                builder.append(ip_base_url.getText().toString());
                Constants.BASE_URL = builder.toString();
                Constants.IP_ADDRESS = ip_address.getText().toString();
                Constants.isReLogin = true;
                //保存到缓存中
                SharedPreferencesHelper.putString(IPActivity.this, "IP_ADDRESS", Constants
                        .IP_ADDRESS);
                SharedPreferencesHelper.putString(IPActivity.this, "BASE_URL", Constants.BASE_URL);
                SoftResource.r_list.clear();
                SoftResource.s_list.clear();
                Intent itt = new Intent(IPActivity.this, SplashActivity.class);
                startActivity(itt);
                finish();

            }
        });

    }

    @Override
    public void onBackPressed() {
        Constants.isReLogin = true;
        String s = getIntent().getExtras().getString(Constants.FROM);
        if (s.equals("MainActivity")) {
            Intent itt = new Intent(IPActivity.this, MainActivity.class);
            startActivity(itt);
        } else {
            Intent itt = new Intent(IPActivity.this, SplashActivity.class);
            startActivity(itt);
        }
        finish();
        super.onBackPressed();
    }
}
