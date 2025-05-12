package com.example.bookdiary;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executors;

public class BookDetailFragment extends Fragment implements OnSessionChangedListener {
    private static final String ARG_BOOK_ID = "book_id";
    private long bookId;

    public static BookDetailFragment newInstance(long bookId) {
        BookDetailFragment fragment = new BookDetailFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_BOOK_ID, bookId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            bookId = getArguments().getLong(ARG_BOOK_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_detail, container, false);

        // Inicializar la base de datos
        AppDatabase db = DatabaseClient.getInstance(getContext()).getAppDatabase();

        // Referencias a las vistas
        TextView titleText = view.findViewById(R.id.detailTitle);
        TextView authorText = view.findViewById(R.id.detailAuthor);
        TextView pagesText = view.findViewById(R.id.detailPages);
        TextView isbnText = view.findViewById(R.id.detailIsbn);
        ImageView coverImage = view.findViewById(R.id.detailCover);
        RecyclerView sessionsRecyclerView = view.findViewById(R.id.sessionsRecyclerView);

        Button editBtn = view.findViewById(R.id.editBookButton);
        Button addSessionBtn = view.findViewById(R.id.addReadingSessionButton);

        // Configurar el RecyclerView para mostrar las sesiones de lectura
        sessionsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Executors.newSingleThreadExecutor().execute(() -> {
            // Obtener el libro por su ID
            Book book = db.bookDao().getBookById(bookId);

            // Obtener las sesiones de lectura del libro y ordenarlas de forma descendente
            List<ReadingSession> sessions = db.readingSessionDao().getReadingSessionsForBook(bookId);
            Collections.sort(sessions, new Comparator<ReadingSession>() {
                @Override
                public int compare(ReadingSession o1, ReadingSession o2) {
                    // Orden descendente, la sesión más reciente primero
                    return o2.startDate.compareTo(o1.startDate);
                }
            });

            // Obtener el adaptador para las sesiones de lectura y configurarlo en el RecyclerView
            requireActivity().runOnUiThread(() -> {
                SessionAdapter adapter = new SessionAdapter(sessions, getContext(),this);
                sessionsRecyclerView.setAdapter(adapter);

                // Mostrar los detalles del libro
                titleText.setText(book.getTitle());
                authorText.setText(book.getAuthor());
                pagesText.setText(getString(R.string.book_pages) + ": " + book.getPages());
                isbnText.setText(getString(R.string.book_isbn) + ": " + book.getIsbn());

                // Cargar la portada usando Glide
                if (!book.getCoverImage().isEmpty()) {
                    Glide.with(getContext())
                            .load(book.getCoverImage())
                            .into(coverImage);
                }

                // Configurar el botón de editar
                editBtn.setOnClickListener(v -> {
                    Fragment editFragment = EditBookFragment.newInstance(bookId);
                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, editFragment)
                            .addToBackStack(null)
                            .commit();
                });

                // Botón de añadir sesión de lectura (sin funcionalidad por ahora)
                addSessionBtn.setOnClickListener(v -> {
                    // Crear y mostrar el diálogo para añadir una nueva sesión
                    AddSessionDialogFragment dialogFragment = AddSessionDialogFragment.newInstance(bookId);
                    dialogFragment.setTargetFragment(BookDetailFragment.this, 0); // Para que pueda llamar a loadSessions()
                    dialogFragment.show(requireFragmentManager(), "AddSessionDialog");
                });

            });
        });

        return view;
    }

    // Método para recargar las sesiones después de una modificación o eliminación
    void loadSessions() {
        AppDatabase db = DatabaseClient.getInstance(getContext()).getAppDatabase();

        Executors.newSingleThreadExecutor().execute(() -> {
            // Obtener el libro por su ID
            Book book = db.bookDao().getBookById(bookId);

            // Obtener las sesiones de lectura del libro y ordenarlas de forma descendente
            List<ReadingSession> sessions = db.readingSessionDao().getReadingSessionsForBook(bookId);
            Collections.sort(sessions, new Comparator<ReadingSession>() {
                @Override
                public int compare(ReadingSession o1, ReadingSession o2) {
                    // Orden descendente, la sesión más reciente primero
                    return o2.startDate.compareTo(o1.startDate);
                }
            });

            // Actualizar el RecyclerView en el hilo principal
            requireActivity().runOnUiThread(() -> {
                SessionAdapter adapter = new SessionAdapter(sessions, getContext(),this);
                RecyclerView sessionsRecyclerView = getView().findViewById(R.id.sessionsRecyclerView);
                sessionsRecyclerView.setAdapter(adapter);
            });
        });
    }

    @Override
    public void onSessionUpdated() {
        loadSessions();
    }

    @Override
    public void onSessionDeleted(long sessionId) {
        loadSessions();
    }

    // Cuando crees el dialog:
    void showEditDialog(long sessionId) {
        EditReadingSessionDialogFragment dialog = EditReadingSessionDialogFragment.newInstance(sessionId);
        dialog.setOnSessionChangedListener(this);
        dialog.show(getParentFragmentManager(), "editDialog");
    }
}
