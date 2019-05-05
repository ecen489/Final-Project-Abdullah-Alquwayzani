package com.example.finalproject489;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements loginFrag.loginInterface {
    public loginFrag LoginFrag;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        LoginFrag=(loginFrag) getSupportFragmentManager().findFragmentById(R.id.LoginFrag);
    }
    public void createPress (String email, String password, boolean save) {
        if(save){
            getSharedPreferences("credentials",0).edit().putString("email",email);
            getSharedPreferences("credentials",0).edit().putString("pass",password);
        }
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getApplicationContext(),
                                    "Created Account",
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(
                                    MainActivity.this,
                                    EventsList.class);
                            startActivity(intent);
                        }
                        else{
                            Toast.makeText(getApplicationContext(),
                                    "Something went wrong",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        getSharedPreferences("credentials",0).edit().clear();
    }
    public void loginPress (String email, String password, boolean save) {
        if(save){
            getSharedPreferences("credentials",0).edit().putString("email",email);
            getSharedPreferences("credentials",0).edit().putString("pass",password);
        }
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(),
                                    "Login Successful!",
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(
                                    MainActivity.this,
                                    EventsList.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "Something went wrong",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        getSharedPreferences("credentials",0).edit().clear();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("email",LoginFrag.getEmail());
        outState.putString("pass",LoginFrag.getPass());
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        LoginFrag.setEmail(savedInstanceState.getString("email"));
        LoginFrag.setPass(savedInstanceState.getString("pass"));
    }
}
