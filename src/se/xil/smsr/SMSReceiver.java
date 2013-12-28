package se.xil.smsr;

import java.io.IOException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.telephony.SmsMessage;
import android.util.Log;

public class SMSReceiver extends BroadcastReceiver {
	private final String DEBUG_TAG = getClass().getSimpleName().toString();
	private static final String ACTION_SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
	private Context mContext;
	private Intent mIntent;

	// Retrieve SMS
	public void onReceive(Context context, Intent intent) {
		mContext = context;
		mIntent = intent;

		String action = intent.getAction();

		if (action.equals(ACTION_SMS_RECEIVED)) {

			String address = "";
			String str = "";

			SmsMessage[] msgs = getMessagesFromIntent(mIntent);
			if (msgs != null) {
				for (int i = 0; i < msgs.length; i++) {
					address = msgs[i].getOriginatingAddress();
					// contactId = ContactsUtils.getContactId(mContext, address,
					// "address");
					str += msgs[i].getMessageBody().toString();
					str += "\n";
				}
			}
			
			final String xx = address;
			final String y = str;

			AsyncTask<Integer, Integer, Integer> x = new AsyncTask<Integer, Integer, Integer>(){

				@Override
				protected Integer doInBackground(Integer... params) {

					showNotification(xx, y);
					return null;
				}
				
			};
			
			x.execute(0);

			Intent broadcastIntent = new Intent();
			broadcastIntent.setAction("SMS_RECEIVED_ACTION");
			broadcastIntent.putExtra("sms", str);
			context.sendBroadcast(broadcastIntent);
		}
	}

	public static SmsMessage[] getMessagesFromIntent(Intent intent) {
		Object[] messages = (Object[]) intent.getSerializableExtra("pdus");
		byte[][] pduObjs = new byte[messages.length][];

		for (int i = 0; i < messages.length; i++) {
			pduObjs[i] = (byte[]) messages[i];
		}
		byte[][] pdus = new byte[pduObjs.length][];
		int pduCount = pdus.length;
		SmsMessage[] msgs = new SmsMessage[pduCount];
		for (int i = 0; i < pduCount; i++) {
			pdus[i] = pduObjs[i];
			msgs[i] = SmsMessage.createFromPdu(pdus[i]);
		}
		return msgs;
	}

	protected void showNotification(String contact, String message) {

		Log.i(DEBUG_TAG, "From: " + contact + "\nText: " + message);
		
		try {
			String translated;
			translated = TranslateHelper.translate("", "en", message);
			Log.i(DEBUG_TAG, "To: " + contact + "\nText: " + translated);

			MainActivity.sendSMS(contact, translated);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}