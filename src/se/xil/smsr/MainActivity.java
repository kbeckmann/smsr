package se.xil.smsr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import se.xil.smsr.R;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Button;

;

public class MainActivity extends Activity {

	private EditText mRecipient;
	private EditText mMessage;
	private OnClickListener mSendListener = new OnClickListener() {

		@Override
		public void onClick(View v) {

			String[] text = mMessage.getText().toString().split("\\r?\\n");
			Log.i("SMSR", "Sending..." + text.length);

			for (String mess : text) {

				
				Log.i("SMSR", mess);
				sendSMS(mRecipient.getText().toString(), mess);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
	};
	private OnClickListener mDownloadListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			mDownloader.execute(0);
		}
	};
	
	AsyncTask<Integer, Integer, Integer> mDownloader = new AsyncTask<Integer, Integer, Integer>() {

		@Override
		protected Integer doInBackground(Integer... params) {
			try {
				final String mess = download("http://sms.xil.se/a.txt");
				
				runOnUiThread(new Runnable(){

					@Override
					public void run() {
						mMessage.setText(mess);
					}});
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
	};
	
	
	public static String download(String url) throws ParseException, IOException {

	    DefaultHttpClient httpClient = new DefaultHttpClient();
	    HttpGet httpGet = new HttpGet(url);

	    HttpResponse httpResponse = httpClient.execute(httpGet);
	    HttpEntity httpEntity = httpResponse.getEntity();
	    return EntityUtils.toString(httpEntity);
	}

	private static void sendSMS(String phoneNumber, String message) {
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
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
