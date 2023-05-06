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
import android.view.Gravity;
import android.widget.Button;
import android.widget.Toast;

import com.doctors.doctorsfinalproject.R;
import com.doctors.doctorsfinalproject.adapters.AllDoctorsAdapter;
import com.doctors.doctorsfinalproject.databinding.ActivityAllDoctorsBinding;
import com.doctors.doctorsfinalproject.model.AllDoctorsModel;
import com.doctors.doctorsfinalproject.MyListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AllDoctorsActivity extends AppCompatActivity {
    ActivityAllDoctorsBinding binding;
    AllDoctorsAdapter adapter;
    ArrayList<AllDoctorsModel> myDataList = new ArrayList<>();
    public Dialog delete_dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAllDoctorsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getData();
        getAdapter();
        clickListener();

    }

    private void clickListener() {
        binding.floatingActionButton.setOnClickListener(View -> {
            startActivity(new Intent(getBaseContext(), AddDataActivity.class));
        });

        binding.hide.setOnClickListener(View -> {
            startActivity(new Intent(getBaseContext(), HiddenDataActivity.class));
        });

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

        CollectionReference collectionRef = db.collection("AllDoctors");

        collectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    myDataList = new ArrayList<>();

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        AllDoctorsModel myData = document.toObject(AllDoctorsModel.class);
                        myDataList.add(myData);
                    }

                    adapter = new AllDoctorsAdapter(getBaseContext(), myDataList, new MyListener() {
                        @Override
                        public void delete(String id, int pos) {
                            deleteDialog(id,pos);
                        }
                    });

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

        adapter = new AllDoctorsAdapter(getBaseContext(), myDataList, new MyListener() {
            @Override
            public void delete(String id, int pos) {
                deleteDialog(id,pos);
            }

        });


    }

    private void deleteSelectedModel(String selectedModel, int pos) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference myCollection = db.collection("AllDoctors");

        myCollection.document(selectedModel)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        adapter.notifyDataSetChanged();
                         Log.e("TAG", selectedModel);
                        Toast.makeText(AllDoctorsActivity.this, "delete successfully", Toast.LENGTH_SHORT).show();
                        delete_dialog.dismiss();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("TAG", "Error deleting document", e);
                    }
                });

        myDataList.remove(pos);
        adapter.notifyItemRemoved(pos);
    }

    private void searchByName(String query) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference usersRef = db.collection("AllDoctors");
        Query nameQuery = usersRef.whereGreaterThanOrEqualTo("name", query)
                .whereLessThanOrEqualTo("name", query + "\uf8ff");

        nameQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<AllDoctorsModel> userList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        AllDoctorsModel user = document.toObject(AllDoctorsModel.class);
                        userList.add(user);
                    }

                    adapter = new AllDoctorsAdapter(getBaseContext(), userList, null);
                    binding.recyclerview.setAdapter(adapter);
                } else {
                    Log.d("TAG", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    private void deleteDialog(String id, int pos) {
        delete_dialog = new Dialog(this);
        delete_dialog.setContentView(R.layout.delete_dialog);
        delete_dialog.setCancelable(false);
        delete_dialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER);
        delete_dialog.getWindow().setBackgroundDrawableResource(R.drawable.transparent);

        Button yes = delete_dialog.findViewById(R.id.yes);
        yes.setOnClickListener(View->{
            deleteSelectedModel(id, pos);

        });

        Button no = delete_dialog.findViewById(R.id.no);
        no.setOnClickListener(View->{
            delete_dialog.dismiss();
        });
        delete_dialog.show();
    }

}