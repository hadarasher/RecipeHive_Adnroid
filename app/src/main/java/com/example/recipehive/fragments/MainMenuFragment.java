package com.example.recipehive.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.recipehive.R;
import com.example.recipehive.activities.MainActivity;
import com.google.android.gms.common.util.JsonUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainMenuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainMenuFragment extends Fragment {

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
        System.out.println("Login succeed. Moving to Main Menu.");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_menu, container, false);

        TextView helloUserText=view.findViewById(R.id.helloTtextView);
        Button uploadBtn=view.findViewById(R.id.uploadRecipeBtn);
        Button addRecipeBtn=view.findViewById(R.id.addRecipeBtn);
        Button addIngredientBtn=view.findViewById(R.id.addIngredientsBtn);
        Button saveFavBtn=view.findViewById(R.id.addFavBtn);
        Button recipeListBtn=view.findViewById(R.id.allRecipesBtn);

        //get user's name to show
        FirebaseUser user = mAuth.getCurrentUser();
        if(user==null){//TODO: add error handling
            System.out.println("user does not exists. checkout for the problem.");
        }else{
            System.out.println("User authenticated. getting username.");
        }
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child(getString(R.string.username));
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                username = snapshot.getValue(String.class);
                helloUserText.setText("Hello, "+username);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


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
}