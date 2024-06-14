package com.example.layoutideia.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.layoutideia.R;
import com.example.layoutideia.config.ConfiguracaoFirebase;
import com.example.layoutideia.helper.Base64Custom;
import com.example.layoutideia.helper.GeraCodigo;
import com.example.layoutideia.helper.VendedorFirebase;
import com.example.layoutideia.model.Vendedor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class CadastroActivity extends AppCompatActivity {

    private TextView nome;
    private TextView email;
    private TextView senha;
    private Button buttonCadastro;
    private FirebaseAuth autenticacao;
    private ProgressBar progressBar;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        // Config. Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Cadastro");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        nome = findViewById(R.id.editTextNome);
        email = findViewById(R.id.editTextEmail);
        senha = findViewById(R.id.editTextSenha);
        buttonCadastro = findViewById(R.id.buttonCadastro);
        progressBar = findViewById(R.id.progressBar);
        
        buttonCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validaCampos();
            }
        });
        
    }

    public void cadastrarVendedor(Vendedor vendedor){
        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();
        autenticacao.createUserWithEmailAndPassword(
            vendedor.getEmail(), vendedor.getSenha()
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(CadastroActivity.this, "Sucesso ao cadastrar vendedor", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);

                    try {

                        String idVendedor = Base64Custom.codificarBase64(vendedor.getEmail());
                        vendedor.setId(idVendedor);
                        vendedor.salvarVendedor(vendedor);

                        Intent intent = new Intent(CadastroActivity.this, MainActivity.class);
                        intent.putExtra("NomeVendedor", vendedor.getNome());
                        startActivity(intent);

                    }catch (Exception e){
                        e.printStackTrace();
                        finish();
                    }

                }else{
                    String excesao = "";
                    try{
                        throw task.getException();
                    }catch (FirebaseAuthWeakPasswordException e){
                        excesao = "Digite uma senha mais forte";
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        excesao = "Digite um e-mail válido";
                    }catch (FirebaseAuthUserCollisionException e){
                        excesao = "Conta já cadastrada";
                    }catch (Exception e){
                        excesao = "Erro ao cadastrar vendedor: " + e.getMessage();
                        e.printStackTrace();
                    }

                    Toast.makeText(CadastroActivity.this, excesao, Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
    
    public boolean validaCampos(){
        progressBar.setVisibility(View.VISIBLE);

        String textNome = nome.getText().toString();
        String textEmail = email.getText().toString();
        String textSenha = senha.getText().toString();
        
        if(textNome.isEmpty()){
            Toast.makeText(this, "Campo 'Nome' está inválido", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return  false;
        }else if(textEmail.isEmpty()){
            Toast.makeText(this, "Campo 'E-mail' está inválido", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return false;
        } else if(textSenha.isEmpty()){
            Toast.makeText(this, "Campo 'Senha' está inválido", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return false;
        }

        Vendedor vendedor = new Vendedor();
        vendedor.setNome(textNome);
        vendedor.setEmail(textEmail);
        vendedor.setSenha(textSenha);

        cadastrarVendedor(vendedor);

        return true;
    }
}