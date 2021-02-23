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

public class CategoryListDialogFragment extends BottomSheetDialogFragment
        implements OnItemClickListener {
    private OnCategorySelectListener onCategorySelectListener;

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
        final RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new CategoryAdapter(30, this));
    }

    @Override
    public void onItemClick(int id) {

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

        private final int mItemCount;
        private OnItemClickListener onItemClickListener;

        CategoryAdapter(int itemCount,
                OnItemClickListener onItemClickListener) {
            mItemCount = itemCount;
            this.onItemClickListener = onItemClickListener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent, onItemClickListener);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.id = position;
            holder.text.setText(String.valueOf(position));
        }

        @Override
        public int getItemCount() {
            return mItemCount;
        }

    }

}