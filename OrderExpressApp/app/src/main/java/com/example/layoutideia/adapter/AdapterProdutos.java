package com.example.layoutideia.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.layoutideia.R;
import com.example.layoutideia.activity.DadosPedidoActivity;
import com.example.layoutideia.fragments.ItensPedidoFragment;
import com.example.layoutideia.model.Produto;
import com.example.layoutideia.viewmodel.CarrinhoViewModel;

import java.util.List;

public class AdapterProdutos extends RecyclerView.Adapter<AdapterProdutos.MyViewHolder> {

    private List<Produto> listaProdutos;
    private CarrinhoViewModel carrinhoViewModel;

    public AdapterProdutos(List<Produto> listaProdutos, CarrinhoViewModel carrinhoViewModel) {
        this.listaProdutos = listaProdutos;
        this.carrinhoViewModel = carrinhoViewModel;
    }

    public AdapterProdutos(List<Produto> listaProdutos) {
        this.listaProdutos = listaProdutos;
    }

    public List<Produto> getListaProdutos(){
        return this.listaProdutos;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemLista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_produtos, parent, false);

        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Produto produto = listaProdutos.get(position);

        holder.nome.setText(produto.getNome() + " (Cod.:" + produto.getCodigo() + ")");
        holder.containerProduto.setBackgroundResource(R.color.white);
        
        if (carrinhoViewModel != null) {
            String codigoProduto = produto.getCodigo();

            if (carrinhoViewModel.buscaPorId(codigoProduto)) {
                holder.containerProduto.setBackgroundResource(R.color.pedidos_carrinho);
            }
        }

        if(produto.getQuantidade() != 0){
            holder.quantidade.setText("Qtnd: " + produto.getQuantidade());
            double totalProduto = Double.parseDouble(produto.getQuantidade().toString()) * Double.parseDouble(produto.getPreco().toString());
            holder.preco.setText(String.format("Total: R$%.2f", totalProduto));

            Log.d("QUANTIDADE ENTROU", produto.getNome() + " " + produto.getQuantidade());

            holder.valorUnidade.setVisibility(View.VISIBLE);
            holder.valorUnidade.setText("Unidade: R$" + produto.getPreco());
        }else{
            holder.valorUnidade.setVisibility(View.GONE);
            holder.quantidade.setText("Qtnd: " + produto.getEstoque());
            holder.preco.setText("R$" + produto.getPreco());
            Log.d("QUANTIDADE NÃO ENTROU", produto.getNome() + " " + produto.getQuantidade());
        }

    }

    @Override
    public int getItemCount() {
        return listaProdutos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView nome;
        TextView preco;
        TextView quantidade;
        TextView valorUnidade;
        LinearLayout containerProduto;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            nome = itemView.findViewById(R.id.Nome_Cod_Produto);
            preco = itemView.findViewById(R.id.Valor_Produto);
            quantidade = itemView.findViewById(R.id.Quantidade_Produto);
            containerProduto = itemView.findViewById(R.id.containerProduto);
            valorUnidade = itemView.findViewById(R.id.textValorUnidade);

        }
    }

}
