package com.doctors.doctorsfinalproject.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.doctors.doctorsfinalproject.MyListener;
import com.doctors.doctorsfinalproject.R;
import com.doctors.doctorsfinalproject.activities.EditActivity;
import com.doctors.doctorsfinalproject.databinding.DoctorsItemBinding;
import com.doctors.doctorsfinalproject.model.AllDoctorsModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HiddenDoctorsAdapter extends RecyclerView.Adapter<HiddenDoctorsAdapter.viewHolder> {

    Context context;
    private ArrayList<AllDoctorsModel> list;
    MyListener myListener;

    public HiddenDoctorsAdapter(Context context, ArrayList<AllDoctorsModel> list, MyListener myListener ) {
        this.context = context;
        this.list = list;
        this.myListener = myListener;
      }


    @NonNull
    @Override
    public HiddenDoctorsAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        DoctorsItemBinding binding = DoctorsItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new viewHolder(binding);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull HiddenDoctorsAdapter.viewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.binding.doctorName.setText(list.get(position).getName());
         holder.binding.doctorMajor.setText(list.get(position).getTitle());
        holder.binding.description.setText(list.get(position).getDescription());
        Glide.with(context).load(list.get(position).getImage()).placeholder(R.drawable.ic_launcher_background).into((holder).binding.imageDoctor);


        holder.binding.edit.setVisibility(View.GONE);
        holder.binding.delete.setVisibility(View.GONE);
        holder.binding.hide.setVisibility(View.GONE);



    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }


    public   class viewHolder extends RecyclerView.ViewHolder{
        DoctorsItemBinding binding;

        public viewHolder(DoctorsItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }

    }




}

