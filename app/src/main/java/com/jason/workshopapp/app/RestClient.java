package com.jason.workshopapp.app;

        import android.content.Context;
        import android.util.Log;

        import org.apache.http.Header;
        import org.apache.http.HttpEntity;
        import org.apache.http.HttpResponse;
        import org.apache.http.NameValuePair;
        import org.apache.http.client.ClientProtocolException;
        import org.apache.http.client.HttpClient;
        import org.apache.http.client.entity.UrlEncodedFormEntity;
        import org.apache.http.client.methods.HttpGet;
        import org.apache.http.client.methods.HttpPost;
        import org.apache.http.client.methods.HttpUriRequest;
        import org.apache.http.impl.client.DefaultHttpClient;
        import org.apache.http.message.BasicNameValuePair;

        import java.io.BufferedInputStream;
        import java.io.IOException;
        import java.io.InputStream;
        import java.io.UnsupportedEncodingException;
        import java.net.URLEncoder;
        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.Map;

public class RestClient {

    private static final String TAG = "REST CLIENT";
    private Context context;

    private Header[] responseHeaders;
    private static String accessToken;
    private static long accessTokenExpirationTime;

    public enum RequestMethod {POST, GET, PUT}

    private ArrayList<NameValuePair> params;
    private ArrayList<NameValuePair> headers;

    private String url;

    private int responseCode;
    private String message;

    private String response;

    public String getResponse() {
        return response;
    }

    public String getErrorMessage() {
        return message;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public RestClient(String url, Context context) {
        this.url = url;
        params = new ArrayList<NameValuePair>();
        headers = new ArrayList<NameValuePair>();
        this.context = context;
    }

    public Map<String, String> getHeaders() {
        HashMap<String, String> results = new HashMap<String, String>();
        for (Header header : responseHeaders) {
            results.put(header.getName(), header.getValue());
        }
        return results;
    }

    public void AddParam(String name, String value) {
        params.add(new BasicNameValuePair(name, value));
    }

    public void AddHeader(String name, String value) {
        headers.add(new BasicNameValuePair(name, value));
    }

    public void Execute(RequestMethod method) throws Exception {

        switch (method) {
            case GET: {
                sendGet();
                break;
            }

            case POST: {
                sendPost();
                break;
            }
            case PUT: {
                throw new Exception("Not Implemented");
            }
        }
    }

    private void sendPost() throws UnsupportedEncodingException {
        HttpPost request = new HttpPost(url);
        for (NameValuePair h : headers) {
            request.addHeader(h.getName(), h.getValue());
        }

        if (!params.isEmpty()) {
            request.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
        }

        executeRequest(request, url);
    }

    private void sendGet() throws UnsupportedEncodingException {
        //add parameters
        String combinedParams = "";
        if (!params.isEmpty()) {
            combinedParams += "?";
            for (NameValuePair p : params) {
                String paramString = p.getName() + "=" + URLEncoder.encode(p.getValue(), "UTF-8");
                if (combinedParams.length() > 1) {
                    combinedParams += "&" + paramString;
                } else {
                    combinedParams += paramString;
                }
            }
        }

        HttpGet request = new HttpGet(url + combinedParams);

        //add headers
        for (NameValuePair h : headers) {
            request.addHeader(h.getName(), h.getValue());
        }

        executeRequest(request, url);
        return;
    }

    private void executeRequest(HttpUriRequest request, String url) {
        Log.v("REST", "Sending Request....");

        HttpClient client = new DefaultHttpClient();

        HttpResponse httpResponse;

        try {
            httpResponse = client.execute(request);
            responseCode = httpResponse.getStatusLine().getStatusCode();
            Log.v("REST", "HTTP Response Code: " + responseCode);
            message = httpResponse.getStatusLine().getReasonPhrase();
            Log.v("REST", "HTTP Message: " + message);
            HttpEntity entity = httpResponse.getEntity();

            responseHeaders = httpResponse.getAllHeaders();

            if (entity != null) {

                InputStream instream = entity.getContent();
                response = convertStreamToString(instream);
                Log.v("REST", "HTTP Response: " + response);

                // Closing the input stream will trigger connection release
                instream.close();
            }

        } catch (ClientProtocolException e) {
            Log.e("REST", "ERROR: " + e.getLocalizedMessage());
            client.getConnectionManager().shutdown();
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("REST", "ERROR: " + e.getLocalizedMessage());
            client.getConnectionManager().shutdown();
            e.printStackTrace();
        } catch (Exception e) {
            Log.e("REST", "Other error: " + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    private static String convertStreamToString(InputStream is) {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(is);
        return getStringFromSomething(bufferedInputStream);
    }

    private static String getStringFromSomething(BufferedInputStream input) {
        int currentByteReadCount;
        byte[] data = new byte[40000];
        StringBuilder  result = new StringBuilder();

        try {
            while ((currentByteReadCount = input.read(data)) != -1) {
                String readData = new String(data, 0, currentByteReadCount);
                result.append(readData);
            }
        } catch (IOException e) {
            Log.e("REST", e.getLocalizedMessage());
        }

        return result.toString();
    }

}