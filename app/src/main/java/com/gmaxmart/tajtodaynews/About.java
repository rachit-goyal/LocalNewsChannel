package com.gmaxmart.tajtodaynews;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class About extends AppCompatActivity {
    TextView text;
    Toolbar toolbar;
    private ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        dialog = new ProgressDialog(About.this);
        text=(TextView)findViewById(R.id.nocontent);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        if(isNetworkAvailable()){
            getdata();
        }
        else{
            Toast.makeText(About.this,"No Network",Toast.LENGTH_SHORT).show();
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void getdata() {
        this.dialog.setMessage("Please Wait");
        this.dialog.show();
        String url="http://tajtodaynews.com/wp-json/wp/v2/pages/546";
        StringRequest str=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(dialog.isShowing()) {
                    dialog.dismiss();
                }
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    JSONObject jsonObject1=jsonObject.getJSONObject("content");
                    String data=jsonObject1.getString("rendered");
                    text.setText(Html.fromHtml(data));
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(dialog.isShowing()){
                    dialog.dismiss();
                }
                if(error instanceof NoConnectionError){
                    text.setVisibility(View.GONE);
                    Toast.makeText(About.this,"No Network",Toast.LENGTH_SHORT).show();

                }
                else{
                    Toast.makeText(About.this,"Some error occurred please try after some time.",Toast.LENGTH_SHORT).show();
                }
            }
        });
        AppController.getInstance().addToRequestQueue(str);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) About.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
