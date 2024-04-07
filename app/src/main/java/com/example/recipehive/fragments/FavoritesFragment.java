package com.example.recipehive.fragments;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.recipehive.R;
import com.example.recipehive.data.ImageRecipe;
import com.example.recipehive.data.URLRecipe;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;


public class FavoritesFragment extends Fragment {

    private static final String TAG = "FavoritesFragment";
    private String recipeTypeSpinnerString;
    private String recipeName;
    private String recipeURL;

    public static FavoritesFragment newInstance() {
        FavoritesFragment fragment = new FavoritesFragment();

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
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        EditText recipeNameFavEditText=view.findViewById(R.id.recipeNameFavEditText);
        Spinner recipeTypeFavSpinner=view.findViewById(R.id.recipeTypeFavSpinner);
        EditText urlEditText=view.findViewById(R.id.urlEditText);
        Button addFavToListBtn=view.findViewById(R.id.addFavToListBtn);

        // Set spinner values
        ArrayAdapter arrayAdapter=new ArrayAdapter(getContext(), R.drawable.spinner_item,getResources().getStringArray(R.array.recipe_types));
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        recipeTypeFavSpinner.setAdapter(arrayAdapter);
        recipeTypeFavSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                recipeTypeSpinnerString = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Set save to favorite button click
        addFavToListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recipeName=recipeNameFavEditText.getText().toString();
                recipeURL=urlEditText.getText().toString();
                Log.d(TAG, "recipe name = " + recipeName);
                Log.d(TAG, "recipe URL = " + recipeURL);
                if(recipeName.isEmpty()) {
                    Toast.makeText(getContext(), "Recipe name must be added first...", Toast.LENGTH_SHORT).show();
                } else if (recipeURL.isEmpty()) {
                    Toast.makeText(getContext(), "Recipe URL not added...", Toast.LENGTH_SHORT).show();
                }else{
                    uploadURLToFirebase();
                }
            }
        });


        return view;
    }

    private void uploadURLToFirebase()
    {

        // Showing progressDialog while uploading
        ProgressDialog progressDialog= new ProgressDialog(getContext());
        progressDialog.setTitle("Uploading...");
        progressDialog.show();


        // adding listeners on upload or failure of image

        URLRecipe urlRecipe=new URLRecipe(recipeName,recipeTypeSpinnerString,recipeURL);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference recipesRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("URLRecipes");
        String recipeKey = recipesRef.push().getKey();
        recipesRef.child(recipeKey).setValue(urlRecipe);

        // Show a success message
        progressDialog.dismiss();
        Toast.makeText(getContext()," Recipe Added!",Toast.LENGTH_SHORT).show();
        Navigation.findNavController(getView()).navigate(R.id.action_favoritesFragment_to_mainMenuFragment);
    }
}