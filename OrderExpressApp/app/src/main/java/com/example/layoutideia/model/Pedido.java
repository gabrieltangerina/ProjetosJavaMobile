package com.example.layoutideia.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.layoutideia.config.ConfiguracaoFirebase;
import com.example.layoutideia.helper.GeraCodigo;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Pedido implements Serializable {

    private String id;
    private String dataPedido;
    private Cliente cliente;
    private Vendedor vendedor;
    private List<Produto> itens;
    private double total;
    private String operacaoVenda;
    private String formaPagamento;
    private String descricao;


    public Pedido() {
    }

    public Pedido(Cliente cliente, String dataPedido, double total, String operacaoVenda, String formaPagamento, String descricao) {
        this.dataPedido = dataPedido;
        this.cliente = cliente;
        this.total = total;
        this.operacaoVenda = operacaoVenda;
        this.formaPagamento = formaPagamento;
        this.descricao = descricao;
    }

    public void salvarPedido(DatabaseReference.CompletionListener listener){

        DatabaseReference databaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference pedido = databaseRef
                .child("pedidos")
                .child(vendedor.getId())
                .child(cliente.getCodigo())
                .child(getId());

        // Para coletar todos os pedidos referentes ao vendedor em "Meus Pedidos"
        DatabaseReference todosPedidosVendedor = databaseRef
                .child("todosPedidosVendedor")
                .child(vendedor.getId())
                .child(getId());


        String dataPedido = formatarData(getDataPedido());
        DatabaseReference vendasVendedor = databaseRef
                .child("vendas")
                .child(vendedor.getId())
                .child(dataPedido)
                .child(getId());

        for (Produto produto : itens) {
            produto.diminuirEstoque(produto.getQuantidade(), (databaseError, databaseReference) -> {
                if (databaseError != null) {
                    Log.e("Firebase", "Erro ao atualizar estoque: " + databaseError.getMessage());
                } else {
                    Log.d("Firebase", "Estoque atualizado com sucesso.");
                }
            });
        }

        todosPedidosVendedor.setValue(this);
        vendasVendedor.setValue(this);
        pedido.setValue(this, listener);
    }

    public void excluirPedido(DatabaseReference.CompletionListener listener){
        DatabaseReference databaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference pedido = databaseRef
                .child("pedidos")
                .child(vendedor.getId())
                .child(cliente.getCodigo())
                .child(getId());

        // Para coletar todos os pedidos referentes ao vendedor em "Meus Pedidos"
        DatabaseReference todosPedidosVendedor = databaseRef
                .child("todosPedidosVendedor")
                .child(vendedor.getId())
                .child(getId());

        pedido.removeValue(listener);
        todosPedidosVendedor.removeValue();
    }

    private static String formatarData(String date) {
        // Dividir a string da data em partes (dia, mês, ano)
        String[] dateParts = date.split("/");

        // Obter o mês e o ano
        String month = dateParts[1];
        String year = dateParts[2];

        // Se o mês tiver um dígito, adicionar um zero à esquerda
        if (month.length() == 1) {
            month = "0" + month;
        }

        // Concatenar o mês e o ano
        return month + year;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDataPedido() {
        return dataPedido;
    }

    public void setDataPedido(String dataPedido) {
        this.dataPedido = dataPedido;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Vendedor getVendedor() {
        return vendedor;
    }

    public void setVendedor(Vendedor vendedor) {
        this.vendedor = vendedor;
    }

    public List<Produto> getItens() {
        return itens;
    }

    public void setItens(List<Produto> itens) {
        this.itens = itens;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
    public String getOperacaoVenda() {
        return operacaoVenda;
    }

    public void setOperacaoVenda(String operacaoVenda) {
        this.operacaoVenda = operacaoVenda;
    }

    public String getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(String formaPagamento) {
        this.formaPagamento = formaPagamento;
    }


}
