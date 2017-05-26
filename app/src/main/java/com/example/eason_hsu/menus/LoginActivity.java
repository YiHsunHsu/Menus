package com.example.eason_hsu.menus;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONObject;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    private static final int REGISTRATION_FUC = 1;
    private static final int RC_SIGN_IN = 2;
    //Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser firebaseUser;

    //Facebook
    private CallbackManager callbackManager;

    private User user;

    private EditText editTextLoginEmail;
    private EditText editTextLoginPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Facebook
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                final AccessToken accessToken = loginResult.getAccessToken();
                GraphRequest graphRequest = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                            handleFacebookAccessToken(accessToken, object);
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "email,name");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();
            }

            @Override
            public void onCancel() {
                Log.d("facebook:onCancel", "CANCEL");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("facebook:onCancel", error.getMessage());
                Toast.makeText(LoginActivity.this, "" + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                firebaseUser = firebaseAuth.getCurrentUser();
                if(firebaseUser != null){
                    Log.d("LoginActivity", "onAuthStateChanged:Login,UID:" + firebaseUser.getUid());
                }
            }
        };

        editTextLoginEmail = (EditText)findViewById(R.id.editTextLoginEmail);
        editTextLoginPassword = (EditText)findViewById(R.id.editTextLoginPassword);
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(authStateListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case RC_SIGN_IN :
                break;
            case REGISTRATION_FUC:
                if(resultCode == RESULT_OK){
                    String email = data.getStringExtra("USEREMAIL");
                    String password = data.getStringExtra("USERPASSWORD");
                    editTextLoginEmail.setText(email);
                    editTextLoginPassword.setText(password);
                    Log.d("LoginActivity", "onActivityResult -> FUNC_REGISTRATION : USEREMAIL = " + email + "USERPASSWORD = " + password);
                }
                break;
        }
    }

    //email login
    public void email_login(View view) {
        Log.d("LoginActivity", "email_login");
        String email = editTextLoginEmail.getText().toString();
        String password = editTextLoginPassword.getText().toString();
        if(!email.equals("") && !password.equals("")){
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("LoginActivity", "email_login -> signInWithEmail:success");
                                firebaseUser = firebaseAuth.getCurrentUser();
                                getIntent().putExtra("UID", firebaseUser.getUid());
                                setResult(RESULT_OK, getIntent());
                                finish();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.d("LoginActivity", "email_login -> signInWithEmail:failure", task.getException());
                                //show error message
                                Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

    //Facebook
    public void facebook_login(View view) {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends", "email"));
    }
    public void registration(View view) {
        startActivityForResult(new Intent(this, RegistrationActivity.class), REGISTRATION_FUC);
    }

    //
    private void handleFacebookAccessToken(AccessToken token, final JSONObject jsonObject) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            try{
                                firebaseUser = firebaseAuth.getCurrentUser();
                                user = new User();
                                user.setUid(firebaseUser.getUid());
                                user.setEmail(jsonObject.getString("email"));
                                user.setNickname(jsonObject.getString("name"));
                                user.saveUser("FACEBOOK");
                                setResult(RESULT_OK, getIntent());
                                getIntent().putExtra("UID", user.getUid());
                                finish();
                            } catch (Exception ex) {
                                Log.d("LoginActivity", ex.getMessage());
                            }
                        } else {
                            //show error message
                        }
                    }
                });
    }
}
