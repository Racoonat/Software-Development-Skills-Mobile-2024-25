package com.example.bookdiary;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.content.res.Configuration;
import androidx.appcompat.app.AlertDialog;


import androidx.fragment.app.Fragment;

import java.util.Locale;
import java.util.concurrent.Executors;

public class SettingsFragment extends Fragment {

    public SettingsFragment() {
        // Constructor vacío
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        // Botón para cambiar el idioma
        Button changeLanguageButton = rootView.findViewById(R.id.changeLanguageButton);
        changeLanguageButton.setOnClickListener(v -> showLanguageSelectionDialog());

        // Botón para borrar todos los datos
        Button deleteAllDataButton = rootView.findViewById(R.id.deleteAllDataButton);
        deleteAllDataButton.setOnClickListener(v -> showDeleteConfirmationDialog());

        return rootView;
    }

    private void changeLanguage(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            config.setLocales(new android.os.LocaleList(locale));
        } else {
            config.locale = locale; // solo para compatibilidad con versiones antiguas
        }

        getContext().getResources().updateConfiguration(config,
                getContext().getResources().getDisplayMetrics());

        // Guardar preferencia si quieres recordar idioma
        SharedPreferences prefs = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
        prefs.edit().putString("app_language", languageCode).apply();

        requireActivity().recreate(); // Recrea para aplicar el cambio
    }


    private void showLanguageSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.select_language))
                .setItems(new String[]{"Español", "English", "Suomi"}, (dialog, which) -> {
                    switch (which) {
                        case 0: changeLanguage("es"); break;
                        case 1: changeLanguage("en"); break;
                        case 2: changeLanguage("fi"); break;
                    }
                })

                .show();
    }



    private void showDeleteConfirmationDialog() {
        new android.app.AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.confirm_delete_title))
                .setMessage(getString(R.string.confirm_delete_msg))
                .setPositiveButton(getString(R.string.yes), (dialog, which) -> deleteAllData())
                .setNegativeButton(getString(R.string.no), null)
                .show();
    }

    private void deleteAllData() {
        // Aquí eliminarías los datos, por ejemplo, eliminando todos los libros y sesiones de lectura
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = DatabaseClient.getInstance(getContext()).getAppDatabase();
            db.bookDao().deleteAllBooks();
            db.readingSessionDao().deleteAllReadingSessions();

            // Mostrar un mensaje confirmando la eliminación
            requireActivity().runOnUiThread(() -> {
                Toast.makeText(getContext(), getString(R.string.all_data_deleted), Toast.LENGTH_SHORT).show();
            });
        });
    }
}

