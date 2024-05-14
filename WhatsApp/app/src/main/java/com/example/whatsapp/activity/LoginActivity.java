package com.example.whatsapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.whatsapp.R;
import com.example.whatsapp.config.ConfiguracaoFirebase;
import com.example.whatsapp.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAuth();
    private TextInputEditText inputEmail;
    private TextInputEditText inputSenha;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputEmail = findViewById(R.id.editEmail);
        inputSenha = findViewById(R.id.editSenha);
    }

    public void abrirTelaCadastro(View v){
        startActivity(new Intent(this, CadastroActivity.class));
    }

    public void abrirTelaPrincipal(){
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Verificando se o usuário está logado
        FirebaseUser usuarioAtual = autenticacao.getCurrentUser();
        if(usuarioAtual != null){
            abrirTelaPrincipal();
        }
    }

    public void logarUsuario(View view){
        String textoEmail = String.valueOf(inputEmail.getText());
        String textoSenha = String.valueOf(inputSenha.getText());

        if(validaCampos(textoEmail, textoSenha)){
            autenticacao.signInWithEmailAndPassword(textoEmail, textoSenha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        abrirTelaPrincipal();
                    }else{
                        String excecao = "";
                        try{
                            throw task.getException();
                        }catch (FirebaseAuthInvalidUserException e){
                            excecao = "Usuário não cadastrado";
                        }catch (FirebaseAuthInvalidCredentialsException e){
                            excecao = "E-mail e/ou senha inválidos";
                        }catch (Exception e){
                            excecao = "Erro ao cadastrar usuário " + e.getMessage();
                            e.printStackTrace();
                        }

                        Toast.makeText(LoginActivity.this, excecao, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

    public boolean validaCampos(String email, String senha){
        boolean camposCorretos = true;

        if(email.isEmpty()){
            Toast.makeText(this, "Campo 'E-mail' está vazio", Toast.LENGTH_SHORT).show();
            camposCorretos = false;
        }else if (senha.isEmpty()){
            Toast.makeText(this, "Campo 'Senha' está vazio", Toast.LENGTH_SHORT).show();
            camposCorretos = false;
        }

        return camposCorretos;
    }

}