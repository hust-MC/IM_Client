package com.example.audio_clientrev;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class ClientThread implements Runnable
{
	final int PORT = 6666;
	final int TIMEOUT = 3000;
	static final int CONNECT_FAILED = 1;
	static final int CONNECT_SUCCESS = 2;

	static Socket socket;
	static short[] audioData;
	Object obj;
	static OutputStream out = null;
	Handler handler, revHandler;
	String content;

	DataTransmission dataTransmission = new DataTransmission();

	public ClientThread(Handler handler)
	{
		this.handler = handler;
	}

	public void sendMsg(Object obj)
	{
		Message msg = new Message();
		msg.obj = obj;
		handler.sendMessage(msg);
	}

	public void sendMsg(int type)
	{
		Message msg = new Message();
		msg.arg1 = type;
		handler.sendMessage(msg);
	}

	@Override
	public void run()
	{
		Log.d("MC", "RUN");
		try
		{
			socket = new Socket();
			socket.connect(new InetSocketAddress("115.29.243.38", PORT), 3000);
			sendMsg(CONNECT_SUCCESS);
		}
		catch (Exception e)
		{
			sendMsg(CONNECT_FAILED);
		}
		try
		{
			while ((obj = dataTransmission.rev()) != null)
			{
				if (obj instanceof String || obj instanceof byte[])
				{
					sendMsg(obj);
				}
				else if (obj instanceof short[])
				{
					audioData = (short[]) obj;
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
