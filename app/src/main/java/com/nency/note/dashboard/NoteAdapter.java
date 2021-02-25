package com.nency.note.dashboard;

import android.content.Context;
import android.graphics.Rect;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nency.note.R;
import com.nency.note.interfaces.OnItemClickListener;
import com.nency.note.room.Note;
import com.nency.note.room.NoteWithCategory;
import com.nency.note.utils.ColorUtils;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private List<NoteWithCategory> notes = null;
    private OnItemClickListener onItemClickListener;

    public NoteAdapter(Context context,
            List<NoteWithCategory> list,
            @NonNull OnItemClickListener onItemClickListener) {
        notes = list;
        this.onItemClickListener = onItemClickListener;
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder {

        // note ID
        int id = -1;
        ViewGroup container;
        ImageView image;
        TextView title, description, category, date;

        public NoteViewHolder(@NonNull View itemView,
                final OnItemClickListener onItemClickListener) {
            super(itemView);
            // set click listener for whole view holder
            itemView.setOnClickListener(v -> onItemClickListener.onItemClick(id));
            container = itemView.findViewById(R.id.container);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            category = itemView.findViewById(R.id.category);
            date = itemView.findViewById(R.id.date);
            image = itemView.findViewById(R.id.image);
        }
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(v, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = notes.get(position).note;
        holder.id = note.getId();
        holder.itemView.setTag(note);
        holder.title.setText(note.getTitle());
        holder.description.setText(note.getDescription());
        holder.category.setText(notes.get(position).category.getName());
        displayDate(holder, note);
        displayImageIfIsThere(holder, note);
        setBackground(holder, position);
    }

    // set background color
    private void setBackground(@NonNull NoteViewHolder holder,
            int position) {
        holder.container.setBackgroundColor(ColorUtils.getColor(holder.itemView.getContext(),
                position % 6));

    }

    // parse date from String to Date class then format to display format string
    private void displayDate(@NonNull NoteViewHolder holder,
            Note note) {
        try {
            SimpleDateFormat parseFormat =
                    new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.CANADA);
            SimpleDateFormat displayFormat = new SimpleDateFormat("MMM dd, yyyy",
                    Locale.CANADA);
            holder.date.setText(displayFormat.format(parseFormat.parse(note.getDate())));
            holder.date.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
            holder.date.setVisibility(View.GONE);
        }

    }

    // check images is there in note then display otherwise hide it
    private void displayImageIfIsThere(@NonNull NoteViewHolder holder, Note note) {
        if (note.getImages().size() > 0) {
            holder.image.setImageURI(Uri.parse(note.getImages().get(0)));
            holder.image.setVisibility(View.VISIBLE);
        } else {
            holder.image.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        if (notes == null) {
            return 0;
        } else {
            return notes.size();
        }
    }

    // grid spacing code
    public static class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect,
                View view,
                RecyclerView parent,
                RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing /
                        spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing /
                        spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left =
                        column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing /
                        spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

}
