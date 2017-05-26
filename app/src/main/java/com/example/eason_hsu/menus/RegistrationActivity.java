package com.example.eason_hsu.menus;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegistrationActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private User user;

    private EditText editTextRegistrationEmail;
    private EditText editTextNickname;
    private EditText editTextRegistrationPassword;
    private EditText editTextConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();


        editTextRegistrationEmail = (EditText)findViewById(R.id.editTextRegistrationEmail);
        editTextNickname = (EditText)findViewById(R.id.editTextNickname);
        editTextRegistrationPassword = (EditText)findViewById(R.id.editTextRegistrationPassword);
        editTextConfirmPassword = (EditText)findViewById(R.id.editTextConfirmPassword);
    }
    public void createUser(View view){
        final String email = editTextRegistrationEmail.getText().toString();
        final String password = editTextRegistrationPassword.getText().toString();
        final String nickname = editTextNickname.getText().toString();
        String confirmPassword = editTextConfirmPassword.getText().toString();

        if(password.equals(confirmPassword)) {
            final ProgressDialog progressDialog = ProgressDialog.show(RegistrationActivity.this, "請稍後", "帳號建立中", true);
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d("Registration", "onComplete yes");
                                try{
                                    user = new User();
                                    user.setUid(firebaseUser.getUid());
                                    user.setEmail(email);
                                    user.setNickname(nickname);
                                    user.saveUser("EMAIL");
                                    Toast.makeText(RegistrationActivity.this, "帳號建立完成", Toast.LENGTH_LONG).show();
                                    getIntent().putExtra("USEREMAIL", email);
                                    getIntent().putExtra("USERPASSWORD", password);
                                    setResult(RESULT_OK, getIntent());
                                    finish();
                                } catch (Exception ex){
                                    Log.d("ERROR:", ex.getMessage());
                                }
                            } else {
                                Log.w("Registration", "ERROR", task.getException());
                                Toast.makeText(RegistrationActivity.this, "註冊失敗", Toast.LENGTH_LONG).show();
                            }
                            progressDialog.dismiss();
                        }
                    });
        } else {
            editTextRegistrationPassword.setText(null);
            editTextConfirmPassword.setText(null);
            Toast.makeText(RegistrationActivity.this, "請確認密碼欄與確認碼欄是否一致", Toast.LENGTH_LONG).show();
        }
    }

}
