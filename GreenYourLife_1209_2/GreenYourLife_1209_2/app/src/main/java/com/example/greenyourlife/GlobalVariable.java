package com.example.greenyourlife;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Application;
public class GlobalVariable  extends android.app.Application {
    private int a,b;//coupon counter
    public void seta(int a){
        this.a=a;
    }
    public void setb(int b){
        this.b=b;
    }
    public int geta(){
        return a;
    }
    public int getb(){
        return b;
    }
}

