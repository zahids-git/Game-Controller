package com.example.mdzahidul.gamecontroller;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class SocketRunnable extends Thread {

    String dstAddress;
    int dstPort;
    Context context;
    Socket socket = null;
    DataOutputStream out;
    String p;

    SharedPreferences info_ip_port;

    public SocketRunnable(Context context, String p, String dstAddress, int dstPort){
        info_ip_port = context.getSharedPreferences("info_ip_port", 0);
        this.dstAddress = info_ip_port.getString("ip",null);
        this.dstPort = info_ip_port.getInt("port", 0);
        this.context = context;
        this.p = p;
    }

    @Override
    public void run() {
        socket = null;
        try {
            socket = new Socket(dstAddress, dstPort);
            OutputStream outToServer = socket.getOutputStream();
            out = new DataOutputStream(outToServer);
            out.writeUTF(p);
            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
