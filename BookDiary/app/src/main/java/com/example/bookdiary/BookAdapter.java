package com.example.bookdiary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    public interface OnBookClickListener {
        void onBookClick(Book book);
    }

    private final List<BookWithSession> booksWithSessions;
    private final OnBookClickListener listener;
    private final Context context;

    public BookAdapter(List<BookWithSession> booksWithSessions, OnBookClickListener listener, Context context) {
        this.booksWithSessions = booksWithSessions;
        this.listener = listener;
        this.context = context;
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {
        ImageView coverImage;
        TextView title, author, status;
        RatingBar ratingBar;

        public BookViewHolder(View view) {
            super(view);
            coverImage = view.findViewById(R.id.bookCoverImage);
            title = view.findViewById(R.id.bookTitle);
            author = view.findViewById(R.id.bookAuthor);
            status = view.findViewById(R.id.bookStatus);
            ratingBar = view.findViewById(R.id.bookRatingBar);
        }
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        BookWithSession bookWithSession = booksWithSessions.get(position);
        Book book = bookWithSession.book;

        holder.title.setText(book.getTitle());
        holder.author.setText(book.getAuthor());

        Glide.with(holder.coverImage.getContext())
                .load(book.getCoverImage())
                .placeholder(R.drawable.default_book_cover)
                .into(holder.coverImage);

        if (bookWithSession.sessions != null && !bookWithSession.sessions.isEmpty()) {
            ReadingSession session = bookWithSession.sessions.get(0); // Usamos la primera sesión
            holder.status.setText(session.status);
            holder.ratingBar.setRating(session.rating);
        } else {
            holder.status.setText(context.getString(R.string.no_sessions));
            holder.ratingBar.setRating(0);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBookClick(book);
            }
        });
    }

    @Override
    public int getItemCount() {
        return booksWithSessions.size();
    }
}
