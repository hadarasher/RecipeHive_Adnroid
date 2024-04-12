package com.example.recipehive.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.recipehive.R;
import com.example.recipehive.activities.CustomIngAdapter;
import com.example.recipehive.activities.CustomStepAdapter;
import com.example.recipehive.activities.MainActivity;
import com.example.recipehive.activities.SelectListenerIng;
import com.example.recipehive.activities.SelectListenerStep;
import com.example.recipehive.data.IngDataModel;
import com.example.recipehive.data.Ingredient;
import com.example.recipehive.data.NewRecipeData;
import com.example.recipehive.data.UserRecipe;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class AddRecipeFragment extends Fragment implements SelectListenerIng, SelectListenerStep {

    private String TAG = "AddRecipeFragment";
    private String recipeName;
    private String recipeTypeAddSpinnerString;
    private NewRecipeData recipe;
    private RecyclerView ingredientsRecyclerView;
    private ArrayList<IngDataModel> ingDataSet;
    private CustomIngAdapter ingAdapter;
    private LinearLayoutManager inglayoutManager;
    private RecyclerView stepsRecyclerView;
    private ArrayList<String> stepsDataSet;
    private CustomStepAdapter stepAdapter;
    private LinearLayoutManager steplayoutManager;
    private Dialog dialogAddIng;
    private Dialog dialogAddStep;
    private Dialog dialogDeleteItem;
    private ImageView buttonClose;


    public static AddRecipeFragment newInstance() {
        AddRecipeFragment fragment = new AddRecipeFragment();

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
        View view = inflater.inflate(R.layout.fragment_add_recipe, container, false);

        recipe=new NewRecipeData();

        EditText recipeNameAddEditText=view.findViewById(R.id.recipeNameAddEditText);
        Spinner recipeTypeAddSpinner=view.findViewById(R.id.recipeTypeAddSpinner);
        ingredientsRecyclerView=view.findViewById(R.id.ingredientsRecyclerView);
        Button addIngredBtn=view.findViewById(R.id.addIngredBtn);
        stepsRecyclerView=view.findViewById(R.id.stepsRecyclerView);
        Button addStepBtn=view.findViewById(R.id.addStepBtn);
        Button saveRecipeBtn=view.findViewById(R.id.saveRecipeBtn);
        dialogAddIng=new Dialog(getContext());
        dialogAddStep=new Dialog(getContext());
        dialogDeleteItem=new Dialog(getContext());

        // Set spinner
        ArrayAdapter arrayAdapter=new ArrayAdapter(getContext(), R.drawable.spinner_item,getResources().getStringArray(R.array.recipe_types));
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        recipeTypeAddSpinner.setAdapter(arrayAdapter);
        recipeTypeAddSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                recipeTypeAddSpinnerString = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Set up ingredients recycler view
        inglayoutManager=new LinearLayoutManager(this.getContext());
        ingredientsRecyclerView.setLayoutManager(inglayoutManager);
        ingredientsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        ingAdapter=new CustomIngAdapter(recipe.getIngredientsToRecipe(),this);
        ingredientsRecyclerView.setAdapter(ingAdapter);


        // Set up steps recycler view
        steplayoutManager=new LinearLayoutManager(this.getContext());
        stepsRecyclerView.setLayoutManager(steplayoutManager);
        stepsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        stepAdapter=new CustomStepAdapter(recipe.getStepsToRecipe(),this);
        stepsRecyclerView.setAdapter(stepAdapter);

        // Set buttons
        addIngredBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddIngDialog();
            }
        });
        addStepBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddStepDialog();
            }
        });

        saveRecipeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recipeName = recipeNameAddEditText.getText().toString();
                if(recipe.getIngredientsSize() == 0){
                    Toast.makeText(getContext(), "Recipe ingredients must be added first...", Toast.LENGTH_SHORT).show();
                }else if(recipe.getStepsSize()==0){
                    Toast.makeText(getContext(), "Recipe steps must be added first...", Toast.LENGTH_SHORT).show();
                } else if (recipeName.isEmpty()) {
                    Toast.makeText(getContext(), "Recipe name must be added first...", Toast.LENGTH_SHORT).show();
                }else{
                    UploadRecipeToFirebase();
                }
            }
        });


        return view;
    }

    @Override
    public void onItemClick(IngDataModel dataModel) {
        dialogDeleteItem.setContentView(R.layout.delete_item_dialog_layout);
        dialogDeleteItem.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button deleteDialogBtn = dialogDeleteItem.findViewById(R.id.deleteDialogBtn);
        ImageView buttonClose = dialogDeleteItem.findViewById(R.id.buttonClose);

        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDeleteItem.dismiss();
            }
        });
        deleteDialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                recipe.removeIngredientFromRecipe(dataModel);
                ingAdapter.notifyDataSetChanged();
                dialogDeleteItem.dismiss();
            }
        });
    }

    @Override
    public void onItemClick(String dataModel) {
        dialogDeleteItem.setContentView(R.layout.delete_item_dialog_layout);
        dialogDeleteItem.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button deleteDialogBtn = dialogDeleteItem.findViewById(R.id.deleteDialogBtn);
        ImageView buttonClose = dialogDeleteItem.findViewById(R.id.buttonClose);

        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDeleteItem.dismiss();
            }
        });
        deleteDialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recipe.removeStepFromRecipe(dataModel);
                stepAdapter.notifyDataSetChanged();
                dialogDeleteItem.dismiss();
            }
        });
        dialogDeleteItem.show();

    }

    private void openAddIngDialog(){
        dialogAddIng.setContentView(R.layout.add_ingredient_to_recipe_dialog_layout);
        dialogAddIng.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        EditText amountDialogEditText=dialogAddIng.findViewById(R.id.amountDialogEditText);
        AutoCompleteTextView enterIngoComplete=dialogAddIng.findViewById(R.id.enterIngoComplete);
        Button addIngDialogBtn=dialogAddIng.findViewById(R.id.addIngDialogBtn);
        ImageView buttonClose= dialogAddIng.findViewById(R.id.buttonClose);

        // Set close button
        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogAddIng.dismiss();
            }
        });

        // Set autocomplete text
        //IngredientAutoCompleteAdapter adapter = new IngredientAutoCompleteAdapter(getContext(), MainActivity.arrIng);
        ArrayAdapter<Ingredient> arrayIngAdapter = new ArrayAdapter<>(getActivity(),R.drawable.spinner_item,MainActivity.arrIng);
        enterIngoComplete.setThreshold(5);
        enterIngoComplete.setAdapter(arrayIngAdapter);
        Log.d(TAG, "openAddIngDialog: got autocomplete ingredient name adapter");
        addIngDialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amount = amountDialogEditText.getText().toString();
                String ingredient = enterIngoComplete.getText().toString();

                if(isIngredientValid(ingredient)){
                  recipe.addIngredientToRecipe(new IngDataModel(ingredient, amount));
                  ingAdapter.notifyDataSetChanged();
                  dialogAddIng.dismiss();
                }else {
                    enterIngoComplete.setTextColor(getResources().getColor(R.color.red));
                    Toast.makeText(getContext(), "Ingredient not found. Add new Ingredient.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialogAddIng.show();
    }

    private void openAddStepDialog(){
        dialogAddStep.setContentView(R.layout.add_step_dialog_layyout);
        dialogAddStep.getWindow().setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));

        EditText stepDialogEditText = dialogAddStep.findViewById(R.id.stepDialogEditText);
        Button addStepDialogBtn=dialogAddStep.findViewById(R.id.addStepDialogBtn);
        ImageView buttonClose=dialogAddStep.findViewById(R.id.buttonClose);

        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogAddIng.dismiss();
            }
        });
        addStepDialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String step=stepDialogEditText.getText().toString();
                if(step.isEmpty()){
                    Toast.makeText(getContext(), "You must enter step first", Toast.LENGTH_SHORT).show();
                }else {
                    recipe.addStepToRecipe(step);
                    stepAdapter.notifyDataSetChanged();
                    dialogAddStep.dismiss();
                }
            }
        });
        dialogAddStep.show();
    }

    private boolean isIngredientValid(String enteredText) {
        for (Ingredient ingredient : MainActivity.arrIng) {
            if (ingredient.getName().equalsIgnoreCase(enteredText)) {
                return true;
            }
        }
        return false;
    }

    private void UploadRecipeToFirebase(){ //TODO: add name and type!
        // Showing progressDialog while uploading
        ProgressDialog progressDialog= new ProgressDialog(getContext());
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        UserRecipe userRecipe=new UserRecipe(recipeName,recipeTypeAddSpinnerString,recipe);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference recipesRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("UserRecipes");
        String recipeKey = recipesRef.push().getKey();
        recipesRef.child(recipeKey).setValue(userRecipe);
    }
}