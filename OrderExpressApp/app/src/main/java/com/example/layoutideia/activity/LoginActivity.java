package com.example.layoutideia.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.layoutideia.R;
import com.example.layoutideia.config.ConfiguracaoFirebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAuth();

    private TextView textEmail;
    private TextView textSenha;
    private TextView textCadastro;
    private Button buttonLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Config. Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("OrderExpress");
        setSupportActionBar(toolbar);

        textEmail = findViewById(R.id.editTextEmail);
        textSenha = findViewById(R.id.editTextSenha);
        buttonLogin = findViewById(R.id.buttonLogin);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validaCampos()){
                    logar();
                }
            }
        });

        textCadastro = findViewById(R.id.textCadastro);
        textCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, CadastroActivity.class));
            }
        });
    }

    public boolean validaCampos(){
        String email = textEmail.getText().toString();
        String senha = textSenha.getText().toString();

        if(email.isEmpty()){
            Toast.makeText(this, "Campo 'E-mail' inválido", Toast.LENGTH_SHORT).show();
            return false;
        }else if(senha.isEmpty()){
            Toast.makeText(this, "Campo 'Senha' inválido", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
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

    public void abrirTelaPrincipal(){
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
    }

    public void logar(){
        String email = textEmail.getText().toString();
        String senha = textSenha.getText().toString();

        autenticacao.signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        }else{
                            String excesao = "";
                            try{
                                throw task.getException();
                            }catch (FirebaseAuthInvalidUserException e){
                                excesao = "Vendedor não cadastrado";
                            }catch (FirebaseAuthInvalidCredentialsException e){
                                excesao = "E-mail ou senha inválidos";
                            }catch (Exception e){
                                excesao = "Erro ao fazer autenticação: " + e.getMessage();
                                e.printStackTrace();
                            }

                            Toast.makeText(LoginActivity.this, excesao, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void abrirDialogChaveAcesso(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chave de acesso");

        // Infla o layout do diálogo com um EditText
        final EditText input = new EditText(this);

        input.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        // Adicionando icone a esquerda
        input.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_seguranca_login, 0, 0, 0);
        input.setCompoundDrawablePadding(10); // Define o espaçamento entre o ícone e o texto

        input.setHint("Senha");
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD); // Define o tipo de entrada como senha

        builder.setView(input);

        // Configura o botão de confirmação
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String textoInserido = input.getText().toString();

                // Faça algo com o texto inserido, como exibir em um Toast
                Toast.makeText(getApplicationContext(), "Texto inserido: " + textoInserido, Toast.LENGTH_SHORT).show();
            }
        });

        // Configura o botão de cancelamento (opcional)
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Fecha o diálogo sem fazer nada
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}