package com.example.organizze.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.organizze.R;
import com.example.organizze.helper.DateCustom;
import com.example.organizze.model.Movimentacao;
import com.google.android.material.textfield.TextInputEditText;

public class ReceitasActivity extends AppCompatActivity {

    private TextInputEditText campoData, campoCategoria, campoDescricao;
    private EditText campoValor;
    private Movimentacao movimentacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receitas);

        campoData = findViewById(R.id.editData);
        campoCategoria = findViewById(R.id.editCategoria);
        campoDescricao = findViewById(R.id.editDescricao);
        campoValor = findViewById(R.id.editValor);

        // Adicionando data atual no campo data
        campoData.setText(DateCustom.dataAtual());

    }

    public void salvaReceita(View v){
        String data = campoData.getText().toString();
        String categoria = campoCategoria.getText().toString();
        String descricao = campoDescricao.getText().toString();
        Double valor = Double.parseDouble(campoValor.getText().toString());

        movimentacao = new Movimentacao(data, categoria, descricao, "r", valor);

        try{
            movimentacao.salvar(data);
            Toast.makeText(this, "Receita salva com sucesso", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(this, "Falha ao salvar receita " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}