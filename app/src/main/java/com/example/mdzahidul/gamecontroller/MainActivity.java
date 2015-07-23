package com.example.mdzahidul.gamecontroller;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends Activity implements View.OnTouchListener,View.OnClickListener{

    boolean check;
    Button start,stop;
    int front_accelerate, back_accelerate , left_accelerate , right_accelerate , nitro , stop_car, select_btn, exit_btn;
    String send_json;
    int time = 20;

    SharedPreferences info_ip_port;

    Button switch_active , select , exit , settings, info_btn;
    boolean is_switch = false;

    /* Soln for side change y */
    float sideChangeValuePre_y = 0;
    float sideChangeValueNow_y = 0;
    boolean isFirst_y = true;

    /* Soln for side change x */
    float sideChangeValuePre_x = 0;
    float sideChangeValueNow_x = 0;
    boolean  isfirst_x = true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        start = (Button) findViewById(R.id.accelerate);
        stop = (Button) findViewById(R.id.stop);

        select = (Button) findViewById(R.id.select);
        exit = (Button) findViewById(R.id.exit);

        settings = (Button) findViewById(R.id.settings);
        info_btn = (Button) findViewById(R.id.info_btn);

        switch_active = (Button) findViewById(R.id.switch_active);

        start.setOnTouchListener(this);
        stop.setOnTouchListener(this);

        select.setOnClickListener(this);
        exit.setOnClickListener(this);

        switch_active.setOnClickListener(this);
        select.setOnClickListener(this);
        exit.setOnClickListener(this);

        settings.setOnClickListener(this);
        info_btn.setOnClickListener(this);

        SensorManager sensorManager = (SensorManager)getSystemService(this.SENSOR_SERVICE);
        Sensor sensor=sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL);

        info_ip_port = getSharedPreferences("info_ip_port", 0);
        sendSocketDataTest();
    }

    SensorEventListener listener=new SensorEventListener() {
        public void onSensorChanged(SensorEvent event) {
            //String sensorData="X: "+event.values[0]+" Y: "+event.values[1]+" Z: "+event.values[2];

            float sensor_value_y = Float.parseFloat(String.format("%.2f", event.values[1]));
            float sensor_value_x = Float.parseFloat(String.format("%.2f", event.values[0]));
            sensor_value_y *= 10;
            sensor_value_x *=10;

            /* For Y-axis */
            if(isFirst_y){
                isFirst_y = false;
                sideChangeValueNow_y = sensor_value_y;
            }
            else {
                float temp_y = sideChangeValueNow_y;
                sideChangeValueNow_y = sensor_value_y;
                sideChangeValuePre_y = temp_y;
            }
            if(sensor_value_y >= 10 && sideChangeValueNow_y > sideChangeValuePre_y){
                right_accelerate = 1;
                left_accelerate = 0;
                if(sensor_value_y >= 10 && sensor_value_y <= 12) setTheTime(100);
                else if(sensor_value_y > 12 && sensor_value_y <= 19) setTheTime(80);
                else if(sensor_value_y > 19 && sensor_value_y <= 26) setTheTime(60);
                else if(sensor_value_y > 26 && sensor_value_y<= 33) setTheTime(40);
                else if(sensor_value_y> 33 && sensor_value_y<= 40) setTheTime(30);
                else setTheTime(20);
            }
            else if(sensor_value_y<= -25 && sideChangeValueNow_y < sideChangeValuePre_y){
                left_accelerate = 1;
                right_accelerate = 0;
                if(sensor_value_y<= -10 && sensor_value_y>= -12) setTheTime(100);
                else if(sensor_value_y< -12 && sensor_value_y>= -19) setTheTime(80);
                else if(sensor_value_y< -19 && sensor_value_y>= -26) setTheTime(60);
                else if(sensor_value_y< -26 && sensor_value_y>= -33) setTheTime(40);
                else if(sensor_value_y< -33 && sensor_value_y>= -40) setTheTime(30);
                else setTheTime(20);
            }
            else{
                left_accelerate = 0;
                right_accelerate = 0;
            }

             /*For X-Axis */
            if(isfirst_x){
                isfirst_x = false;
                sideChangeValueNow_x = sensor_value_x;
            }
            else {
                float temp_x = sensor_value_x;
                sideChangeValueNow_x = sensor_value_x;
                sideChangeValuePre_x = temp_x;
            }
            if(sensor_value_x <= 55 ){
                nitro = 1;
            }
            /*else if(sensor_value_x >= 90 ){
                stop_car = 1;
            }*/
            else {
                nitro = 0;
                stop_car = 0;
            }
        }
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    };

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        setTheTime();
        if(view == start){
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                start.setBackground(getResources().getDrawable(R.drawable.start_presed));
                front_accelerate = 1;
            }
            else if (event.getAction() == MotionEvent.ACTION_UP){
                start.setBackground(getResources().getDrawable(R.drawable.start_normal));
                front_accelerate = 0;
            }
        }
        if(view == stop){
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                stop.setBackground(getResources().getDrawable(R.drawable.stop_presed));
                back_accelerate = 1;
            }
            else if (event.getAction() == MotionEvent.ACTION_UP){
                stop.setBackground(getResources().getDrawable(R.drawable.stop_normal));
                back_accelerate = 0;
            }
        }

        return true;
    }



    private void setTheTime(int time) {
        this.time = time;
    }
    private void setTheTime() {
        this.time = 20;
    }

    private void sendSocketDataTest(){
        new Thread(new Runnable() {
            @Override
            public void run() {
            while(true){
                try {
                    Thread.sleep(time);
                    send_json  = "{ \"f\": "+front_accelerate+", \"b\": "+back_accelerate+", \"l\": "+left_accelerate+", \"r\": "+right_accelerate+", \"n\": "+nitro+", \"s\": "+stop_car+", \"sb\": "+select_btn+", \"eb\": "+exit_btn+" }";
                    if(is_switch) new SocketRunnable(getApplicationContext(),send_json,info_ip_port.getString("ip",null),info_ip_port.getInt("port", 0)).start();
                    if(select_btn==1 || exit_btn==1){
                        select_btn = 0;
                        exit_btn = 0;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            }
        }).start();
    }

    @Override
    public void onClick(View view) {
        if(view == switch_active){
            if(is_switch){
                is_switch = false;
                switch_active.setBackground(getResources().getDrawable(R.drawable.switch_off));
            }
            else if (!is_switch){
                info_ip_port = getSharedPreferences("info_ip_port", 0);
                if(info_ip_port.getString("ip",null).equals(null) && info_ip_port.getInt("port",0) == 0){
                    Intent ii = new Intent(this, AlertCustomView.class);
                    startActivity(ii);
                }
                else{
                    is_switch = true;
                    switch_active.setBackground(getResources().getDrawable(R.drawable.switch_on));
                }
            }
        }
        if (view == select){
            select_btn = 1;
        }
        if (view == exit){
            exit_btn = 1;
        }
        if(view == settings){
            if(is_switch){
                Toast.makeText(getApplicationContext(), "Please turn off the connection", Toast.LENGTH_SHORT).show();
            }
            else{
                Intent ii = new Intent(this, AlertCustomView.class);
                startActivity(ii);
            }

        }

    }



}
