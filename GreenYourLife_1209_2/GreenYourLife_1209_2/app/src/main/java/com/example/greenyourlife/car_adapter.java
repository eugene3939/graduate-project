package com.example.greenyourlife;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class car_adapter extends ArrayAdapter<userCar> {
    Context context;
    int resourceId;
    List<userCar> carList;

    public car_adapter(@NonNull Context context, int resource, @NonNull List<userCar> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resourceId = resource;
        this.carList = objects;
    }

    @Override
    public int getCount() {
        return carList.size();
    }

    @Nullable
    @Override
    public userCar getItem(int position) {
        return carList.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        try {
            if (view == null){
                LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = layoutInflater.inflate(resourceId,parent,false);

                userCar userCar = carList.get(position);
                if (userCar != null){
                    TextView name = view.findViewById(R.id.car_name);
                    ImageView imageView = view.findViewById(R.id.car_image);

                    name.setText(userCar.getName());
                    imageView.setImageResource(userCar.getImage());
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return view;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        try {
            if (view == null){
                LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = layoutInflater.inflate(resourceId,parent,false);

                userCar userCar = carList.get(position);
                if (userCar != null){
                    TextView name = view.findViewById(R.id.car_name);
                    ImageView imageView = view.findViewById(R.id.car_image);

                    name.setText(userCar.getName());
                    imageView.setImageResource(userCar.getImage());
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return view;
    }
}
