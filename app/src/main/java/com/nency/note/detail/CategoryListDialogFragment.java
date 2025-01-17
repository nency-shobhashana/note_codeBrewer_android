package com.nency.note.detail;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.nency.note.R;
import com.nency.note.interfaces.OnCategorySelectListener;
import com.nency.note.interfaces.OnItemClickListener;
import com.nency.note.room.Category;
import com.nency.note.room.NoteRoomDatabase;

import java.util.ArrayList;
import java.util.List;

public class CategoryListDialogFragment extends BottomSheetDialogFragment {

    private OnCategorySelectListener onCategorySelectListener;
    private int selectedId;
    private NoteRoomDatabase noteRoomDatabase;

    private ArrayList<Category> categories = new ArrayList<>();

    private RecyclerView recyclerView ;
    private TextView btnAddCategory;

    public static CategoryListDialogFragment newInstance(int selectedId,
            @NonNull OnCategorySelectListener onCategorySelectListener) {
        final CategoryListDialogFragment fragment = new CategoryListDialogFragment();
        fragment.onCategorySelectListener = onCategorySelectListener;
        fragment.selectedId = selectedId;
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
        btnAddCategory = view.findViewById(R.id.btnAddCategory);
        recyclerView = view.findViewById(R.id.listCategory);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new CategoryAdapter(categories, selectedId,
                onCategorySelectListener));
        loadCategory();
        loadListener();
    }

    private void loadListener() {
        btnAddCategory.setOnClickListener(v -> showAddNewCategoryAlert());
    }

    private void showAddNewCategoryAlert() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.add_new_category_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.edit1);

        dialogBuilder.setTitle("Add New Category");
        dialogBuilder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                addCategory(edt.getText().toString());
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });
        dialogBuilder.create().show();
    }

    private void addCategory(String category){
        noteRoomDatabase.CategoryDao().insertCategory(new Category(category));
        loadCategory();
    }

    private void loadCategory(){
        noteRoomDatabase = NoteRoomDatabase.getInstance(requireContext());
        categories.clear();
        categories.addAll(noteRoomDatabase.CategoryDao().getAllCategories());
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        final TextView text;
        final ImageView btnChecked;
        Category category;

        ViewHolder(LayoutInflater inflater,
                ViewGroup parent,
                final OnCategorySelectListener onCategorySelectListener) {
            // TODO: Customize the item layout
            super(inflater.inflate(R.layout.category_list_dialog_item, parent, false));
            itemView.setOnClickListener(v -> {
                onCategorySelectListener.onCategorySelected(category);
                dismiss();
            });
            text = itemView.findViewById(R.id.categoryTitle);
            btnChecked = itemView.findViewById(R.id.btnChecked);
        }
    }

    private class CategoryAdapter extends RecyclerView.Adapter<ViewHolder> {

        private final List<Category> categories;
        private final int selectedId;
        private final OnCategorySelectListener onCategorySelectListener;

        CategoryAdapter(List<Category> categories,
                int selectedId,
                OnCategorySelectListener onCategorySelectListener) {
            this.categories = categories;
            this.selectedId = selectedId;
            this.onCategorySelectListener = onCategorySelectListener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent, onCategorySelectListener);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.category = categories.get(position);
            holder.text.setText(categories.get(position).getName());

            holder.btnChecked.setSelected(categories.get(position).getId() == selectedId);
        }

        @Override
        public int getItemCount() {
            return categories.size();
        }

    }

}