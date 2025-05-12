package com.example.bookdiary;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.concurrent.Executors;

public class AddSessionDialogFragment extends DialogFragment {

    private long bookId;

    public static AddSessionDialogFragment newInstance(long bookId) {
        AddSessionDialogFragment fragment = new AddSessionDialogFragment();
        Bundle args = new Bundle();
        args.putLong("BOOK_ID", bookId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (getArguments() != null) {
            bookId = getArguments().getLong("BOOK_ID");
        }

        // Inflar la vista del diálogo
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_add_session, null);

        // Inicializar los componentes
        Spinner statusSpinner = view.findViewById(R.id.statusSpinner);
        EditText startDatePicker = view.findViewById(R.id.startDatePicker);
        EditText endDatePicker = view.findViewById(R.id.endDatePicker);
        EditText notesEditText = view.findViewById(R.id.notesEditText);
        RatingBar ratingBar = view.findViewById(R.id.ratingBar);
        Button saveButton = view.findViewById(R.id.saveSessionButton);

        // Configurar el Spinner para los estados de la sesión
        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(
                getActivity(),
                R.array.book_status,
                android.R.layout.simple_spinner_item
        );
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(statusAdapter);

        // Crear un AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle(getString(R.string.add_reading_session))
                .setCancelable(true);

        // Configurar el botón de guardar
        saveButton.setOnClickListener(v -> {

            // Crear una nueva sesión de lectura
            ReadingSession newSession = new ReadingSession(
                    bookId,
                    statusSpinner.getSelectedItem().toString(),
                    startDatePicker.getText().toString(),
                    endDatePicker .getText().toString(),
                    ratingBar.getRating(),
                    notesEditText.getText().toString()
            );

            // Guardar la sesión en la base de datos
            AppDatabase db = DatabaseClient.getInstance(getContext()).getAppDatabase();
            Executors.newSingleThreadExecutor().execute(() -> {
                db.readingSessionDao().insert(newSession);
                requireActivity().runOnUiThread(() -> {
                    // Cerrar el diálogo
                    dismiss();
                    // Actualizar las sesiones
                    ((BookDetailFragment) getTargetFragment()).loadSessions();
                });
            });
        });

        return builder.create();
    }
}
