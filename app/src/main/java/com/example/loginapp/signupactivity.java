package com.example.loginapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Document;

import java.security.Timestamp;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class signupactivity extends AppCompatActivity {
    public static final String TAG = "TAG";
    private TextView login_textView;
    private EditText username_sign_up;
    private EditText email_sign_up;
    private EditText phone_sign_up;
    private EditText password_sign_up;
    private EditText password_again_sign_up;
    private ImageView sign_up_button;
    private ProgressBar progressBar;
    FirebaseFirestore fstore;
    FirebaseAuth firebaseAuth;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signupactivity);
        login_textView=findViewById(R.id.login_textView);
        username_sign_up=findViewById(R.id.username_sign_up);
        phone_sign_up=findViewById(R.id.phone_sign_up);
        email_sign_up=findViewById(R.id.email_sign_up);
        password_sign_up=findViewById(R.id.password_sign_up);
        password_again_sign_up=findViewById(R.id.password_again_sign_up);
        sign_up_button=findViewById(R.id.sign_up_button);
        progressBar=findViewById(R.id.progressBar);
        fstore=FirebaseFirestore.getInstance();

        sign_up_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sign_up_button.setImageAlpha(200);
                sign_up_button.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sign_up_button.setImageAlpha(255);
                    }
                },60);
                String Username=username_sign_up.getText().toString();
                String Email=email_sign_up.getText().toString();
                String Phone=phone_sign_up.getText().toString();
                String Password=password_sign_up.getText().toString();
                String ConfirmPassword=password_again_sign_up.getText().toString();
                if(!validateData(Username,Email,Password,ConfirmPassword))
                {
                    return;
                }
                CreateAccountInFirebase(Username,Phone,Email,Password);
            }
        });


        login_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(signupactivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }


    boolean validateData(String Username,String Email,String Password,String ConfirmPassword)
    {
        if(Username.equals(""))
        {
            username_sign_up.setError("Field should not be empty");
            return false;
        }
        if(Email.equals(""))
        {
            email_sign_up.setError("Field should not be empty");
            return false;
        }
        if(Password.equals(""))
        {
            password_sign_up.setError("Field should not be empty");
            return false;
        }
        if(ConfirmPassword.equals(""))
        {
            password_again_sign_up.setError("Field should not be empty");
            return false;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(Email).matches())
        {
            email_sign_up.setError("Invalid Email-ID");
            return false;
        }
        if(Password.length()<=6)
        {
            password_sign_up.setError("Password should have atleast 6 characters");
            password_again_sign_up.setError("Password should have atleast 6 characters");
            return false;
        }
        if(!Password.equals(ConfirmPassword))
        {
            password_sign_up.setError("Password not matched");
            password_again_sign_up.setError("Password not matched");
            return false;
        }
        return true;
    }

    void CreateAccountInFirebase(String Username,String Phone,String Email,String Password)
    {
        ChangeInProgress(true);
        firebaseAuth = FirebaseAuth.getInstance();

        firebaseAuth.createUserWithEmailAndPassword(Email,Password).addOnCompleteListener(signupactivity.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        ChangeInProgress(false);
                        if(task.isSuccessful())
                        {
                            Toast.makeText(signupactivity.this, "Account created successfully, Check email to verify", Toast.LENGTH_SHORT).show();
                            Objects.requireNonNull(firebaseAuth.getCurrentUser()).sendEmailVerification();
                            userID = firebaseAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fstore.collection("users").document(userID);
                            Map<String,Object> user = new HashMap<>();
                            user.put("Full_Name",Username);
                            user.put("Phone_No",Phone);
                            user.put("Email-ID",Email);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d(TAG,"onSuccess: user Profile is created for "+userID);
                                    Intent i1 = new Intent(signupactivity.this,MainActivity.class);
                                }
                            });

                            finish();

                        }
                        else
                        {
                            Toast.makeText(signupactivity.this, Objects.requireNonNull(task.getException()).getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
//        firebaseAuth.signOut();
//        finish();
    }

    void ChangeInProgress(boolean isProgress)
    {
        if(isProgress)
        {
            progressBar.setVisibility(View.VISIBLE);
            sign_up_button.setVisibility(View.GONE);
        }
        else
        {
            progressBar.setVisibility(View.GONE);
            sign_up_button.setVisibility(View.VISIBLE);
        }
    }



}