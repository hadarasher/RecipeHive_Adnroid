package com.example.recipehive.activities;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipehive.R;
import com.example.recipehive.data.Recipe;

import java.util.ArrayList;

public class CustomRecipeAdapter extends RecyclerView.Adapter {
    private ArrayList<Recipe> dataSet;
    private SelectListenerRecipe clickListener;

    public CustomRecipeAdapter(ArrayList<Recipe> dataSet, SelectListenerRecipe clickListener) {
        this.dataSet = dataSet;
        this.clickListener = clickListener;
    }
    public void setFilteredList(ArrayList<Recipe> filteredList){
        this.dataSet=filteredList;
        notifyDataSetChanged();
    }

    public static class RecipeViewHolder extends RecyclerView.ViewHolder{
        TextView recipeNameText;
        TextView recipeTypeText;
        CardView cardView;

        public RecipeViewHolder(View itemView){
            super(itemView);
            recipeNameText=itemView.findViewById(R.id.recipeNameText);
            recipeTypeText=itemView.findViewById(R.id.recipeTypeText);
            cardView=itemView.findViewById(R.id.recipeCard);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_card_layout, parent, false);
        RecipeViewHolder myViewHolder = new RecipeViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        RecipeViewHolder myViewHolder = (RecipeViewHolder) holder;

        TextView recipeNameText=myViewHolder.recipeNameText;
        TextView recipeTypeText=myViewHolder.recipeTypeText;

        recipeNameText.setText(dataSet.get(position).getRecipeName());
        recipeTypeText.setText(dataSet.get(position).getRecipeType());

        myViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onItemClick(dataSet.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

}
