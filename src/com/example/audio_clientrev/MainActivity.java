package com.example.audio_clientrev;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends Activity
{
	static int i = 0;

	ClientThread clientThread;
	EditText inputMessage;
	Button send_bt;
	ListView lv;
	ToggleButton sound_bt;
	Handler handler;
	String mContent;
	ChatAdapter chatAdapter;
	DataTransmission dataTransmission = new DataTransmission();

	public void wiget_init()
	{
		lv = (ListView) findViewById(R.id.lv);
		inputMessage = (EditText) findViewById(R.id.inputMessage);
		send_bt = (Button) findViewById(R.id.send_bt);
		sound_bt = (ToggleButton) findViewById(R.id.sound_bt);
	}

	public void onClick_send(View view)
	{
		mContent = inputMessage.getText().toString();
		chatAdapter.addList(mContent, true);

		Log.d("MC", mContent);
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					dataTransmission.send(mContent);
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
		if (sound_bt.isChecked()) // 正在发送语音消息
		{
			inputMessage.setText("Checkd");
			new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					new Audio().record();
				}
			}).start();
		}
		else
		// 停止发送语音消息
		{
			inputMessage.setText("not Checkd");
			Audio.isRecording = false;
		}
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

	public void onClick_magnify(View view)
	{
		Log.d("MC", "before");
		// ((ImageView) view).setDrawingCacheEnabled(true);
		Drawable drawable = ((ImageView) view).getDrawable();
		Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
		// ((ImageView) view).setDrawingCacheEnabled(false);
		Log.d("MC", "after");
		Intent intent = new Intent(this, Magnify.class);
		intent.putExtra("bitmap", bitmap);
		Log.d("MC", "start");
		startActivity(intent);
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
			public void handleMessage(Message msg)                            // process UI
			{
				Log.d("MC", msg.obj.toString());
				if (msg.obj instanceof byte[])
				{
					byte[] picByte = (byte[]) msg.obj;
					Bitmap bitmap = BitmapFactory.decodeByteArray(picByte, 0,
							picByte.length);
					chatAdapter.addList(bitmap, false);
				}
				else if (msg.obj instanceof String)
				{
					chatAdapter.addList(msg.obj, false);
				}
				r.play();
			}
		};
		clientThread = new ClientThread(handler);
		new Thread(clientThread).start();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		final Bitmap cameraBitmap;
		if (requestCode == 1)
		{
			if (resultCode == Activity.RESULT_OK)
			{
				cameraBitmap = (Bitmap) data.getExtras().get("data");
				chatAdapter.addList(cameraBitmap, true);
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				cameraBitmap.compress(CompressFormat.JPEG, 80, bos);

				final byte[] pic = bos.toByteArray();
				new Thread(new Runnable()
				{
					@Override
					public void run()
					{
						try
						{
							dataTransmission.send(pic);
						} catch (IOException e)
						{
							Log.d("MC", "IOexception");
							e.printStackTrace();
						}
					}
				}).start();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case 1:
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

			startActivityForResult(intent, 1);
			break;

		case 2:
			new AlertDialog.Builder(this).setTitle("关于")
					.setMessage("版本: 通信工具(V1.4)").setNegativeButton("确定", null)
					.show();
			break;

		default:
			break;
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
