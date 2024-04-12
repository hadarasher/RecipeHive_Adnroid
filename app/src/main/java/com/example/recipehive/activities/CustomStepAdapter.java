package com.example.recipehive.activities;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipehive.R;
import java.util.ArrayList;

public class CustomStepAdapter extends RecyclerView.Adapter {
    private ArrayList<String> dataSet;
    private SelectListenerStep clickListener;

    public CustomStepAdapter(ArrayList<String>dataSet,SelectListenerStep clickListener){
        this.dataSet=dataSet;
        this.clickListener=clickListener;
    }

    public static class StepViewHolder extends RecyclerView.ViewHolder{
        private CardView cardView;
        private TextView stepText;
        public StepViewHolder(View itemView){
            super(itemView);

            cardView=itemView.findViewById(R.id.stepItemCardView);
            stepText=itemView.findViewById(R.id.stepTextView);
        }
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.step_to_recipe_layout,parent,false);
        CustomStepAdapter.StepViewHolder myViewHolder=new CustomStepAdapter.StepViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CustomStepAdapter.StepViewHolder myViewHolder=(CustomStepAdapter.StepViewHolder) holder;
        TextView textViewStep = myViewHolder.stepText;

        textViewStep.setText(dataSet.get(position));

        myViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    clickListener.onItemClick(dataSet.get(adapterPosition));
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
