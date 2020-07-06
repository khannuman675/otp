package com.numan.otpverify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class Verifypage extends AppCompatActivity implements MessageListener{
    EditText otp;
    FirebaseAuth firebaseAuth;
    Button resendBtn;
    String Mob, verificationId;
    ProgressDialog progressDialog;
    TextView mobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verifypage);

        MessageReciever.bindListener(this);
        otp = findViewById(R.id.GetOtp);
        firebaseAuth = FirebaseAuth.getInstance();
        resendBtn=findViewById(R.id.resendOTP);
        //for temporary
        Mob = getIntent().getExtras().getString("mobile");
        verificationId = getIntent().getExtras().getString("OTP");
        //end temp
        setTitle("OTP");


        mobile=(TextView)findViewById(R.id.place);
        mobile.setText(Mob);
        resendOtp();

        findViewById(R.id.Submit).setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {

                String code = otp.getText().toString().trim();

                if ((code.isEmpty() || code.length() < 6)) {

                    otp.setError("Enter code...");
                    otp.requestFocus();
                    return;
                }
                verifyCode(code);

            }
        });
        findViewById(R.id.resendOTP).setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                resendVerificationCode(Mob);
                resendOtp();
            }
        });

    }
    void resendOtp() {
        resendBtn.setEnabled(false);

        new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                resendBtn.setText("Resend OTP in: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                resendBtn.setText("Resend OTP");
                resendBtn.setEnabled(true);
            }
        }.start();
    }
    public void resendVerificationCode(String number){

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,
                Verifypage.this,
                mCallBack
        );
    }
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            Toast.makeText(Verifypage.this, "OTP Sent Successfully",Toast.LENGTH_LONG).show();

            verificationId = s;
//            mob.setText("");



        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
//            Toast.makeText(MainActivity.this, ,Toast.LENGTH_LONG).show();

//            if (code != null) {
////                progressBar.setVisibility(View.VISIBLE);
//                verifyCode(code);
//            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(Verifypage.this,"Something went wrong please try again later" ,Toast.LENGTH_LONG).show();

        }
    };

    private void verifyCode(String code) {

        progressDialog=new ProgressDialog(Verifypage.this);
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Verifying OTP");
        progressDialog.show();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);


        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        try {
            firebaseAuth.signInWithCredential(credential)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {

                                Toast.makeText(Verifypage.this, "com", Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(Verifypage.this, Dashboard.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                                startActivity(intent);
                                if(progressDialog.isShowing()){
                                    progressDialog.dismiss();
                                }
                            } else {
                                Toast.makeText(Verifypage.this, "Invalid OTP", Toast.LENGTH_LONG).show();
                                if(progressDialog.isShowing()){
                                    progressDialog.dismiss();
                                }
                            }
                        }

                    });
        } catch (Exception e) {
            Toast.makeText(Verifypage.this, e.getMessage(), Toast.LENGTH_LONG).show();

        }
    }


    @Override
    public void messageRecieved(String string) {
        otp.setText(string.trim());
        Toast.makeText(this,string,Toast.LENGTH_LONG).show();

    }
}
