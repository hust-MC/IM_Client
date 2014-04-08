package com.example.audio_clientrev;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

public class MainActivity extends Activity
{
	static int i = 0;

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
		chatAdapter.addText(mContent, true);
		Log.d("MC", mContent);
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					new DataTransmission(clientThread.socket).send(mContent);
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}).start();
		inputMessage.setText("");
	}

	public void onClick_sound(View view)
	{
		inputMessage.setText("df");
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			Log.d("MC", "finish");
			System.exit(0);
			return true;
		}
		return super.onKeyDown(keyCode, event);
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
			Uri notification = RingtoneManager
					.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			Ringtone r = RingtoneManager.getRingtone(getApplicationContext(),
					notification);

			@Override
			public void handleMessage(Message msg)
			{
				Log.d("MC", msg.obj.toString());
				chatAdapter.addText(msg.obj.toString(), false);
				r.play();
			}
		};
		clientThread = new ClientThread(handler);
		new Thread(clientThread).start();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if (item.getItemId() == 2)
		{
			new AlertDialog.Builder(this).setTitle("关于")
					.setMessage("版本: 即时通信(V1.1)").setNegativeButton("确定", null)
					.show();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		menu.add(0, 1, 1, "拍照");
		menu.add(0, 2, 2, "关于");
		return super.onCreateOptionsMenu(menu);
	}
}
