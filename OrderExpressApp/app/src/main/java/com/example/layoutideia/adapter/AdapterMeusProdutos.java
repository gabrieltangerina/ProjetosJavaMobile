package com.example.layoutideia.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.layoutideia.R;
import com.example.layoutideia.model.Produto;

import java.util.List;

public class AdapterMeusProdutos extends RecyclerView.Adapter<AdapterMeusProdutos.MyViewHolder>{

    private List<Produto> listaProdutos;

    public AdapterMeusProdutos(List<Produto> listaProdutos) {
        this.listaProdutos = listaProdutos;
    }

    public List<Produto> getListaProdutos(){
        return this.listaProdutos;
    }

    @NonNull
    @Override
    public AdapterMeusProdutos.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemLista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_produtos, parent, false);

        return new AdapterMeusProdutos.MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterMeusProdutos.MyViewHolder holder, int position) {
        Produto produto = listaProdutos.get(position);

        holder.nome.setText(produto.getNome() + " (Cod.:" + produto.getCodigo() + ")");
        holder.estoque.setText("Estoque: " + produto.getEstoque());
        holder.preco.setText("R$" + produto.getPreco());
    }

    @Override
    public int getItemCount() {
        return listaProdutos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView nome;
        TextView preco;
        TextView estoque;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            nome = itemView.findViewById(R.id.Nome_Cod_Produto);
            preco = itemView.findViewById(R.id.Valor_Produto);
            estoque = itemView.findViewById(R.id.Quantidade_Produto);
        }
    }

}
