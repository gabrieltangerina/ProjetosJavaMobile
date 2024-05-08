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
import com.github.clans.fab.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class DespesasActivity extends AppCompatActivity {

    private TextInputEditText campoData, campoCategoria, campoDescricao;
    private EditText campoValor;
    private Movimentacao movimentacao;
    private DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private Double despesaTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_despesas);

        campoData = findViewById(R.id.editData);
        campoCategoria = findViewById(R.id.editCategoria);
        campoDescricao = findViewById(R.id.editDescricao);
        campoValor = findViewById(R.id.editValor);

        campoData.setText(DateCustom.dataAtual());

        recuperarDespesaTotal();
    }

    public void salvarDespesa(View v){

        if(!validarCamposDespesas()) return;

        String data = campoData.getText().toString();
        Double valor = Double.parseDouble(campoValor.getText().toString());

        movimentacao = new Movimentacao(
                data,
                campoCategoria.getText().toString(),
                campoDescricao.getText().toString(),
                "d",
                valor);

        Double despesaAtualizada = despesaTotal + valor;
        atualizarDespesas(despesaAtualizada);

        movimentacao.salvar(data);
        Toast.makeText(this, "Despesa salva", Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "Falha ao salvar despesa", Toast.LENGTH_SHORT).show();

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

    public void recuperarDespesaTotal(){
        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(emailUsuario);
        DatabaseReference usuarioRef = firebaseRef.child("usuarios").child(idUsuario);

        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue(Usuario.class);
                despesaTotal = usuario.getDespesaTotal();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void atualizarDespesas(Double despesa){
        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(emailUsuario);
        DatabaseReference usuarioRef = firebaseRef.child("usuarios").child(idUsuario);

        usuarioRef.child("despesaTotal").setValue(despesa);
    }

}