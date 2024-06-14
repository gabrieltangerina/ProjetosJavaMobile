package com.example.layoutideia.model;

import com.example.layoutideia.config.ConfiguracaoFirebase;
import com.example.layoutideia.helper.Base64Custom;
import com.example.layoutideia.helper.VendedorFirebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

public class Vendedor {
    private String id;
    private String nome;
    private String email;
    private String senha;

    public void salvarVendedor(Vendedor vendedorEnviado){
        String idVendedor = Base64Custom.codificarBase64(getEmail());

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference vendedor = firebaseRef
                .child("vendedores")
                .child(idVendedor);

        // Atualizando nome no perfil do vendedor (Auth)
        VendedorFirebase.atualizarNomeVendedor(vendedorEnviado.getNome());

        vendedor.setValue(this);
    }

    public Vendedor() {
    }

    public Vendedor(String id, String nome, String email, String senha) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public String getSenha() {
        return senha;
    }


    public void setSenha(String senha) {
        this.senha = senha;
    }
}
