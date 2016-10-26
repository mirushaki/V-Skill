package com.comxa.unitext.myserver;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class GameActivity extends AppCompatActivity {

    TextView sMSG;
    TextView cMSG;
    Button fireBTN;
    Button waterBTN;
    String role;
    BufferedReader reader;
    PrintWriter writer;
    Handler msgHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        sMSG = (TextView) findViewById(R.id.serverMSG);
        cMSG = (TextView) findViewById(R.id.clientMSG);
        fireBTN = (Button) findViewById(R.id.fireBTN);
        waterBTN = (Button) findViewById(R.id.waterBTN);

        msgHandler = new Handler();

        Bundle info = this.getIntent().getExtras();
        role = info.getString("role");
        if (role.equals("server")) {
            Log.i("SERVER", "Setting reader/writer");
            //reader = ConnectionActivity.msgReader;
            //writer = ConnectionActivity.msgWriter;
        } else if (role.equals("client")) {
            Log.i("CLIENT", "Setting reader/writer");
            //reader = ClientConnectionActivity.msgReader;
            //writer = ClientConnectionActivity.msgWriter;
        } else Log.i("UNKNOWN-role", role);
        //reader = (BufferedReader)info.getParcelable("reader");
        //writer = (PrintWriter)info.getParcelable("writer");
        new Thread(new CommunicationThread()).start();

        fireBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = fireBTN.getText().toString();
                sendMSG(s);
            }
        });

        waterBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = waterBTN.getText().toString();
                sendMSG(s);
            }
        });
    }

    private void sendMSG(String s)
    {
        cMSG.setText(s);
        if(role.equals("server"))
            ConnectionActivity.msgWriter.println(s);
        else if(role.equals("client"))
            ClientConnectionActivity.msgWriter.println(s);
    }

    class CommunicationThread implements Runnable
    {
        public void run()
        {
            while(!Thread.currentThread().isInterrupted())
            {
                //if(role == "server")
                //{

                    try {
                        String message = null;
                        if(role.equals("server"))
                            message = ConnectionActivity.msgReader.readLine();
                        else if(role.equals("client"))
                            message = ClientConnectionActivity.msgReader.readLine();

                        msgHandler.post(new receiveMsgThread(message));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                //}
            }
        }
    }
    class receiveMsgThread implements Runnable
    {
        String message;
        receiveMsgThread(String message)
        {
            this.message = message;
        }

        public void run()
        {
            sMSG.setText(message);
        }
    }
}
