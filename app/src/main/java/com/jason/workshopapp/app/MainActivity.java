package com.jason.workshopapp.app;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;


public class MainActivity extends Activity {

    private static String twitterCreds = "a2lVVThCajNGdVF1UmdndVVBYVlKZzphQ3Q5NkR0ZlBzUGkxWDc1Z1V1Y3lSb0s1azJkV0pVRGJTc1RQNFU1dw==";
    private static String twitterAuthUrl = "https://api.twitter.com/oauth2/token";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button heresTheButton = (Button) findViewById(R.id.search_button);
        heresTheButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchTwitter();
            }
        });
    }

    private void searchTwitter() {

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    return getTwitterToken();
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String result) {
                Toast.makeText(MainActivity.this, result, Toast.LENGTH_LONG).show();
                Gson gson = new Gson();
                TwitterToken token = gson.fromJson(result, TwitterToken.class);

                runTwitterSearch(token.access_token);

            }
        }.execute();


        Intent intent = new Intent(this, SearchResultsActivity.class);
        intent.putExtra(SearchResultsActivity.EXTRA_SOME_TEXT, "Here is the text from the Main Activity!");
        //startActivity(intent);
    }

    private void runTwitterSearch(String access_token) {

        final RestClient client = new RestClient("https://api.twitter.com/1.1/search/tweets.json", this);
        client.AddHeader("Authorization", "Bearer " + access_token);

        EditText searchText = (EditText) findViewById(R.id.search_text);
        String searchString = searchText.getText().toString();

        client.AddParam("q", searchString);

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    client.Execute(RestClient.RequestMethod.GET);
                    return client.getResponse();
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String s) {
                Log.i("TWITTER", s);
                Gson gson = new Gson();
                StatusResponse response = gson.fromJson(s, StatusResponse.class);

                Intent intent = new Intent(MainActivity.this, SearchResultsActivity.class);
                intent.putExtra(SearchResultsActivity.EXTRA_SOME_TEXT, response);
                startActivity(intent);
            }
        }.execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private String getTwitterToken() throws Exception {
        RestClient client = new RestClient(twitterAuthUrl, this);
        client.AddHeader("Authorization", "Basic " + twitterCreds);
        client.AddHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

        // POST Body Parameter
        client.AddParam("grant_type", "client_credentials");

        client.Execute(RestClient.RequestMethod.POST);

        return client.getResponse();
    }

}

