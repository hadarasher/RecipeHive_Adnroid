package com.example.recipehive.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.recipehive.R;
import com.example.recipehive.activities.CustomRecipeAdapter;
import com.example.recipehive.activities.SelectListenerRecipe;
import com.example.recipehive.data.ImageRecipe;
import com.example.recipehive.data.NewRecipeData;
import com.example.recipehive.data.Recipe;
import com.example.recipehive.data.URLRecipe;
import com.example.recipehive.data.UserRecipe;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import retrofit2.http.Tag;


public class RecipeListFragment extends Fragment implements SelectListenerRecipe {
    private String TAG = "RecipeListFragment";
    private ArrayList<Recipe> recipeList;
    private RecyclerView recyclerViewRecipes;
    private LinearLayoutManager layoutManager;
    private CustomRecipeAdapter adapter;
    private SearchView searchRecipe;
    //TODO: add dialogs to show full recipe

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
        //TODO: set dialog to show full recipe (by type?)
    }
}