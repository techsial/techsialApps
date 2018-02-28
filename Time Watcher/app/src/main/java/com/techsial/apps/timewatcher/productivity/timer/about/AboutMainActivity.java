package com.techsial.apps.timewatcher.productivity.timer.about;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.techsial.apps.timewatcher.productivity.timer.R;

public class AboutMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generic_layout);

        getFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new AboutFragment())
                .commit();
    }
}
