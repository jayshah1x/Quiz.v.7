package com.bvm.ui_example_4;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

import static android.os.Build.VERSION_CODES.P;


public class Assignments extends AppCompatActivity {
    private static TextView Marks, Status, Assessed, Total_marks, Assignment_no;
    private static String assignment, marks_scored, flag, totalmarks;
    private static Button Upload, submit;
    int PICK_IMAGE_REQUEST = 2342;
    Uri filePath;
    ProgressDialog pd;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl("gs://newapp1-baff1.appspot.com");    //change the url according to your firebase app

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignments);
        Status = findViewById(R.id.submitteds);
        Marks = findViewById(R.id.Score);
        Assessed = findViewById(R.id.assessed);
        Total_marks = findViewById(R.id.Marks);
        Assignment_no = findViewById(R.id.Ass);
        Upload = findViewById(R.id.Upload);
        submit = findViewById(R.id.Submit);

        DatabaseReference myref = FirebaseDatabase.getInstance().getReference();
        myref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              String  flag1 = dataSnapshot.child("Subject").child("A-Flag").getValue().toString();
                if(flag1.equals("true")){
                    Status.setText(flag1);
                    assignment = dataSnapshot.child("Subject").child("Current-A").getValue().toString();
                    totalmarks = dataSnapshot.child("Subject").child("Mark").getValue().toString();
                    String status = dataSnapshot.child("Login Info").child("18EL003").child("Subject").child("flag").getValue().toString();
                    Assessed.setText(status);
                    Assignment_no.setText(assignment);
                    Total_marks.setText(totalmarks);

                    if(status.equals("true")){
                        String score = dataSnapshot.child("Login Info").child("18EL003").child("Subject").child("SA").getValue().toString();
                        Marks.setText(score);

                    }else{
                        pd = new ProgressDialog(Assignments.this);
                        pd.setMessage("Uploading....");

                        Upload.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent();
                                intent.setType("application/pdf");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
                            }
                        });
                        submit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(filePath != null) {
                                    pd.show();

                                    StorageReference childRef = storageRef.child( assignment + "" + "18EL003");

                                    //uploading the image
                                    UploadTask uploadTask = childRef.putFile(filePath);

                                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            pd.dismiss();
                                            Toast.makeText(Assignments.this, "Upload successful", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            pd.dismiss();
                                            Toast.makeText(Assignments.this, "Upload Failed -> " + e, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                                else {
                                    Toast.makeText(Assignments.this, "Select an file", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }

                }else{
                    Toast.makeText(Assignments.this, "There is no upcoming Assignment", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();

            try {
                //getting image from gallery
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}






/*
assignment = dataSnapshot.child("Subject").child("Current-A").getValue().toString();
                    totalmarks = dataSnapshot.child("Subject").child("Mark").getValue().toString();
                    String status = dataSnapshot.child("Login Info").child("18EL003").child("Subject").child("flag").getValue().toString();
  String score = dataSnapshot.child("Login Info").child("18EL003").child("Subject").child("SA").getValue().toString();
  databaseReference.child("Login Info").child("18EL003").child(filename)
 */
