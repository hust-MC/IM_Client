package com.example.audio_clientrev;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

public class MainActivity extends Activity
{
	static int i = 0;
	public static String[] data = new String[]
	{ "����ɫ������", "�����ڵ���", "��ɫ��������", "�ο��˽��", "�紫�����໨��", "�Թ�������", "���۴�", "Ц��" };

	ClientThread clientThread;
	EditText inputMessage;
	Button send_bt;
	ListView lv;
	Handler handler;
	String mContent;
	ChatAdapter chatAdapter;

	public void wiget_init()
	{
		lv = (ListView) findViewById(R.id.lv);
		inputMessage = (EditText) findViewById(R.id.inputMessage);
		send_bt = (Button) findViewById(R.id.send_bt);
	}

	public void onClick_send(View view)
	{
		mContent = inputMessage.getText().toString();
		chatAdapter.addText(inputMessage.getText().toString(), true);
		inputMessage.setText("");

		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					ClientThread.out.write(inputMessage.getText().toString()
							.getBytes("utf-8"));
				} catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		wiget_init();
		chatAdapter = new ChatAdapter(this);
		lv.setAdapter(chatAdapter);

		handler = new Handler()
		{
			@Override
			public void handleMessage(Message msg)
			{
				chatAdapter.addText(msg.obj.toString(), false);
			}
		};
		clientThread = new ClientThread(handler);
		new Thread(clientThread).start();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if (item.getItemId() == 1)
		{
			new AlertDialog.Builder(this).setTitle("����")
					.setMessage("�汾: Զ�̹ػ�(V1.4)").setNegativeButton("ȷ��", null)
					.show();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		menu.add(0, 1, 1, "����");
		return super.onCreateOptionsMenu(menu);
	}
}
