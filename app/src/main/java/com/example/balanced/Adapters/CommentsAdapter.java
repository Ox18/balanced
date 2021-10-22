package com.example.balanced.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.balanced.Entity.Comment;
import com.example.balanced.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder>{
    private ArrayList<Comment> dataset;
    private Context context;
    private String uid;
    private String courseID;
    private DatabaseReference mDatabase;

    public CommentsAdapter(){
        this.context = context;
        dataset = new ArrayList<>();
        uid = "";
        courseID = "";
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @NonNull
    @Override
    public CommentsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_comment, parent, false);
        return new CommentsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsAdapter.ViewHolder holder, int position) {
        Comment comment = dataset.get(position);
        holder.commentario.setText(comment.comment);
        holder.nombre.setText(comment.author);
        holder.ratingBar.setRating(Float.parseFloat(comment.rating));
        holder.letter.setText(comment.author.substring(0, 1).toUpperCase());

        if(comment.userID.compareTo(this.uid) == 0){
            holder.btnDelete.setVisibility(View.VISIBLE);
        }else{
            holder.btnDelete.setVisibility(View.GONE);
        }
        holder.commentID = comment.id;
        holder.setOnClickListener();
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public void addUID(String uid){
        this.uid = uid;
    }

    public void addCourseID(String courseID){
        this.courseID = courseID;
    }

    public void adicionarLista(ArrayList<Comment> comments){
        dataset.clear();
        dataset.addAll(comments);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        Context context;
        private TextView commentario;
        private TextView nombre;
        private TextView letter;
        private RatingBar ratingBar;
        private Button btnDelete;
        private String commentID;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            context = itemView.getContext();
            commentID = "";
            btnDelete = itemView.findViewById(R.id.btnDeleteComment);
            letter = itemView.findViewById(R.id.letter);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            commentario = itemView.findViewById(R.id.commentario);
            nombre = itemView.findViewById(R.id.nombre);
        }

        void setOnClickListener(){
            btnDelete.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btnDeleteComment:
                    DeleteComment(commentID);
                    break;
            }
        }
    }

    private void DeleteComment(String commentID){
        mDatabase
                .child("Courses")
                .child(this.courseID)
                .child("Comments")
                .child(commentID)
                .removeValue();
    }
}
