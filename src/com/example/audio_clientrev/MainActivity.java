package com.example.audio_clientrev;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
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
	static byte[] pic;
	static ChatAdapter chatAdapter;
	DataTransmission dataTransmission = new DataTransmission();

	public void wiget_init()
	{
		lv = (ListView) findViewById(R.id.lv);
		inputMessage = (EditText) findViewById(R.id.inputMessage);
		send_bt = (Button) findViewById(R.id.send_bt);
		sound_bt = (ToggleButton) findViewById(R.id.sound_bt);

		lv.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{
				if (ChatAdapter.chatList.get(position).Content instanceof Bitmap)
				{
					Intent intent = new Intent(MainActivity.this, Magnify.class);
					intent.putExtra("position", position);
					Log.d("MC", "start");
					startActivity(intent);
				}
			}
		});
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
		if (sound_bt.isChecked()) // sending audio message
		{
			Toast.makeText(this, "打开语音对讲", Toast.LENGTH_SHORT).show();
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
				// stop sending audio message
		{
			Toast.makeText(this, "关闭语音对讲", Toast.LENGTH_SHORT).show();
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
					File dir = new File(
							Environment.getExternalStorageDirectory() + "/mc");
					if (!dir.exists())
					{
						dir.mkdirs();
					}
					File file = new File(dir, String.valueOf(chatAdapter.getCount()) + ".png");
					byte[] picByte = (byte[]) msg.obj;
					Bitmap bitmap = BitmapFactory.decodeByteArray(picByte, 0,
							picByte.length);

					chatAdapter.addList(bitmap, false);
					FileOutputStream out = null;
					try
					{
						out = new FileOutputStream(file);
						bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
						out.flush();
						out.close();
					} catch (FileNotFoundException e)
					{
						e.printStackTrace();
					} catch (IOException e)
					{
						e.printStackTrace();
					}
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
		int size = 0;
		if (requestCode == 1)
		{
			if (resultCode == Activity.RESULT_OK)
			{
				Log.d("MC", "pic");
				File file = new File(Environment.getExternalStorageDirectory()
						+ "/mc/" + String.valueOf(chatAdapter.getCount())
						+ ".png");
				FileInputStream fis = null;
				try
				{
					fis = new FileInputStream(file);
					size = fis.available();
					System.out.println("size = " + size);
					pic = new byte[size];
					fis.read(pic);
				} catch (FileNotFoundException e1)
				{
					e1.printStackTrace();
				} catch (IOException e)
				{
					e.printStackTrace();
				}

				final Bitmap cameraBitmap = BitmapFactory.decodeFile(file
						.getAbsolutePath());
				Log.d("MC", "file");
				chatAdapter.addList(cameraBitmap, true);

				new Thread(new Runnable()
				{
					@Override
					public void run()
					{
						try
						{
							dataTransmission.send(MainActivity.pic);
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
			String status = Environment.getExternalStorageState();
			if (status.equals(Environment.MEDIA_MOUNTED))
			{
				try
				{
					File dir = new File(
							Environment.getExternalStorageDirectory() + "/mc");
					if (!dir.exists())
						dir.mkdirs();

					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					File f = new File(dir, String.valueOf(chatAdapter
							.getCount()) + ".png");// localTempImgDir和localTempImageFileName是自己定义的名字
					Uri uri = Uri.fromFile(f);
					intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
					intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
					startActivityForResult(intent, 1);
				} catch (ActivityNotFoundException e)
				{
					Toast.makeText(this, "没有找到储存目录", Toast.LENGTH_LONG).show();
				}
			}
			else
			{
				Toast.makeText(this, "没有储存卡", Toast.LENGTH_LONG).show();
			}
			break;

		case 2:
			new AlertDialog.Builder(this).setTitle("关于")
					.setMessage("版本: 即时通信(V1.4)").setNegativeButton("确定", null)
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
