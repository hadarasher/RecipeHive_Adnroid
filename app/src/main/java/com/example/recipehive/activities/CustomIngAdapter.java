package com.example.recipehive.activities;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipehive.R;
import com.example.recipehive.data.IngDataModel;

import java.util.ArrayList;

public class CustomIngAdapter extends RecyclerView.Adapter {
    private ArrayList<IngDataModel> dataSet;
    private SelectListenerIng clickListener;

    public CustomIngAdapter(ArrayList<IngDataModel>dataSet,SelectListenerIng clickListener){
        this.dataSet=dataSet;
        this.clickListener=clickListener;
    }

    public static class IngViewHolder extends RecyclerView.ViewHolder{
        private CardView cardView;
        private TextView ingName;
        private TextView ingAmount;
        public IngViewHolder(View itemView){
            super(itemView);

            cardView=itemView.findViewById(R.id.ingItemCardView);
            ingName=itemView.findViewById(R.id.ingredientText);
            ingAmount=itemView.findViewById(R.id.amountText);

        }
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ingredient_to_recipe_layout,parent,false);
        IngViewHolder myViewHolder=new IngViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        IngViewHolder myViewHolder=(IngViewHolder) holder;
        TextView textViewName = myViewHolder.ingName;
        TextView textViewAmount=myViewHolder.ingAmount;

        textViewName.setText(dataSet.get(position).getName());
        textViewAmount.setText(dataSet.get(position).getAmount());

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
