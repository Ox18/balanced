package com.example.balanced.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.balanced.Entity.Course;
import com.example.balanced.Entity.MyCoursePreview;
import com.example.balanced.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class PreviewMyCoursesAdapter extends RecyclerView.Adapter<PreviewMyCoursesAdapter.ViewHolder> {
    private ArrayList<MyCoursePreview> dataset;
    private Context context;

    public PreviewMyCoursesAdapter(){
        this.context = context;
        dataset = new ArrayList<>();
    }

    @NonNull
    @Override
    public PreviewMyCoursesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.preview_card_my_course, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MyCoursePreview myCoursePreview = dataset.get(position);
        holder.txtNameCourse.setText(myCoursePreview.name);

        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.placeholder(R.drawable.ic_launcher_background);

        Glide.with(holder.imageCourse.getContext())
                .load(myCoursePreview.image)
                .centerCrop()
                .apply(requestOptions)
                .into(holder.imageCourse);
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public void adicionarLista(ArrayList<MyCoursePreview> MyCoursesPreview){
        dataset.addAll(MyCoursesPreview);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        Context context;
        private ImageView imageCourse;
        private TextView txtNameCourse;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            imageCourse = itemView.findViewById(R.id.imageCourse);
            txtNameCourse = itemView.findViewById(R.id.txtNameCourse);

        }

        @Override
        public void onClick(View view) {

        }
    }
}
