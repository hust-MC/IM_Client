package com.example.audio_clientrev;

import java.io.IOException;
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
	{ "天青色等烟雨", "而我在等你", "月色被打捞起", "晕开了结局", "如传世的青花瓷", "自顾自美丽", "你眼带", "笑意" };

	Socket socket = null;
	EditText inputMessage;
	Button send_bt;
	ListView lv;
	Handler handler;
	ChatAdapter chatAdapter;

	public void wiget_init()
	{
		lv = (ListView) findViewById(R.id.lv);
		inputMessage = (EditText) findViewById(R.id.inputMessage);
		send_bt = (Button) findViewById(R.id.send_bt);
	}

	public void onClick_send(View view)
	{
		chatAdapter.addText(inputMessage.getText().toString(), true);
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
				chatAdapter.addText(inputMessage.getText().toString(), false);
			}
		};
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if (item.getItemId() == 1)
		{
			new AlertDialog.Builder(this).setTitle("关于")
					.setMessage("版本: 远程关机(V1.4)").setNegativeButton("确定", null)
					.show();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		menu.add(0, 1, 1, "关于");
		return super.onCreateOptionsMenu(menu);
	}
}
