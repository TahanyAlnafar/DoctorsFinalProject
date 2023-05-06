package com.doctors.doctorsfinalproject.activities;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

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
import com.doctors.doctorsfinalproject.databinding.ActivityEditBinding;
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

import java.util.HashMap;
import java.util.Map;

public class EditActivity extends AppCompatActivity {

    ActivityEditBinding binding;
    StorageReference storageReference;
     FirebaseStorage firebaseStorage;
     String image1,image2;
    ActivityResultLauncher<String> al1;
    ActivityResultLauncher<String> al2;
    String item_id;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

         firebaseStorage = FirebaseStorage.getInstance();
        clickListener();
        logoImage();
        userImage();
        getData();
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
            updateData();
        });

        binding.backArrow.setOnClickListener(View->{
            finish();
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestStoragePermission() {
        requestPermissions(new String[]{
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
    }


    private void getData(){

        if (getIntent().getExtras()!=null){
            String name =  getIntent().getStringExtra("name");
            String title =  getIntent().getStringExtra("title");
            String description =  getIntent().getStringExtra("description");
            image1 =  getIntent().getStringExtra("image");
            image2 =  getIntent().getStringExtra("logo");
             item_id =  getIntent().getStringExtra("item_id");


            binding.doctorName.setText(name);
            binding.description.setText(description);
            binding.name.setText(title);
            Glide.with(this).load(image2).placeholder(R.drawable.ic_launcher_background).into(binding.logo);
            Glide.with(this).load(image1).placeholder(R.drawable.ic_launcher_background).into(binding.userImage);

        }
    }

    private void updateData(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference itemsRef = db.collection("AllDoctors");

        DocumentReference itemRef = itemsRef.document(item_id);
        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("title", binding.name.getText().toString());
        updatedData.put("name", binding.doctorName.getText().toString());
        updatedData.put("description", binding.description.getText().toString());
        updatedData.put("logo", image2);
        updatedData.put("image", image1);

        updatedData.put("item_id",item_id);
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
}