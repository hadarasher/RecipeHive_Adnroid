package com.example.recipehive.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.recipehive.R;
import com.example.recipehive.activities.MainActivity;
import com.example.recipehive.data.Ingredient;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;



public class AddIngredientFragment extends Fragment {

    private DatabaseReference ingredientsRef;

    public static AddIngredientFragment newInstance() {
        AddIngredientFragment fragment = new AddIngredientFragment();


        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ingredientsRef = FirebaseDatabase.getInstance().getReference();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_ingredient, container, false);

        EditText ingNameEditText = view.findViewById(R.id.ingNameEditText);
        Button addIngToListBtn = view.findViewById(R.id.addIngToListBtn);

        addIngToListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ingredientStr=ingNameEditText.getText().toString().trim();
                
                if (ingredientStr.isEmpty()) {
                    Toast.makeText(getContext(),"Enter ingredient first",Toast.LENGTH_SHORT).show();
                } else {
                    Ingredient ingredient=new Ingredient(ingredientStr);
                    if (isContainsIng(ingredient)) {
                        Toast.makeText(getContext(),"Ingredient already exists",Toast.LENGTH_SHORT).show();
                    }else{
                        // Add ingredient to Firebase Realtime Database
                        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        String ingredientKey = ingredientsRef.push().getKey();
                        ingredientsRef.child("users").child(userId).child("ingredients").child(ingredientKey).setValue(ingredient);


                        MainActivity.arrIng.add(ingredient);

                        Toast.makeText(getContext(),"Ingredient added!",Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(view).navigate(R.id.action_addIngredientFragment_to_mainMenuFragment);
                    }
                }
            }
        });



        return view;
    }

    public Boolean isContainsIng(Ingredient ing){
        for (Ingredient ingredient: MainActivity.arrIng){
            if (ingredient.isEqual(ing)) return true;
        }
        return false;
    }
}