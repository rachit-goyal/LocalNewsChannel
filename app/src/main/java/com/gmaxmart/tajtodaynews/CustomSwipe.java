package com.gmaxmart.tajtodaynews;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public class CustomSwipe extends PagerAdapter {
    private ArrayList<res> resArrayList;
    Context context;
    DatabaseHelper databaseHelper;
    ArrayList<String> id;
    String name;
    String youid;

    static TextToSpeech t1;
    URLImageParser urlImageParser;
    UrlImg urlImg;

    CustomSwipe(ArrayList<res> resArrayList, Context context) {
        this.resArrayList = resArrayList;
        this.context = context;
        t1=new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(new Locale("hin"));
                }
            }
        });
        databaseHelper = new DatabaseHelper(context);

    }

    @Override
    public int getCount() {
        return resArrayList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == (LinearLayout) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull final ViewGroup container, final int position) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View item_view = layoutInflater.inflate(R.layout.swipe_layout, container, false);

        ImageView imageView = (ImageView) item_view.findViewById(R.id.imgpost);
        LinearLayout ll = (LinearLayout) item_view.findViewById(R.id.tagsbgs);
        ImageView save = (ImageView) item_view.findViewById(R.id.save);
        final ImageView tts = (ImageView) item_view.findViewById(R.id.tts);
        TextView dateval=(TextView)item_view.findViewById(R.id.date);
        TextView tags_name = (TextView) item_view.findViewById(R.id.tags_name);
        final TextView speak = (TextView) item_view.findViewById(R.id.speak);
        final TextView tsize = (TextView) item_view.findViewById(R.id.tsize);
        TextView textView = (TextView) item_view.findViewById(R.id.textqw);
        final TextView textViewcon = (TextView) item_view.findViewById(R.id.textcont);
        ImageView imgshare = (ImageView) item_view.findViewById(R.id.imgshare);
        final ImageView textsize = (ImageView) item_view.findViewById(R.id.textsize);
        if (resArrayList.get(position).getImag() == null) {
            imageView.setImageResource(R.drawable.img);
        } else {
            Picasso.with(context).load(resArrayList.get(position).getImag()).placeholder(R.drawable.app_logo).into(imageView);
        }
        tags_name.setText("Tags:");
        tags_name.setVisibility(View.GONE);
        ll.setVisibility(View.GONE);
        textView.setText(Html.fromHtml(resArrayList.get(position).getTitle()));
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(resArrayList.get(position).getTime());
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            //long val=date.getTime();
            //Log.d("valuedate", sdf.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
        DateFormat targetFormat = new SimpleDateFormat("dd MMMM yyyy");
        try {
            Date date1 = originalFormat.parse(sdf.format(date));
            Log.d("valuedate", targetFormat.format(date1));
            String valuetime = targetFormat.format(date1);
            dateval.setText(valuetime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //textViewtime.setText(resArrayList.get(position).getTime().substring(0, 10));


        textViewcon.setText(Html.fromHtml(resArrayList.get(position).getContent()));
        for (int i = 0; i < resArrayList.get(position).getAbc().get(0).getTgs().size(); i++) {
            final TextView tv = new TextView(context);
            tv.setText(resArrayList.get(position).getAbc().get(0).getTgs().get(i));
            tv.setBackgroundResource(R.drawable.tags);
            tv.setTextColor(Color.parseColor("#ffffff"));
            tv.setPadding(8, 8, 8, 8);
            tv.setTag(resArrayList.get(position).getAbc().get(0).getTgs1().get(i));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.bottomMargin = 5;
            params.leftMargin=10;

        }
        if (resArrayList.get(position).getAbc().get(0).getTgs().size() == 0) {
            tags_name.setVisibility(View.GONE);
        }
        textViewcon.setMovementMethod(LinkMovementMethod.getInstance());
        urlImageParser = new URLImageParser(textViewcon, context);
        urlImg = new UrlImg(textViewcon, context);
        textViewcon.setText(Html.fromHtml(resArrayList.get(position).getContent(), urlImageParser, null));
        container.addView(item_view, 0);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cursor sde = databaseHelper.getAllData();
                id = new ArrayList<String>();
                while (sde.moveToNext()) {
                    id.add(sde.getString(6));
                }
                boolean isExist = false;
                for (int i = 0; i < id.size(); i++) {
                    if (resArrayList.get(position).getId().equals(id.get(i))) {
                        Toast.makeText(context, "Already saved", Toast.LENGTH_SHORT).show();
                        isExist = true;
                        break;
                    }
                }

                if(!isExist) {
                    Picasso.with(context)
                            .load(resArrayList.get(position).getImag())
                            .into(new Target() {
                                      @Override
                                      public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                          try {
                                              String root = context.getFilesDir().getPath();
                                              File myDir = new File(root + "/tajtodaynews");

                                              if (!myDir.exists()) {
                                                  myDir.mkdirs();
                                              }

                                              name = new Date().toString() + ".jpg";
                                              myDir = new File(myDir, name);
                                              FileOutputStream out = new FileOutputStream(myDir);
                                              bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);

                                              out.flush();
                                              out.close();
                                          } catch (Exception e) {
                                              // some action
                                          }
                                      }

                                      @Override
                                      public void onBitmapFailed(Drawable errorDrawable) {
                                      }

                                      @Override
                                      public void onPrepareLoad(Drawable placeHolderDrawable) {
                                      }
                                  }
                            );

                    boolean isinserted = databaseHelper.inserdata(resArrayList.get(position).getTitle(), resArrayList.get(position).getLink(), resArrayList.get(position).getTime(), resArrayList.get(position).getContent().replaceAll("<img.+?>", ""), name, resArrayList.get(position).getId());
                    if (isinserted) {
                        Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Unable To Save", Toast.LENGTH_SHORT).show();
                    }
                }

            }




        });
        textsize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tsize.getText().equals("Text++")){
                    DisplayMetrics metrics;
                    metrics = context.getApplicationContext().getResources().getDisplayMetrics();
                    float Textsize =textViewcon.getTextSize()/metrics.density;
                    textViewcon.setTextSize(Textsize+3);
                    textsize.setImageResource(R.drawable.dec);
                    tsize.setText("Text--");
                }
                else{
                    DisplayMetrics metrics;
                    metrics = context.getApplicationContext().getResources().getDisplayMetrics();
                    float Textsize =textViewcon.getTextSize()/metrics.density;
                    textViewcon.setTextSize(Textsize-3);
                    textsize.setImageResource(R.drawable.fontsize);
                    tsize.setText("Text++");
                }
            }
        });
        tts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(speak.getText().toString().equals("Off")) {
                    tts.setImageResource(R.drawable.tts);
                    speak.setText("Listen");
                    if (t1 != null) {
                        t1.stop();

                    }
                }
                else if(speak.getText().toString().equals("Listen")){
                    tts.setImageResource(R.drawable.ttsoff);
                    speak.setText("Off");
                    t1.speak(textViewcon.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });
        imgshare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, resArrayList.get(position).getLink()+"\nShare Taj Today News app");
                context.startActivity(Intent.createChooser(shareIntent, "Share Via..."));
            }
        });
        if (t1 != null) {
            t1.stop();
        }
        return item_view;
    }


    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout) object);

    }


}
