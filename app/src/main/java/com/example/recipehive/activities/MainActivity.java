package com.example.recipehive.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.example.recipehive.R;
import com.example.recipehive.data.Ingredient;
import com.example.recipehive.services.DataService;
import com.google.firebase.auth.FirebaseAuth;

import java.net.MalformedURLException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth=FirebaseAuth.getInstance();


        try {
            ArrayList<Ingredient> arrIng= DataService.getArrIngredients();

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        setContentView(R.layout.activity_main);
    }

    public static FirebaseAuth getFirebaseAuth(){
        return mAuth;
    }


}