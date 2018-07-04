package mg.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.common.images.internal.ImageUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;

import jp.wasabeef.blurry.Blurry;

public class MainActivity extends AppCompatActivity {
    EditText login_email;
    EditText login_password;
    EditText reg_userid;
    EditText reg_name;
    EditText reg_email;
    EditText reg_password;
    TextView not_reg;
    ImageButton loginBtn;
    ImageButton regBtn;
    ImageButton regbackBtn;
    View login_view;
    View reg_view;
    Animation fade_out;
    Animation fade_in;
    Animation slide_in;
    Animation slide_out;
    RelativeLayout login_frame;
    RelativeLayout reg_frame;
    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth=FirebaseAuth.getInstance();

        fade_out= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_out);
        fade_in= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
        slide_in=AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_in);
        slide_out=AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_out);
        login_frame=findViewById(R.id.login_frame);
        reg_frame=findViewById(R.id.reg_frame);

        login_email=findViewById(R.id.email);
        login_password=findViewById(R.id.password);
        loginBtn=findViewById(R.id.login_btn);

        reg_userid=findViewById(R.id.register_userid);
        reg_name=findViewById(R.id.register_name);
        reg_email=findViewById(R.id.register_email);
        reg_password=findViewById(R.id.register_password);
        regBtn=findViewById(R.id.register_btn);
        regbackBtn=findViewById(R.id.reg_back_btn);
        not_reg=findViewById(R.id.notreg_Btn);

        not_reg.setOnClickListener(notreg_listner);
        regbackBtn.setOnClickListener(backbutton_listner);
        regBtn.setOnClickListener(regBtn_listner);
        loginBtn.setOnClickListener(loginBtn_listner);

        authStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth curAuth) {
                if(curAuth.getCurrentUser()!=null){
                    Intent intent=new Intent(MainActivity.this,PostLogin.class);
                    startActivity(intent);
                    finish();
                }
            }
        };



    }
    void toggleLoader(boolean flag){
        ImageView loaderView=findViewById(R.id.loaderIV);

        Glide.with(this)
                .asGif()
                .load("file:///android_asset/loader.gif")
                .apply(new RequestOptions().override(200,200))
                .into(loaderView);

        RelativeLayout loaderLayout=findViewById(R.id.loader_layout);

        if(flag)
            loaderLayout.setVisibility(View.VISIBLE);
        else
            loaderLayout.setVisibility(View.INVISIBLE);
    }

    View.OnClickListener notreg_listner= new View.OnClickListener(){
        @Override
        public void onClick(View view) {

            login_frame.startAnimation(fade_out);
            login_frame.setVisibility(View.INVISIBLE);
            reg_frame.setVisibility(View.VISIBLE);
            reg_frame.startAnimation(slide_in);
        }
    };

    View.OnClickListener backbutton_listner=new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            reg_frame.startAnimation(slide_out);
            reg_frame.setVisibility(View.INVISIBLE);
            login_frame.setVisibility(View.VISIBLE);
            login_frame.startAnimation(fade_in);

        }
    };


    View.OnClickListener regBtn_listner=new View.OnClickListener(){
        @Override
        public void onClick(View view) {
           toggleLoader(true);
           String userid=reg_userid.getText().toString();
           String name=reg_name.getText().toString();
           String email=reg_email.getText().toString();
           String password=reg_password.getText().toString();
           if(!userid.isEmpty() && !name.isEmpty() && !email.isEmpty() && !password.isEmpty()){
               UsersDAO.addUser(userid,name,email,password);
               firebaseAuth.createUserWithEmailAndPassword(email,password)
                       .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                           @Override
                           public void onComplete(@NonNull Task<AuthResult> task) {
                               if(task.isSuccessful()){
                                   toggleLoader(false);
                                   Toast.makeText(MainActivity.this, "Registered", Toast.LENGTH_SHORT).show();
                                   reg_frame.startAnimation(slide_out);
                                   reg_frame.setVisibility(View.INVISIBLE);
                                   login_frame.setVisibility(View.VISIBLE);
                                   login_frame.startAnimation(fade_in);
                               }
                               else{
                                   Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                               }
                           }
                       });

           }
           else
           {
               Toast.makeText(MainActivity.this, "Fill all fields", Toast.LENGTH_SHORT).show();
           }

        }
    };
    View.OnClickListener loginBtn_listner=new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            toggleLoader(true);
            String email=login_email.getText().toString();
            String password=login_password.getText().toString();
            if(!email.isEmpty() && !password.isEmpty()){
                firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            Toast.makeText(MainActivity.this, "LoggedIn", Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(MainActivity.this,PostLogin.class);
                            startActivity(intent);
                            finish();
                        }
                        else{
                            Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            else
            {
                Toast.makeText(MainActivity.this, "Fill all fields", Toast.LENGTH_SHORT).show();
            }

        }
    };



}

