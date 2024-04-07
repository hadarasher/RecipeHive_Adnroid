package com.example.recipehive.fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.recipehive.R;
import com.example.recipehive.activities.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginFragment extends Fragment {
    private static final String TAG = "LoginFragment";
    private Dialog registrationDialog;

    // Initialize Firebase Auth
    private FirebaseAuth mAuth = MainActivity.getFirebaseAuth();

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();

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
        System.out.println("got to login fragment!");
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        //TODO: add try catch
        EditText emailEditText=view.findViewById(R.id.emailEditText);
        EditText passwordEdit=view.findViewById(R.id.passwordEditText);
        Button loginBtn=view.findViewById(R.id.buttonLogin);
        Button registerBtn=view.findViewById(R.id.buttonRegister);
        TextView errorMsg=view.findViewById(R.id.errorMsg);
        registrationDialog = new Dialog(view.getContext());
        Bundle bundle=new Bundle();


        /*set button listeners for login and register button.
        on login click - check user details. if found on list go to shop fragment, else get error message.
        on register click - opens dialog for registration, and add the user to the list (if username available).
         */
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=emailEditText.getText().toString().trim();
                String password=passwordEdit.getText().toString().trim();

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    //FirebaseUser user = mAuth.getCurrentUser();
                                    Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_mainMenuFragment);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    errorMsg.setVisibility(View.VISIBLE);
                                }
                            }
                        });
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrationDialog.setContentView(R.layout.register_dialog_layout);
                registrationDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                ImageView closeImg=registrationDialog.findViewById(R.id.buttonClose);
                EditText regUsernameEdit = registrationDialog.findViewById(R.id.regUsernameEditText);
                EditText regPasswordEdit = registrationDialog.findViewById(R.id.regPasswordEditText);
                EditText regMailEdit = registrationDialog.findViewById(R.id.regMailEditText);
                TextView regErrorMsg = registrationDialog.findViewById(R.id.usernameError);
                Button buttonReg=registrationDialog.findViewById(R.id.buttonRegisterLayout);

                buttonReg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String username = regUsernameEdit.getText().toString();
                        String password = regPasswordEdit.getText().toString();
                        String email = regMailEdit.getText().toString();
                        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

                        //check if all user put all details. than check username availability. if available add user to users list
                        if(!username.isEmpty() && !password.isEmpty() && email.matches(emailPattern)) {
                            mAuth.createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                // Sign in success, update UI with the signed-in user's information
                                                //TODO: add checkif the email is already in use
                                                FirebaseUser user = mAuth.getCurrentUser();
                                                if (user != null) {
                                                    String userId = user.getUid();
                                                    //FirebaseDatabase database = ;
                                                    DatabaseReference usernameRef = FirebaseDatabase.getInstance().getReference();
                                                    usernameRef.child("users").child(userId).child("username").setValue(username);
                                                registrationDialog.dismiss();
                                                Log.d(TAG,"Login succeed. Moving to Main Menu.");
                                                Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_mainMenuFragment);

                                            } else {
                                                // If sign in fails, display a message to the user.
                                                regErrorMsg.setVisibility(View.VISIBLE);
                                                }
                                            }
                                        }
                                    });

                        }else{regErrorMsg.setVisibility(View.VISIBLE);}
                    }
                });
                closeImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        registrationDialog.dismiss();
                    }
                });

                registrationDialog.show();
            }
        });




        return view;
    }


}