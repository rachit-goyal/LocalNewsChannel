package com.gmaxmart.tajtodaynews;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Interview extends Fragment {
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
    LinearLayout noint;
    TextView nointernet;
    String you_id,author_name,link,con;
    static  int i=0;
    private DataAdapter adapter;
    private TextView textView;
    RelativeLayout relativeLayout;
    private String JSON_STRING;
    ProgressBar progressBar;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    ArrayList<res> contactArrayList;
    String ttl,image,time,description;
    ArrayList data;
    Map<String, Object> post_meta;
    protected Handler handler;
    String[]postTitle;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        i=0;
        View rootView = inflater.inflate(R.layout.lifestyle_tab_layout,container,false);
        textView=(TextView)rootView.findViewById(R.id.nocontent);
        contactArrayList = new ArrayList<res>();
        handler = new Handler();
        progressBar=(ProgressBar)rootView.findViewById(R.id.pro);
        noint=(LinearLayout)rootView.findViewById(R.id.noint);
        nointernet=(TextView)rootView.findViewById(R.id.noin);
        relativeLayout=(RelativeLayout)rootView.findViewById(R.id.relate);
        recyclerView = (RecyclerView)rootView.findViewById(R.id.recyler);
        if (recyclerView != null) {
            recyclerView.setHasFixedSize(true);
        }
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        if (isNetworkAvailable()) {
            loadData();

        } else {
            Toast.makeText(getActivity(), "No Network", Toast.LENGTH_SHORT).show();
        }

        return rootView;
    }

    private void loadData() {
        getdata();
    }

    private void getdata() {
        i++;
        progressBar.setVisibility(View.VISIBLE);

        JSON_STRING = "http://tajtodaynews.com/wp-json/wp/v2/posts/?page="+i+"&categories=18";
        StringRequest request = new StringRequest(Request.Method.GET, JSON_STRING, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                progressBar.setVisibility(View.GONE);

                gson = new Gson();
                list = (List) gson.fromJson(s, List.class);
                postTitle = new String[list.size()];
                for(int i=0;i<list.size();++i){
                    res res=new res();
                    mapPost = (Map<String,Object>)list.get(i);
                    guild=(Map<String, Object>) mapPost.get("guid");
                    link=(String )guild.get("rendered");
                    mapTitle = (Map<String, Object>) mapPost.get("title");
                    ttl = (String) mapTitle.get("rendered");
                    mapdesc = (Map<String, Object>) mapPost.get("content");
                    description = (String) mapdesc.get("rendered").toString();
                    img=(Map<String, Object>) mapPost.get("better_featured_image");
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
                    mapid=((Double)mapPost.get("id")).intValue();
                    if(img!=null) {
                        image = (String) img.get("source_url");
                    }
                    else{
                        image=null;
                    }

                    time=(String)mapPost.get("date_gmt");
                    res.setId(String.valueOf(mapid));
                    res.setImag(image);
                    res.setTime(time);
                    res.setTitle(ttl);
                    res.setLink(link);
                    res.setCategory("18");
                    res.setFrag_name("इंटरव्यू");
                    res.setContent(description);

                    if(you_id!=null) {
                        res.setYoutube_id(you_id);
                    }
                    contactArrayList.add(res);
                }
                if(contactArrayList.size()==0){
                    textView.setVisibility(View.VISIBLE);
                }
                else {
                    recyclerView.setHasFixedSize(true);

                    layoutManager = new LinearLayoutManager(getContext());

                    // use a linear layout manager
                    recyclerView.setLayoutManager(layoutManager);

                    // create an Object for Adapter
                    adapter = new DataAdapter(contactArrayList, recyclerView);

                    // set the adapter object to the Recyclerview
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();


                    if (contactArrayList.isEmpty()) {
                        recyclerView.setVisibility(View.GONE);
                        textView.setVisibility(View.VISIBLE);

                    } else {
                        recyclerView.setVisibility(View.VISIBLE);
                        textView.setVisibility(View.GONE);
                    }

                    adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
                        @Override
                        public void onLoadMore() {
                            if(isNetworkAvailable()){
                                //add null , so the adapter will check view_type and show progress bar at bottom
                                contactArrayList.add(null);
                                adapter.notifyItemInserted(contactArrayList.size() - 1);

                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        i++;
                                        //   remove progress item

                                        JSON_STRING = "http://tajtodaynews.com/wp-json/wp/v2/posts/?page="+i+"&categories=18";
                                        StringRequest request = new StringRequest(Request.Method.GET, JSON_STRING, new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String s) {
                                                contactArrayList.remove(contactArrayList.size() - 1);
                                                adapter.notifyItemRemoved(contactArrayList.size());
                                                gson = new Gson();
                                                list = (List) gson.fromJson(s, List.class);
                                                postTitle = new String[list.size()];
                                                for (int i = 0; i < list.size(); ++i) {
                                                    res res = new res();
                                                    mapPost = (Map<String, Object>) list.get(i);
                                                    guild=(Map<String, Object>) mapPost.get("guid");
                                                    link=(String )guild.get("rendered");
                                                    mapTitle = (Map<String, Object>) mapPost.get("title");
                                                    ttl = (String) mapTitle.get("rendered");
                                                    mapdesc = (Map<String, Object>) mapPost.get("content");
                                                    description = (String) mapdesc.get("rendered").toString();
                                                    img = (Map<String, Object>) mapPost.get("better_featured_image");
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
                                                    mapid=((Double)mapPost.get("id")).intValue();
                                                    if (img != null) {
                                                        image = (String) img.get("source_url");
                                                    } else {
                                                        image = null;
                                                    }

                                                    time = (String) mapPost.get("date_gmt");
                                                    res.setId(String.valueOf(mapid));
                                                    res.setImag(image);
                                                    res.setTime(time);
                                                    res.setTitle(ttl);
                                                    res.setCategory("18");
                                                    res.setFrag_name("इंटरव्यू");
                                                    res.setLink(link);
                                                    if(you_id!=null) {
                                                        res.setYoutube_id(you_id);
                                                    }
                                                    res.setContent(description);
                                                    contactArrayList.add(res);
                                                    adapter.notifyItemInserted(contactArrayList.size());
                                                }
                                                adapter.setLoaded();
                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError volleyError) {
                                                contactArrayList.remove(contactArrayList.size() - 1);
                                                adapter.notifyItemRemoved(contactArrayList.size());
                                                if(volleyError instanceof NoConnectionError){
                                                    if(isAdded()) {
                                                        // Toast.makeText(getActivity(), "No Network",Toast.LENGTH_SHORT).show();
                                                        contactArrayList.remove(contactArrayList.size() - 1);
                                                        adapter.notifyItemRemoved(contactArrayList.size());
                                                        //Toast.makeText(getActivity(), "No Network",Toast.LENGTH_SHORT).show();
                                                        noint.setVisibility(View.VISIBLE);
                                                        nointernet.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) {
                                                                if (isNetworkAvailable()) {
                                                                    i--;
                                                                    noint.setVisibility(View.GONE);
                                                                    onLoadMore();
                                                                }
                                                            }
                                                        });

                                                    }
                                                }
                                                NetworkResponse response = volleyError.networkResponse;
                                                if (volleyError instanceof ServerError && response != null) {
                                                    try {
                                                        String res = new String(response.data,
                                                                HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                                                        // Now you can use any deserializer to make sense of data
                                                        JSONObject obj = new JSONObject(res);
                                                    } catch (UnsupportedEncodingException e1) {
                                                        // Couldn't properly decode data to string
                                                        e1.printStackTrace();
                                                    } catch (JSONException e2) {
                                                        // returned data is not JSONObject?
                                                        e2.printStackTrace();
                                                    }

                                                }
                                                if(volleyError instanceof TimeoutError){
                                                    if(isAdded()) {
                                                        Toast.makeText(getActivity(), "You are on slow network", Toast.LENGTH_SHORT).show();

                                                    }
                                                }

                                            }
                                        }){
                                            @Override
                                            public Map<String, String> getHeaders() throws AuthFailureError {
                                                HashMap<String, String> headers = new HashMap<String, String>();

                                                headers.put("Content-Type", "application/json; charset=utf-8");
                                                return headers;
                                            }
                                        };
                                        request.setRetryPolicy(new DefaultRetryPolicy(
                                                MY_SOCKET_TIMEOUT_MS,
                                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                                        AppController.getInstance().addToRequestQueue(request);
                                    }
                                }, 2000);
                            }
                            else{
                                contactArrayList.remove(contactArrayList.size() - 1);
                                adapter.notifyItemRemoved(contactArrayList.size());
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
                }




            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                progressBar.setVisibility(View.GONE);

                if(volleyError instanceof NoConnectionError){
                    if(isAdded()) {
                        Toast.makeText(getActivity(), "No Network",Toast.LENGTH_SHORT).show();

                    }
                }
                NetworkResponse response = volleyError.networkResponse;
                if (volleyError instanceof ServerError && response != null) {
                    try {
                        String res = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        // Now you can use any deserializer to make sense of data
                        JSONObject obj = new JSONObject(res);
                    } catch (UnsupportedEncodingException e1) {
                        // Couldn't properly decode data to string
                        e1.printStackTrace();
                    } catch (JSONException e2) {
                        // returned data is not JSONObject?
                        e2.printStackTrace();
                    }

                }
                if(volleyError instanceof TimeoutError){
                    if(isAdded()) {
                        Toast.makeText(getActivity(), "You are on slow network", Toast.LENGTH_SHORT).show();

                    }
                }

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(request);

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}

