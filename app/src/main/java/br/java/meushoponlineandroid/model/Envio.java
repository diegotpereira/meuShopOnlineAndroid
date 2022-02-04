package br.java.meushoponlineandroid.model;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.Exclude;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Envio {

    private String mNome;
    private String mImagemUrl;
    private String mKey;
    private String mEmail;
    private String mUsuario;
    private String mPreco;
    private String mData;
    private String mDesc;

    public Envio() {
    }

    public Envio(String nome, String imagemUrl, String preco, String desc) {
        if (nome.trim().equals("")) {
            nome = "Sem Nome";
        }
        mNome = nome;
        mImagemUrl = imagemUrl;
        mEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        mDesc = desc;
        mUsuario = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        mPreco = preco;
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        Date date = new Date();
        mData = df.format(date);
    }

    public String getNome() {
        return mNome;
    }

    public void setNome(String mNome) {
        this.mNome = mNome;
    }

    public String getImagemUrl() {
        return mImagemUrl;
    }

    public void setImagemUrl(String mImagemUrl) {
        this.mImagemUrl = mImagemUrl;
    }

    @Exclude
    public String getKey() {
        return mKey;
    }

    @Exclude
    public void setKey(String mKey) {
        this.mKey = mKey;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public String getUsuario() {
        return mUsuario;
    }

    public void setUsuario(String mUsuario) {
        this.mUsuario = mUsuario;
    }

    public String getPreco() {
        return mPreco;
    }

    public void setPreco(String mPreco) {
        this.mPreco = mPreco;
    }

    public String getData() {
        return mData;
    }

    public void setData(String mData) {
        this.mData = mData;
    }

    public String getDesc() {
        return mDesc;
    }

    public void setDesc(String mDesc) {
        this.mDesc = mDesc;
    }
}
