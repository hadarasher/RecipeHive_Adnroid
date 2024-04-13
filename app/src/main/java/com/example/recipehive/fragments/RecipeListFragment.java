package com.example.recipehive.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.recipehive.R;
import com.example.recipehive.activities.CustomIngAdapter;
import com.example.recipehive.activities.CustomRecipeAdapter;
import com.example.recipehive.activities.CustomStepAdapter;
import com.example.recipehive.activities.SelectListenerRecipe;
import com.example.recipehive.activities.SelectListenerIng;
import com.example.recipehive.activities.SelectListenerStep;
import com.example.recipehive.data.ImageRecipe;
import com.example.recipehive.data.IngDataModel;
import com.example.recipehive.data.NewRecipeData;
import com.example.recipehive.data.Recipe;
import com.example.recipehive.data.URLRecipe;
import com.example.recipehive.data.UserRecipe;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import retrofit2.http.Tag;


public class RecipeListFragment extends Fragment implements SelectListenerRecipe, SelectListenerIng, SelectListenerStep {
    private String TAG = "RecipeListFragment";
    private ArrayList<Recipe> recipeList;
    private RecyclerView recyclerViewRecipes;
    private LinearLayoutManager layoutManager;
    private CustomRecipeAdapter adapter;
    private SearchView searchRecipe;
    private Dialog imageRecipeDialog;
    private Dialog URLRecipeDialog;
    private Dialog userRecipeDialog;
    FirebaseStorage storage;
    StorageReference storageRef;

    public RecipeListFragment() {
    }

