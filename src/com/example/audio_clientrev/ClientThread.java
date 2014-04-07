package com.example.audio_clientrev;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class ClientThread implements Runnable
{
	final int PORT = 6666;
	final int TIMEOUT = 3000;

	Socket socket;
	static OutputStream out = null;
	Handler handler, revHandler;
	String content;

	public ClientThread(Handler handler)
	{
		this.handler = handler;
	}

	@Override
	public void run()
	{
		Log.d("MC", "RUN");
		try
		{
			socket = new Socket();
			socket.connect(new InetSocketAddress("115.29.243.38", PORT),3000);
			Log.d("MC", "连上了");
		} catch (Exception e)
		{
			Log.d("MC", "连不上");
		}
		try
		{
			while ((content = (String) new DataTransmission(socket).rev()) != null)
			{
				Message msg = new Message();
				msg.obj = content;
				handler.sendMessage(msg);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
