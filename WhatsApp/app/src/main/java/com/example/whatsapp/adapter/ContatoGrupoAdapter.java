package com.example.whatsapp.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsapp.R;
import com.example.whatsapp.model.Usuario;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContatoGrupoAdapter extends RecyclerView.Adapter<ContatoGrupoAdapter.MyViewHolder> {

    private List<Usuario> membrosSelecionados;
    private Context context;

    public ContatoGrupoAdapter(List<Usuario> membrosSelecionados, Context context) {
        this.membrosSelecionados = membrosSelecionados;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View usuarioSelecionado = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_contato_selecionado, parent, false);
        return new ContatoGrupoAdapter.MyViewHolder(usuarioSelecionado);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Usuario usuario = membrosSelecionados.get(position);

        holder.nome.setText(usuario.getNome());

        if(usuario.getFoto() != null){
            Uri uri = Uri.parse(usuario.getFoto());
            Glide.with(context).load(uri).into(holder.foto);
        }else{
            holder.foto.setImageResource(R.drawable.padrao);
        }
    }

    @Override
    public int getItemCount() {
        return membrosSelecionados.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        CircleImageView foto;
        TextView nome;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            foto = itemView.findViewById(R.id.imageViewFotoMembroSelecionado);
            nome = itemView.findViewById(R.id.textNomeMembroSelecionado);
        }
    }
}
