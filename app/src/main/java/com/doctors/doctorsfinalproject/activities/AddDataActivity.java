package com.doctors.doctorsfinalproject.activities;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.doctors.doctorsfinalproject.R;
import com.doctors.doctorsfinalproject.databinding.ActivityAddDataBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AddDataActivity extends AppCompatActivity {
    ActivityAddDataBinding binding;
    StorageReference storageReference;
     FirebaseStorage firebaseStorage;
    public Dialog pic_image_dialog;
    String image1,image2;
    ActivityResultLauncher<String> al1;
    ActivityResultLauncher<String> al2;
    ActivityResultLauncher<String> al3;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddDataBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        pic_image_dialog = new Dialog(this);
        firebaseStorage = FirebaseStorage.getInstance();
        clickListener();
        logoImage();
        userImage();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void clickListener() {
        binding.userImage.setOnClickListener(View -> {
            requestStoragePermission();
            al2.launch("image/*");
        });

        binding.logo.setOnClickListener(View -> {
            requestStoragePermission();
            al1.launch("image/*");
        });

        binding.saveBtn.setOnClickListener(View -> {
            AddData();

        });
    }

    private void AddData() {
        binding.progressBar.setVisibility(View.VISIBLE);
        Map<String, Object> map = new HashMap<>();
        map.put("title", binding.name.getText().toString());
        map.put("name", binding.doctorName.getText().toString());
        map.put("description", binding.description.getText().toString());
        map.put("logo", image2);
        map.put("image", image1);


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionRef = db.collection("AllDoctors");

        collectionRef.add(map)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        CollectionReference itemsRef = db.collection("AllDoctors");

                        DocumentReference itemRef = itemsRef.document(documentReference.getId());
                        Map<String, Object> updatedData = new HashMap<>();
                        updatedData.put("item_id",documentReference.getId());
                        itemRef.update(updatedData)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Handle errors here
                                    }
                                });


                        Log.d("TAG", "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error adding document", e);
                    }
                });



    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestStoragePermission() {
        requestPermissions(new String[]{
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
    }

    private void logoImage(){

        al1 = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        //Todo storage the result
                        Log.e("lllllllllllllllllll",result.getPath());
                        binding.logoProgressBar.setVisibility(View.VISIBLE);

                        storageReference = firebaseStorage.getReference("images/" + result.getLastPathSegment());
                        StorageTask<UploadTask.TaskSnapshot> uploadTask = storageReference.putFile(result);
                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        binding.logoProgressBar.setVisibility(View.GONE);

                                        image2 = task.getResult().toString();
                                        Glide.with(getBaseContext()).load(image2).transform(new RoundedCorners(8)).error(R.drawable.doctor_ann_marie_navar_2).into(binding.logo);
                                        Log.e("UploadActivity1", image2);

                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                binding.logoProgressBar.setVisibility(View.GONE);

                                e.printStackTrace();
                                Toast.makeText(getBaseContext(), "Image Uploaded Failed!!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });




    }

    private void userImage(){
        al2 = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        //Todo storage the result
                        binding.imageProgressBar.setVisibility(View.VISIBLE);

                        Log.e("lllllllllllllllllll",result.getPath());
                        storageReference = firebaseStorage.getReference("images/" + result.getLastPathSegment());
                        StorageTask<UploadTask.TaskSnapshot> uploadTask = storageReference.putFile(result);

                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        binding.imageProgressBar.setVisibility(View.GONE);

                                        image1 = task.getResult().toString();
                                        Glide.with(getBaseContext()).load(image1).transform(new RoundedCorners(8)).error(R.drawable.doctor_ann_marie_navar_2).into(binding.userImage);
                                        Log.e("UploadActivity1", image1);

                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                binding.imageProgressBar.setVisibility(View.GONE);

                                e.printStackTrace();
                                Toast.makeText(getBaseContext(), "Image Uploaded Failed!!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });




    }

    private void video(){
        al3 = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                        // Handle the selected video file URI here
                        Log.d("TAG", "Selected video URI: " + uri.toString());

                        // Upload the selected video file to Firebase Storage
                        uploadVideoToStorage(uri);
                    }
                });


        binding.videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                al3.launch("video/*");
            }
        });
    }

    private void uploadVideoToStorage(Uri file ){
         StorageReference storageRef = FirebaseStorage.getInstance().getReference();

         file = Uri.fromFile(new File("path/to/video.mp4"));
        StorageReference videoRef = storageRef.child("videos/" + file.getLastPathSegment());

// Upload the video file to Firebase Storage
        videoRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Video uploaded successfully
                        Log.d("TAG", "Video uploaded successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Video upload failed
                        Log.e("TAG", "Error uploading video", e);
                    }
                });

    }
}