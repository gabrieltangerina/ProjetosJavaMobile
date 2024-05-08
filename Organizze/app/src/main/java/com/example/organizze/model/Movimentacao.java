package com.example.organizze.model;

import com.example.organizze.config.ConfiguracaoFirebase;
import com.example.organizze.helper.Base64Custom;
import com.example.organizze.helper.DateCustom;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class Movimentacao {

    private String data, categoria, descricao, tipo;
    private double valor;

    public Movimentacao() {
    }

    public Movimentacao(String data, String categoria, String descricao, String tipo, double valor) {
        this.data = data;
        this.categoria = categoria;
        this.descricao = descricao;
        this.tipo = tipo;
        this.valor = valor;
    }

    public void salvar(String data){

        FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        String dataFormatada = DateCustom.mesAnoDataEscolhida(data);

        DatabaseReference referencia = ConfiguracaoFirebase.getFirebaseDatabase();
        referencia.child("movimentacao") // nó movimentacao
                  .child(Base64Custom.codificarBase64(autenticacao.getCurrentUser().getEmail())) // nó com o codigo do usuario
                  .child(dataFormatada) // no com o dia mes da despesa ou receita
                  .push() // O push faz gerar um id aleatório no firebase
                  .setValue(this); // salva todos os atributos dessa classe nesse caminho

    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }
}
