package com.example.whatsapp.helper;

import com.example.whatsapp.config.ConfiguracaoFirebase;
import com.google.firebase.auth.FirebaseAuth;

public class UsuarioFirebase {

    public static String getIdUser(){
        FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAuth();
        String email = autenticacao.getCurrentUser().getEmail();
        String id = Base64Custom.codificarBase64(email);

        return id;
    }

}
