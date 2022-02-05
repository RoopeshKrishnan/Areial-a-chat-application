package com.fivefour.aerial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class OTP_authentication extends AppCompatActivity {


    TextView changeNumber;
    EditText getOtp;
    Button verify_otp;
    String entered_otp;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_authentication);


        changeNumber=(TextView)findViewById(R.id.changeNumber);
        getOtp=(EditText)findViewById(R.id.getotp);
        verify_otp=(Button)findViewById(R.id.verifyotp);

        firebaseAuth=FirebaseAuth.getInstance();


        changeNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OTP_authentication.this,MainActivity.class);
                startActivity(intent);
            }
        });

        verify_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                entered_otp=getOtp.getText().toString();
                if (entered_otp.isEmpty()){

                    Toast.makeText(getApplicationContext(),"Please enter the OTP",Toast.LENGTH_SHORT).show();
                } else if(entered_otp.length()<4 && entered_otp.length()==5)
                {
                    Toast.makeText(getApplicationContext(),"Please enter the Correct OTP",Toast.LENGTH_SHORT).show();
                } else {

                    String Otp_recevied = getIntent().getStringExtra("otp");

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(Otp_recevied,entered_otp);
                    signInWithPhoneAuthCredential(credential);

                }
            }
        });




        // end of oncreate
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential)
    {
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    Toast.makeText(getApplicationContext(),"Login Successful",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(OTP_authentication.this,Profile.class);
                    startActivity(intent);
                    finish();


                }else{

                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException){

                        Toast.makeText(getApplicationContext(),"Failed,Try again",Toast.LENGTH_SHORT).show();

                    }

                }
            }
        });
    }
}