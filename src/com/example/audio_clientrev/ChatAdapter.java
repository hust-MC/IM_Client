package com.example.audio_clientrev;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ChatAdapter extends BaseAdapter
{
	private Context context;
	private Boolean Tx;
	private List<Message> chatList = new ArrayList<Message>();

	public ChatAdapter(Context context)
	{
		this.context = context;
	}

	@Override
	public int getCount()
	{
		return chatList.size();
	}

	@Override
	public Object getItem(int position)
	{
		return chatList.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		LayoutInflater layoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout linearLayout = null;

		if ((chatList.get(position)).Tx)                                                  //表示发送信息
		{
			
			if (chatList.get(position).Content instanceof Bitmap)                         //待发送的是图片数据
			{
				linearLayout = (LinearLayout) layoutInflater.inflate(
						R.layout.picture_r, null);
				ImageView chatView = (ImageView) linearLayout.findViewById(R.id.picture_r);
				chatView.setImageBitmap((Bitmap)chatList.get(position).Content);
			}
			else if (chatList.get(position).Content instanceof String)                    //待发送的是字符串数据
			{
				linearLayout = (LinearLayout) layoutInflater.inflate(
						R.layout.message_r, null);
				TextView chatView = (TextView) linearLayout
						.findViewById(R.id.chatView_r);
				chatView.setText(String.valueOf(chatList.get(position).Content));
			}
		}
		else                                                                                //表示接受信息
		{
			linearLayout = (LinearLayout) layoutInflater.inflate(
					R.layout.message_l, null);
			TextView chatView = (TextView) linearLayout
					.findViewById(R.id.chatView_l);
			chatView.setText(String.valueOf(chatList.get(position).Content));
		}
		return linearLayout;
	}

	public void addList(Object content, Boolean Tx)
	{
		Message message = new Message(content, Tx);
		chatList.add(message);
		notifyDataSetChanged();
	}

	@Override
	public boolean isEnabled(int position)
	{
		return false;
	}

	private class Message
	{
		private Object Content;
		private Boolean Tx;

		public Message(Object Content, Boolean Tx)
		{
			this.Tx = Tx;
			this.Content = Content;
		}
	}
}
