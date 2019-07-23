package com.gmaxmart.tajtodaynews;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Post extends AppCompatActivity {
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
    String authorName,frag_name;
    String you_id,author_name,link;
    int position;
    VidAdapter vidAdapter;
    Toolbar toolbar;
    RelativeLayout relativeLayout;
    String ttl,image,time,description;
    ArrayList data;
    Map<String, Object> post_meta;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    protected Handler handler;
    String[]postTitle;
    String category;
    private ProgressDialog progressDialog;
    private String JSON_STRING;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        progressDialog = new ProgressDialog(Post.this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        resArrayList = new ArrayList<res>();
        setSupportActionBar(toolbar);
        relativeLayout=(RelativeLayout)findViewById(R.id.relate);
        recyclerView = (RecyclerView)findViewById(R.id.recyler);
        if (recyclerView != null) {
            recyclerView.setHasFixedSize(true);
        }
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
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
                        mapPost = (Map<String, Object>) list.get(i);
                        guild = (Map<String, Object>) mapPost.get("guid");
                        link = (String) guild.get("rendered");
                        mapTitle = (Map<String, Object>) mapPost.get("title");
                        ttl = (String) mapTitle.get("rendered");
                        mapdesc = (Map<String, Object>) mapPost.get("content");
                        description = (String) mapdesc.get("rendered").toString();
                        img = (Map<String, Object>) mapPost.get("better_featured_image");
                        author_me = (Map<String, Object>) mapPost.get("author_meta");
                        author_name = (String) author_me.get("user_nicename");
                        post_meta = (Map<String, Object>) mapPost.get("post-meta-fields");
                        data = (ArrayList) post_meta.get("_fvp_video");
                        if (data != null) {
                            for (int j = 0; j < data.size(); j++) {
                                SerializedPhpParser serializedPhpParser = new SerializedPhpParser(data.get(0).toString());
                                Object result = serializedPhpParser.parse();
                                HashMap newMap = new HashMap((Map) result);
                                String sas = newMap.get("full").toString();

                                String pattern = "(?<=watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*";

                                Pattern compiledPattern = Pattern.compile(pattern);
                                Matcher matcher = compiledPattern.matcher(sas);

                                if (matcher.find()) {
                                    Log.d("abc", matcher.group());
                                    you_id = matcher.group();
                                }

                            }

                        }
                        else{
                            you_id = null;
                        }

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
                        res.setAuthor_name(author_name);
                        res.setLink(link);
                        res.setContent(description);
                        if(you_id!=null) {
                            res.setYoutube_id(you_id);
                        }
                        resArrayList.add(res);

                    }
                    Iterator<res> iterator = resArrayList.iterator();
                    while(iterator.hasNext())
                    {
                        res r=iterator.next();
                        if(Integer.parseInt(r.getId())>Integer.parseInt(id) || r.getYoutube_id()==null)
                            iterator.remove();
                    }
                    vidAdapter = new VidAdapter(Post.this,resArrayList);
                    recyclerView.setAdapter(vidAdapter);

                    progressDialog.dismiss();


                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    if (error instanceof NoConnectionError) {
                        Toast.makeText(Post.this, "No Network", Toast.LENGTH_LONG).show();
                    } else if (error instanceof TimeoutError) {
                        Toast.makeText(Post.this, "You are on slow network", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(Post.this, "Some error occur please try after some time", Toast.LENGTH_LONG).show();
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
            Toast.makeText(Post.this,"No Network",Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) Post.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
