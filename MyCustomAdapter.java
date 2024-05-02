package com.example.projet;

import static android.content.ContentValues.TAG;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class MyCustomAdapter extends RecyclerView.Adapter<MyCustomAdapter.ViewHolder> {

    private List<Photo> photos;
    private List <Photo> library;
    public MyCustomAdapter(Context context, List<Photo> photos) {
        this.photos = photos;
        this.library=new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Photo photo = photos.get(position);
        String imageUrl = photo.getUrls().getRegular();
        Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .centerCrop()
                .into(holder.imageView);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle visibility of the save button
                if (holder.saveButton.getVisibility() == View.VISIBLE) {
                    holder.saveButton.setVisibility(View.INVISIBLE);
                } else {
                    holder.saveButton.setVisibility(View.VISIBLE);
                }
            }
        });
        holder.saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                PictureDAO pictureDAO = new PictureDAO(v.getContext());

                pictureDAO.open();

// Insert a URL
                pictureDAO.insertURL(imageUrl.toString());

// Retrieve all URLs
               List<String> urls = pictureDAO.getAllURLs();

                pictureDAO.close();

                /**if (imageUrl != null) {
                     FirebaseStorage storage = FirebaseStorage.getInstance();
                     StorageReference storageRef = storage.getReference().child("/cats").child(photo.getId());
                     Uri imageUri = Uri.parse(imageUrl);
                     UploadTask uploadTask = storageRef.putFile(imageUri);
                     uploadTask.addOnSuccessListener(taskSnapshot -> {
                         Toast.makeText(v.getContext(), "Success", Toast.LENGTH_SHORT).show();
                         // Image uploaded successfully
                         // Handle success, such as displaying a success message
                     }).addOnFailureListener(exception -> {
                         if (exception instanceof StorageException) {
                             StorageException storageException = (StorageException) exception;
                             int resultCode = storageException.getHttpResultCode();

                         } else {
                             Log.e(TAG, "Upload failed with exception: " + exception.getMessage());
                         }
                         // Handle unsuccessful uploads
                         String s1= exception.getMessage();
                         Toast.makeText(v.getContext(), "Upload failed: " + exception.getMessage(),Toast.LENGTH_SHORT).show();
                         // Display an error message to the user
                     });
                 } else {
                     // Handle the case where imageUrl is nul
                 }*/
            }
        });

    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        Button saveButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            saveButton = itemView.findViewById(R.id.saveButton);
        }
    }
}
