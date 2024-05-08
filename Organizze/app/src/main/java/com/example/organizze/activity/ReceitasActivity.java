package com.example.organizze.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.organizze.R;
import com.example.organizze.config.ConfiguracaoFirebase;
import com.example.organizze.helper.Base64Custom;
import com.example.organizze.helper.DateCustom;
import com.example.organizze.model.Movimentacao;
import com.example.organizze.model.Usuario;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class ReceitasActivity extends AppCompatActivity {

    private TextInputEditText campoData, campoCategoria, campoDescricao;
    private EditText campoValor;
    private Movimentacao movimentacao;
    private DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private Double receitaTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receitas);

        campoData = findViewById(R.id.editData);
        campoCategoria = findViewById(R.id.editCategoria);
        campoDescricao = findViewById(R.id.editDescricao);
        campoValor = findViewById(R.id.editValor);

        // Preenche o campo data com a data atual
        campoData.setText(DateCustom.dataAtual());

        recuperaReceitaTotalUser();

    }

    public void salvaReceita(View v){

        if(!validaCamposReceita()) return;

        String data = campoData.getText().toString();
        String categoria = campoCategoria.getText().toString();
        String descricao = campoDescricao.getText().toString();
        Double valor = Double.parseDouble(campoValor.getText().toString());

        movimentacao = new Movimentacao(data, categoria, descricao, "r", valor);
        Double receitaAtualizada = receitaTotal + valor;

        try{
            movimentacao.salvar(data);
            atualizaReceitaTotalUser(receitaAtualizada);
            Toast.makeText(this, "Receita salva com sucesso", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(this, "Falha ao salvar receita " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public boolean validaCamposReceita(){
        String data = campoData.getText().toString();
        String categoria = campoCategoria.getText().toString();
        String descricao = campoDescricao.getText().toString();
        String valor = campoValor.getText().toString();

        if(data.isEmpty()){
            Toast.makeText(this, "Campo 'Data' precisa ser preenchido", Toast.LENGTH_SHORT).show();
            return false;
        }else if(categoria.isEmpty()){
            Toast.makeText(this, "Campo 'Categoria' precisa ser preenchido", Toast.LENGTH_SHORT).show();
            return false;
        }else if(descricao.isEmpty()){
            Toast.makeText(this, "Campo 'Descrição' precisa ser preenchido", Toast.LENGTH_SHORT).show();
            return false;
        }else if(valor.isEmpty()){
            Toast.makeText(this, "Campo 'Valor' precisa ser preenchido", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public void recuperaReceitaTotalUser(){
        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(emailUsuario);
        DatabaseReference usuarioRef = firebaseRef.child("usuarios").child(idUsuario);

        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue(Usuario.class);
                receitaTotal = usuario.getReceitaTotal();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void atualizaReceitaTotalUser(Double receita){
        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(emailUsuario);
        DatabaseReference usuarioRef = firebaseRef.child("usuarios").child(idUsuario);

        usuarioRef.child("receitaTotal").setValue(receita);
    }
}