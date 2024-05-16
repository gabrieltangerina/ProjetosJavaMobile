package com.example.whatsapp.helper;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class Permissao {
    public static boolean validarPermissoes(String[] permissoes, Activity activity, int requestCode){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            List<String> listaPermissoesNaoAutorizadas = new ArrayList<>();

            for(String permissao : permissoes){
                Boolean temPermissao = ContextCompat.checkSelfPermission(activity, permissao) == PackageManager.PERMISSION_GRANTED;
                Toast.makeText(activity, permissao + " | " + temPermissao, Toast.LENGTH_SHORT).show();
                if(!temPermissao) listaPermissoesNaoAutorizadas.add(permissao);
            }


            if(listaPermissoesNaoAutorizadas.isEmpty()) return true;


            String[] novasPermissoes = new String[listaPermissoesNaoAutorizadas.size()];
            listaPermissoesNaoAutorizadas.toArray(novasPermissoes);
            ActivityCompat.requestPermissions(activity, novasPermissoes, requestCode);
        }

        return true;
    }
}
