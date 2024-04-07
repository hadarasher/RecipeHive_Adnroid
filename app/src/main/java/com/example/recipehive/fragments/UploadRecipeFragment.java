package com.example.recipehive.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.health.connect.datatypes.units.Length;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.recipehive.R;
import com.example.recipehive.data.ImageRecipe;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;


public class UploadRecipeFragment extends Fragment {
    private String recipeTypeSpinnerString;
    private String recipeName;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int REQUEST_CAMERA_PERMISSION = 3;
    private static final int REQUEST_EXTERNAL_STORAGE_PERMISSION = 4;

    private Uri imageUri;

    ImageView recipeImageView;
    FirebaseStorage storage;
    StorageReference storageReference;
    public static UploadRecipeFragment newInstance() {
        UploadRecipeFragment fragment = new UploadRecipeFragment();
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
        View view = inflater.inflate(R.layout.fragment_upload_recipe, container, false);

        EditText recipeNameEdit=view.findViewById(R.id.recipeNameEditText);
        Button uploadFromGalleryBtn=view.findViewById(R.id.uploadFromGalleryBtn);
        Button pictureWithCameraBtn=view.findViewById(R.id.pictureWithCameraBtn);
        Spinner recipeTypeSpinner=view.findViewById(R.id.recipeTypeSpinner);
        recipeImageView=view.findViewById(R.id.recipeImageView);
        Button saveRecipeBtn=view.findViewById(R.id.saveRecipeBtn);

        // get the Firebase  storage reference
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        // Set spinner values
        ArrayAdapter arrayAdapter=new ArrayAdapter(getContext(), R.drawable.spinner_item,getResources().getStringArray(R.array.recipe_types));
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        recipeTypeSpinner.setAdapter(arrayAdapter);
        recipeTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                recipeTypeSpinnerString = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //set buttons' listeners
        uploadFromGalleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: handle gallery permissions
                SelectImage();
            }
        });
        pictureWithCameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestCameraPermission();
            }
        });
        saveRecipeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recipeName=recipeNameEdit.getText().toString();
                System.out.println("recipe name = "+recipeName);
                System.out.println("image uri = "+ imageUri.toString());
                if(recipeName.isEmpty()) {
                    Toast.makeText(getContext(), "Recipe name must be added first...", Toast.LENGTH_SHORT).show();
                } else if (imageUri==null) {
                    Toast.makeText(getContext(), "Recipe image not added...", Toast.LENGTH_SHORT).show();
                }else {
                    uploadImageToFirebase();
                }
            }
        });



        return view;
    }

    private void SelectImage() {
        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            System.out.println("Permission is not granted, request it");
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        } else {
            System.out.println("Permission is already granted, proceed with capturing image");
            dispatchTakePictureIntent();
        }
    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    System.out.println("Camera permission granted, proceed with capturing image");
                    dispatchTakePictureIntent();
                } else {
                    System.out.println("Camera permission denied");
                    Toast.makeText(getContext(), "Camera permission is required to capture images", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_EXTERNAL_STORAGE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    System.out.println("External storage permission granted, proceed with opening the gallery");
                    SelectImage();
                } else {
                    System.out.println("External storage permission denied");
                    Toast.makeText(getContext(), "Gallery access permission is required", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            recipeImageView.setImageURI(imageUri);
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            recipeImageView.setImageBitmap(imageBitmap);
        }
    }

    private void requestExternalStoragePermission() {
        if(getActivity()==null) System.out.println("Activity = null");
        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            System.out.println("Permission is not granted, request it");
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_EXTERNAL_STORAGE_PERMISSION);
            System.out.println("after gallery permission request");
        } else {
            System.out.println("Permission is already granted, proceed with opening the gallery");
            SelectImage();
        }
    }

    private void uploadImageToFirebase()
    {
        if (imageUri != null) {
            // Showing progressDialog while uploading
            ProgressDialog progressDialog= new ProgressDialog(getContext());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Defining the child of storageReference
            StorageReference imgRef = storageReference.child("images/"+ UUID.randomUUID().toString());

            // adding listeners on upload or failure of image
            imgRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                                {
                                    // Image uploaded successfully, get the download URL
                                    imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            // URI of the uploaded image
                                            String imgUrl = uri.toString();

                                            ImageRecipe imgRecipe=new ImageRecipe(recipeName,recipeTypeSpinnerString,imgUrl);

                                            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                            DatabaseReference recipesRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("imagesRecipes");
                                            //userRef.child("profileImageUrl").setValue(imgUrl);
                                            String recipeKey = recipesRef.push().getKey();
                                            recipesRef.child(recipeKey).setValue(imgRecipe);

                                            // Show a success message
                                            progressDialog.dismiss();
                                            Toast.makeText(getContext(),"Image Uploaded!",Toast.LENGTH_SHORT).show();
                                            Navigation.findNavController(getView()).navigate(R.id.action_uploadRecipeFragment_to_mainMenuFragment);
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            // Error, Image not uploaded
                            progressDialog.dismiss();
                            Toast.makeText(getContext(),"Failed " + e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                // Progress Listener for loading percentage on the dialog box
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot)
                                {
                                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage("Uploaded " + (int)progress + "%");
                                }
                            });
        }
    }

}