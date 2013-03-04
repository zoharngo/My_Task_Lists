package il.ac.shenkar.controller.outmessaging;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import android.util.Log;

public class HttpClientUtils {
	private static final String TAG = "il.ac.shenkar.controller.outmessaging.HttpClientUtils";

	private static String convertStreamToString(InputStream in)
			throws IOException, SQLException, GeneralSecurityException {

		InputStreamReader inReader = new InputStreamReader(in);
		BufferedReader bufferedReader = new BufferedReader(inReader);
		StringBuilder responseBuilder = new StringBuilder();
		for (String line = bufferedReader.readLine(); line != null; line = bufferedReader
				.readLine()) {
			responseBuilder.append(line);
		}
		String response = responseBuilder.toString();
		if (response.contains("SQLException")
				|| response.contains("CommunicationsException")) {
			throw new SQLException("SQLException from server.");
		}
		if (response.contains("GeneralSecurityException")) {
			throw new GeneralSecurityException(
					"GeneralSecurityException from server.!");
		}

		return responseBuilder.toString();
	}

	public static String sendPostRequest(JSONObject jsonObject, String URL)
			throws IOException, SQLException, GeneralSecurityException {
		InputStream inputStream = null;
		String result = null;
		try {

			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(URL);
			String jsonString = jsonObject.toString();
			StringEntity se = new StringEntity(jsonString);
			se.setContentType("application/json;charset=UTF-8");
			se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
					"application/json;charset=UTF-8"));

			httpPost.setEntity(se);

			long t = System.currentTimeMillis();

			HttpResponse response = httpClient.execute(httpPost);

			Log.i(TAG,
					"HTTPResponse received in ["
							+ (System.currentTimeMillis() - t) + "ms]");
			// Get hold of the response entity (-> the data):
			HttpEntity entity = response.getEntity();

			if (entity != null) {
				// Read the content stream
				inputStream = entity.getContent();
				Header contentEncoding = response
						.getFirstHeader("Content-Encoding");
				if (contentEncoding != null
						&& contentEncoding.getValue().equalsIgnoreCase("gzip")) {
					inputStream = new GZIPInputStream(inputStream);
				}
				// convert content stream to a String
				result = convertStreamToString(inputStream);

			}
		} catch (IOException e) {
			throw e;
		} catch (SQLException e) {
			throw e;
		} catch (GeneralSecurityException e) {
			throw e;
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					throw e;
				}
			}
		}
		return result;

	}

}
