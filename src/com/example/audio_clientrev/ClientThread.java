package com.example.audio_clientrev;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

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
			socket.connect(new InetSocketAddress("192.168.122.75", PORT), 3000);
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
