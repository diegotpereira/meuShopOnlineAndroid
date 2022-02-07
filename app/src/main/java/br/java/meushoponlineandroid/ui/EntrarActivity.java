package br.java.meushoponlineandroid.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import br.java.meushoponlineandroid.R;

public class EntrarActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;
    EditText editTextoEmail;
    TextInputEditText editTextoSenha;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrar);

        editTextoEmail = (EditText) findViewById(R.id.editTextoEmail);
        editTextoSenha = (TextInputEditText) findViewById(R.id.editTextoSenha);
        progressBar = findViewById(R.id.progressbar);

        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.textoViewInscrever).setOnClickListener(this);
        findViewById(R.id.btnEntrar).setOnClickListener(this);
        findViewById(R.id.textoView_esqueceu_senha);
    }


    private void usuarioEntrar() {
        String email = editTextoEmail.getText().toString().trim();
        String senha = editTextoSenha.getText().toString().trim();

        if (email.isEmpty()) {
            editTextoEmail.setError("Por favor preencha o campo email");
            editTextoEmail.requestFocus();

            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextoEmail.setError("Por favor preencha com email valido");
            editTextoEmail.requestFocus();

            return;
        }

        if (senha.isEmpty()) {
            editTextoSenha.setError("Por favor preencha o campo email");
            editTextoSenha.requestFocus();

            return;
        }

        if (senha.length() < 6 || senha.length() > 15) {
            editTextoSenha.setError("A senha deve ter de 6 a 15 caracteres");
            editTextoSenha.requestFocus();

            return;
        }

        String letrasMaiusculas = "(.*[A-Z].*)";

        if (!senha.matches(letrasMaiusculas)) {
            editTextoSenha.setError("A senha deve conter pelo menos um número, " +
                    "uma letra minúscula, uma letra maiúscula e um caractere especial.");
            editTextoSenha.requestFocus();

            return;
        }

        String letrasMinusculas = "(.*[a-z].*)";
        if (!senha.matches(letrasMinusculas)) {
            editTextoSenha.setError("A senha deve conter pelo menos um número, " +
                    "uma letra minúscula, uma letra maiúscula e um caractere especial.");

            editTextoSenha.requestFocus();

            return;
        }

        String numeros = "(.*[0-9].*)";
        if (!senha.matches(numeros)) {
            editTextoSenha.setError("A senha deve conter pelo menos um número, " +
                    "uma letra minúscula, uma letra maiúscula e um caractere especial.");
            editTextoSenha.requestFocus();

            return;
        }

        String caracteresEspeciais="(.*[,~,!,@,#,$,%,^,&,*,(,),-,_,=,+,[,{,],},|,;,:,<,>,/,?].*$)";

        if (!senha.matches(caracteresEspeciais)) {
            editTextoSenha.setError("A senha deve conter pelo menos um número, " +
                    "uma letra minúscula, uma letra maiúscula e um caractere especial.");
            editTextoSenha.requestFocus();

            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);

                if (task.isSuccessful()) {
                    if (mAuth.getCurrentUser().isEmailVerified()) {
                        finish();
                        Intent intent = new Intent(EntrarActivity.this, DrawerActivity.class);
                        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                    } else {
                        editTextoEmail.setText("");
                        Toast.makeText(EntrarActivity.this,
                                "Verifique seu e-mail para fazer login", Toast.LENGTH_LONG).show();
                        FirebaseAuth.getInstance().signOut();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(this, DrawerActivity.class));
            finish();
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.textoViewInscrever:
                Intent intentEntrar = new Intent(this, CadastrarActivity.class);
                intentEntrar.addFlags(intentEntrar.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentEntrar);
                finish();
                break;

            case R.id.btnEntrar:
                usuarioEntrar();
                break;
        }
    }
}