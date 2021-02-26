package com.nency.note.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.nency.note.R;
import com.nency.note.interfaces.OnCategoryActionListener;
import com.nency.note.interfaces.OnCategorySelectListener;
import com.nency.note.room.Category;
import com.nency.note.room.NoteRoomDatabase;

import java.util.ArrayList;
import java.util.List;

public class CategoryFilterListDialogFragment extends BottomSheetDialogFragment implements OnCategoryActionListener {

    private OnCategorySelectListener onCategorySelectListener;
    private NoteRoomDatabase noteRoomDatabase;

    private final ArrayList<Category> categories = new ArrayList<>();
    ArrayList<Category> filterCategory;
    private RecyclerView recyclerView ;

    public static CategoryFilterListDialogFragment newInstance(ArrayList<Category> filterCategory,
            @NonNull OnCategorySelectListener onCategoryActionListener) {
        final CategoryFilterListDialogFragment fragment = new CategoryFilterListDialogFragment();
        fragment.filterCategory = filterCategory;
        fragment.onCategorySelectListener = onCategoryActionListener;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.category_filter_list_dialog_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.listCategory);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new CategoryAdapter(categories, filterCategory, this));
        noteRoomDatabase = NoteRoomDatabase.getInstance(requireContext());

        loadCategory();
    }

    private void loadCategory(){
        categories.clear();
        categories.addAll(noteRoomDatabase.CategoryDao().getAllCategories());
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onCategorySelected(Category category) {
        onCategorySelectListener.onCategorySelected(category);
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onCategoryEditSelected(Category category) {
        showUpdateCategoryAlert(category);
    }

    @Override
    public void onCategoryRemoveSelected(Category category) {
        noteRoomDatabase.CategoryDao().deleteCategory(category.getId());
        loadCategory();
    }

    private void showUpdateCategoryAlert(Category category) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.add_new_category_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = dialogView.findViewById(R.id.edit1);
        edt.setText(category.getName());

        dialogBuilder.setTitle("Edit Category");
        dialogBuilder.setPositiveButton("Update",
                (dialog, whichButton) -> updateCategory(category, edt.getText().toString()));
        dialogBuilder.setNegativeButton("Cancel", (dialog, whichButton) -> {});
        dialogBuilder.create().show();
    }

    private void updateCategory(Category category, String newName){
        noteRoomDatabase.CategoryDao().updateCategory(category.getId(), newName);
        loadCategory();
    }

    private void showAddNewCategoryAlert() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.add_new_category_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = dialogView.findViewById(R.id.edit1);

        dialogBuilder.setTitle("Add New Category");
        dialogBuilder.setPositiveButton("Add",
                (dialog, whichButton) -> addCategory(edt.getText().toString()));
        dialogBuilder.setNegativeButton("Cancel", (dialog, whichButton) -> {});
        dialogBuilder.create().show();
    }

    private void addCategory(String category){
        noteRoomDatabase.CategoryDao().insertCategory(new Category(category, 0));
        loadCategory();
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        final TextView text;
        final ImageView btnChecked;
        Category category;

        ViewHolder(LayoutInflater inflater,
                ViewGroup parent,
                final OnCategoryActionListener onCategoryActionListener) {
            // TODO: Customize the item layout
            super(inflater.inflate(R.layout.category_filter_list_dialog_item, parent, false));
            itemView.setOnClickListener(v -> onCategoryActionListener.onCategorySelected(category));
            itemView.findViewById(R.id.btnEdit).setOnClickListener(v -> onCategoryActionListener.onCategoryEditSelected(category));
            itemView.findViewById(R.id.btnRemove).setOnClickListener(v -> onCategoryActionListener.onCategoryRemoveSelected(category));
            text = itemView.findViewById(R.id.categoryTitle);
            btnChecked = itemView.findViewById(R.id.btnChecked);
        }
    }

    private class CategoryAdapter extends RecyclerView.Adapter<ViewHolder> {

        private final List<Category> categories;
        private final OnCategoryActionListener onCategoryActionListener;
        private final ArrayList<Category> filterCategory;

        CategoryAdapter(List<Category> categories,
                ArrayList<Category> filterCategory,
                OnCategoryActionListener onCategoryActionListener) {
            this.categories = categories;
            this.filterCategory = filterCategory;
            this.onCategoryActionListener = onCategoryActionListener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent,
                    onCategoryActionListener);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Category category = categories.get(position);
            holder.category = category;
            holder.text.setText(String.format("%s (%d)",
                    category.getName(),
                    category.getNoOfNotes()));

            holder.btnChecked.setSelected(!filterCategory.contains(category));
        }

        @Override
        public int getItemCount() {
            return categories.size();
        }

    }

}