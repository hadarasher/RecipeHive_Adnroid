package com.example.recipehive.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.recipehive.R;
import com.example.recipehive.activities.MainActivity;
import com.example.recipehive.data.Ingredient;
import com.example.recipehive.services.DataService;
import com.google.android.gms.common.util.JsonUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.MalformedURLException;
import java.util.ArrayList;


public class MainMenuFragment extends Fragment {
    private static final String TAG = "MainMenuFragment";
    private FirebaseAuth mAuth= MainActivity.getFirebaseAuth();
    private String username;



    public static MainMenuFragment newInstance() {
        MainMenuFragment fragment = new MainMenuFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_menu, container, false);

        getIngredientList();

        TextView helloUserText=view.findViewById(R.id.helloTtextView);
        Button uploadBtn=view.findViewById(R.id.uploadRecipeBtn);
        Button addRecipeBtn=view.findViewById(R.id.addRecipeBtn);
        Button addIngredientBtn=view.findViewById(R.id.addIngredientsBtn);
        Button saveFavBtn=view.findViewById(R.id.addFavBtn);
        Button recipeListBtn=view.findViewById(R.id.allRecipesBtn);

        //get user's name to show
        FirebaseUser user = mAuth.getCurrentUser();
        if(user==null){
            Log.d(TAG, "onCreateView: user does not exists. checkout for the problem.");
            Toast.makeText(getContext(), "No user found. Please login again.", Toast.LENGTH_SHORT).show();
        }else {
            Log.d(TAG, "onCreateView: User authenticated. getting username.");
            String userId = user.getUid();
            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("username");
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    username = snapshot.getValue(String.class);
                    helloUserText.setText("Hello, " + username);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }


        //Handle every button click to move to the relevant fragment
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_mainMenuFragment_to_uploadRecipeFragment);
            }
        });
        addRecipeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_mainMenuFragment_to_addRecipeFragment);
            }
        });
        addIngredientBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_mainMenuFragment_to_addIngredientFragment);
            }
        });
        saveFavBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_mainMenuFragment_to_favoritesFragment);
            }
        });
        recipeListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_mainMenuFragment_to_recipeListFragment);
            }
        });


        return view;
    }

    public void getIngredientList(){
        Log.d(TAG,"Getting ingredient list");
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Getting ingredients from the API, then get the users' added ingredients from the storage.
        try {
            MainActivity.arrIng = DataService.getArrIngredients();

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        if (currentUser!=null) {
            DatabaseReference userIngredientsRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid()).child("ingredients");
            userIngredientsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        // Get ingredient data and add to arrIng
                        Ingredient ingredient = new Ingredient(snapshot.child("name").getValue(String.class));
                        MainActivity.arrIng.add(ingredient);
                    }

                    // Print the ingredients added to arrIng
                    Log.d(TAG, "Ingredients in list: " + MainActivity.arrIng.size());

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("MainActivity", "Failed to read ingredients from database", databaseError.toException());
                }
            });
        }
    }
}