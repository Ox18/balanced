package com.example.balanced.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.balanced.Activities.CursoDetalleActivity;
import com.example.balanced.Entity.Course;
import com.example.balanced.R;

import java.util.ArrayList;

public class PreviewCourseAdapter extends RecyclerView.Adapter<PreviewCourseAdapter.ViewHolder> {
    private ArrayList<Course> dataset;
    private Context context;

    public PreviewCourseAdapter(){
        this.context = context;
        dataset = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_preview_course, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Course course = dataset.get(position);
        holder.txtNombreDeCurso.setText(course.name);
        holder.txtRate.setText(course.rate);
        holder.txtTime.setText(course.time);
        holder.txtPriceAditional.setText(course.priceAditional);
        holder.txtNameProfesional.setText(course.profesionalName);
        holder.txtStatusCourse.setText(course.state);
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.placeholder(R.drawable.ic_launcher_background);
        holder.id = course.id;
        Glide.with(holder.imagePreview.getContext())
                .load(course.image)
                .centerCrop()
                .apply(requestOptions)
                .into(holder.imagePreview);
        holder.setOnClickListener();
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public void adicionarLista(ArrayList<Course> courses){
        dataset.addAll(courses);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        Context context;
        private String id;
        private TextView txtNombreDeCurso;
        private ImageView imagePreview;
        private TextView txtRate;
        private TextView txtPriceAditional;
        private TextView txtTime;
        private TextView txtNameProfesional;
        private TextView txtStatusCourse;
        private LinearLayout LinearLayoutCourse;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            LinearLayoutCourse = itemView.findViewById(R.id.LinearLayoutCourse);
            txtStatusCourse = itemView.findViewById(R.id.txtStatusCourse);
            txtNameProfesional = itemView.findViewById(R.id.txtNameProfesional);
            txtPriceAditional = itemView.findViewById(R.id.txtPriceAditional);
            txtRate = itemView.findViewById(R.id.txtRate);
            txtTime = itemView.findViewById(R.id.txtTime);
            imagePreview = itemView.findViewById(R.id.imagePreview);
            txtNombreDeCurso = itemView.findViewById(R.id.txtNombreDeCurso);
        }

        void setOnClickListener(){
            LinearLayoutCourse.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.LinearLayoutCourse:
                    Intent intent = new Intent(context, CursoDetalleActivity.class);
                    intent.putExtra("id", id);
                    context.startActivity(intent);
                    ((Activity)context).finish();
                    break;
            }
        }
    }
}
