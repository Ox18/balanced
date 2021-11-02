package com.example.balanced.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.balanced.Activities.CursoDetalleActivity;
import com.example.balanced.Activities.LobbyProfesionalActivity;
import com.example.balanced.Activities.VideoCourseActivity;
import com.example.balanced.Entity.VideoCourseEntity;
import com.example.balanced.R;

import java.util.ArrayList;
import java.util.List;

public class ListVideosCourseAdapter extends RecyclerView.Adapter<ListVideosCourseAdapter.ViewHolder>{
    private List<VideoCourseEntity> dataset;
    private Context context;
    public Boolean active;
    public String courseID = "";
    public String userID = "";

    public ListVideosCourseAdapter(){
        this.context = context;
        dataset = new ArrayList<>();
        active = false;
    }

    @NonNull
    @Override
    public ListVideosCourseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_video_course, parent, false);
        return new ListVideosCourseAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListVideosCourseAdapter.ViewHolder holder, int position) {
        VideoCourseEntity videoCourseEntity = dataset.get(position);
        holder.txtNumber.setText(videoCourseEntity.number);
        holder.txtTitle.setText(videoCourseEntity.Title);
        holder.txtMinute.setText(videoCourseEntity.time);
        holder.videoURL = videoCourseEntity.url;
        holder.videoID = videoCourseEntity.id;
        holder.setOnClickListener();
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public void adicionarLista(List<VideoCourseEntity> videos){
        dataset.clear();
        dataset.addAll(videos);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            Context context;
            private TextView txtNumber;
            private TextView txtTitle;
            private TextView txtMinute;
            private LinearLayout llPlay;
            private String videoURL;
            private String videoID;

            public  ViewHolder(@NonNull View itemView){
                super(itemView);

                context = itemView.getContext();
                videoURL = "";
                videoID = "";
                llPlay = itemView.findViewById(R.id.llPlay);
                txtNumber = itemView.findViewById(R.id.txtNumber);
                txtTitle = itemView.findViewById(R.id.txtTitle);
                txtMinute = itemView.findViewById(R.id.txtMinute);
            }

        void setOnClickListener(){
            llPlay.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.llPlay:
                    //PlayVideo(context, videoURL);
                  Intent intent = new Intent(context, VideoCourseActivity.class);
                  intent.putExtra("videoID", videoID);
                  intent.putExtra("videoURL", videoURL);
                  intent.putExtra("courseID", courseID);
                  intent.putExtra("userID", userID);

                  context.startActivity(intent);
                  //((Activity)context).finish();
                    break;
            }
        }
    }

    private void PlayVideo(Context ctx, String videoURL){
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_show_video, null);

        builder.setView(view);

        VideoView video = view.findViewById(R.id.video);
        TextView txtNote = view.findViewById(R.id.txtNote);
        ProgressBar progressBar = view.findViewById(R.id.progressBar);

        AlertDialog dialog = builder.create();
        if(active){
            progressBar.setVisibility(View.VISIBLE);
            video.setVideoPath(videoURL);
            video.start();


            new android.os.Handler(Looper.getMainLooper()).postDelayed(
                    new Runnable() {
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                            video.setVisibility(View.VISIBLE);
                        }
                    },
                    3000);
        }else{
            txtNote.setVisibility(View.VISIBLE);
        }
        dialog.show();
    }
}
