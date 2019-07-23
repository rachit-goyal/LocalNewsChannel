package com.gmaxmart.tajtodaynews;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


public class DataAdapter extends RecyclerView.Adapter {
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private List<res> studentList;
    static Context context;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;


    private OnLoadMoreListener onLoadMoreListener;


    public DataAdapter(ArrayList<res> students, RecyclerView recyclerView) {

        studentList = students;

        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
                    .getLayoutManager();


            recyclerView
                    .addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(RecyclerView recyclerView,
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

        return studentList.get(position) != null ? VIEW_ITEM : VIEW_PROG;


    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        context = parent.getContext();
        RecyclerView.ViewHolder vh = null;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.single_resulted, parent, false);

            vh = new StudentViewHolder(v);
        } else {
            if (isNetworkAvailable()) {
                View v = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.progress_item, parent, false);

                vh = new ProgressViewHolder(v);
            }

        }

        return vh;
    }


    private static boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof StudentViewHolder) {

            res singleStudent = (res) studentList.get(position);
            if (singleStudent.getTitle().length() > 50) {

                ((StudentViewHolder) holder).title.setText(Html.fromHtml(singleStudent.getTitle().substring(0, 50) + "..."));
            } else {

                ((StudentViewHolder) holder).title.setText(Html.fromHtml(singleStudent.getTitle()));
            }
            DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date date = null;
            try {
                date = sdf.parse(singleStudent.getTime());
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                //long val=date.getTime();
                Log.d("valuedate", sdf.format(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
            DateFormat targetFormat = new SimpleDateFormat("dd MMMM yyyy");
            try {
                Date date1 = originalFormat.parse(sdf.format(date));
                Log.d("valuedate", targetFormat.format(date1));
                String valuetime = targetFormat.format(date1);
                ((StudentViewHolder) holder).date.setText(valuetime);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (singleStudent.getContent().length() > 40) {

                ((StudentViewHolder) holder).desc.setText(Html.fromHtml(singleStudent.getContent().substring(0, 40) + "..."));
            } else {

                ((StudentViewHolder) holder).desc.setText(Html.fromHtml(singleStudent.getContent()));
            }
            if (singleStudent.getYoutube_id() == null) {
                ((StudentViewHolder) holder).img_overlay.setVisibility(View.GONE);
            } else {
                ((StudentViewHolder) holder).img_overlay.setVisibility(View.VISIBLE);
            }

            //((StudentViewHolder) holder).author_name.setText(singleStudent.getAuthor_name());
            if (studentList.get(position).getImag() == null) {
                ((StudentViewHolder) holder).img.setImageDrawable(null);
                ((StudentViewHolder) holder).img.setImageResource(R.drawable.img);

            } else if (studentList.get(position).getImag() != null) {
                Picasso.with(((StudentViewHolder) holder).img.getContext()).load(studentList.get(position).getImag()).placeholder(R.drawable.app_logo).resize(250, 250).into(((StudentViewHolder) holder).img);
            }

            ((StudentViewHolder) holder).student = singleStudent;

        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
            if (studentList.size() < 10) {
                ((ProgressViewHolder) holder).progressBar.setVisibility(View.GONE);
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
        public TextView title;
        public TextView desc;
        //public TextView author_name;
        public TextView date;
        public ImageView img, img_overlay;

        public res student;

        public StudentViewHolder(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.title1);
            desc = (TextView) v.findViewById(R.id.desc);
            //author_name=(TextView)v.findViewById(R.id.aut_name);
            img_overlay = (ImageView) v.findViewById(R.id.VideoPreviewPlayButton);
            date = (TextView) v.findViewById(R.id.date);
            img = (ImageView) v.findViewById(R.id.img);
            v.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (isNetworkAvailable()) {
                        if (student.getYoutube_id() != null) {
                            Intent intent = new Intent(v.getContext(), Post.class);
                            intent.putExtra("id", student.getId());
                            intent.putExtra("authorname", student.getAuthor_name());
                            intent.putExtra("position", getAdapterPosition());
                            intent.putExtra("category", student.getCategory());
                            intent.putExtra("youtb", student.getYoutube_id());
                            intent.putExtra("fragname", student.getFrag_name());
                            v.getContext().startActivity(intent);
                        } else {
                            Intent intent = new Intent(v.getContext(), Slidertest.class);
                            intent.putExtra("id", student.getId());
                            intent.putExtra("authorname", student.getAuthor_name());
                            intent.putExtra("position", getAdapterPosition());
                            intent.putExtra("category", student.getCategory());
                            intent.putExtra("fragname", student.getFrag_name());
                            intent.putExtra("youtb", "abc");
                            Log.d("position", String.valueOf(getAdapterPosition()));


                            v.getContext().startActivity(intent);
                        }
                    } else {
                        Toast.makeText(context, "No network", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }


    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;


        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar1);
        }
    }
}
