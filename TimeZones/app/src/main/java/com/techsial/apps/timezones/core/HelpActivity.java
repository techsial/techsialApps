package com.techsial.apps.timezones.core;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.techsial.apps.timezones.R;

public class HelpActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        TextView textView = (TextView) findViewById(R.id.helpText);
        textView.setTypeface(MainActivity.fonts.get("Roboto-Regular"));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_help, menu);
        return true;
    }

}
