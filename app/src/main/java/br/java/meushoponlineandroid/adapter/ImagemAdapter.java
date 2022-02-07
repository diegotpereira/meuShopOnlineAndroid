package br.java.meushoponlineandroid.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import br.java.meushoponlineandroid.R;
import br.java.meushoponlineandroid.fragment.ComprarFragmento;
import br.java.meushoponlineandroid.model.ConexaoDeRede;
import br.java.meushoponlineandroid.model.Envio;

public class ImagemAdapter extends RecyclerView.Adapter<ImagemAdapter.ImagemViewHolder> {

    private Context mContext;
    private List<Envio> meusEnvios;

    public ImagemAdapter(Context context, List<Envio> envios) {
        mContext = context;
        meusEnvios = envios;

        ConexaoDeRede conexaoDeRede = new ConexaoDeRede();

        if (conexaoDeRede.estaConectadoNaInternet(mContext)
            || conexaoDeRede.estaConectadoNaRedeMovel(mContext)
            || conexaoDeRede.estaConectadoNoWifi(mContext)) {

        } else {
            conexaoDeRede.exibirDialogoDeErroSemInternetDisponivel(mContext);

            return;
        }
    }
    @NonNull
    @Override
    public ImagemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.imagem_item, parent, false);
        return new ImagemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImagemAdapter.ImagemViewHolder holder, int position) {
        Envio envioAtual = meusEnvios.get(position);
        holder.textViewwNome.setText(envioAtual.getNome());
        holder.textViewwPreco.setText("R$ " + envioAtual.getPreco());

        Picasso.with(mContext)
                .load(envioAtual.getImagemUrl())
                .placeholder(R.mipmap.ic_loading)
                .fit()
                .centerInside()
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return meusEnvios.size();
    }

    public class ImagemViewHolder extends RecyclerView.ViewHolder{

        public TextView textViewwNome;
        public TextView textViewwPreco;
        public ImageView imageView;

        public ImagemViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewwNome = itemView.findViewById(R.id.text_view_nome);
            textViewwPreco = itemView.findViewById(R.id.text_view_preco);
            imageView = itemView.findViewById(R.id.image_view_upload);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ComprarFragmento comprarFragmento = new ComprarFragmento();
                    Bundle bundle = new Bundle();

                    int posicao = getAdapterPosition();
                    Envio atual = meusEnvios.get(posicao);
                    String nome = atual.getNome();

                    bundle.putInt("posicao", posicao);
                    bundle.putString("nome", nome);
                    bundle.putString("preco", atual.getPreco());

                    if (imageView != null)
                        bundle.putString("imagemUrl", atual.getImagemUrl());
                    else
                        bundle.putString("imagemUrl", null);
                        bundle.putString("usuarioNome", atual.getUsuario());
                        bundle.putString("data", atual.getData());
                        bundle.putString("desc", atual.getDesc());
                        bundle.putString("email", atual.getEmail());
                        bundle.putString("key", atual.getKey());

                        comprarFragmento.setArguments(bundle);

                    ((FragmentActivity) mContext)
                            .getSupportFragmentManager()
                            .beginTransaction().replace(R.id.frag_container, comprarFragmento)
                            .addToBackStack(null).commit();
                }
            });
        }
    }
}
