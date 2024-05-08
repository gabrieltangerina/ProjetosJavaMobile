package com.example.organizze.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.organizze.R;
import com.example.organizze.helper.DateCustom;
import com.example.organizze.model.Movimentacao;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

public class DespesasActivity extends AppCompatActivity {

    private TextInputEditText campoData, campoCategoria, campoDescricao;
    private EditText campoValor;
    private Movimentacao movimentacao;
    private String textoData, textoCategoria, textoDescricao;
    private Double textoValor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_despesas);

        campoData = findViewById(R.id.editData);
        campoCategoria = findViewById(R.id.editCategoria);
        campoDescricao = findViewById(R.id.editDescricao);
        campoValor = findViewById(R.id.editValor);

        campoData.setText(DateCustom.dataAtual());

    }

    public void salvarDespesa(View v){

        if(!validarCamposDespesas()) return;

        String data = campoData.getText().toString();

        movimentacao = new Movimentacao(
                data,
                campoCategoria.getText().toString(),
                campoDescricao.getText().toString(),
                "d",
                Double.parseDouble(campoValor.getText().toString()));

        try{
            movimentacao.salvar(data);
            Toast.makeText(this, "Despesa salva", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(this, "Falha ao salvar despesa", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean validarCamposDespesas(){

        if(campoData.getText().toString().isEmpty()){
            Toast.makeText(this, "Campo 'Data' precisa ser preenchido", Toast.LENGTH_SHORT).show();
            return false;
        }else if(campoCategoria.getText().toString().isEmpty()){
            Toast.makeText(this, "Campo 'Categoria' precisa ser preenchido", Toast.LENGTH_SHORT).show();
            return false;
        }else if(campoDescricao.getText().toString().isEmpty()){
            Toast.makeText(this, "Campo 'Descrição' precisa ser preenchido", Toast.LENGTH_SHORT).show();
            return false;
        }else if(campoValor.getText().toString().isEmpty()){
            Toast.makeText(this, "Campo 'Valor' precisa ser preenchido", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}