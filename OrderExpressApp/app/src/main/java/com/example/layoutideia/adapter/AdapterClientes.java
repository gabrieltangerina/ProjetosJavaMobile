package com.example.layoutideia.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.layoutideia.R;
import com.example.layoutideia.model.Cliente;

import java.util.List;

public class AdapterClientes extends RecyclerView.Adapter<AdapterClientes.MyViewHolder> {

    private List<Cliente> listaClientes;
    private Context context;

    public AdapterClientes(List<Cliente> lista, Context c) {
        this.listaClientes = lista;
        this.context = c;
    }

    public List<Cliente> getListaClientes(){
        return this.listaClientes;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemLista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_cliente, parent, false);

        return new MyViewHolder(itemLista);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Cliente cliente = listaClientes.get(position);

        // holder.Nome_Cod_Cliente.setText("(Cód.:" + cliente.getCodigo() + ")\n" + cliente.getNomeCliente());
        holder.Nome_Cod_Cliente.setText("Nome: " + cliente.getNomeCliente());
        holder.NomeFantasia_Cliente.setText("Fantasia: " + cliente.getNomeFantasia());
        holder.CNPJ_IE_Cliente.setText("CPF: " + cliente.getCpnj() + "\nCidade: " + cliente.getCidade());
        holder.Endereco_Cliente.setText("Endereço: " + cliente.getEndereco());
    }

    @Override
    public int getItemCount() {
        return listaClientes.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView Nome_Cod_Cliente;
        TextView NomeFantasia_Cliente;
        TextView CNPJ_IE_Cliente;
        TextView Endereco_Cliente;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            Nome_Cod_Cliente = itemView.findViewById(R.id.Nome_Cod_Cliente);
            NomeFantasia_Cliente = itemView.findViewById(R.id.NomeFantasia_Cliente);
            CNPJ_IE_Cliente = itemView.findViewById(R.id.CNPJ_IE_Cliente);
            Endereco_Cliente = itemView.findViewById(R.id.Endereco_Cliente);
        }
    }

}
