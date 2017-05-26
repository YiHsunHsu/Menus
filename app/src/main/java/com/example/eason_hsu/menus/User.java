package com.example.eason_hsu.menus;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Eason_Hsu on 2017/5/18.
 */

public class User {
    private String uid;
    private String email;
    private String nickname;
    public User(){
    }
    public String getUid(){
        return uid;
    }
    public void setUid(String uid){
        this.uid = uid;
    }
    public String getEmail(){
        return email;
    }
    public void setEmail(String email){
        this.email = email;
    }
    public String getNickname(){
        return nickname;
    }
    public void setNickname(String nickname){
        this.nickname = nickname;
    }
    public void saveUser(String type){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference userRef = db.getReference("users");
        userRef.child(getUid()).child("email").setValue(getEmail());
        userRef.child(getUid()).child("nickname").setValue(getNickname());
        userRef.child(getUid()).child("login_type").setValue(type);
    }
}
