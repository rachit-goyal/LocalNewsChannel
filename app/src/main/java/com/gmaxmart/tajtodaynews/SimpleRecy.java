package com.gmaxmart.tajtodaynews;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by RACHIT GOYAL on 8/25/2017.
 */

public class SimpleRecy extends RecyclerView.Adapter<SimpleRecy.MyViewHolder> {

    private ArrayList<res> dataSet;
    Context context;


    public SimpleRecy(ArrayList<res> data, Context context) {
        this.dataSet = data;
        this.context=context;
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView content;
        TextView date;
        ImageView img;

        public MyViewHolder(View itemView) {
            super(itemView);
           this.title = (TextView) itemView.findViewById(R.id.title1);
            this.content = (TextView) itemView.findViewById(R.id.desc);
            this.date = (TextView) itemView.findViewById(R.id.date);
            this.img = (ImageView) itemView.findViewById(R.id.img);
        }
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_resulted, parent, false);

       // view.setOnClickListener(MainActivity.myOnClickListener);

        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {
        if(dataSet.get(listPosition).getTitle().length()>100) {
            holder.title.setText(Html.fromHtml(dataSet.get(listPosition).getTitle().substring(0, 100)+"..."));
        }
        else{
            holder.title.setText(Html.fromHtml(dataSet.get(listPosition).getTitle()));
        }
        if(dataSet.get(listPosition).getContent().length()>100) {
            holder.content.setText(Html.fromHtml(dataSet.get(listPosition).getContent().substring(0, 100)+"..."));
        }
        else{
            holder.content.setText(Html.fromHtml(dataSet.get(listPosition).getContent()));
        }
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(dataSet.get(listPosition).getTime());
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
        holder.date.setText(valuetime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String photoPath = context.getFilesDir().getPath()+"/tajtodaynews/"+dataSet.get(listPosition).getImag();
        Bitmap b = BitmapFactory.decodeFile(photoPath);
        if(b==null){
            holder.img.setImageResource(R.drawable.img);
        }
        else{
            holder.img.setImageBitmap(b);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), LocalNEws.class);
                intent.putExtra("id", String.valueOf(dataSet.get(listPosition).getLocalid()));
                view.getContext().startActivity(intent);
                //Toast.makeText(context,String.valueOf(dataSet.get(listPosition).getLocalid()),Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
