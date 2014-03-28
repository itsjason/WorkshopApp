package com.jason.workshopapp.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


public class SearchResultsActivity extends Activity {

    public static String EXTRA_SOME_TEXT = "com.jason.workshopapp.app.SearchResultsText";
    public static String TAG = "SEARCH";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        StatusResponse statusResponse = (StatusResponse) getIntent().getSerializableExtra(EXTRA_SOME_TEXT);
        TwitterStatus[] status = statusResponse.statuses;

        String[] tweets = new String[status.length];

        LinearLayout container = (LinearLayout) findViewById(R.id.tweet_container);


        for(int i = 0; i < status.length; i++) {
            tweets[i] = status[i].text;
            TextView tweetTextView = new TextView(this);
            tweetTextView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tweetTextView.setText(status[i].text);
            container.addView(tweetTextView);
        }



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_results, menu);
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

}
