package com.example.whatsapp.model;

import android.widget.Toast;

import com.example.whatsapp.config.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

public class Conversa {
    private String idDestinatario;
    private String idRemetente;
    private String ultimaMensagem;
    private Usuario usuarioExibicao;

    public void salvar(){
        DatabaseReference database = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference conversaRef = database.child("conversas");

        conversaRef
                .child(this.getIdRemetente())
                .child(this.getIdDestinatario())
                .setValue(this);
    }

    public Conversa(){

    }

    public String getIdDestinatario() {
        return idDestinatario;
    }

    public void setIdDestinatario(String idDestinatario) {
        this.idDestinatario = idDestinatario;
    }

    public String getIdRemetente() {
        return idRemetente;
    }

    public void setIdRemetente(String idRemetente) {
        this.idRemetente = idRemetente;
    }

    public String getUltimaMensagem() {
        return ultimaMensagem;
    }

    public void setUltimaMensagem(String ultimaMensagem) {
        this.ultimaMensagem = ultimaMensagem;
    }

    public Usuario getUsuarioExibicao() {
        return usuarioExibicao;
    }

    public void setUsuarioExibicao(Usuario usuarioExibicao) {
        this.usuarioExibicao = usuarioExibicao;
    }
}
