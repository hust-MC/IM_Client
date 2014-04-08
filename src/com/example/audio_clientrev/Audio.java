package com.example.audio_clientrev;

import android.util.Log;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.media.AudioRecord;

public class Audio
{
	static boolean isRecording = false;
	static AudioRecord audioRecord;
	AudioTrack audioTrack;
	int bufferReadResult;
	DataTransmission dataTransmission = new DataTransmission();

	public void record()
	{
		int frequency = 11025;
		int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
		int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;

		try
		{
			int bufferSize = AudioRecord.getMinBufferSize(frequency,
					channelConfiguration, audioEncoding);
			audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
					frequency, channelConfiguration, audioEncoding, bufferSize);

			isRecording = true;

			int trackSize = AudioTrack.getMinBufferSize(frequency,
					channelConfiguration, AudioFormat.ENCODING_PCM_16BIT);
			audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 11025,
					AudioFormat.CHANNEL_CONFIGURATION_MONO,
					AudioFormat.ENCODING_PCM_16BIT, trackSize,
					AudioTrack.MODE_STREAM);

			short[] TxBuffer = new short[bufferSize];

			audioRecord.startRecording(); // 开始录音
			audioTrack.play(); // 开始播放
			while (isRecording)
			{
				bufferReadResult = audioRecord.read(TxBuffer, 0, bufferSize); // 从麦克风读取音频
				dataTransmission.send(TxBuffer);

				// System.arraycopy(buffer, 0, tmpBuf, 0, bufferReadResult); //
				// 复制文件
				// audioTrack.write(TxBuffer, 0, bufferReadResult);
				new Thread(new Runnable()
				{
					@Override
					public void run()
					{
						audioTrack.write(ClientThread.audioData, 0,
								bufferReadResult);
					}
				}).start();

			}
			audioTrack.stop();
			audioRecord.stop();

		} catch (Throwable t)
		{
			Log.e("AudioRecord", "Recording Failed");
		}
	}
}
