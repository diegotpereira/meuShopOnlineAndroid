package br.java.meushoponlineandroid.fragment;

import static android.app.Activity.RESULT_OK;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import br.java.meushoponlineandroid.R;
import br.java.meushoponlineandroid.model.ConexaoDeRede;
import br.java.meushoponlineandroid.model.Envio;

public class VenderFragmento extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_CAMERA_REQUEST = 0;

    private Button mBtnEscolherImagem;
    private Button mBtnEnviar;

    private EditText mEditTextoArquivoNome;
    private EditText mEditTextoArquivoPreco;
    private ImageView mImagemView;
    private TextView mDescricao;
    private ProgressBar mProgressBar;
    private Uri mImagemUri;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private StorageTask mEnviarTarefa;

    FirebaseAuth mAuth;
    Uri uri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragmento_vender, container, false);

        mBtnEscolherImagem = v.findViewById(R.id.btn_escolher_imagem);
        mBtnEnviar = v.findViewById(R.id.btn_enviar);
        mEditTextoArquivoNome = v.findViewById(R.id.edit_texto_file_arquivo_nome);
        mEditTextoArquivoPreco = v.findViewById(R.id.edit_texto_file_arquivo_preco);
        mImagemView = v.findViewById(R.id.image_view);
        mProgressBar = v.findViewById(R.id.progress_bar);
        mDescricao = v.findViewById(R.id.Description);
        mStorageRef = FirebaseStorage.getInstance().getReference("envios");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("envios");

        mBtnEscolherImagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                escolherArquivoAbrir();
            }
        });

        mBtnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mEnviarTarefa != null && mEnviarTarefa.isInProgress()) {

                    Toast.makeText(getActivity(), "Envio em progresso", Toast.LENGTH_SHORT).show();
                } else {

                    enviarArquivo();
                }
            }
        });
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        ConexaoDeRede conexaoDeRede = new ConexaoDeRede();

        if (conexaoDeRede.estaConectadoNaInternet(getActivity())
            || conexaoDeRede.estaConectadoNaRedeMovel(getActivity())
            || conexaoDeRede.estaConectadoNoWifi(getActivity())) {
        } else {
            conexaoDeRede.exibirDialogoDeErroSemInternetDisponivel(getActivity());

            return;
        }
    }

    private void escolherArquivoAbrir() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
            && data != null  && data.getData() != null) {
            mImagemUri = data.getData();

            Picasso.with(getActivity()).load(mImagemUri).into(mImagemView);
        }
    }

    private String obterExtensaoArquivo(Uri uri) {
        ContentResolver cR = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();

        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void enviarArquivo() {
        if(mAuth.getInstance().getCurrentUser().getDisplayName() == null) {
            PerfilFragmento perfilFragmento = new PerfilFragmento();
            Bundle bundle = new Bundle();
            bundle.putInt("vendedor", 1);
            perfilFragmento.setArguments(bundle);
            getActivity().getSupportFragmentManager()
                    .beginTransaction().replace(R.id.frag_container, perfilFragmento)
                    .commit();
            return;
        }

        if (mEditTextoArquivoNome.getText().toString().trim().isEmpty()) {
            mEditTextoArquivoNome.setText("Nome  é obrigatório");
            mEditTextoArquivoNome.requestFocus();

            return;
        }

        if (mEditTextoArquivoPreco.getText().toString().trim().isEmpty()) {
            mEditTextoArquivoPreco.setError("Preço  é obrigatório");
            mEditTextoArquivoPreco.requestFocus();

            return;
        }

        if (mImagemUri != null) {
            final StorageReference arquivoRef = mStorageRef.child(System.currentTimeMillis()
                + "." + obterExtensaoArquivo(mImagemUri));

            mEnviarTarefa = arquivoRef.putFile(mImagemUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressBar.setProgress(0);
                                }
                            }, 500);

                            mImagemUri = null;
                            mImagemView.setImageBitmap(null);

                            Toast.makeText(getActivity(), "Sucesso no envio", Toast.LENGTH_LONG).show();

                            taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(
                                    new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Envio envio = new Envio(mEditTextoArquivoNome.getText().toString().trim(),
                                                    uri.toString(), mEditTextoArquivoPreco.getText().toString().trim(),
                                                    mDescricao.getText().toString().trim());

                                            String envioId = mDatabaseRef.push().getKey();
                                            mDatabaseRef.child(envioId).setValue(envio);
                                            mEditTextoArquivoNome.setText("");
                                            mEditTextoArquivoPreco.setText("");
                                            mDescricao.setText("");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            double progresso = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                            mProgressBar.setProgress((int) progresso);
                        }
                    });
        } else {

            Toast.makeText(getActivity(), "Item não selecionado", Toast.LENGTH_SHORT).show();
        }
    }
}