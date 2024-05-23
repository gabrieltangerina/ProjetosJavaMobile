package com.example.whatsapp.model;

import com.example.whatsapp.config.ConfiguracaoFirebase;
import com.example.whatsapp.helper.Base64Custom;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.List;

public class Grupo implements Serializable {
    // Serializabre é para passar dados de uma activity para outra
    private String id;
    private String nome;
    private String foto;
    private List<Usuario> membros;

    public Grupo() {
        // Criando id único do grupo para salvar no Firebase
        DatabaseReference database = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference grupoRef = database.child("grupos");

        String idFirebase = grupoRef.push().getKey(); // getKey() é para recuperar o id gerado pelo push
        setId(idFirebase);
    }

    public void salvar(){
        DatabaseReference database = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference grupoRef = database.child("grupos");

        grupoRef.child(getId()).setValue(this);

        // Salvar grupo em conversas para ele ser listado
        for(Usuario membro: getMembros()){
            String idRemetente = Base64Custom.codificarBase64(membro.getEmail());
            String idDestinatario = getId();

            Conversa conversa = new Conversa();
            conversa.setIdRemetente(idRemetente);
            conversa.setIdDestinatario(idDestinatario);
            conversa.setUltimaMensagem("");
            conversa.setIsGroup("true");
            conversa.setGrupo(this);

            conversa.salvar();
        }
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

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public List<Usuario> getMembros() {
        return membros;
    }

    public void setMembros(List<Usuario> membros) {
        this.membros = membros;
    }
}
