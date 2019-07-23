package com.gmaxmart.tajtodaynews;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class LocalNEws extends AppCompatActivity {
String localid;
    ImageView imageView,shareimg;
    TextView title,content,datevv;
    Toolbar toolbar;
    DatabaseHelper databaseHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_news);
        localid=getIntent().getExtras().getString("id");
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        imageView=(ImageView)findViewById(R.id.imgpost);
        shareimg=(ImageView)findViewById(R.id.imgshare);
        title=(TextView)findViewById(R.id.toolbar_title);
        datevv=(TextView)findViewById(R.id.time1);
        content=(TextView)findViewById(R.id.textcont);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        databaseHelper=new DatabaseHelper(this);
        final Cursor sde=databaseHelper.getIndividualData(localid);
        if (sde.moveToFirst())
        {
            do
            {
                title.setText(sde.getString(1));
                content.setText(Html.fromHtml(sde.getString(4)));
                DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                Date date = null;
                try {
                    date = sdf.parse(sde.getString(3));
                    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                    //long val=date.getTime();
                    Log.d("valuedate",sdf.format(date));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
                DateFormat targetFormat = new SimpleDateFormat("dd MMMM yyyy");
                try {
                    Date date1 = originalFormat.parse(sdf.format(date));
                    Log.d("valuedate",targetFormat.format(date1));
                    String  valuetime=targetFormat.format(date1);
                  datevv.setText(valuetime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                String photoPath = getApplicationContext().getFilesDir().getPath()+"/tajtodaynews/"+sde.getString(5);
                Bitmap b = BitmapFactory.decodeFile(photoPath);
                if(b==null){
                    imageView.setImageResource(R.drawable.img);
                }
                else{
                    imageView.setImageBitmap(b);
                }
                final String abc=sde.getString(2)+"\nShare by Taj Today News app";

                shareimg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType("text/plain");
                        shareIntent.putExtra(Intent.EXTRA_TEXT, abc);
                        startActivity(Intent.createChooser(shareIntent, "Share Via..."));
                    }
                });

            }while (sde.moveToNext());
        }



        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


}
