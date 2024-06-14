package com.example.layoutideia.helper;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.layoutideia.config.ConfiguracaoFirebase;
import com.example.layoutideia.model.Vendedor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class VendedorFirebase {

    public static FirebaseUser getVendedorAtual(){
        FirebaseAuth vendedor = ConfiguracaoFirebase.getFirebaseAuth();
        return vendedor.getCurrentUser();
    }

    public static Vendedor getDadosVendedorLogado(){
        FirebaseUser firebaseUser = getVendedorAtual();

        Vendedor vendedor = new Vendedor();
        String id = Base64Custom.codificarBase64(firebaseUser.getEmail());
        vendedor.setId(id);
        vendedor.setEmail(firebaseUser.getEmail());
        vendedor.setNome(firebaseUser.getDisplayName());

        return vendedor;
    }

    public static String getIdVendedorLogado(){
        FirebaseUser firebaseUser = getVendedorAtual();
        String id = Base64Custom.codificarBase64(firebaseUser.getEmail());

        return id;
    }

    public static boolean atualizarNomeVendedor(String nome){
        try{
            FirebaseUser user = getVendedorAtual();
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(nome)
                    .build();

            user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(!task.isSuccessful()){
                        Log.d("Perfil", "Erro ao atualizar nome de perfil");
                    }
                }
            });

            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

}
