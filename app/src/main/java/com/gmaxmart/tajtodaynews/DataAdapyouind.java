package com.gmaxmart.tajtodaynews;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class DataAdapyouind extends RecyclerView.Adapter {
    private final int VIEW_ITEM = 1;
    private List<res> studentList;
    private Context context;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;


    private OnLoadMoreListener onLoadMoreListener;


    DataAdapyouind(ArrayList<res> students, RecyclerView recyclerView) {

        studentList = students;

        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
                    .getLayoutManager();


            recyclerView
                    .addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(@NonNull RecyclerView recyclerView,
                                               int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);

                            totalItemCount = linearLayoutManager.getItemCount();
                            lastVisibleItem = linearLayoutManager
                                    .findLastVisibleItemPosition();
                            if (!loading
                                    && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                                if (onLoadMoreListener != null) {
                                    onLoadMoreListener.onLoadMore();
                                }
                                loading = true;
                            }
                        }
                    });
        }
    }

    @Override
    public int getItemViewType(int position) {

        int VIEW_PROG = 0;
        return studentList.get(position) != null ? VIEW_ITEM : VIEW_PROG;


    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                      int viewType) {
        context = parent.getContext();
        RecyclerView.ViewHolder vh = null;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.singleyou, parent, false);

            vh = new DataAdapyouind.StudentViewHolder(v);
        } else {
            if (isNetworkAvailable()) {
                View v = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.progress_item, parent, false);

                vh = new DataAdapyouind.ProgressViewHolder(v);
            }
            else{
                Toast.makeText(context,"No Network",Toast.LENGTH_SHORT).show();
            }

        }
        return vh;
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof DataAdapyouind.StudentViewHolder) {

            res singleStudent = (res) studentList.get(position);
            final YouTubeThumbnailLoader.OnThumbnailLoadedListener  onThumbnailLoadedListener = new YouTubeThumbnailLoader.OnThumbnailLoadedListener(){
                @Override
                public void onThumbnailError(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader.ErrorReason errorReason) {

                }

                @Override
                public void onThumbnailLoaded(YouTubeThumbnailView youTubeThumbnailView, String s) {
                    youTubeThumbnailView.setVisibility(View.VISIBLE);
                    ((DataAdapyouind.StudentViewHolder) holder).relativeLayoutOverYouTubeThumbnailView.setVisibility(View.VISIBLE);
                }
            };
            ((DataAdapyouind.StudentViewHolder) holder).content.setText(Html.fromHtml(studentList.get(position).getVideoDesc()));
            ((DataAdapyouind.StudentViewHolder) holder).tit.setText(Html.fromHtml(studentList.get(position).getVideoName()));

            Picasso.with(((DataAdapyouind.StudentViewHolder) holder).youTubeThumbnailView.getContext()).load(studentList.get(position).getUrl()).placeholder(R.drawable.app_logo).into(((DataAdapyouind.StudentViewHolder) holder).youTubeThumbnailView);

            ((DataAdapyouind.StudentViewHolder) holder).youTubeThumbnailView.initialize(PlayerConfig.API_KEY, new YouTubeThumbnailView.OnInitializedListener() {
                @Override
                public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader youTubeThumbnailLoader) {
                    ((DataAdapyouind.StudentViewHolder) holder).youTubeThumbnailView.setImageResource(0);
                    ((DataAdapyouind.StudentViewHolder) holder).youTubeThumbnailView.setImageDrawable(null);
                    Picasso.with(((DataAdapyouind.StudentViewHolder) holder).youTubeThumbnailView.getContext()).load(studentList.get(position).getUrl()).placeholder(R.drawable.app_logo).into(((DataAdapyouind.StudentViewHolder) holder).youTubeThumbnailView);
                    youTubeThumbnailLoader.setVideo(studentList.get(position).getVideoId());
                    youTubeThumbnailLoader.setOnThumbnailLoadedListener(onThumbnailLoadedListener);
                }

                @Override
                public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {
                    //write something for failure
                }
            });
            ((DataAdapyouind.StudentViewHolder) holder).playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = YouTubeStandalonePlayer.createVideoIntent((Activity) context, PlayerConfig.API_KEY,studentList.get(position).getVideoId(),0,true,false);
                    context.startActivity(intent);
                }
            });
            ((DataAdapyouind.StudentViewHolder) holder).student = singleStudent;

        }
        else {
            ((DataAdapyouind.ProgressViewHolder) holder).progressBar.setIndeterminate(true);
            if(studentList.size()<10){
                ((DataAdapyouind.ProgressViewHolder) holder).progressBar.setVisibility(View.GONE);
            }
        }


    }

    public void setLoaded() {
        loading = false;
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }



    //
    public static class StudentViewHolder extends RecyclerView.ViewHolder {
        protected RelativeLayout relativeLayoutOverYouTubeThumbnailView;
        YouTubeThumbnailView youTubeThumbnailView;
        ImageView playButton;
        protected TextView content;
        protected TextView tit;

        res student;

        StudentViewHolder(View v) {
            super(v);
            playButton=(ImageView)itemView.findViewById(R.id.btnYoutube_player);
            content=(TextView)itemView.findViewById(R.id.vidcontent);
            tit=(TextView)itemView.findViewById(R.id.tit);
            relativeLayoutOverYouTubeThumbnailView = (RelativeLayout) itemView.findViewById(R.id.relativeLayout_over_youtube_thumbnail);
            youTubeThumbnailView = (YouTubeThumbnailView) itemView.findViewById(R.id.youtube_thumbnail);
        }



    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;


        ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar1);
        }
    }




}
