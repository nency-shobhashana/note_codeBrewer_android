package com.nency.note.detail;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.nency.note.R;
import com.nency.note.dashboard.OnItemClickListener;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class ImageListDialogFragment extends BottomSheetDialogFragment
        implements OnItemClickListener {

    private RecyclerView recyclerView;

    // TODO: Customize parameter argument names
    private static final int REQUEST_IMAGE_CAPTURE = 101;
    private static final int PICK_IMAGE = 102;
    private Uri newPhotoURI = null;
    private ArrayList<Uri> imageList ;

    // TODO: Customize parameters
    public static ImageListDialogFragment newInstance(ArrayList<Uri> imageList) {
        final ImageListDialogFragment fragment = new ImageListDialogFragment();
        fragment.imageList = imageList;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_item_list_dialog_list_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setPeekHeight();
        recyclerView = (RecyclerView) view;
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        recyclerView.setAdapter(new ImageAdapter(imageList, this));
    }

    private void setPeekHeight() {
        Dialog baseDialog = getDialog();
        if (baseDialog instanceof BottomSheetDialog) {
            BottomSheetDialog dialog = (BottomSheetDialog) baseDialog;
            BottomSheetBehavior<?> behavior = dialog.getBehavior();
            behavior.setPeekHeight(500);
        }
    }

    @Override
    public void onItemClick(int position) {
        if(position != 0){
            return;
        }
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
        ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                    getActivity(),
                    new String[]{Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    0);
        } else {
            selectImage(requireContext());
        }
    }

    private void selectImage(Context context) {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose your profile picture");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo")) {
                    dispatchTakePictureIntent();

                } else if (options[item].equals("Choose from Gallery")) {
                    openGallery();

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp;
        File storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(requireContext().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(requireContext(),
                        "com.nency.note.fileprovider",
                        photoFile);
                newPhotoURI = photoURI;
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            addNewPhotoInList();
        } else {
            newPhotoURI = null;
        }
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK){
            newPhotoURI = data.getData();
            addNewPhotoInList();
        }
    }

    private void addNewPhotoInList(){
        imageList.add(newPhotoURI);
        recyclerView.getAdapter().notifyDataSetChanged();
    }


    private static class ImageViewHolder extends RecyclerView.ViewHolder {
        int position = 0;
        final ImageView image;

        ImageViewHolder(LayoutInflater inflater, ViewGroup parent,
                final OnItemClickListener onItemClickListener) {
            // TODO: Customize the item layout
            super(inflater.inflate(R.layout.fragment_item_list_dialog_list_dialog_item,
                    parent,
                    false));
            image = itemView.findViewById(R.id.image);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(position);
                }
            });
        }
    }

    private static class ImageAdapter extends RecyclerView.Adapter<ImageViewHolder> {

        private final List<Uri> imageList;
        private final OnItemClickListener onItemClickListener;

        ImageAdapter(List<Uri> imageList, OnItemClickListener onItemClickListener) {
            this.imageList = imageList;
            this.onItemClickListener = onItemClickListener;
        }

        @NonNull
        @Override
        public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ImageViewHolder(LayoutInflater.from(parent.getContext()),
                    parent,
                    onItemClickListener);
        }

        @Override
        public void onBindViewHolder(ImageViewHolder holder, int position) {
            if (position == 0){
                holder.image.setImageResource(R.drawable.ic_add_photo);
            } else {
                holder.image.setImageURI(imageList.get(position - 1));
                holder.position = position;
            }
        }

        @Override
        public int getItemCount() {
            return imageList.size() + 1;
        }

    }

}