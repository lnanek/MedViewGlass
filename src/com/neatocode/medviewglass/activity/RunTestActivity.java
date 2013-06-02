package com.neatocode.medviewglass.activity;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.neatocode.medviewglass.Constants;
import com.neatocode.medviewglass.R;

public class RunTestActivity extends Activity {

	private static final String LOG_TAG = "RunTestActivity";

	private TextView resultsView;
	private EditText input;
	private Button button;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_meaning);

		resultsView = (TextView) findViewById(R.id.result);
		input = (EditText) findViewById(R.id.text);
		button = (Button) findViewById(R.id.find);

		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				resultsView.setText("Contacting SpringSense server...");
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							callService(input.getText().toString());
						} catch (final IOException e) {
							e.printStackTrace();

							runOnUiThread(new Runnable() {

								@Override
								public void run() {
									resultsView
											.setText("Failed to get meaning: "
													+ e.getMessage());
								}

							});

						}
					}
				}).start();
				// TODO Auto-generated method stub

			}
		});

	}

	public void callService(String input) throws IOException {

		//input = URLEncoder.encode(input, "UTF-8");
		//input.replace("+", "%20");
		
		input = escapeURIPathParam(input);
		
		String urlString = "https://springsense.p.mashape.com/disambiguate?body="
				+ input + "&strategy=accuracy";
		Log.i(Constants.LOG_TAG, "urlString = " + urlString);
		
		HttpURLConnection httpUrlConnection = null;
		URL url = new URL(urlString);
		httpUrlConnection = (HttpURLConnection) url.openConnection();

		httpUrlConnection.setRequestMethod("GET");
		httpUrlConnection
				.setRequestProperty("Content-Type", "application/json");
		httpUrlConnection.setRequestProperty("X-Mashape-Authorization",
				"INSERT_YOUR_KEY_HERE");

		httpUrlConnection.setUseCaches(true);
		// httpUrlConnection.setDoOutput(true);
		httpUrlConnection.setRequestProperty("Connection", "Keep-Alive");

		InputStream responseStream = new BufferedInputStream(
				httpUrlConnection.getInputStream());

		BufferedReader responseStreamReader = new BufferedReader(
				new InputStreamReader(responseStream));
		String line = "";
		StringBuilder stringBuilder = new StringBuilder();
		while ((line = responseStreamReader.readLine()) != null) {
			stringBuilder.append(line).append("\n");
		}
		responseStreamReader.close();

		final String response = stringBuilder.toString();
		Log.i(LOG_TAG, "Response: " + response);

		responseStream.close();
		httpUrlConnection.disconnect();

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				resultsView.setText("");
				try {
					JSONArray array = new JSONArray(response);
					JSONObject o = array.getJSONObject(0);
					JSONArray terms = o.getJSONArray("terms");
					int termCount = terms.length();
					for(int i = 0; i < termCount; i++) {
						JSONObject t = terms.getJSONObject(i);
						JSONArray meanings = t.getJSONArray("meanings");
						String definition = meanings.getJSONObject(0).getString("definition");
						resultsView.setText(resultsView.getText() + " " + definition);
					}
					
					
				} catch (Throwable e) {

					resultsView.setText("" + e.getClass().getName() + " error parsing: " + response);
					
					e.printStackTrace();
				}
				
				
				
			}

		});
	}
	public static String escapeURIPathParam(String input) {
		  StringBuilder resultStr = new StringBuilder();
		  for (char ch : input.toCharArray()) {
		   if (isUnsafe(ch)) {
		    resultStr.append('%');
		    resultStr.append(toHex(ch / 16));
		    resultStr.append(toHex(ch % 16));
		   } else{
		    resultStr.append(ch);
		   }
		  }
		  return resultStr.toString();
		 }

		 private static char toHex(int ch) {
		  return (char) (ch < 10 ? '0' + ch : 'A' + ch - 10);
		 }

		 private static boolean isUnsafe(char ch) {
		  if (ch > 128 || ch < 0)
		   return true;
		  return " %$&+,/:;=?@<>#%".indexOf(ch) >= 0;
		 }
}