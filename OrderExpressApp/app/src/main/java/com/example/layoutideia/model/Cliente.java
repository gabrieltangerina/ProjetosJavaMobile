package com.example.layoutideia.model;

import android.util.Log;

import com.example.layoutideia.config.ConfiguracaoFirebase;
import com.example.layoutideia.helper.VendedorFirebase;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;

public class Cliente implements Serializable {
    private String nomeCliente;
    private String codigo;
    private String  nomeFantasia;
    private String  cpnj;
    private String  cidade;
    private String  endereco;

    public Cliente(String nomeCliente, String codigo, String nomeFantasia, String cpnj, String cidade, String endereco) {
        this.nomeCliente = nomeCliente;
        this.codigo = codigo;
        this.nomeFantasia = nomeFantasia;
        this.cpnj = cpnj;
        this.cidade = cidade;
        this.endereco = endereco;
    }

    public Cliente(){
    }

    public void salvarCliente(DatabaseReference.CompletionListener listener){
        DatabaseReference databaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference cliente = databaseRef
                .child("clientes")
                .child(VendedorFirebase.getIdVendedorLogado())
                .child(getCodigo());

        cliente.setValue(this, listener);
    }

    public void excluirCliente(DatabaseReference.CompletionListener listener){
        DatabaseReference databaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference cliente = databaseRef
                .child("clientes")
                .child(VendedorFirebase.getIdVendedorLogado())
                .child(getCodigo());

        cliente.removeValue(listener);
    }

    public void alterarCliente(DatabaseReference.CompletionListener listener){
        DatabaseReference databaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference cliente = databaseRef
                .child("clientes")
                .child(VendedorFirebase.getIdVendedorLogado())
                .child(getCodigo());

        cliente.setValue(this, listener);
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }

    public String getCpnj() {
        return cpnj;
    }

    public void setCpnj(String cpnj) {
        this.cpnj = cpnj;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }
}
