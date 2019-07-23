package com.gmaxmart.tajtodaynews;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Slidertest extends AppCompatActivity {
ViewPager viewPager;
    ArrayList<res> resArrayList;
    private static final int MY_SOCKET_TIMEOUT_MS =5000 ;
    List list;
    Gson gson;
    Map<String,Object> mapPost;
    Map<String,Object> mapTitle;
    Map<String,Object> mapdesc;
    Map<String,Object> img;
    Map<String,Object> guild;
    Map<String,Object> author_me;
    int mapid;
    ArrayList<Integer> tags_value;
    ArrayList<String> tag_ids;
    String authorName,count,frag_name;
    String you_id,author_name,link;
   int position;
    ArrayList <id_val> ids;
    Toolbar toolbar;
    RelativeLayout relativeLayout;
    String ttl,image,time,description;
    ArrayList data;
    Map<String, Object> post_meta;
    protected Handler handler;
    String[]postTitle;
    String category;
    private ProgressDialog progressDialog;
    CustomSwipeAdapter adapter;
    private String JSON_STRING;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slidertest);
        progressDialog = new ProgressDialog(Slidertest.this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        resArrayList = new ArrayList<res>();

        setSupportActionBar(toolbar);
        final String id = getIntent().getExtras().getString("id");
        authorName=getIntent().getExtras().getString("authorname");
        frag_name=getIntent().getExtras().getString("fragname");
       category=getIntent().getExtras().getString("category");
        position=getIntent().getIntExtra("position",0);
        position=position/10;
        position=position+1;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle(frag_name);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               onBackPressed();
            }
        });

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        if (isNetworkAvailable()) {
            progressDialog.setMessage("Please Wait");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();
            if(category==null) {
                JSON_STRING = "http://tajtodaynews.com/wp-json/wp/v2/posts/?page=" + position;
            }
            else{
                JSON_STRING = "http://tajtodaynews.com/wp-json/wp/v2/posts/?page=" + position+"&categories="+category;
            }
            StringRequest request = new StringRequest(Request.Method.GET, JSON_STRING, new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {
                    gson = new Gson();
                    list = (List) gson.fromJson(s, List.class);
                    postTitle = new String[list.size()];
                    for (int i = 0; i < list.size(); ++i) {
                        res res = new res();
                        ids = new ArrayList<id_val>();
                        id_val id_val=new id_val();
                        mapPost = (Map<String, Object>) list.get(i);
                        guild = (Map<String, Object>) mapPost.get("guid");
                        link = (String) guild.get("rendered");
                        mapTitle = (Map<String, Object>) mapPost.get("title");
                        ttl = (String) mapTitle.get("rendered");
                        mapdesc = (Map<String, Object>) mapPost.get("content");
                        description = (String) mapdesc.get("rendered").toString();
                        tags_value=(ArrayList)mapPost.get("tags");
                        tag_ids= (ArrayList) mapPost.get("tag_names");
                        //tags=new ArrayList<>();
                     /*   for(int p=0;p<tag_ids.size();p++){
                            String sq= (String) tag_ids.get(p);
                            tags.add(sq);
                        }
                        tag_val=new ArrayList<>();
                        for(int w=0;w<tags_value.size();w++){
                            id_val.setTgs1(tags_value);
                            tag_val.add(tags_value.get(w));
                        }*/
                        id_val.setTgs1(tags_value);
                        id_val.setTgs(tag_ids);
                        ids.add(id_val);
                        img = (Map<String, Object>) mapPost.get("better_featured_image");
                        author_me = (Map<String, Object>) mapPost.get("author_meta");
                        author_name = (String) author_me.get("user_nicename");
                        post_meta = (Map<String, Object>) mapPost.get("post-meta-fields");
                     /*   data1 = (ArrayList) post_meta.get("post_views_count");
                        for(int p=0;p<data1.size();p++) {
                            count = (String) data1.get(0);
                        }*/

                        mapid = ((Double) mapPost.get("id")).intValue();
                        if (img != null) {
                            image = (String) img.get("source_url");
                        } else {
                            image = null;
                        }

                        time = (String) mapPost.get("date_gmt");
                        res.setId(String.valueOf(mapid));
                        res.setTime(time);
                        res.setImag(image);
                        res.setTitle(ttl);
                        //res.setCount(count);
                        res.setAbc(ids);
                        res.setLink(link);
                        res.setContent(description);
                        resArrayList.add(res);

                    }
                    Iterator<res> iterator = resArrayList.iterator();
                    while(iterator.hasNext())
                    {
                        res r=iterator.next();
                        if(Integer.parseInt(r.getId())>Integer.parseInt(id))
                            iterator.remove();
                    }
                    adapter = new CustomSwipeAdapter(resArrayList, Slidertest.this);
                    viewPager.setAdapter(adapter);

                    progressDialog.dismiss();



                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    if (error instanceof NoConnectionError) {
                        Toast.makeText(Slidertest.this, "No Network", Toast.LENGTH_LONG).show();
                    } else if (error instanceof TimeoutError) {
                        Toast.makeText(Slidertest.this, "You are on slow network", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(Slidertest.this, "Some error occur please try after some time", Toast.LENGTH_LONG).show();
                    }
                }

            });
            request.setRetryPolicy(new DefaultRetryPolicy(
                    MY_SOCKET_TIMEOUT_MS,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            AppController.getInstance().addToRequestQueue(request);


        }
        else{
            Toast.makeText(Slidertest.this,"No Network",Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) Slidertest.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onBackPressed() {

            if (CustomSwipeAdapter.t1 != null) {
                CustomSwipeAdapter.t1.stop();
                CustomSwipeAdapter.t1.shutdown();
            }
            super.onBackPressed();
    }
}
