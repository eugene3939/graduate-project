package com.example.greenyourlife;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

public class friends_gallery extends AppCompatActivity {
    //initialize variable
    SliderView sliderView;
    int[] images = {R.drawable.car1,R.drawable.car2,R.drawable.car3,R.drawable.car5};

    SlideAdapter slideAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_gallery);

        sliderView = findViewById(R.id.gallery_sliderView);
        slideAdapter = new SlideAdapter(images);
        sliderView.setSliderAdapter(slideAdapter);
        //indicator animation
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
        //transformation animation
        sliderView.setSliderTransformAnimation(SliderAnimations.DEPTHTRANSFORMATION);
        //start auto cycle
        sliderView.startAutoCycle();

        //if is user can update picture
    }
}