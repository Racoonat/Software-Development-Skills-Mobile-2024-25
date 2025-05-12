package com.example.bookdiary;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.concurrent.Executors;

public class BookListFragment extends Fragment {

    private RecyclerView recyclerView;

    public BookListFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_list, container, false);
        recyclerView = view.findViewById(R.id.bookRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadBooks();

        return view;
    }

    private void loadBooks() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<BookWithSession> bookWithSessions = DatabaseClient
                    .getInstance(requireContext())
                    .getAppDatabase()
                    .bookDao()
                    .getBooksWithSessions();

            requireActivity().runOnUiThread(() -> {
                BookAdapter.OnBookClickListener listener = book -> {
                    Fragment detailFragment = BookDetailFragment.newInstance(book.getId());

                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, detailFragment)
                            .addToBackStack(null)
                            .commit();
                };

                BookAdapter adapter = new BookAdapter(bookWithSessions, listener,getContext());
                recyclerView.setAdapter(adapter);
            });
        });
    }
}
