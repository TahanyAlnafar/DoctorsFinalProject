package com.doctors.doctorsfinalproject.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.doctors.doctorsfinalproject.adapters.DoctorsCategoryAdapter;
import com.doctors.doctorsfinalproject.databinding.ActivityDoctorsCategoryBinding;
import com.doctors.doctorsfinalproject.model.DoctorsCategoryModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class DoctorsCategoryActivity extends AppCompatActivity {
ActivityDoctorsCategoryBinding binding;
     DoctorsCategoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDoctorsCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getData();
        getAdapter();


        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchByName(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchByName(newText);
                return false;
            }
        });
    }

     private void getData() {

         FirebaseFirestore db = FirebaseFirestore.getInstance();

         CollectionReference collectionRef = db.collection("doctors");

         collectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<DoctorsCategoryModel> myDataList = new ArrayList<>();

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        DoctorsCategoryModel myData = document.toObject(DoctorsCategoryModel.class);
                        myDataList.add(myData);
                    }

                     adapter = new DoctorsCategoryAdapter(getBaseContext(),myDataList);

                     binding.recyclerview.setAdapter(adapter);

                     adapter.notifyDataSetChanged();
                } else {
                    Log.e("TAG", "Error getting documents: ", task.getException());
                }
            }
        });


    }

    private void getAdapter(){
         LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                RecyclerView.VERTICAL, false);
        binding.recyclerview.setLayoutManager(layoutManager);


     }

    private void searchByName(String query) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference usersRef = db.collection("doctors");
        Query nameQuery = usersRef.whereGreaterThanOrEqualTo("name", query)
                .whereLessThanOrEqualTo("name", query + "\uf8ff");

        nameQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<DoctorsCategoryModel> userList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        DoctorsCategoryModel user = document.toObject(DoctorsCategoryModel.class);
                        userList.add(user);
                    }

                     adapter = new DoctorsCategoryAdapter(getBaseContext(),userList);
                    binding.recyclerview.setAdapter(adapter);
                } else {
                    Log.d("TAG", "Error getting documents: ", task.getException());
                }
            }
        });
    }
}