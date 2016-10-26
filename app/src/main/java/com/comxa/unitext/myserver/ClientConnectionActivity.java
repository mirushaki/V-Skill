package com.comxa.unitext.myserver;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientConnectionActivity extends AppCompatActivity {

    private Socket cSocket;
    InetAddress serverIP;
    private static final int port = 3030;

    public static BufferedReader msgReader;
    public static PrintWriter msgWriter;

    EditText ipText;
    Button setipButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_connection);

        ipText = (EditText)findViewById(R.id.ipText);
        setipButton = (Button)findViewById(R.id.setipBTN);

        setipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ipS = ipText.getText().toString();
                try {
                    serverIP = InetAddress.getByName(ipS);
                    new Thread(new CommunicationThread()).start();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    class CommunicationThread implements Runnable
    {
        public void run()
        {
            //while(!Thread.currentThread().isInterrupted())
            //{
                try {
                    cSocket = new Socket(serverIP, port);
                    Log.i("CLIENT", "connected to server!");

                    Bundle info = new Bundle();
                    msgReader = new BufferedReader(new InputStreamReader(cSocket.getInputStream()));
                    msgWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(cSocket.getOutputStream())), true);
                    info.putString("role", "client");
                    //info.putParcelable("writer", (Parcelable) msgWriter);
                    //info.putParcelable("reader", (Parcelable) msgReader);

                    Intent game = new Intent(ClientConnectionActivity.this, GameActivity.class);
                    game.putExtras(info);

                    startActivity(game);
                    //Thread.currentThread().interrupt();
                    //msgWriter.println("this was send from client!");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            //}
        }
    }
}
