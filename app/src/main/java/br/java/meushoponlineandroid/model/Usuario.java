package br.java.meushoponlineandroid.model;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.Exclude;

public class Usuario {

    private String uNome;
    private String mKey;
    private String uEmail;
    private String uToken;
    FirebaseAuth mAuth;

    public Usuario() {
    }

    public Usuario(String token) {
        uNome = mAuth.getInstance().getCurrentUser().getDisplayName();
        uEmail = mAuth.getInstance().getCurrentUser().getEmail();
        uToken = token;
    }

    public String getNome() {
        return uNome;
    }

    public void setNome(String nome) {
        this.uNome = nome;
    }

    @Exclude
    public String getKey() {
        return mKey;
    }

    @Exclude
    public void setKey(String key) {
        this.mKey = key;
    }

    public String getEmail() {
        return uEmail;
    }

    public void setEmail(String email) {
        this.uEmail = email;
    }

    public String getuToken() {
        return uToken;
    }

    public void setuToken(String uToken) {
        this.uToken = uToken;
    }
}
