package com.example.bookdiary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.SessionViewHolder> {

    private List<ReadingSession> sessions;
    private Context context;
    private BookDetailFragment parentFragment;

    public SessionAdapter(List<ReadingSession> sessions, Context context, BookDetailFragment parentFragment) {
        this.sessions = sessions;
        this.context = context;
        this.parentFragment = parentFragment;
    }

    @NonNull
    @Override
    public SessionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_reading_session, parent, false);
        return new SessionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SessionViewHolder holder, int position) {
        ReadingSession session = sessions.get(position);
        holder.status.setText(session.getStatus());

        if (session.getStartDate().isEmpty()) {
            holder.startDate.setVisibility(View.GONE); // Ocultar si está vacía
        } else {
            holder.startDate.setText(context.getString(R.string.start_date)+": " + session.getStartDate());
            holder.startDate.setVisibility(View.VISIBLE);
        }

        if (session.getEndDate().isEmpty()) {
            holder.endDate.setVisibility(View.GONE);
        } else {
            holder.endDate.setText(context.getString(R.string.end_date)+": " + session.getEndDate());
            holder.endDate.setVisibility(View.VISIBLE);
        }

        if (session.getPersonalNotes().isEmpty()) {
            holder.notes.setVisibility(View.GONE);
        } else {
            holder.notes.setText(context.getString(R.string.notes)+": "+"\n" + session.getPersonalNotes());
            holder.notes.setVisibility(View.VISIBLE);
        }

        holder.ratingBar.setRating(session.getRating());
        holder.itemView.setOnClickListener(v -> {
            parentFragment.showEditDialog(session.getId());
        });
    }


    @Override
    public int getItemCount() {
        return sessions.size();
    }

    public static class SessionViewHolder extends RecyclerView.ViewHolder {
        TextView startDate, endDate, status, notes;
        RatingBar ratingBar;

        public SessionViewHolder(@NonNull View itemView) {
            super(itemView);
            startDate = itemView.findViewById(R.id.sessionStartDate);
            endDate = itemView.findViewById(R.id.sessionEndDate);
            status = itemView.findViewById(R.id.sessionStatus);
            ratingBar = itemView.findViewById(R.id.sessionRatingBar);
            notes = itemView.findViewById(R.id.sessionNotes);
        }
    }
}
