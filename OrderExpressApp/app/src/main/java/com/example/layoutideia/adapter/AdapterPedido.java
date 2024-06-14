package com.example.layoutideia.adapter;

import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.layoutideia.R;
import com.example.layoutideia.model.Cliente;
import com.example.layoutideia.model.Pedido;

import java.util.List;

public class AdapterPedido extends RecyclerView.Adapter<AdapterPedido.MyViewHolder> {

    private List<Pedido> listaPedidos;

    public AdapterPedido(List<Pedido> listaPedidos) {
        this.listaPedidos = listaPedidos;
    }

    public List<Pedido> getListaPedido(){
        return this.listaPedidos;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View pedido = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_pedidos, parent, false);

        return new MyViewHolder(pedido);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Pedido pedido = listaPedidos.get(position);
        Cliente cliente = pedido.getCliente();

        holder.nomeCliente.setText(cliente.getNomeCliente());
        holder.dataPedido.setText(pedido.getDataPedido());
        String totalFormatado = String.format("Total: R$%.2f", pedido.getTotal());
        holder.total.setText(totalFormatado);
        holder.formaPagamento.setText("Form. Pagamento: " + pedido.getFormaPagamento());
        holder.operacaoVenda.setText("Operação: " + pedido.getOperacaoVenda());
    }

    @Override
    public int getItemCount() {
        return listaPedidos.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView nomeCliente;
        TextView dataPedido;
        TextView total;
        TextView formaPagamento;
        TextView operacaoVenda;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            nomeCliente = itemView.findViewById(R.id.textNomeCliente);
            dataPedido = itemView.findViewById(R.id.textDataPedido);
            total = itemView.findViewById(R.id.textTotalPedido);
            formaPagamento = itemView.findViewById(R.id.textFormaPagamento);
            operacaoVenda = itemView.findViewById(R.id.textOpVenda);
        }
    }

}
