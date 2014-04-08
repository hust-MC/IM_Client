package com.example.audio_clientrev;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.StreamCorruptedException;
import java.net.Socket;

public class DataTransmission
{
	public DataTransmission()
	{
	}
	
	public void send(Object data) throws IOException
	{
		OutputStream os = ClientThread.socket.getOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(os);
		out.writeObject(data);
		out.flush();
	}
	
	public Object rev() throws StreamCorruptedException, IOException, ClassNotFoundException
	{
		ObjectInputStream in = new ObjectInputStream(ClientThread.socket.getInputStream());
		return in.readObject();
	}
}
