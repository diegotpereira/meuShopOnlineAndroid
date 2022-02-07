package br.java.meushoponlineandroid.fragment;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import br.java.meushoponlineandroid.R;
import br.java.meushoponlineandroid.model.EnviarEmail;

public class ComentarioFragmento extends Fragment implements View.OnClickListener{

    private EditText editarTextoMensagem;
    private Button btnEnviar;
    private String Nome;
    private String Email;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragmento_comentario, container, false);

        editarTextoMensagem = (EditText) v.findViewById(R.id.edit_texto_message);
        btnEnviar = (Button) v.findViewById(R.id.btn_enviar);
        btnEnviar.setOnClickListener(this);

        Nome = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        Email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        // Inflate the layout for this fragment
        return v;
    }

    private void enviarEmail() {
        String email = "diegotestefirebased@gmail.com";
        String destinatario = "[Comentário] " + Nome;
        String mensagem = editarTextoMensagem.getText().toString().trim() + "\n\nenviado por " + Email;
        EnviarEmail sm = new EnviarEmail(getActivity(), email, destinatario, mensagem);
        sm.execute();
    }

    @Override
    public void onClick(View view) {
        if (editarTextoMensagem.getText().toString().length() < 1) {
            editarTextoMensagem.setError("A mensagem não pode estar vazia.");
            editarTextoMensagem.requestFocus();

            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Comentário");
        builder.setMessage("Seu feedback é muito valioso para nós.");

        builder.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                enviarEmail();

                editarTextoMensagem.setText("");
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                editarTextoMensagem.setText("");

                Toast.makeText(getActivity(), "Mensagem descartada", Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog ad = builder.create();
        ad.show();
    }
}