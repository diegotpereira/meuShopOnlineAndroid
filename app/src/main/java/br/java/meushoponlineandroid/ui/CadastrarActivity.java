package br.java.meushoponlineandroid.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

import br.java.meushoponlineandroid.R;

public class CadastrarActivity extends AppCompatActivity implements View.OnClickListener{

    ProgressBar progressBar;
    EditText editTextoEmail;
    TextInputEditText editTextoSenha;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar);

        editTextoEmail = (EditText) findViewById(R.id.editTextoEmail);
        editTextoSenha = (TextInputEditText) findViewById(R.id.editTextoSenha);
        progressBar = findViewById(R.id.progressbar);

        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.btnCadastrar).setOnClickListener(this);
        findViewById(R.id.textViewEntrar).setOnClickListener(this);
    }

    private void cadastrarUsuario() {
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

        mAuth.createUserWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);

                if (task.isSuccessful()) {

                    enviarEmaiVerificacao();
                } else {
                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        editTextoSenha.setText("");

                        Toast.makeText(getApplicationContext(), "Você já está registrado", Toast.LENGTH_SHORT).show();
                    } else {
                        editTextoSenha.setText("");

                        Toast.makeText(CadastrarActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void enviarEmaiVerificacao() {
        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();

        if (usuario != null) {
            usuario.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {

                        Toast.makeText(CadastrarActivity.this, "Verifique seu e-mail para verificação", Toast.LENGTH_LONG).show();
                        FirebaseAuth.getInstance().signOut();
                        editTextoEmail.setText("");
                        editTextoSenha.setText("");
                    } else {
                        Toast.makeText(CadastrarActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnCadastrar:
                cadastrarUsuario();
                break;

            case R.id.textViewEntrar:
                Intent intentEnt = new Intent(this, EntrarActivity.class);
                intentEnt.addFlags(intentEnt.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentEnt);
                finish();

                break;
        }
    }
}