package com.gmaxmart.tajtodaynews;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class VidAdapter extends RecyclerView.Adapter<VidAdapter.VideoInfoHolder> {

    //these ids are the unique id for each video
    ArrayList<res> data;
    Context ctx;

    public VidAdapter(Context context, ArrayList<res> resArrayList) {
        this.ctx = context;
        this.data=resArrayList;
    }

    @Override
    public VideoInfoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sinngle_video, parent, false);
        return new VideoInfoHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final VideoInfoHolder holder, final int position) {


        final YouTubeThumbnailLoader.OnThumbnailLoadedListener  onThumbnailLoadedListener = new YouTubeThumbnailLoader.OnThumbnailLoadedListener(){
            @Override
            public void onThumbnailError(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader.ErrorReason errorReason) {

            }

            @Override
            public void onThumbnailLoaded(YouTubeThumbnailView youTubeThumbnailView, String s) {
                youTubeThumbnailView.setVisibility(View.VISIBLE);
                holder.relativeLayoutOverYouTubeThumbnailView.setVisibility(View.VISIBLE);
            }
        };
        if(data.get(position).getTitle().length()>50){
            holder.content.setText(Html.fromHtml(data.get(position).getTitle().substring(0,50)+"..."));

        }
        else{
            holder.content.setText(Html.fromHtml(data.get(position).getTitle()));
        }
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(data.get(position).getTime());
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
        DateFormat targetFormat = new SimpleDateFormat("dd MMMM yyyy");
        try {
            Date date1 = originalFormat.parse(sdf.format(date));
            Log.d("valuedate", targetFormat.format(date1));
            String valuetime = targetFormat.format(date1);
            holder.time.setText(valuetime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.tit.setText(Html.fromHtml(data.get(position).getContent()));
        holder.tit.setMovementMethod(LinkMovementMethod.getInstance());
        holder.tit.setText(Html.fromHtml(data.get(position).getContent(), new URLImageParser(holder.tit, ctx), null));


        holder.youTubeThumbnailView.initialize(PlayerConfig.API_KEY, new YouTubeThumbnailView.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader youTubeThumbnailLoader) {

                youTubeThumbnailLoader.setVideo(data.get(position).getYoutube_id());
                youTubeThumbnailLoader.setOnThumbnailLoadedListener(onThumbnailLoadedListener);
            }

            @Override
            public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {
                //write something for failure
            }
        });

        if(holder.getAdapterPosition()==0){
            holder.more.setVisibility(View.VISIBLE);
        }
        else{
            holder.more.setVisibility(View.GONE);
        }
        if(data.size()==1){
            holder.more.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class VideoInfoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected RelativeLayout relativeLayoutOverYouTubeThumbnailView;
        YouTubeThumbnailView youTubeThumbnailView;
        protected ImageView playButton;
        protected TextView more;
        ImageView share;
        TextView time;
        protected TextView content;
        protected TextView tit;

        public VideoInfoHolder(View itemView) {
            super(itemView);
            playButton=(ImageView)itemView.findViewById(R.id.btnYoutube_player);
            more=(TextView)itemView.findViewById(R.id.morevid);
            playButton.setOnClickListener(this);
            time=(TextView)itemView.findViewById(R.id.time1);
            share=(ImageView)itemView.findViewById(R.id.imgshare);
            content=(TextView)itemView.findViewById(R.id.vidcontent);
            tit=(TextView)itemView.findViewById(R.id.tit);


            relativeLayoutOverYouTubeThumbnailView = (RelativeLayout) itemView.findViewById(R.id.relativeLayout_over_youtube_thumbnail);
            youTubeThumbnailView = (YouTubeThumbnailView) itemView.findViewById(R.id.youtube_thumbnail);

            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, data.get(getAdapterPosition()).getLink()+"\nShare by Taj Today News app");
                    ctx.startActivity(Intent.createChooser(shareIntent, "Share Via..."));
                }
            });

        }

        @Override
        public void onClick(View v) {


            Intent intent = YouTubeStandalonePlayer.createVideoIntent((Activity) ctx, PlayerConfig.API_KEY, data.get(getAdapterPosition()).getYoutube_id(),100,     //after this time, video will start automatically
                    true,               //autoplay or not
                    false);
            ctx.startActivity(intent);

        }
    }
}