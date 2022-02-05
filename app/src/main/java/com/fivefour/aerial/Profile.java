package com.fivefour.aerial;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Profile extends AppCompatActivity {

    private CardView card_get_userimage;
    private ImageView image_get_userimage;
    private static int PICK_IMAGE=123;
    private Uri imagepath;

    private EditText getUsername;
    private Button saveProfile;
    private FirebaseAuth firebaseAuth;
    private String name;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private String ImageUriAccessToken;
    private FirebaseFirestore firebaseFirestore;

    ProgressBar progressBar;


 /*   ActivityResultLauncher<Intent> startForResult=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode()== Activity.RESULT_OK){

                imagepath=data.getData
            }

        }
    });*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage=FirebaseStorage.getInstance();
        storageReference= firebaseStorage.getReference();
        firebaseFirestore=FirebaseFirestore.getInstance();


        getUsername =(EditText)findViewById(R.id.username);
        card_get_userimage =(CardView)findViewById(R.id.card_userImage);
        image_get_userimage =(ImageView)findViewById(R.id.userImage);
        saveProfile=(Button)findViewById(R.id.saveprofile);
        progressBar=(ProgressBar)findViewById(R.id.profile_progressbar);


        // pick image from gallery
        card_get_userimage.setOnClickListener(view -> {
          //  Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
          // startActivityForResult(intent,PICK_IMAGE);







            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            someActivityResultLauncher.launch(photoPickerIntent);
















        });

        saveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name=getUsername.getText().toString();
                if (name.isEmpty()){

                    Toast.makeText(getApplicationContext(),"Please enter the name",Toast.LENGTH_SHORT).show();
                } else if(imagepath==null){

                    Toast.makeText(getApplicationContext(),"Please Select an image",Toast.LENGTH_SHORT).show();
                } else{


                    progressBar.setVisibility(View.VISIBLE);
                    sendDataForNewUser();
                    progressBar.setVisibility(View.INVISIBLE);
                    Intent intent = new Intent(Profile.this,ChatActivity.class);
                    startActivity(intent);
                    finish();
                    // end of else
                }
            }
        });

// end of oncreate
    }


    private void sendDataForNewUser(){
        sendDataToRealtimeDatabase();
    }

    private void sendDataToRealtimeDatabase(){

       name=getUsername.getText().toString().trim();
        FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
        DatabaseReference databaseReference= firebaseDatabase.getReference(firebaseAuth.getUid());

        UserProfile A_userprofile = new UserProfile(name,firebaseAuth.getUid());
        databaseReference.setValue(A_userprofile);
        Toast.makeText(getApplicationContext(),"Profile Created",Toast.LENGTH_SHORT).show();;

        sendImageToStore();

    }
    private void sendImageToStore(){

        StorageReference imageRef =storageReference.child("images").child(firebaseAuth.getUid()).child("profile picture");
         // image compression
        Bitmap bitmap = null;
        try {
            bitmap=MediaStore.Images.Media.getBitmap(getContentResolver(),imagepath);
        }
        catch (IOException e){

            e.printStackTrace();
        }

        ByteArrayOutputStream byteArrayOutputStream =new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,25,byteArrayOutputStream);
        byte[] data =byteArrayOutputStream.toByteArray();


        // putting image to store

        UploadTask uploadTask = imageRef.putBytes(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        ImageUriAccessToken=uri.toString();
                        Toast.makeText(getApplicationContext(),"Uri get Access",Toast.LENGTH_SHORT).show();
                        sendDataToCloudFireStore();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(getApplicationContext(),"Uri get Failed",Toast.LENGTH_SHORT).show();

                    }
                });
                Toast.makeText(getApplicationContext(),"Image uploaded",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"Image not uploaded",Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void sendDataToCloudFireStore() {

        DocumentReference documentReference =firebaseFirestore.collection("users").document(firebaseAuth.getUid());
        Map<String,Object> userdata = new HashMap<>();
        userdata.put("name",name);
        userdata.put("image",ImageUriAccessToken);
        userdata.put("uid",firebaseAuth.getUid());
        userdata.put("status","online");

        documentReference.set(userdata).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(),"data on cloud fire store send sucessfully",Toast.LENGTH_SHORT).show();
            }
        });

    }

  /*  @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {


        if (requestCode==PICK_IMAGE && requestCode==RESULT_OK){

            imagepath= data.getData();
            image_get_userimage.setImageURI(imagepath);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }*/





    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // There are no request codes
                    // doSomeOperations();
                    Intent data = result.getData();
                    Uri selectedImage = Objects.requireNonNull(data).getData();
                    InputStream imageStream = null;
                    try {
                        imageStream = getContentResolver().openInputStream(selectedImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    BitmapFactory.decodeStream(imageStream);
                    image_get_userimage.setImageURI(selectedImage);// To display selected image in image view
                }
            });




    // the end
}