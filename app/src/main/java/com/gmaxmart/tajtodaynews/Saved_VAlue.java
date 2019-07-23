package com.gmaxmart.tajtodaynews;

import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

public class Saved_VAlue extends AppCompatActivity {
DatabaseHelper databaseHelper;
    ArrayList<res> resArrayList;
    Toolbar toolbar;
    TextView textView;
    private RecyclerView recyclerView;
    private SimpleRecy mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_saved__value);
        databaseHelper=new DatabaseHelper(this);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        resArrayList=new ArrayList<>();
        textView=(TextView)findViewById(R.id.nocontent);
        recyclerView = (RecyclerView) findViewById(R.id.simple_recycler_view);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        recyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        Cursor sde=   databaseHelper.getAllData();
        Log.d("data", String.valueOf(sde));
        if(sde.getCount()==0){
            textView.setVisibility(View.VISIBLE);
        }
        else {
            textView.setVisibility(View.GONE);
            while (sde.moveToNext()) {
                res res = new res();
                res.setLocalid(sde.getInt(0));
                res.setTitle(sde.getString(1));
                res.setLink(sde.getString(2));
                res.setTime(sde.getString(3));
                res.setContent(sde.getString(4));
                res.setImag(sde.getString(5));
                res.setId(sde.getString(6));
                resArrayList.add(res);
            }
            Collections.reverse(resArrayList);
                mAdapter = new SimpleRecy(resArrayList, this);
                recyclerView.setAdapter(mAdapter);
                ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
                itemTouchHelper.attachToRecyclerView(recyclerView);
        if(resArrayList.size()!=0){
            Toast.makeText(Saved_VAlue.this,"Swipe to delete", Toast.LENGTH_SHORT).show();
        }
        }


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
           // Toast.makeText(Saved_VAlue.this, "on Move", Toast.LENGTH_SHORT).show();
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
            //Toast.makeText(Saved_VAlue.this, "on Swiped ", Toast.LENGTH_SHORT).show();
            //Remove swiped item from list and notify the RecyclerView
            int position = viewHolder.getAdapterPosition();
            Integer deleterow=databaseHelper.deleteData(String.valueOf(resArrayList.get(position).getLocalid()));
            if(deleterow>0){
                Snackbar.make(findViewById(android.R.id.content), "Deleted",
                        Snackbar.LENGTH_LONG).show();
            }
            resArrayList.remove(position);
            if(resArrayList.size()==0){

                textView.setVisibility(View.VISIBLE);
            }
            else{

                textView.setVisibility(View.GONE);
            }
            mAdapter.notifyDataSetChanged();

        }
    };


}
