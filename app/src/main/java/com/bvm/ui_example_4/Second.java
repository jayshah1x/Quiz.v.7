package com.bvm.ui_example_4;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Second extends AppCompatActivity {
    private static EditText Username, Password, ConfirmPassword,Email, Contact;
    private static Button SignUp;
    DatabaseReference myref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        Username = findViewById(R.id.username);
        Password = findViewById(R.id.password);
        ConfirmPassword = findViewById(R.id.cofirmpassword);
        Email = findViewById(R.id.email);
        Contact = findViewById(R.id.contact);
        SignUp = findViewById(R.id.signup);

        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(validateEmail() || validateConfrimPass() || validateContact() || validatePassword() ||validateUsername()){

                    if(!((Password.getText().toString()).equals(ConfirmPassword.getText().toString()))){
                        SignUp.setError("Password and Confirm Password does not match ");
                    }else{
                        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                        rootRef.child("Login Info").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String username = Username.getText().toString();
                                if(dataSnapshot.hasChild(username)){
                                    SignUp.setError("User already exits");
                                    Intent intent = new Intent(Second.this, MainActivity.class);
                                    startActivity(intent);
                                }else{
                                    writeCreds();
                                    Toast.makeText(Second.this,"You are Signed up :)",Toast.LENGTH_SHORT).show();

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }


                }
            }

            private void writeCreds() {
                String user = Username.getText().toString();
                String pass = Password.getText().toString();
                String email = Email.getText().toString();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                myref= database.getReference().child("Login Info");

                myref.child(user).child("pass").setValue(pass);
                myref.child(user).child("score").setValue("0");
                myref.child(user).child("mail").setValue(email);
            }


            private Boolean validateEmail() {
                String email = Email.getText().toString();
                if(email.isEmpty()){
                    Email.setError("This field cannot be Empty");
                    return false;
                }
                else{
                    Email.setError(null);
                    return true;
                }
            }

            private Boolean validateContact() {
                String contact = Contact.getText().toString();
                if(contact.isEmpty()){
                    Contact.setError("This field cannot be Empty");
                    return false;
                }
                else{
                    Contact.setError(null);

                    return true;                }
            }

            private Boolean validateConfrimPass() {
                String confrim = ConfirmPassword.getText().toString();
                String password = Password.getText().toString();
                if(confrim.isEmpty()){
                    ConfirmPassword.setError("This field cannot be Empty");
                    return false;
                }
                else{
                    ConfirmPassword.setError(null);
                    return true;
                }
            }

            private Boolean validatePassword() {
                String password = Password.getText().toString();
                if(password.isEmpty()){
                    Password.setError("This field cannot be Empty");
                    return false;
                }
                else if(password.matches("$%^&*()!@# _-")){
                    Password.setError("Password cannot contain special character");
                    return false;
                }
                else{
                    Password.setError(null);
                    return true;
                }
            }

            private Boolean validateUsername() {
                String username = Username.getText().toString();
                if(username.isEmpty()){
                    Username.setError("This field cannot be Empty");
                    return false;
                }
                else{
                    Username.setError(null);
                    return true;
                }
            }

        });
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

