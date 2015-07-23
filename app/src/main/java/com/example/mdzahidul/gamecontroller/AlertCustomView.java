package com.example.mdzahidul.gamecontroller;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Md.Zahidul on 10-Jun-15.
 */
public class AlertCustomView extends Activity {

    Button save_ip_port;
    EditText ip_add, port_num;

    SharedPreferences info_ip_port;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alert_view);

        save_ip_port = (Button) findViewById(R.id.save_ip_port);
        ip_add = (EditText) findViewById(R.id.ip_address);
        port_num = (EditText) findViewById(R.id.port_number);

        info_ip_port = getSharedPreferences("info_ip_port", 0);
        if(!info_ip_port.getString("ip",null).equals(null)) ip_add.setText(info_ip_port.getString("ip",null));
        if(info_ip_port.getInt("port",0) != 0) port_num.setText(info_ip_port.getInt("port",0)+"");

        save_ip_port.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            setValueOfIpPost();
            Toast.makeText(getApplicationContext(),"Changed",Toast.LENGTH_SHORT).show();
            finish();
            }
        });
    }

    private void setValueOfIpPost(){

        info_ip_port = getSharedPreferences("info_ip_port", 0);
        SharedPreferences.Editor editor = this.info_ip_port.edit();
        editor.putString("ip", ip_add.getText().toString());
        editor.putInt("port", Integer.parseInt(port_num.getText().toString()));
        editor.commit();

    }


    @Override
    protected void onStop() {
        super.onStop();
    }
}
