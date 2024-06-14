package com.example.layoutideia.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.layoutideia.R;
import com.example.layoutideia.model.Produto;

import java.util.List;

public class AdapterItensCarrinho extends RecyclerView.Adapter<AdapterItensCarrinho.MyViewHolder> {

    private List<Produto> itensCarrinho;
    private Context context;

    public AdapterItensCarrinho(List<Produto> itensCarrinho, Context context) {
        this.itensCarrinho = itensCarrinho;
        this.context = context;
    }

    public List<Produto> getItensCarrinho(){
        return this.itensCarrinho;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemCarrinho = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_itens_carrinho, parent, false);

        return new MyViewHolder(itemCarrinho);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Produto produto = itensCarrinho.get(position);

        holder.nome.setText(produto.getCodigo() + " - " + produto.getNome());
        holder.quantidade.setText("Qtnd: " + produto.getQuantidade());
        holder.valorUnidade.setText("Valor Uni.: R$" + produto.getPreco());

        double quantidade = Double.parseDouble(produto.getQuantidade().toString());
        double precoUnidade = Double.parseDouble(produto.getPreco().toString());
        double total = quantidade * precoUnidade;

        String totalFormatado = String.format("Total: R$%.2f", total);
        holder.valorTotal.setText(totalFormatado);
    }

    @Override
    public int getItemCount() {
        return itensCarrinho.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView nome;
        TextView quantidade;
        TextView valorTotal;
        TextView valorUnidade;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            nome = itemView.findViewById(R.id.nome);
            quantidade = itemView.findViewById(R.id.quantidadeTotal);
            valorTotal = itemView.findViewById(R.id.valorTotal);
            valorUnidade = itemView.findViewById(R.id.textValorUnidade);

        }
    }

}
