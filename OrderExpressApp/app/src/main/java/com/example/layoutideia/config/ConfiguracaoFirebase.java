package com.example.layoutideia.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ConfiguracaoFirebase {

    private static DatabaseReference database;
    private static FirebaseAuth autenticacao;

    public static DatabaseReference getFirebaseDatabase(){
        if(database == null){
            database = FirebaseDatabase.getInstance().getReference();
        }

        return database;
    }

    public static FirebaseUser getVendedorAtual(){
        FirebaseAuth usuario = ConfiguracaoFirebase.getFirebaseAuth();
        return usuario.getCurrentUser();
    }

    public static FirebaseAuth getFirebaseAuth(){
        if(autenticacao == null){
            autenticacao = FirebaseAuth.getInstance();
        }

        return autenticacao;
    }

}
