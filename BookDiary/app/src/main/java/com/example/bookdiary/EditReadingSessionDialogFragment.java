package com.example.bookdiary;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.concurrent.Executors;

public class EditReadingSessionDialogFragment extends DialogFragment {

    private static final String ARG_SESSION_ID = "session_id";
    private long sessionId;
    private OnSessionChangedListener listener;

    public void setOnSessionChangedListener(OnSessionChangedListener listener) {
        this.listener = listener;
    }

    public static EditReadingSessionDialogFragment newInstance(long sessionId) {
        EditReadingSessionDialogFragment fragment = new EditReadingSessionDialogFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_SESSION_ID, sessionId);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        sessionId = getArguments().getLong(ARG_SESSION_ID);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_reading_session, null);

        Spinner statusSpinner = view.findViewById(R.id.editStatusSpinner);
        EditText startDateInput = view.findViewById(R.id.editStartDate);
        EditText endDateInput = view.findViewById(R.id.editEndDate);
        RatingBar ratingBar = view.findViewById(R.id.editRatingBar);
        EditText notesInput = view.findViewById(R.id.editNotes);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.book_status,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(adapter);

        AppDatabase db = DatabaseClient.getInstance(getContext()).getAppDatabase();

        Executors.newSingleThreadExecutor().execute(() -> {
            ReadingSession session = db.readingSessionDao().getSessionById(sessionId);
            requireActivity().runOnUiThread(() -> {
                if (session != null) {
                    int spinnerPosition = adapter.getPosition(session.getStatus());
                    statusSpinner.setSelection(spinnerPosition);
                    startDateInput.setText(session.getStartDate());
                    endDateInput.setText(session.getEndDate());
                    ratingBar.setRating(session.getRating());
                    notesInput.setText(session.getPersonalNotes());
                }
            });
        });

        return new AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.edit_reading_session))
                .setView(view)
                .setPositiveButton(getString(R.string.save), (dialog, which) -> {
                    Executors.newSingleThreadExecutor().execute(() -> {
                        ReadingSession session = db.readingSessionDao().getSessionById(sessionId);
                        if (session != null) {
                            session.setStatus(statusSpinner.getSelectedItem().toString());
                            session.setStartDate(startDateInput.getText().toString());
                            session.setEndDate(endDateInput.getText().toString());
                            session.setRating(ratingBar.getRating());
                            session.setPersonalNotes(notesInput.getText().toString());

                            db.readingSessionDao().update(session);

                            requireActivity().runOnUiThread(() -> {
                                if (listener != null) {
                                    listener.onSessionUpdated();
                                }
                            });
                        }
                    });

                })
                .setNegativeButton(getString(R.string.delete), (dialog, which) -> {
                    Executors.newSingleThreadExecutor().execute(() -> {
                        ReadingSession session = db.readingSessionDao().getSessionById(sessionId);
                        if (session != null) {
                            db.readingSessionDao().delete(session);

                            requireActivity().runOnUiThread(() -> {
                                if (listener != null) {
                                    listener.onSessionDeleted(sessionId);
                                }
                            });
                        }
                    });
                })
                .setNeutralButton(getString(R.string.cancel), null)
                .create();
    }
}
