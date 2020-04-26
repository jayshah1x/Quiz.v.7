package com.bvm.ui_example_4;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.SharedElementCallback;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    public static  final String PREFS_NAME = "PrefsFile";
    private static EditText Username, Password, Temp;
    private static Button Login, register;
    private static String pass_var, user_var;
    private static Button Login_btn, Register_btn;
    private static String username, password1, password;
    private static CheckBox remember;
    SharedPreferences sharedPreferences;
    SharedPreferences mypref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        Username = findViewById(R.id.username);
        Password = findViewById(R.id.password);
        Login = findViewById(R.id.login);
        register = findViewById(R.id.register);
        Login_btn = findViewById(R.id.login);
       // Temp = findViewById(R.id.password1);
        Register_btn = findViewById(R.id.register);
        remember = findViewById(R.id.remember);


        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        Register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Second.class);
                startActivity(intent);
            }
        });

        Login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
            }
        });

        getDetails();



    }

    private void getDetails() {

        if(sharedPreferences.contains("user_p")){
            String user = sharedPreferences.getString("user_p", "not found");
            Username.setText(user);
        }
        if(sharedPreferences.contains("password_p")){
            String pass = sharedPreferences.getString("password_p", "not found");
            Password.setText(pass);
        }
        if(sharedPreferences.contains("check_p")){
            Boolean b = sharedPreferences.getBoolean("check_p",false);
            remember.setChecked(b);
        }
    }

    private void validate() {

        if(validatePassword() || validateUsername())
        {

            checkFirebase();
        }
    }

    private void checkFirebase() {
         username = Username.getText().toString();

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("Login Info").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild(username)) {
                    password = snapshot.child(username).child("pass").getValue().toString();
                    password1 = Password.getText().toString();
                    if(password.equals(password1)){
                        if(remember.isChecked()){
                            Boolean check = remember.isChecked();
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("user_p", Username.getText().toString());
                            editor.putString("password_p", Password.getText().toString());
                            editor.putBoolean("check_p", check);
                            editor.apply();
                        }
                        else{
                            sharedPreferences.edit().clear().apply();
                        }
                        Toast.makeText(MainActivity.this,"Your are logged in", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, Third.class);
                        startActivity(intent);

                }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private Boolean validateUsername(){
        String username = Username.getText().toString();
        if(username.isEmpty())
        {
            Username.setError("Field cannot be empty");
            return false;
        }
        else{
            Username.setError(null);
            return true;
        }

    }
    private Boolean validatePassword(){
        String username = Password.getText().toString();
        if(username.isEmpty())
        {
            Password.setError("Field cannot be empty");
            return false;
        }
        else{
            Password.setError(null);
            return true;
        }

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                             | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                             |View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
}
