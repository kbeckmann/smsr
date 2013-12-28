package se.xil.smsr;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

public class MainActivity extends Activity {

	private ProgressBar mProgressBar;
	private EditText mRecipient;
	private EditText mMessage;
	private OnClickListener mSendListener = new OnClickListener() {

		@Override
		public void onClick(View v) {

			new SendSMSTask().execute(0);
		}
	};

	private OnClickListener mDownloadListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			
			new DownloadTask().execute(0);
		}
	};

	class DownloadTask extends AsyncTask<Integer, Integer, Integer> {

		@Override
		protected Integer doInBackground(Integer... params) {
			try {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						mProgressBar.setVisibility(View.VISIBLE);
					}
				});

				final String mess = download("http://sms.xil.se/a.txt");

				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						mProgressBar.setVisibility(View.GONE);
						mMessage.setText(mess);
					}
				});
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
	}

	class SendSMSTask extends AsyncTask<Integer, Integer, Integer> {

		@Override
		protected Integer doInBackground(Integer... params) {
			String[] text = mMessage.getText().toString().split("\\r?\\n");
			Log.i("SMSR", "Sending..." + text.length);

			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					mProgressBar.setVisibility(View.VISIBLE);
				}
			});

			for (String mess : text) {

				Log.i("SMSR", mess);
				sendSMS(mRecipient.getText().toString(), mess);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
			}

			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					mProgressBar.setVisibility(View.GONE);
				}
			});
			return null;
		}
	}

	public static String download(String url) throws ParseException,
			IOException {

		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);

		HttpResponse httpResponse = httpClient.execute(httpGet);
		HttpEntity httpEntity = httpResponse.getEntity();
		return EntityUtils.toString(httpEntity);
	}

	public static void sendSMS(String phoneNumber, String message) {
		SmsManager sms = SmsManager.getDefault();
		sms.sendTextMessage(phoneNumber, null, message, null, null);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mRecipient = (EditText) findViewById(R.id.recipient);
		mMessage = (EditText) findViewById(R.id.message);
		((Button) findViewById(R.id.download))
				.setOnClickListener(mDownloadListener);
		((Button) findViewById(R.id.send)).setOnClickListener(mSendListener);
		mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
