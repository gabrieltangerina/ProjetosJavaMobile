package com.example.layoutideia.helper;

import java.util.UUID;

public class GeraCodigo {

    // Gera um código único usando UUID
    public static String gerarCodigoUnico() {
        return UUID.randomUUID().toString();
    }

    // Se quiser usar um código numérico aleatório
    public static String gerarCodigoNumerico() {
        int codigo = (int)(Math.random() * 1000000); // Gera um número de 6 dígitos
        return String.format("%06d", codigo); // Formata o número com 6 dígitos, preenchendo com zeros à esquerda se necessário
    }

}
