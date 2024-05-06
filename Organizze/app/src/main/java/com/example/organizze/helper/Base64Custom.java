package com.example.organizze.helper;

import android.util.Base64;

public class Base64Custom {

    public static String codificarBase64(String texto){
        // "\\n|\\r" - remove espaços no começo e no final do texto
        return Base64.encodeToString(texto.getBytes(), Base64.NO_WRAP).replace("(\\n|\\r)","");
    }

    public static String decodificarBase64(String textoCodificado){
        // O método decode não retorna uma string então esta sendo feito a conversão com new String(...)
        return new String(Base64.decode(textoCodificado, Base64.DEFAULT));
    }

}
