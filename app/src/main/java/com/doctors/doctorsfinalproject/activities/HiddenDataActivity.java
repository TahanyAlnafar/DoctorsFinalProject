package com.doctors.doctorsfinalproject.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.doctors.doctorsfinalproject.MyListener;
import com.doctors.doctorsfinalproject.R;
import com.doctors.doctorsfinalproject.adapters.AllDoctorsAdapter;
import com.doctors.doctorsfinalproject.adapters.HiddenDoctorsAdapter;
import com.doctors.doctorsfinalproject.databinding.ActivityAllDoctorsBinding;
import com.doctors.doctorsfinalproject.databinding.ActivityHiddenDataBinding;
import com.doctors.doctorsfinalproject.model.AllDoctorsModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class HiddenDataActivity extends AppCompatActivity {
    ActivityHiddenDataBinding binding;
    HiddenDoctorsAdapter adapter;
    ArrayList<AllDoctorsModel> myDataList = new ArrayList<>();

     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         binding = ActivityHiddenDataBinding.inflate(getLayoutInflater());
         setContentView(binding.getRoot());

         getData();
         getAdapter();
         clickListener();
    }

    private void clickListener() {
        binding.back.setOnClickListener(View -> {
           finish();
        });
    }

    private void getData() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference collectionRef = db.collection("Hidden");

        collectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    myDataList = new ArrayList<>();

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        AllDoctorsModel myData = document.toObject(AllDoctorsModel.class);
                        myDataList.add(myData);
                    }

                    adapter = new HiddenDoctorsAdapter(getBaseContext(),myDataList,null);

                    binding.recyclerview.setAdapter(adapter);

                    adapter.notifyDataSetChanged();
                } else {
                    Log.e("TAG", "Error getting documents: ", task.getException());
                }
            }
        });


    }

    private void getAdapter() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                RecyclerView.VERTICAL, false);
        binding.recyclerview.setLayoutManager(layoutManager);

        adapter = new HiddenDoctorsAdapter(getBaseContext(), myDataList,null);


    }

}