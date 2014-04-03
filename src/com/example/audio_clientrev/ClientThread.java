package com.example.audio_clientrev;

import java.io.BufferedReader;
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
	BufferedReader in = null;
	Handler handler, revHandler;
	String content;

	public ClientThread(Handler handler)
	{
		this.handler = handler;
	}

	@Override
	public void run()
	{
		try
		{
			socket = new Socket("192.168.1.1", PORT);
			in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			out = socket.getOutputStream();
		} catch (Exception e)
		{
			Log.d("MC", "¡¨≤ª…œ");
		}
		try
		{
			while ((content = in.readLine()) != null)
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
