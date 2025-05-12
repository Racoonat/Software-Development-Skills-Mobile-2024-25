package com.example.bookdiary;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.example.bookdiary.AppDatabase;
import com.example.bookdiary.Book;
import com.example.bookdiary.BookListFragment;
import com.example.bookdiary.DatabaseClient;

import java.util.concurrent.Executors;

public class EditBookFragment extends Fragment {

    private static final String ARG_BOOK_ID = "book_id";
    private long bookId;
    private static final int PICK_IMAGE_REQUEST = 1;

    public EditBookFragment() {}

    public static EditBookFragment newInstance(long bookId) {
        EditBookFragment fragment = new EditBookFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_BOOK_ID, bookId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_book, container, false);

        if (getArguments() != null) {
            bookId = getArguments().getLong(ARG_BOOK_ID);
        }

        AppDatabase db = DatabaseClient.getInstance(getContext()).getAppDatabase();

        Executors.newSingleThreadExecutor().execute(() -> {
            Book book = db.bookDao().getBookById(bookId);

            requireActivity().runOnUiThread(() -> {
                EditText titleInput = view.findViewById(R.id.editTitleInput);
                EditText authorInput = view.findViewById(R.id.editAuthorInput);
                EditText isbnInput = view.findViewById(R.id.editIsbnInput);
                EditText pagesInput = view.findViewById(R.id.editPagesInput);
                Button saveButton = view.findViewById(R.id.saveBookButton);
                Button deleteButton = view.findViewById(R.id.deleteBookButton);
                ImageButton coverImageButton = view.findViewById(R.id.editCoverImageButton);

                titleInput.setText(book.getTitle());
                authorInput.setText(book.getAuthor());
                isbnInput.setText(book.getIsbn());
                pagesInput.setText(String.valueOf(book.getPages()));

                // Si hay una URL de portada, cargala con Glide
                if (!TextUtils.isEmpty(book.getCoverImage())) {
                    Glide.with(getContext())
                            .load(book.getCoverImage())
                            .placeholder(R.drawable.default_book_cover)
                            .into(coverImageButton);
                }

                // Configurar el botón para seleccionar una imagen
                coverImageButton.setOnClickListener(v -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle(getString(R.string.select_cover_image))
                            .setItems(new String[]{getString(R.string.paste_cover_URL), getString(R.string.choose_from_device)}, (dialog, which) -> {
                                if (which == 0) {
                                    showEnterUrlDialog(coverImageButton);  // Mostrar diálogo para pegar URL
                                } else if (which == 1) {
                                    openGallery();  // Abrir galería para seleccionar una imagen
                                }
                            })
                            .show();
                });

                // Guardar cambios
                saveButton.setOnClickListener(v -> {
                    book.setTitle(titleInput.getText().toString());
                    book.setAuthor(authorInput.getText().toString());
                    book.setIsbn(isbnInput.getText().toString());
                    String pagesText = pagesInput.getText().toString();
                    int pages = pagesText.isEmpty() ? 0 : Integer.parseInt(pagesText);
                    book.setPages(pages);

                    // Obtener la URL o URI de la portada desde el tag del botón
                    String coverImage = coverImageButton.getTag() != null ? coverImageButton.getTag().toString() : "";

                    book.setCoverImage(coverImage); // Establecer la portada

                    Executors.newSingleThreadExecutor().execute(() -> {
                        db.bookDao().update(book); // Actualizar el libro en la base de datos

                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), getString(R.string.book_updated), Toast.LENGTH_SHORT).show();
                            requireActivity().getSupportFragmentManager().popBackStack();
                        });
                    });
                });

                // Eliminar libro
                deleteButton.setOnClickListener(v -> {
                    new android.app.AlertDialog.Builder(getContext())
                            .setTitle(getString(R.string.confirm_delete_book_title))
                            .setMessage(getString(R.string.confirm_delete_book_msg))
                            .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                                Executors.newSingleThreadExecutor().execute(() -> {
                                    db.bookDao().delete(book);  // Eliminar el libro de la base de datos
                                    requireActivity().runOnUiThread(() -> {
                                        Toast.makeText(getContext(), getString(R.string.book_deleted), Toast.LENGTH_SHORT).show();
                                        requireActivity().getSupportFragmentManager()
                                                .beginTransaction()
                                                .replace(R.id.fragment_container, new BookListFragment()) // Mostrar lista de libros
                                                .commit();
                                    });
                                });
                            })
                            .setNegativeButton(getString(R.string.no), null)
                            .show();
                });
            });
        });

        return view;
    }

    // Método para pegar la URL de la portada
    private void showEnterUrlDialog(ImageButton coverImageButton) {
        EditText urlInput = new EditText(getContext());
        urlInput.setHint("https://...");

        new AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.paste_cover_URL))
                .setView(urlInput)
                .setPositiveButton(getString(R.string.accept), (dialog, which) -> {
                    String url = urlInput.getText().toString();
                    coverImageButton.setTag(url);
                    Glide.with(this)
                            .load(url)
                            .placeholder(R.drawable.default_book_cover)
                            .into(coverImageButton);
                })
                .setNegativeButton(getString(R.string.cancel), null)
                .show();
    }

    // Método para abrir la galería y seleccionar una imagen
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            ImageButton coverImageButton = getView().findViewById(R.id.editCoverImageButton);
            coverImageButton.setTag(imageUri.toString());

            Glide.with(this)
                    .load(imageUri)
                    .placeholder(R.drawable.default_book_cover)
                    .into(coverImageButton);
        }
    }
}
