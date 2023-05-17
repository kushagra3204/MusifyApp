package com.example.loginapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    ImageView sign_in_button;
    EditText email;
    EditText password;
    TextView sign_up_textView;
    CircleImageView facebook_sign_in_btn;
    CircleImageView google_sign_in_btn;
    CircleImageView twitter_sign_in_btn;
    ProgressBar progressBar2;
    FirebaseAuth firebaseAuth;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    boolean loginThroughFirebase=false;
    boolean loginThroughGoogle=false;
    private SignInClient oneTapClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sign_in_button=findViewById(R.id.sign_in_button);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        google_sign_in_btn=findViewById(R.id.google_sign_in_btn);
        facebook_sign_in_btn=findViewById(R.id.facebook_sign_in_btn);
        twitter_sign_in_btn=findViewById(R.id.twitter_sign_btn);
        sign_up_textView=findViewById(R.id.sign_up_textView);
        progressBar2=findViewById(R.id.progressBar2);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this,gso);
        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        GoogleSignInAccount acc = GoogleSignIn.getLastSignedInAccount(this);

        if(currentUser!=null && currentUser.isEmailVerified())
        {
            loginThroughFirebase=true;
            CallingMainActivity2();
            finish();
        }
        else if(acc!=null)
        {
            loginThroughGoogle=true;
            CallingMainActivity2();
            finish();
        }

        sign_in_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sign_in_button.setImageAlpha(200);
                sign_in_button.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sign_in_button.setImageAlpha(255);
                    }
                },60);
                
                String Email=email.getText().toString();
                String Password=password.getText().toString();
                if(!validateData(Email,Password))
                {
                    return;
                }

                LoginAccountInFirebase(Email,Password);
            }
        });

        google_sign_in_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSignIn();
            }
        });

        facebook_sign_in_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Facebook", Toast.LENGTH_SHORT).show();
            }
        });

        twitter_sign_in_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Twitter", Toast.LENGTH_SHORT).show();
            }
        });

        sign_up_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,signupactivity.class);
                startActivity(intent);
                finish();
            }
        });


    }


    boolean validateData(String Email,String Password)
    {
        if(Email.equals(""))
        {
            email.setError("Field should not be empty");
            return false;
        }
        if(Password.equals(""))
        {
            password.setError("Field should not be empty");
            return false;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(Email).matches())
        {
            email.setError("Invalid Email-ID");
            return false;
        }
        if(Password.length()<=6)
        {
            password.setError("Password should have atleast 6 characters");
            return false;
        }
        return true;
    }


    void ChangeInProgress(boolean isProgress)
    {
        if(isProgress)
        {
            progressBar2.setVisibility(View.VISIBLE);
            sign_in_button.setVisibility(View.GONE);
        }
        else
        {
            progressBar2.setVisibility(View.GONE);
            sign_in_button.setVisibility(View.VISIBLE);
        }
    }


    void LoginAccountInFirebase(String Email,String Password)
    {

        firebaseAuth = FirebaseAuth.getInstance();
        ChangeInProgress(true);
        firebaseAuth.signInWithEmailAndPassword(Email,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                ChangeInProgress(false);
                if(task.isSuccessful())
                {
                    if(firebaseAuth.getCurrentUser().isEmailVerified())
                    {
                        loginThroughFirebase=true;
                        finish();
                        CallingMainActivity2();
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this, "Email not verified, please verify your email through link sent to your email-id", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(MainActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    void googleSignIn()
    {
        Intent signInIntent = gsc.getSignInIntent();
        startActivityIfNeeded(signInIntent,1000);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1000)
        {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount acc=task.getResult(ApiException.class);
                String idac=acc.getId();
//                firebaseAuthWithGoogle(acc);
                navigateToSecondActivity();
            } catch (ApiException e) {
                Toast.makeText(this, "Something went wrong!!", Toast.LENGTH_SHORT).show();
            }

        }

    }

    void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        if(acct.getId()!=null)
        {
            AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(acct.getId(), null);
            firebaseAuth.signInWithCredential(firebaseCredential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    void navigateToSecondActivity()
    {
        loginThroughGoogle=true;
        finish();
        CallingMainActivity2();
    }

    void CallingMainActivity2()
    {
        Intent intent = new Intent(MainActivity.this,MainActivity2.class);
        intent.putExtra("IsLoginGoogle",loginThroughGoogle);
        intent.putExtra("IsLoginFirebase",loginThroughFirebase);
        startActivity(intent);
    }
}