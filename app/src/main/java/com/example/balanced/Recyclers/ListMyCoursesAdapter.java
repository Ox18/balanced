package com.example.balanced.Recyclers;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.balanced.Activities.DetailMyCourse;
import com.example.balanced.Entity.Course;
import com.example.balanced.R;

import java.util.ArrayList;
import java.util.List;

public class ListMyCoursesAdapter extends RecyclerView.Adapter<ListMyCoursesAdapter.ViewHolder> {
    private ArrayList<Course> dataset;
    private Context context;

    public ListMyCoursesAdapter() {
        this.context = context;
        dataset = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_course, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Course course = dataset.get(position);
        holder.courseNAME.setText(course.getName());
        holder.setOnClickListener();
        holder.courseID = course.getId();
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public void adicionarListaCourses(ArrayList<Course> listCourse) {
        clearListaCourses();
        dataset.addAll(listCourse);
        notifyDataSetChanged();
    }

    private void clearListaCourses(){
        dataset.clear();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        Context context;
        private TextView courseNAME;
        private LinearLayout cardVIEW;
        private String courseID;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            context = itemView.getContext();
            courseNAME = (TextView)itemView.findViewById(R.id.courseNAME);
            cardVIEW = (LinearLayout) itemView.findViewById(R.id.cardVIEW);
        }

        void setOnClickListener(){
            cardVIEW.setOnClickListener(this);
        }

        @Override
        public void onClick(View view){
            switch (view.getId()){
                case R.id.cardVIEW:
                    Intent intent = new Intent(context, DetailMyCourse.class);
                    intent.putExtra("courseID", courseID);
                    context.startActivity(intent);
                    break;
            }
        }
    }
}