package com.vmoksha.schoolbusdriver.httprequest;

import android.os.AsyncTask;
import android.util.Log;

import com.vmoksha.schoolbusdriver.model.ModelClass;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class HTTP_PUT extends AsyncTask<String, Void, String> {

	@Override
	protected String doInBackground(String... params) {
		HttpURLConnection urlConnection = null;
		String result = "";
		String api_url = params[0];
		String strJsonData = params[1];
		
		/*try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}*/
		
		try {
			// URL url = new URL("http://125.21.227.181:8065/api/address/75");
			URL url = new URL(api_url);
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.addRequestProperty("Content-Type", "application/json");
			urlConnection.addRequestProperty("Accept", "application/json");
			urlConnection.addRequestProperty("x-access-token", ModelClass.TOKEN);
			urlConnection.setConnectTimeout(5000);
			urlConnection.setDoInput(true);
			urlConnection.setRequestMethod("PUT");
			// Allow Outputs
			urlConnection.setDoOutput(true);
			// Don't use a cached copy.
			urlConnection.setUseCaches(false);

			// adding json data
			if (strJsonData != null && strJsonData.length() > 0) {
				OutputStreamWriter wr = new OutputStreamWriter(
						urlConnection.getOutputStream());
				wr.write(strJsonData);
				wr.flush();
			}
			InputStream in = new BufferedInputStream(
					urlConnection.getInputStream());
			result = readStream(in);

		} catch (Exception e) {
			Log.i("HTTP_Post", e.getMessage());

		} finally {
			urlConnection.disconnect();
		}
		return result;
	}

	private static String readStream(InputStream in) {
		StringBuilder sb = new StringBuilder();
		try {
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(in));
			String nextLine = "";
			while ((nextLine = reader.readLine()) != null) {
				sb.append(nextLine);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

}
