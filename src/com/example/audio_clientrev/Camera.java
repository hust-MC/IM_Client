package com.example.audio_clientrev;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class Camera extends Activity
{
	static byte[] pic;
	DataTransmission dataTransmission = new DataTransmission();

	public void savePhoto()
	{
		int size = 0;

		Log.d("MC", "pic");
		File file = new File(Environment.getExternalStorageDirectory() + "/mc/"
				+ String.valueOf(MainActivity.chatAdapter.getCount()) + ".png");
		FileInputStream fis = null;
		try
		{
			fis = new FileInputStream(file);
			size = fis.available();
			System.out.println("size = " + size);
			pic = new byte[size];
			fis.read(pic);
		}
		catch (FileNotFoundException e1)
		{
			e1.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		final Bitmap cameraBitmap = BitmapFactory.decodeFile(file
				.getAbsolutePath());
		Log.d("MC", "file");
		MainActivity.chatAdapter.addList(cameraBitmap, true);

		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					dataTransmission.send(pic);
				}
				catch (IOException e)
				{
					Log.d("MC", "IOexception");
					e.printStackTrace();
				}
			}
		}).start();
	}

	public Uri openCamera()
	{
		File f = null;
		try
		{
			File dir = new File(Environment.getExternalStorageDirectory()
					+ "/mc");
			if (!dir.exists())
				dir.mkdirs();

			f = new File(dir, String.valueOf(MainActivity.chatAdapter
					.getCount()) + ".png");										// localTempImgDir & localTempImageFileName is custom defined name
		}
		catch (ActivityNotFoundException e)
		{
			Toast.makeText(this, "没有找到储存目录", Toast.LENGTH_LONG).show();
		}
		return Uri.fromFile(f);
	}

	public void handlePhoto(byte[] picByte)
	{
		File dir = new File(Environment.getExternalStorageDirectory() + "/mc");
		if (!dir.exists())
		{
			dir.mkdirs();
		}
		File file = new File(dir, String.valueOf(MainActivity.chatAdapter.getCount())
				+ ".png");
		Bitmap bitmap = BitmapFactory.decodeByteArray(picByte, 0,
				picByte.length);

		MainActivity.chatAdapter.addList(bitmap, false);
		FileOutputStream out = null;
		try
		{
			out = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
			out.flush();
			out.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
