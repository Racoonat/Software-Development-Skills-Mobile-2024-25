package com.example.bookdiary;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;

import java.util.concurrent.Executors;

public class AddBookFragment extends Fragment {

    private AppDatabase db;
    private BookDao bookDao;
    private ReadingSessionDao readingSessionDao;

    private Spinner statusSpinner;
    private EditText startDateInput, endDateInput, notesInput;
    private RatingBar ratingInput;
    private Button addReadingSessionButton;
    private LinearLayout readFieldsLayout;
    private ImageButton coverImageButton;

    private View rootView;
    private String selectedCoverUrl = null;
    private static final int PICK_IMAGE_REQUEST = 1;

    public AddBookFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_add_book, container, false);

        db = DatabaseClient.getInstance(getContext()).getAppDatabase();
        bookDao = db.bookDao();
        readingSessionDao = db.readingSessionDao();

        statusSpinner = rootView.findViewById(R.id.statusSpinner);
        startDateInput = rootView.findViewById(R.id.startDateInput);
        endDateInput = rootView.findViewById(R.id.endDateInput);
        ratingInput = rootView.findViewById(R.id.ratingBar);
        notesInput = rootView.findViewById(R.id.notesInput);
        addReadingSessionButton = rootView.findViewById(R.id.addReadingSessionButton);
        readFieldsLayout = rootView.findViewById(R.id.readFieldsLayout);
        coverImageButton = rootView.findViewById(R.id.coverImageButton);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.book_status, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(adapter);

        statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                handleStatusChange(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });

        coverImageButton.setOnClickListener(v -> {
            new AlertDialog.Builder(getContext())
                    .setTitle(getString(R.string.select_cover_image))
                    .setItems(new String[]{getString(R.string.paste_cover_URL),getString(R.string.choose_from_device)}, (dialog, which) -> {
                        if (which == 0) {
                            showEnterUrlDialog();
                        } else {
                            openGallery();
                        }
                    })
                    .show();
        });

        addReadingSessionButton.setOnClickListener(v -> addBookAndReadingSession());

        return rootView;
    }

    private void handleStatusChange(int position) {
        readFieldsLayout.setVisibility(View.VISIBLE);
        switch (position) {
            case 0: // Read
                ratingInput.setVisibility(View.VISIBLE);
                startDateInput.setVisibility(View.VISIBLE);
                endDateInput.setVisibility(View.VISIBLE);
                break;
            case 1: // Reading
                ratingInput.setVisibility(View.GONE);
                startDateInput.setVisibility(View.VISIBLE);
                endDateInput.setVisibility(View.GONE);
                break;
            case 2: // TBR
                ratingInput.setVisibility(View.GONE);
                startDateInput.setVisibility(View.GONE);
                endDateInput.setVisibility(View.GONE);
                break;
            case 3: // DNF
                ratingInput.setVisibility(View.GONE);
                startDateInput.setVisibility(View.VISIBLE);
                endDateInput.setVisibility(View.VISIBLE);
                break;
        }
        notesInput.setVisibility(View.VISIBLE);
        addReadingSessionButton.setVisibility(View.VISIBLE);
    }

    private void addBookAndReadingSession() {
        String title = ((EditText) rootView.findViewById(R.id.bookTitleInput)).getText().toString();
        String author = ((EditText) rootView.findViewById(R.id.bookAuthorInput)).getText().toString();
        String isbn = ((EditText) rootView.findViewById(R.id.bookisbnInput)).getText().toString();
        String pagesText = ((EditText) rootView.findViewById(R.id.bookpagesInput)).getText().toString();
        int pages = pagesText.isEmpty() ? 0 : Integer.parseInt(pagesText);
        String coverImage = selectedCoverUrl != null ? selectedCoverUrl : "";

        if (title.isEmpty()) {
            Toast.makeText(getContext(), getString(R.string.book_title_mandatory), Toast.LENGTH_SHORT).show();
            return;
        }

        if (author.isEmpty()) {
            author = getString(R.string.unknown_author);
        }

        Book book = new Book(title, author, isbn, pages, coverImage);

        Executors.newSingleThreadExecutor().execute(() -> {
            long bookId = bookDao.insert(book);

            String status = statusSpinner.getSelectedItem().toString();
            String startDate = startDateInput.getText().toString();
            String endDate = endDateInput.getText().toString();
            float rating = ratingInput.getRating();
            String notes = notesInput.getText().toString();

            ReadingSession session = new ReadingSession(
                    bookId,
                    status,
                    startDate,
                    endDate,
                    rating,
                    notes
            );
            readingSessionDao.insert(session);

            requireActivity().runOnUiThread(() -> {
                Toast.makeText(getContext(), getString(R.string.book_added), Toast.LENGTH_SHORT).show();
                ((MainActivity) requireActivity()).updateBottomNavigationSelection(R.id.nav_two);

                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new BookListFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            });
        });

        clearForm();
    }

    private void clearForm() {
        ((EditText) rootView.findViewById(R.id.bookTitleInput)).setText("");
        ((EditText) rootView.findViewById(R.id.bookAuthorInput)).setText("");
        ((EditText) rootView.findViewById(R.id.bookisbnInput)).setText("");
        ((EditText) rootView.findViewById(R.id.bookpagesInput)).setText("");
        ((EditText) rootView.findViewById(R.id.startDateInput)).setText("");
        ((EditText) rootView.findViewById(R.id.endDateInput)).setText("");
        ((EditText) rootView.findViewById(R.id.notesInput)).setText("");

        ratingInput.setRating(0);
        statusSpinner.setSelection(0);
    }

    private void showEnterUrlDialog() {
        EditText urlInput = new EditText(getContext());
        urlInput.setHint("https://...");

        new AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.paste_cover_URL))
                .setView(urlInput)
                .setPositiveButton(getString(R.string.accept), (dialog, which) -> {
                    selectedCoverUrl = urlInput.getText().toString();
                    Glide.with(this)
                            .load(selectedCoverUrl)
                            .placeholder(R.drawable.default_book_cover)
                            .into(coverImageButton);
                })
                .setNegativeButton(getString(R.string.cancel), null)
                .show();
    }

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
            selectedCoverUrl = imageUri.toString();

            Glide.with(this)
                    .load(imageUri)
                    .placeholder(R.drawable.default_book_cover)
                    .into(coverImageButton);
        }
    }
}
