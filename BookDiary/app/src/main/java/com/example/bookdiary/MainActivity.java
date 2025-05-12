package com.example.bookdiary;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    private final Fragment bookListFragment = new BookListFragment();
    private final Fragment addBookFragment = new AddBookFragment();
    private final Fragment settingsFragment = new SettingsFragment();
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNav = findViewById(R.id.bottom_navigation);

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selected = null;
            int id = item.getItemId();

            if (id == R.id.nav_one) {
                selected = bookListFragment;
            } else if (id == R.id.nav_two) {
                selected = addBookFragment;
            } else if (id == R.id.nav_three) {
                selected = settingsFragment;
            }

            if (selected != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selected)
                        .commit();
            }

            return true;
        });

        // Mostrar el primero al inicio
        bottomNav.setSelectedItemId(R.id.nav_one);


    }

    // Método para actualizar el ítem seleccionado en el BottomNavigationView
    public void updateBottomNavigationSelection(int itemId) {
        bottomNav.setSelectedItemId(itemId);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences prefs = newBase.getSharedPreferences("settings", Context.MODE_PRIVATE);
        String lang = prefs.getString("app_language", "en");

        Locale locale = new Locale(lang);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            config.setLocales(new android.os.LocaleList(locale));
        } else {
            config.locale = locale;
        }

        Context context = newBase.createConfigurationContext(config);
        super.attachBaseContext(context);
    }
}
