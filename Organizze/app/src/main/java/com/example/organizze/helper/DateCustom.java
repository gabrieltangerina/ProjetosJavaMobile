package com.example.organizze.helper;

import java.text.SimpleDateFormat;

public class DateCustom {

    public static String dataAtual(){

        long data = System.currentTimeMillis(); // Recupera uma representação da data atual do tipo long
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy"); // Customiza o formato que deseja para a data
        String dataString = simpleDateFormat.format(data); // Pega o formato desejado e adiciona em uma variável

        return dataString;
    }

    public static String mesAnoDataEscolhida(String data){

        String retornoData[] = data.split("/"); // Quebra a String em pedaços e adiciona em um array onde os indices são as quebras
        String mes = retornoData[1];
        String ano = retornoData[2];
        String dataFormatada = mes + ano;

        return dataFormatada;
    }

}