    public static RecipeListFragment newInstance() {
        RecipeListFragment fragment = new RecipeListFragment();

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
        View view = inflater.inflate(R.layout.fragment_recipe_list, container, false);

        // Set recipe dialogs by type
        imageRecipeDialog = new Dialog(getContext());
        URLRecipeDialog = new Dialog(getContext());
        userRecipeDialog = new Dialog(getContext());

        // get the Firebase  storage reference
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        recipeList = new ArrayList<>();
        // Set search line
        searchRecipe=view.findViewById(R.id.searchRecipe);
        searchRecipe.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return true;
            }
        });
        // Set recyclerView
        recyclerViewRecipes=view.findViewById(R.id.recipesRecyclerView);
        layoutManager=new LinearLayoutManager(getContext());
        recyclerViewRecipes.setLayoutManager(layoutManager);
        recyclerViewRecipes.setItemAnimator(new DefaultItemAnimator());

        adapter=new CustomRecipeAdapter(recipeList,this);
        recyclerViewRecipes.setAdapter(adapter);

        getAllRecipesFromFireBase();

        return view;
    }

    private void getAllRecipesFromFireBase() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Get user recipes
        DatabaseReference userRecipesRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("UserRecipes");
        userRecipesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Iterate through the user recipes and add them to the ArrayList
                for (DataSnapshot recipeSnapshot : snapshot.getChildren()) {
                    Recipe recipe = recipeSnapshot.getValue(UserRecipe.class);
                    recipeList.add(recipe);
                }

                // Get image recipes
                DatabaseReference imageRecipesRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("imagesRecipes");
                imageRecipesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // Iterate through the image recipes and add them to the ArrayList
                        for (DataSnapshot recipeSnapshot : snapshot.getChildren()) {
                            Recipe recipe = recipeSnapshot.getValue(ImageRecipe.class);
                            recipeList.add(recipe);
                        }

                        // Get URL recipes
                        DatabaseReference urlRecipesRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("URLRecipes");
                        urlRecipesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                // Iterate through the URL recipes and add them to the ArrayList
                                for (DataSnapshot recipeSnapshot : snapshot.getChildren()) {
                                    Recipe recipe = recipeSnapshot.getValue(URLRecipe.class);
                                    recipeList.add(recipe);
                                }

                                // Log the size of the recipe list
                                Log.d(TAG, "getAllRecipesFromFireBase: recipe list size = " + recipeList.size());
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }

        });
    }


    private void filterList(String text) {
        ArrayList<Recipe> filteredList=new ArrayList<>();
        for (Recipe recipe:recipeList){
            if (recipe.getRecipeName().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(recipe);
            }
        }
        if (filteredList.isEmpty()){
            Toast.makeText(getContext(), "No recipe found...", Toast.LENGTH_SHORT).show();
        }else{
            adapter.setFilteredList(filteredList);
        }
    }

    @Override
    public void onItemClick(Recipe recipe) {
        if(recipe instanceof ImageRecipe){
            try {
                openImageDialog((ImageRecipe) recipe);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (recipe instanceof URLRecipe) {
            openURLDialog((URLRecipe) recipe);
        } else if (recipe instanceof UserRecipe) {
            openUserDialog((UserRecipe) recipe);
        }
    }

    public void openImageDialog(ImageRecipe recipe) throws IOException {
        imageRecipeDialog.setContentView(R.layout.image_recipe_dialog);
        imageRecipeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView recipeNameTextView=imageRecipeDialog.findViewById(R.id.recipeNameTextView);
        TextView recipeTypeTextView=imageRecipeDialog.findViewById(R.id.recipeTypeTextView);
        ImageView imageRecipeView=imageRecipeDialog.findViewById(R.id.imageRecipeView);
        ImageView buttonClose=imageRecipeDialog.findViewById(R.id.buttonClose);

        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageRecipeDialog.dismiss();
            }
        });

        recipeNameTextView.setText(recipe.getRecipeName());
        recipeTypeTextView.setText(recipe.getRecipeType());

        // Get recipe image from Firebase storage by url
        StorageReference httpsReference = storage.getReferenceFromUrl(recipe.getRecipeImageUrl());

        // Download the image into a local file
        File localFile = File.createTempFile("images", "jpg");
        httpsReference.getFile(localFile)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                        imageRecipeView.setImageBitmap(bitmap);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.e(TAG, "Error downloading image: " + exception.getMessage());
                    }
                });
        imageRecipeDialog.show();
    }

    public void openURLDialog(URLRecipe recipe){
        URLRecipeDialog.setContentView(R.layout.url_recipe_dialog);
        URLRecipeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView recipeNameTextView=URLRecipeDialog.findViewById(R.id.recipeNameTextView);
        TextView recipeTypeTextView=URLRecipeDialog.findViewById(R.id.recipeTypeTextView);
        TextView urlTextView=URLRecipeDialog.findViewById(R.id.urlTextView);
        ImageView buttonClose=URLRecipeDialog.findViewById(R.id.buttonClose);

        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageRecipeDialog.dismiss();
            }
        });

        recipeNameTextView.setText(recipe.getRecipeName());
        recipeTypeTextView.setText(recipe.getRecipeType());
        urlTextView.setText(recipe.getRecipeURL());

        // Get url clickable
        urlTextView.setMovementMethod(LinkMovementMethod.getInstance());
        urlTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(recipe.getRecipeURL()));
                startActivity(intent);
            }
        });
        URLRecipeDialog.show();
    }

    public void openUserDialog(UserRecipe recipe){
        userRecipeDialog.setContentView(R.layout.user_recipe_dialog);
        userRecipeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView recipeNameTextView=userRecipeDialog.findViewById(R.id.recipeNameTextView);
        TextView recipeTypeTextView=userRecipeDialog.findViewById(R.id.recipeTypeTextView);
        RecyclerView ingredientsRecycler = userRecipeDialog.findViewById(R.id.ingredientsRecycler);
        RecyclerView stepsRecycler=userRecipeDialog.findViewById(R.id.stepsRecycler);
        ImageView buttonClose=userRecipeDialog.findViewById(R.id.buttonClose);

        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageRecipeDialog.dismiss();
            }
        });

        recipeNameTextView.setText(recipe.getRecipeName());
        recipeTypeTextView.setText(recipe.getRecipeType());

        // Handle ingredient list
        CustomIngAdapter ingAdapter;
        LinearLayoutManager inglayoutManager;

        inglayoutManager=new LinearLayoutManager(this.getContext());
        ingredientsRecycler.setLayoutManager(inglayoutManager);
        ingredientsRecycler.setItemAnimator(new DefaultItemAnimator());
        ingAdapter=new CustomIngAdapter(recipe.getRecipe().getIngredientsToRecipe(),this);
        ingredientsRecycler.setAdapter(ingAdapter);

        // Handle steps list
        CustomStepAdapter stepAdapter;
        LinearLayoutManager steplayoutManager;

        steplayoutManager=new LinearLayoutManager(this.getContext());
        stepsRecycler.setLayoutManager(steplayoutManager);
        stepsRecycler.setItemAnimator(new DefaultItemAnimator());
        stepAdapter=new CustomStepAdapter(recipe.getRecipe().getStepsToRecipe(),this);
        stepsRecycler.setAdapter(stepAdapter);

        userRecipeDialog.show();
    }

    @Override
    public void onItemClick(IngDataModel dataModel) {
        //do nothing
        //TODO: maybe change color?
    }

    @Override
    public void onItemClick(String dataModel) {
        //do nothing
        //TODO: maybe change color?
    }
}