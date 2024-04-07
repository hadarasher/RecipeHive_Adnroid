package com.example.recipehive.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.recipehive.R;
import com.example.recipehive.data.Ingredient;
import com.example.recipehive.services.DataService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.MalformedURLException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static FirebaseAuth mAuth;
    public static ArrayList<Ingredient> arrIng;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth=FirebaseAuth.getInstance();
        MainActivity.arrIng = new ArrayList<>();

        setContentView(R.layout.activity_main);

    }

    public static FirebaseAuth getFirebaseAuth(){
        return mAuth;
    }


}