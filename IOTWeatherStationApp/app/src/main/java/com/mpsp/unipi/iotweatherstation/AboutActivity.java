package com.mpsp.unipi.iotweatherstation;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.util.Linkify;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView geoemailView = (TextView) findViewById(R.id.textVGEOEMAIL);
        geoemailView.setText(R.string.geoEmail);
        Linkify.addLinks(geoemailView, Linkify.ALL);

        TextView kozoemailView = (TextView) findViewById(R.id.textVKOZOEMAIL);
        kozoemailView.setText(R.string.kozoEmail);
        Linkify.addLinks(kozoemailView, Linkify.ALL);

        ImageView img = (ImageView)findViewById(R.id.imageView);
        img.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("http://www.cs.unipi.gr/index.php?lang=en"));
                startActivity(intent);
            }
        });
    }

}
