package com.gmaxmart.tajtodaynews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.Html;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by RACHIT GOYAL on 9/2/2017.
 */

public class UrlImg implements Html.ImageGetter {
    Context context;
    TextView container;
    String name;
    ArrayList<String> img=new ArrayList<>();

    public UrlImg(TextView container, Context context) {
        this.context = context;
        this.container = container;
    }

    public Drawable getDrawable(String source) {
        URLDrawable urlDrawable = new URLDrawable();


        UrlImg.ImageGetterAsyncTask asyncTask =
                new UrlImg.ImageGetterAsyncTask(urlDrawable);

        asyncTask.execute(source);


        return urlDrawable;
    }



    public class ImageGetterAsyncTask extends AsyncTask<String, Void, Drawable> {
        URLDrawable urlDrawable;


        public ImageGetterAsyncTask(URLDrawable d) {
            this.urlDrawable = d;
        }

        @Override
        protected Drawable doInBackground(String... params) {
            String source = params[0];
            return fetchDrawable(source);
        }

        @Override
        protected void onPostExecute(Drawable result) {
            if(result == null)
            {
                urlDrawable.drawable = context.getResources().getDrawable(R.mipmap.app_icon);

                // redraw the image by invalidating the container
                UrlImg.this.container.invalidate();
                return;
            }
            // set the correct bound according to the result from HTTP call
            urlDrawable.setBounds(0, 0, result.getIntrinsicWidth(),
                    result.getIntrinsicHeight());

            // change the reference of the current drawable to the result
            // from the HTTP call
            urlDrawable.drawable = result;

            // redraw the image by invalidating the container
            UrlImg.this.container.invalidate();

            // For ICS
            UrlImg.this.container.setHeight((UrlImg.this.container.getHeight() + result.getIntrinsicHeight()));

            // Pre ICS
            UrlImg.this.container.setEllipsize(null);

            UrlImg.this.container.setText(UrlImg.this.container.getText());

        }


        Drawable fetchDrawable(String urlString) {
            img.add(urlString);
            try {
                Drawable drawable = fetch(urlString);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                return drawable;
            } catch (Exception e) {
                return null;
            }
        }


        private Drawable fetch(final String urlString) throws IOException {
            Picasso.with(context)
                    .load(urlString)
                    .into(new Target() {
                              @Override
                              public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                  try {
                                      String root = Environment.getExternalStorageDirectory().toString();
                                      File myDir = new File(root + "/LegendNews");

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
            return new BitmapDrawable(context.getResources(), name);
        }
    }
}
