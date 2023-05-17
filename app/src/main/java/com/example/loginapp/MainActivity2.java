package com.example.loginapp;

import android.content.Intent;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.widget.ImageView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class MainActivity2 extends AppCompatActivity {
    private FloatingActionButton play;
    private FloatingActionButton next;
    private FloatingActionButton previous;
    private MediaPlayer mediaPlayer;
    private SeekBar seekBar;
    private boolean isPressed=true;
    private int position=R.raw.maan_meri_jaan;
    ImageView signout;
    TextView textView5;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore fStore;
    String userID;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    String full_name;
    String Email_ID;
    boolean loginThroughGoogle;
    boolean LoginSuccessful=false;
    boolean loginThroughFirebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        signout=findViewById(R.id.signout);
        firebaseAuth=FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this,gso);
        loginThroughGoogle=getIntent().getBooleanExtra("IsLoginGoogle",false);
        loginThroughFirebase=getIntent().getBooleanExtra("IsLoginFirebase",false);

        GoogleSignInAccount acc = GoogleSignIn.getLastSignedInAccount(this);
        if(acc!=null && loginThroughGoogle)//for google login
        {
            full_name=acc.getDisplayName();
            Email_ID=acc.getEmail();
            LoginSuccessful=true;
        }
        else if(loginThroughFirebase){ //for firebase login through manually
            userID=firebaseAuth.getCurrentUser().getUid();
            DocumentReference documentReference = fStore.collection("users").document(userID);
            documentReference.addSnapshotListener(this,new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    full_name=value.getString("Full_Name");
                    LoginSuccessful=true;
                }
            });
        }



        //start
        play=findViewById(R.id.play);
        mediaPlayer=MediaPlayer.create(this,position);
        seekBar=findViewById(R.id.seekBar);
        next=findViewById(R.id.next);
        previous=findViewById(R.id.previous);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(position==R.raw.woh)
                    position=R.raw.maan_meri_jaan;
                else
                    position++;
                mediaPlayer.pause();
                mediaPlayer.reset();
                mediaPlayer=MediaPlayer.create(MainActivity2.this,position);
                if(!isPressed)
                {
                    mediaPlayer.start();
                }
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(position==R.raw.maan_meri_jaan)
                    position=R.raw.woh;
                else
                    position--;
                mediaPlayer.pause();
                mediaPlayer.reset();
                mediaPlayer=MediaPlayer.create(MainActivity2.this,position);
                if(!isPressed)
                {
                    mediaPlayer.start();
                }
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPressed)
                {
                    //when song is played
                    isPressed=false;
                    mediaPlayer.start();
                    play.setImageResource(android.R.drawable.ic_media_pause);
                }
                else
                {
                    //when song is paused
                    isPressed=true;
                    mediaPlayer.pause();
                    play.setImageResource(android.R.drawable.ic_media_play);
                }

            }
        });

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

                seekBar.setMax(mediaPlayer.getDuration());
                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if(fromUser)
                        {
                            mediaPlayer.seekTo(progress);
                        }
                    }
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                });
            }
        });

        //end


        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(loginThroughFirebase)
                {
                    mediaPlayer.pause();
                    FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
                    firebaseAuth.signOut();
                    finish();
                    startActivity(new Intent(MainActivity2.this,MainActivity.class));
                    Toast.makeText(MainActivity2.this, "Successfully logged out", Toast.LENGTH_SHORT).show();

                }
                if(loginThroughGoogle)
                {
                    mediaPlayer.pause();
                    gsc.signOut().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            finish();
                            startActivity(new Intent(MainActivity2.this,MainActivity.class));
                            Toast.makeText(MainActivity2.this, "Successfully Logged Out", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });
    }
}