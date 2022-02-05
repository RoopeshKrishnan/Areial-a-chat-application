package com.fivefour.aerial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {


    EditText getphonenumber;
    Button sendOtp;
    CountryCodePicker countryCodePicker;
    String countrycode;
    String phonenumber;

    FirebaseAuth firebaseAuth;
    ProgressBar ac_progressbar;

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    String codesent;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        countryCodePicker = (CountryCodePicker)findViewById(R.id.countrycodepicker);
        sendOtp=(Button)findViewById(R.id.sendotp);
        getphonenumber=(EditText)findViewById(R.id.getphonenumber);
        ac_progressbar=(ProgressBar)findViewById(R.id.progressbar);


        // instance of current user,whether is logged in or not
        firebaseAuth = FirebaseAuth.getInstance();

        countrycode = countryCodePicker.getSelectedCountryCodeWithPlus();
        countryCodePicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                countrycode= countryCodePicker.getSelectedCountryCodeWithPlus();
            }
        });

        sendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number;
                number=getphonenumber.getText().toString();
                if (number.isEmpty()){

                    Toast.makeText(getApplicationContext(),"Please enter your number",Toast.LENGTH_SHORT).show();

                }else if(number.length()<10){

                    Toast.makeText(getApplicationContext(),"Please enter your correct number",Toast.LENGTH_SHORT).show();
                }
                else{

                    ac_progressbar.setVisibility(View.VISIBLE);
                    phonenumber=countrycode+number;

                    PhoneAuthOptions options=PhoneAuthOptions.newBuilder(firebaseAuth)
                            .setPhoneNumber(phonenumber)
                            .setTimeout(60L, TimeUnit.SECONDS)
                            .setActivity(MainActivity.this)
                            .setCallbacks(mCallbacks)
                            .build();

                    PhoneAuthProvider.verifyPhoneNumber(options);







                }


            }
        });

  mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
      @Override
      public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

          // automatically enter the otp

      }

      @Override
      public void onVerificationFailed(@NonNull FirebaseException e) {

      }

      @Override
      public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
          super.onCodeSent(s, forceResendingToken);

          Toast.makeText(getApplicationContext(),"OTP sended",Toast.LENGTH_SHORT).show();
          ac_progressbar.setVisibility(View.INVISIBLE);
          codesent=s;
          Intent intent = new Intent(MainActivity.this,OTP_authentication.class);
          intent.putExtra("otp",codesent);
          startActivity(intent);


      }
  };

      // end of oncreate
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseAuth.getInstance().getCurrentUser()!=null)
        {

            Intent intent = new Intent(MainActivity.this,Profile.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        }
    }



    // last bracket
}