package br.java.meushoponlineandroid.fragment;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

import br.java.meushoponlineandroid.R;
import br.java.meushoponlineandroid.model.ConexaoDeRede;

public class PerfilFragmento extends Fragment {

    private static final int CHOOSE_IMAGE = 101;

    TextView textView;
    TextView textViewEmail;
    ImageView imageView;
    EditText editText;
    Uri uriPerfilImagem;
    ProgressBar progressBar;
    String perfilImagemUrl;
    FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragmento_perfil, container, false);

        mAuth = FirebaseAuth.getInstance();
        editText = (EditText) v.findViewById(R.id.edit_texto_display_nome);
        imageView = (ImageView) v.findViewById(R.id.imageView);
        progressBar = v.findViewById(R.id.progressbar);
        textView = v.findViewById(R.id.texto_view_Verificado);
        textViewEmail = v.findViewById(R.id.text_view_email);
        textViewEmail.setText(mAuth.getCurrentUser().getEmail());

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exibirEscolherImagem();
            }
        });
        carregarInformacoesUsuario();

        v.findViewById(R.id.buttonSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                salvarInformacaoUsuario();
            }
        });

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        Bundle bundle = getArguments();

        if (bundle != null && bundle.getInt("vendedor") == 1) {

            Toast.makeText(getActivity(), "Complete seu perfil primeiro", Toast.LENGTH_SHORT).show();

            return;
        }
    }

    private void carregarInformacoesUsuario() {
        ConexaoDeRede conexaoDeRede = new ConexaoDeRede();

        if (conexaoDeRede.estaConectadoNaInternet(getActivity())
               || conexaoDeRede.estaConectadoNaRedeMovel(getActivity())
               || conexaoDeRede.estaConectadoNoWifi(getActivity())) {

        } else {
            conexaoDeRede.exibirDialogoDeErroSemInternetDisponivel(getActivity());

            return;
        }

        final FirebaseUser usuario = mAuth.getCurrentUser();

        if (usuario != null) {
            if (usuario.getPhotoUrl() != null) {
                String fotoUrl = usuario.getPhotoUrl().toString();

                Glide.with(this)
                        .load(fotoUrl)
                        .into(imageView);
            }

            if (usuario.getDisplayName() != null) {
                String exibirNome = usuario.getDisplayName();
                editText.setText(exibirNome);
            }

            if (usuario.isEmailVerified()) {
                textView.setText("Verificado");
            } else {
                textView.setText("Email não vefirifcado(clique aqui para verificar");

                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        textView.setTextColor(1);

                        usuario.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                Toast.makeText(getActivity(), "Vefiricação de email enviada", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
            }
        }
    }

    private void salvarInformacaoUsuario() {
        ConexaoDeRede conexaoDeRede = new ConexaoDeRede();

        if (conexaoDeRede.estaConectadoNaInternet(getActivity())
                || conexaoDeRede.estaConectadoNaRedeMovel(getActivity())
                || conexaoDeRede.estaConectadoNoWifi(getActivity())) {

        } else {
            conexaoDeRede.exibirDialogoDeErroSemInternetDisponivel(getActivity());

            return;
        }

        String exibirNome = editText.getText().toString();
        
        if (exibirNome.isEmpty()) {
            editText.setError("Nome obrigatório");
            editText.requestFocus();
            
            return;
        }
        
        FirebaseUser usuario = mAuth.getCurrentUser();
        
        if (perfilImagemUrl == null && imageView.getDrawable() == null) {

            Toast.makeText(getActivity(), "Nenhuma imagem selecionada. Clique na câmera para selecionar a imagem do perfil", Toast.LENGTH_SHORT).show();

            return;
        }

        if (usuario != null && perfilImagemUrl != null) {
            UserProfileChangeRequest perfil = new UserProfileChangeRequest.Builder()
                    .setDisplayName(exibirNome)
                    .setPhotoUri(Uri.parse(perfilImagemUrl))
                    .build();
            
            usuario.updateProfile(perfil)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                Toast.makeText(getActivity(), "Perfil atualizado com sucesso.", Toast.LENGTH_LONG).show();
                            } else {

                                Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_LONG).show();

                                return;
                            }
                        }
                    });
        } else {

            Toast.makeText(getActivity(), "Ocorreu algum erro", Toast.LENGTH_LONG).show();

            return;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK  && data != null && data.getData() != null) {
            uriPerfilImagem = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uriPerfilImagem);
                imageView.setImageBitmap(bitmap);
                enviarImagemParaFirebaseStorage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void enviarImagemParaFirebaseStorage() {
        final StorageReference perfilRef;
        perfilRef = FirebaseStorage.getInstance().getReference(mAuth.getCurrentUser().getEmail() + ".jpg");

        if (uriPerfilImagem != null) {
            progressBar.setVisibility(View.VISIBLE);

            perfilRef.putFile(uriPerfilImagem)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressBar.setVisibility(View.GONE);

                            perfilRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    perfilImagemUrl= uri.toString();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.GONE);

                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }
    private void exibirEscolherImagem() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "selecione imagem do perfil"), CHOOSE_IMAGE);
    }
}