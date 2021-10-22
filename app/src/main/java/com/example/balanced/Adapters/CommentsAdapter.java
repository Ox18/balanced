package com.example.balanced.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.balanced.Entity.Comment;
import com.example.balanced.R;

import java.util.ArrayList;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder>{
    private ArrayList<Comment> dataset;
    private Context context;

    public CommentsAdapter(){
        this.context = context;
        dataset = new ArrayList<>();
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
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public void adicionarLista(ArrayList<Comment> comments){
        dataset.clear();
        dataset.addAll(comments);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        Context context;
        private TextView commentario;
        private TextView nombre;
        private TextView letter;
        private RatingBar ratingBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            context = itemView.getContext();
            letter = itemView.findViewById(R.id.letter);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            commentario = itemView.findViewById(R.id.commentario);
            nombre = itemView.findViewById(R.id.nombre);
        }
    }
}
