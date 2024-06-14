package com.example.layoutideia.model;

import java.io.Serializable;

public class Produto implements Serializable {

    private String nome;
    private String codigo;
    private Double preco;
    private Integer estoque;
    private Integer quantidade = 0;
    private boolean selecionado;

    public Produto(String nome, String codigo, Double preco, Integer estoque) {
        this.nome = nome;
        this.codigo = codigo;
        this.preco = preco;
        this.estoque = estoque;
        this.selecionado = false;
    }

    public Produto() {
        this.selecionado = false;
    }

    public boolean isSelecionado() {
        return selecionado;
    }

    public void setSelecionado(boolean selecionado) {
        this.selecionado = selecionado;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

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
