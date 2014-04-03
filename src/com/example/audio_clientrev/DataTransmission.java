package com.example.audio_clientrev;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.StreamCorruptedException;
import java.net.Socket;

public class DataTransmission
{
	Socket socket;
	public DataTransmission(Socket socket)
	{
		this.socket = socket;
	}
	
	public void send(Object data) throws IOException
	{
		OutputStream os = socket.getOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(os);
		out.writeObject(data);
		out.flush();
		out.close();
	}
	
	public Object rev() throws StreamCorruptedException, IOException, ClassNotFoundException
	{
		ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
		return in.readObject();
	}
}
