package com.example.eason_hsu.menus;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {


    private static final int REGISTRATION_FUC =1;

    private EditText editTextLoginEmail;
    private EditText editTextLoginPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextLoginEmail = (EditText)findViewById(R.id.editTextLoginEmail);
        editTextLoginPassword = (EditText)findViewById(R.id.editTextLoginPassword);
    }

    public void email_login(View view) {
        Toast.makeText(this, "email_longin CLICK!", Toast.LENGTH_LONG).show();
    }
    public void registration(View view) {
        startActivityForResult(new Intent(this, RegistrationActivity.class), REGISTRATION_FUC);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case REGISTRATION_FUC:
                if(resultCode == RESULT_OK){
                    String email = data.getStringExtra("USEREMAIL");
                    String password = data.getStringExtra("USERPASSWORD");
                    editTextLoginEmail.setText(email);
                    editTextLoginPassword.setText(password);
                    Log.d("LoginActivity", "onActivityResult -> FUNC_REGISTRATION : USEREMAIL = " + email + "USERPASSWORD = " + password);                }
                break;
        }
    }
}
