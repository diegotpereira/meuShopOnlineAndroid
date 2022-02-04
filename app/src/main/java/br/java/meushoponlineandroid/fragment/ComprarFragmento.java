package br.java.meushoponlineandroid.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

import br.java.meushoponlineandroid.R;
import br.java.meushoponlineandroid.model.ConexaoDeRede;
import br.java.meushoponlineandroid.model.Envio;
import br.java.meushoponlineandroid.model.Usuario;

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
    private List<Envio> mEnvio;

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

        mEnvio = new ArrayList<>();
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragmento_comprar, container, false);
    }
}