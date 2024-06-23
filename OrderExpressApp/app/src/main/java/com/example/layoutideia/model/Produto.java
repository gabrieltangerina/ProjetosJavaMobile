package com.example.layoutideia.model;

import com.example.layoutideia.config.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class Produto implements Serializable {

    private String nome;
    private String codigo;
    private Double preco;
    private Integer estoque;
    private Integer quantidade = 0;

    public Produto(String nome, String codigo, Double preco, Integer estoque) {
        this.nome = nome;
        this.codigo = codigo;
        this.preco = preco;
        this.estoque = estoque;
    }

    public Produto() {
    }

    public void salvarProduto(DatabaseReference.CompletionListener listener){
        DatabaseReference databaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference produto = databaseRef
                .child("produtos")
                .child(getCodigo());

        produto.setValue(this, listener);
    }

    public void excluirProduto(DatabaseReference.CompletionListener listener){
        DatabaseReference databaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference produto = databaseRef
                .child("produtos")
                .child(getCodigo());

        produto.removeValue(listener);
    }

    public void atualizarProduto(DatabaseReference.CompletionListener listener){
        DatabaseReference databaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference produto = databaseRef
                .child("produtos")
                .child(getCodigo());

        produto.setValue(this, listener);
    }

    @Exclude
    public Integer getQuantidade() {
        return quantidade;
    }

    @Exclude
    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCodigo() {
        return codigo;
    }
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
    public Double getPreco() {
        return preco;
    }
    public void setPreco(Double preco) {
        this.preco = preco;
    }
    public Integer getEstoque() {
        return estoque;
    }

    public void setEstoque(Integer estoque) {
        this.estoque = estoque;
    }
}
