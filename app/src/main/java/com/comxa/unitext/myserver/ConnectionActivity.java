package com.comxa.unitext.myserver;

import android.content.Intent;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.MainThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.Enumeration;

public class ConnectionActivity extends AppCompatActivity {

    private ServerSocket sSocket;
    public static int port = 3030;
    ServerThread serverThread;
    //Handler updateConversationHandler;

    public static BufferedReader msgReader;
    public static PrintWriter msgWriter;

    public TextView ipView;
    //public EditText ipText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);

        //ipText = (EditText)findViewById(R.id.ipText);
        ipView = (TextView)findViewById(R.id.ipView);

        //updateConversationHandler = new Handler();

        ServerThread serverThread = new ServerThread();
        new Thread(serverThread).start();
        //this.serverThread = new Thread(new ServerThread());
        //this.serverThread.start();
    }
    @Override
    protected void onStop()
    {
        super.onStop();
        try{
            sSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class ServerThread implements Runnable {
        public void run()
        {
            Socket socket = null;
            try
            {
                String sIP = getServerIp();
                if(sIP != null)
                {
                    ipView.setText(sIP);
                }
                sSocket = new ServerSocket(port);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //while(!Thread.currentThread().isInterrupted())
            //{
                try {
                    socket = sSocket.accept();
                    Log.i("SERVER", "client accepted");

                    //CommunicationThread commThread = new CommunicationThread(socket);
                    //new Thread(commThread).start();
                    //Thread commThread = new Thread(new CommunicationThread(socket));
                    //commThread.start();
                    Bundle info = new Bundle();
                    msgReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    msgWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                    info.putString("role", "server");
                    //info.putParcelable("writer", (Parcelable) msgWriter);
                    //info.putParcelable("reader", (Parcelable) msgReader);

                    Intent game = new Intent(ConnectionActivity.this, GameActivity.class);
                    game.putExtras(info);

                    startActivity(game);
                    //Thread.currentThread().interrupt();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            //}
        }

        public String getServerIp()
        {
            //StringBuilder IFCONFIG=new StringBuilder();
            String s = null;
            try {
                for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                    NetworkInterface intf = en.nextElement();
                    for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress() && inetAddress.isSiteLocalAddress()) {
                            s = inetAddress.getHostAddress().toString();
                            return s;
                            //IFCONFIG.append(inetAddress.getHostAddress().toString()+"\n");
                        }
                    }
                }
            } catch (SocketException ex) {
                Log.e("LOG_TAG", ex.toString());
            }

            return s;
            //String s = IFCONFIG.toString();
            //Log.i("MMM", s);
        }
    }

    class CommunicationThread implements Runnable {
        private Socket cSocket;
        private BufferedReader msgReader;

        public CommunicationThread(Socket cSocket)
        {
            this.cSocket = cSocket;
            try {
                this.msgReader = new BufferedReader(new InputStreamReader(this.cSocket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run()
        {
            while(!Thread.currentThread().isInterrupted())
            {
                try {
                    String message = this.msgReader.readLine();

                    //updateConversationHandler.post(new updateUIThread(message));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        class updateUIThread implements Runnable
        {
            String message;
            updateUIThread(String message)
            {
                this.message = message;
            }

            public void run()
            {
                ipView.setText(message);
            }
        }
    }

}
