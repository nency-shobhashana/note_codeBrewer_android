package com.nency.note.detail;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nency.note.R;
import com.nency.note.interfaces.OnCategorySelectListener;
import com.nency.note.interfaces.OnItemClickListener;
import com.nency.note.room.Category;
import com.nency.note.room.NoteRoomDatabase;

import java.util.ArrayList;
import java.util.List;

public class CategoryListDialogFragment extends BottomSheetDialogFragment
        implements OnItemClickListener {

    private OnCategorySelectListener onCategorySelectListener;
    private NoteRoomDatabase noteRoomDatabase;

    private ArrayList<Category> categories = new ArrayList<>();

    private RecyclerView recyclerView ;

    public static CategoryListDialogFragment newInstance(@NonNull OnCategorySelectListener onCategorySelectListener) {
        final CategoryListDialogFragment fragment = new CategoryListDialogFragment();
        fragment.onCategorySelectListener = onCategorySelectListener;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.category_list_dialog_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView = (RecyclerView) view;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new CategoryAdapter(categories, this));
        loadCategory();
    }

    private void loadCategory(){
        noteRoomDatabase = NoteRoomDatabase.getInstance(requireContext());
        categories.addAll(noteRoomDatabase.CategoryDao().getAllCategories());
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onItemClick(int id) {
        onCategorySelectListener.onCategorySelected(id);
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        final TextView text;
        int id;

        ViewHolder(LayoutInflater inflater,
                ViewGroup parent,
                final OnItemClickListener onItemClickListener) {
            // TODO: Customize the item layout
            super(inflater.inflate(R.layout.category_list_dialog_item, parent, false));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(id);
                }
            });
            text = itemView.findViewById(R.id.text);
        }
    }

    private class CategoryAdapter extends RecyclerView.Adapter<ViewHolder> {

        private final List<Category> categories;
        private final OnItemClickListener onItemClickListener;

        CategoryAdapter(List<Category> categories,
                OnItemClickListener onItemClickListener) {
            this.categories = categories;
            this.onItemClickListener = onItemClickListener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent, onItemClickListener);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.id = categories.get(position).getId();
            holder.text.setText(categories.get(position).getName());
        }

        @Override
        public int getItemCount() {
            return categories.size();
        }

    }

}