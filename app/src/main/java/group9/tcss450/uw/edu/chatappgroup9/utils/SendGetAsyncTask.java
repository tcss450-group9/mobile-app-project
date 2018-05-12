package group9.tcss450.uw.edu.chatappgroup9.utils;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import group9.tcss450.uw.edu.chatappgroup9.R;

public class SendGetAsyncTask extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... strings) {
        if (strings.length != 3) {
            throw new IllegalArgumentException("Three String arguments required.");
        }
        String response = "";
        HttpURLConnection urlConnection = null;
//instead of using a hard coded (found in end_points.xml) url for our web service
// address, here we will build the URL from parts. This can be helpful when
// sending arguments via GET. In this example, we are sending plain text.
        String url = strings[0];
        String endPoint = strings[1];
        String args = strings[2];
        Log.d("SendGetAsyncTask", url + " " + endPoint + " " + args);
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(url)
                .appendPath(endPoint)
                .appendQueryParameter("username", args)
                .build();
        try {
            URL urlObject = new URL(uri.toString());
            urlConnection = (HttpURLConnection) urlObject.openConnection();
            InputStream content = urlConnection.getInputStream();
            BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
            String s = "";
            while ((s = buffer.readLine()) != null) {
                response += s;
            }
        } catch (Exception e) {
            response = "Unable to connect, Reason: "
                    + e.getMessage();
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
        Log.d("SendGetAsyncTask",response);
        return response;
    }
    @Override
    protected void onPostExecute(String result) {
        /*Log.d("SendGetAsyncTask",result);
        try {
            JSONObject contactArray = new JSONObject(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
    }
}
