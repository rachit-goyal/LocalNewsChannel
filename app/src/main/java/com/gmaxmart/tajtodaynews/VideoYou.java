package com.gmaxmart.tajtodaynews;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class VideoYou extends AppCompatActivity {
    private ProgressDialog dialog;
    String status;
    LinearLayout noint;
    TextView nointernet;
    protected Handler handler;
    Toolbar toolbar;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    DataAdapyouind adapter;
    TextView nocon;

    ArrayList<res> videoDetailsArrayList;
    String url="https://www.googleapis.com/youtube/v3/search?part=snippet,id&order=date&channelId=UCC53PEU0PZC7OoElDA45TpA&maxResults=10&key=AIzaSyCC4yXKx3DA_LtF4uZ38gOC3kHmcU00GlM";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_you);

        videoDetailsArrayList=new ArrayList<>();
        nocon=(TextView)findViewById(R.id.noccon);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        recyclerView=(RecyclerView)findViewById(R.id.recyle);
        noint=(LinearLayout)findViewById(R.id.noint);
        handler = new Handler();

        nointernet=(TextView)findViewById(R.id.noin);
        if (recyclerView != null) {
            recyclerView.setHasFixedSize(true);
        }
        layoutManager = new LinearLayoutManager(VideoYou.this);
        recyclerView.setLayoutManager(layoutManager);
        dialog = new ProgressDialog(VideoYou.this);
        if (isNetworkAvailable()) {
            loadData();

        } else {
            Toast.makeText(VideoYou.this, "No Network", Toast.LENGTH_SHORT).show();
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }

    private void loadData() {
        getdata();
    }

    private void getdata() {
        dialog.setMessage("Please Wait...");
        dialog.setCancelable(false);
        dialog.show();
        StringRequest str=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    if (jsonObject.has("nextPageToken")) {
                        status = jsonObject.getString("nextPageToken");
                    }
                    else{
                        status=null;
                    }
                    JSONArray jsonArray=jsonObject.getJSONArray("items");
                    if(jsonArray.length()==0){
                        nocon.setVisibility(View.VISIBLE);
                    }
                    else{
                        for(int i=0;i<jsonArray.length();i++){
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            JSONObject jsonVideoId=jsonObject1.getJSONObject("id");
                            JSONObject jsonsnippet= jsonObject1.getJSONObject("snippet");
                            JSONObject jsonObjectdefault = jsonsnippet.getJSONObject("thumbnails").getJSONObject("medium");
                            res videoDetails=new res();
                            String vvv=jsonVideoId.getString("kind");
                            if(vvv.equals("youtube#video")){
                                String videoid = jsonVideoId.getString("videoId");
                                videoDetails.setUrl(jsonObjectdefault.getString("url"));
                                videoDetails.setVideoName(jsonsnippet.getString("title"));
                                videoDetails.setVideoDesc(jsonsnippet.getString("description"));
                                videoDetails.setVideoId(videoid);

                                videoDetailsArrayList.add(videoDetails);
                            }


                        }
                    }
                    adapter = new DataAdapyouind(videoDetailsArrayList, recyclerView);

                    // set the adapter object to the Recyclerview
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
                        @Override
                        public void onLoadMore() {
                            if(isNetworkAvailable()){
                                if(status!=null){
                                    videoDetailsArrayList.add(null);
                                    adapter.notifyItemInserted(videoDetailsArrayList.size() - 1);
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            String urr="https://www.googleapis.com/youtube/v3/search?pageToken="+status+"&part=snippet,id&order=date&channelId=UCC53PEU0PZC7OoElDA45TpA&maxResults=10&key=AIzaSyCC4yXKx3DA_LtF4uZ38gOC3kHmcU00GlM";
                                            StringRequest stringRequest=new StringRequest(Request.Method.GET, urr, new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    videoDetailsArrayList.remove(videoDetailsArrayList.size() - 1);
                                                    adapter.notifyItemRemoved(videoDetailsArrayList.size());
                                                    JSONObject jsonObject= null;
                                                    try {
                                                        jsonObject = new JSONObject(response);
                                                        if (jsonObject.has("nextPageToken")) {
                                                            status = jsonObject.getString("nextPageToken");
                                                        }
                                                        else{
                                                            status=null;
                                                        }
                                                        JSONArray jsonArray=jsonObject.getJSONArray("items");

                                                        for(int i=0;i<jsonArray.length();i++){
                                                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                                            JSONObject jsonVideoId=jsonObject1.getJSONObject("id");
                                                            JSONObject jsonsnippet= jsonObject1.getJSONObject("snippet");
                                                            JSONObject jsonObjectdefault = jsonsnippet.getJSONObject("thumbnails").getJSONObject("medium");
                                                            res videoDetails=new res();
                                                            String vvv=jsonVideoId.getString("kind");
                                                            if(vvv.equals("youtube#video")){
                                                                String videoid = jsonVideoId.getString("videoId");
                                                                videoDetails.setUrl(jsonObjectdefault.getString("url"));
                                                                videoDetails.setVideoName(jsonsnippet.getString("title"));
                                                                videoDetails.setVideoDesc(jsonsnippet.getString("description"));
                                                                videoDetails.setVideoId(videoid);

                                                                videoDetailsArrayList.add(videoDetails);
                                                            }


                                                        }

                                                            adapter.notifyItemInserted(videoDetailsArrayList.size());

                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                    adapter.setLoaded();
                                                }
                                            }, new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    Snackbar.make(VideoYou.this.findViewById(android.R.id.content),
                                                            "Some error occurred.Please try after some time.", Snackbar.LENGTH_LONG).show();
                                                }
                                            });
                                            AppController.getInstance().addToRequestQueue(stringRequest);
                                        }
                                    },2000);
                                }
                                else{
                                    Snackbar.make(VideoYou.this.findViewById(android.R.id.content),
                                            "No more content to show", Snackbar.LENGTH_LONG).show();
                                }
                            }
                            else{
                                videoDetailsArrayList.remove(videoDetailsArrayList.size() - 1);
                                adapter.notifyItemRemoved(videoDetailsArrayList.size());
                                noint.setVisibility(View.VISIBLE);
                                nointernet.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if(isNetworkAvailable()){
                                            noint.setVisibility(View.GONE);
                                            onLoadMore();
                                        }
                                    }
                                });
                            }

                        }
                    });
                    Log.d("videoDetailsArrayList", String.valueOf(videoDetailsArrayList.size()));


                    // create an Object for Adapter

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Snackbar.make(VideoYou.this.findViewById(android.R.id.content),
                        "Some error occurred.Please try after some time.", Snackbar.LENGTH_LONG).show();
            }
        });
        AppController.getInstance().addToRequestQueue(str);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) VideoYou.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
