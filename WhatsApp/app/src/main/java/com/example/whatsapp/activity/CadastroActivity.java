package com.example.whatsapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.whatsapp.R;
import com.example.whatsapp.config.ConfiguracaoFirebase;
import com.example.whatsapp.helper.Base64Custom;
import com.example.whatsapp.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class CadastroActivity extends AppCompatActivity {

    private TextInputEditText campoNome;
    private TextInputEditText campoEmail;
    private TextInputEditText campoSenha;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        campoNome  = findViewById(R.id.editNome);
        campoEmail = findViewById(R.id.editEmail);
        campoSenha = findViewById(R.id.editSenha);

    }

    public void validarCampos(View v){
        String textoNome = String.valueOf(campoNome.getText());
        String textoEmail = String.valueOf(campoEmail.getText());
        String textoSenha = String.valueOf(campoSenha.getText());

        if(textoNome.isEmpty()){
            Toast.makeText(this, "Campo 'Nome' está vazio", Toast.LENGTH_SHORT).show();
            return;
        }else if (textoEmail.isEmpty()){
            Toast.makeText(this, "Campo 'Email' está vazio", Toast.LENGTH_SHORT).show();
            return;
        }else if (textoSenha.isEmpty()){
            Toast.makeText(this, "Campo 'Senha' está vazio", Toast.LENGTH_SHORT).show();
            return;
        }

        Usuario usuario = new Usuario(textoNome, textoEmail, textoSenha);

        cadastrarUsuario(usuario);
    }

    public void cadastrarUsuario(Usuario usuario){
        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();
        autenticacao.createUserWithEmailAndPassword(usuario.getEmail(), usuario.getSenha())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    try{
                        String idUsuario = Base64Custom.codificarBase64(usuario.getEmail());
                        usuario.setId(idUsuario);
                        usuario.salvar();
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    Toast.makeText(CadastroActivity.this, "Usuário cadastrado com sucesso", Toast.LENGTH_SHORT).show();
                    finish();
                }else{
                    String excesao = "";

                    try{
                        throw task.getException();
                    }catch (FirebaseAuthWeakPasswordException e){
                        excesao = "Digite uma senha mais forte";
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        excesao = "Digite um e-mail válido";
                    }catch (FirebaseAuthUserCollisionException e){
                        excesao = "Esta conta já está cadastrada";
                    }catch (Exception e){
                        excesao = "Erro ao cadastrar conta " + e.getMessage();
                        e.printStackTrace();
                    }

                    Toast.makeText(CadastroActivity.this, excesao, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
