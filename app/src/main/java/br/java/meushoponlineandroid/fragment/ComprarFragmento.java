package br.java.meushoponlineandroid.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import br.java.meushoponlineandroid.R;
import br.java.meushoponlineandroid.model.ConexaoDeRede;
import br.java.meushoponlineandroid.model.Envio;
import br.java.meushoponlineandroid.model.Usuario;
import br.java.meushoponlineandroid.ui.DrawerActivity;

public class ComprarFragmento extends Fragment {

    ImageView pImagem;

    private TextView nome;
    private TextView preco;
    private TextView vendedor;
    private TextView dataVenda;
    private TextView Desc_tag;
    private TextView Desc_text;

    private Button btn_fazer_oferta;
    private Button btn_mensagem;
    private Button btn_deletar;

    boolean mItemClicado = false;

    private String sNome;
    private String sEmail;
    private String pNome;

    private String bNome;
    private String bEmail;

    private int posicao;
    private String key;

    int imagemPosicao;
    String stringImagemUri;

    FirebaseAuth mAuth;
    DatabaseReference mDataRef;
    private FirebaseStorage mStorage;
    DatabaseReference usuarioDatabase;

    private ValueEventListener mLIstener;

    private List<Usuario> mUsuario;
    private List<Envio> mEnvios;

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
        
        String testeEmail = mAuth.getInstance().getCurrentUser().getEmail();
        if (testeEmail.equals(sEmail)) {
            btn_fazer_oferta.setVisibility(View.GONE);
            btn_mensagem.setVisibility(View.GONE);
            btn_deletar.setVisibility(View.VISIBLE);

            Toast.makeText(getActivity(), "Você é vendedor deste produto", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragmento_comprar, container, false);

        nome = (TextView) v.findViewById(R.id.produto_nome);
        preco = (TextView) v.findViewById(R.id.produto_preco);
        vendedor = (TextView) v.findViewById(R.id.produto_vendedor);
        dataVenda = (TextView) v.findViewById(R.id.produto_data);
        btn_fazer_oferta = (Button) v.findViewById(R.id.btn_oferta);
        btn_mensagem = (Button) v.findViewById(R.id.btn_msg);
        btn_deletar = (Button) v.findViewById(R.id.btn_deletar);

        pImagem = (ImageView) v.findViewById(R.id.produto_imagem);
        Desc_tag = (TextView) v.findViewById(R.id.tag_descricao);
        Desc_text = (TextView) v.findViewById(R.id.descricao);
        bNome = mAuth.getInstance().getCurrentUser().getDisplayName();
        bEmail = mAuth.getInstance().getCurrentUser().getEmail();

        mEnvios = new ArrayList<>();

        mStorage = FirebaseStorage.getInstance();
        mDataRef = FirebaseDatabase.getInstance().getReference("envios");

        Bundle bundle = getArguments();
        if (bundle != null) {
            posicao = bundle.getInt("posicao");
            pNome = bundle.getString("nome");

            String pImagemUrl =bundle.getString("imagemUrl");
            String pPreco = bundle.getString("preco");

            sNome = bundle.getString("usuarioNome");
            key = bundle.getString("key");

            String data = bundle.getString("data");
            String desc = bundle.getString("desc");
            sEmail = bundle.getString("email");

            nome.setText(pNome);
            preco.setText("R$ " + pNome);
            vendedor.setText(sNome);
            dataVenda.setText(data);

            if (desc != null) {
                Desc_tag.setVisibility(View.VISIBLE);
                Desc_text.setVisibility(View.VISIBLE);
                Desc_text.setText(desc);
            }

            if (pImagemUrl != null) {
                String fotoUrl = pImagemUrl;
                Glide.with(this)
                        .load(fotoUrl)
                        .into(pImagem);
            }
        }
        btn_mensagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MsgFragmento msgFragmento = new MsgFragmento();
                Bundle bundle1 = new Bundle();
                bundle1.putString("sEmail", sEmail);
                bundle1.putString("pNome", pNome);
                bundle1.putString("sNome", sNome);
                bundle1.putString("bNome", mAuth.getInstance().getCurrentUser().getDisplayName());
                bundle1.putString("bEmail", mAuth.getInstance().getCurrentUser().getEmail());
                msgFragmento.setArguments(bundle1);

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frag_container, msgFragmento)
                        .addToBackStack(null).commit();
            }
        });

        btn_fazer_oferta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Você tem certeza disso?");
                builder.setMessage("Isso enviará uma notificação por e-mail junto com seu ID de e-mail para o vendedor.");

                builder.setPositiveButton("confirma", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        enviarEmailParaVendedor();
                        deletarProduto();

                    }
                });

                builder.setNegativeButton("cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog ad = builder.create();
                ad.show();

            }
        });
        mLIstener = mDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mEnvios.clear();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        btn_deletar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Alerta!");
                builder.setMessage("A exclusão é permanente. Tem certeza de que deseja excluir?");
                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deletarProduto();
                    }
                });
                builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog ad = builder.create();
                ad.show();
            }
        });
        // Inflate the layout for this fragment
        return v;
    }
    private void deletarProduto() {
        Envio selecionarItem = mEnvios.get(posicao);
        final String selecioneChave = selecionarItem.getKey();

        StorageReference imagemRef = mStorage.getReferenceFromUrl(selecionarItem.getImagemUrl());
        imagemRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                startActivity(new Intent(getActivity(), DrawerActivity.class));
                mDataRef.child(selecioneChave).removeValue();

                Toast.makeText(getActivity(), "Iem deletado", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        });
    }
    private void enviarEmailParaVendedor() {
        String email = sEmail;
        String subject = "[ teste] Pedido de produto " + pNome;
        String msg = "unknown-user";
        if (bNome != "")
            msg = bNome;
        String agradecimentoMsg = "\n\nObrigado por usar o teste :)";
        String autoMsg =
                "\n\nEste é um e-mail gerado automaticamente. Por favor não responda esse email.";
        String mensagem = "Olá " + sNome + ". " + msg + " está solicitando seu produto \" " + pNome + "\".Aguarde mais resposta de " + msg + " .Se quiser pode escrever para " + msg + " no id de e-mail" + bEmail + " ." + agradecimentoMsg + autoMsg;

    }
    private void enviarEmailParaComprador() {
        String email = bEmail;
        String subject = "[teste] Solicitação bem-sucedida para " + pNome;
        String agradecimentoMsg = "\n\nObrigado por usar teste :)";
        String autoMsg = "\n\nEste é um e-mail gerado automaticamente. Por favor não responda esse email.";
        String mensagem = "Hello " + bNome + ". You have requested " + sNome  +" for \"" + pNome + "\". You can send message to " + sNome + " in the app by clicking on message button." + agradecimentoMsg + autoMsg ;


    }
}