package com.example.layoutideia.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.layoutideia.R;
import com.example.layoutideia.helper.GeraCodigo;
import com.example.layoutideia.model.Cliente;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

public class CadastrarClienteActivity extends AppCompatActivity {

    private Toolbar toolbar;

    // Componentes de Layout
    private TextInputEditText editNomeCliente;
    private TextInputEditText editNomeFantasia;
    private TextInputEditText editCnpj;
    private TextInputEditText editEndereco;
    private TextInputEditText editCidade;
    private Button buttonCadastrar;
    private Button buttonExcluir;
    private ProgressBar progressBar;

    // Valores campos cadastro
    private String idCliente;
    private String nomeCliente;
    private String nomeFantasia;
    private String cpnj;
    private String endereco;
    private String cidade;

    private String direcionamentoActivity;
    private String flagCadastroAlteracao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_cliente);

        // Config. Toolbar
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Cadastro de Cliente");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Config. Componentes de Layout
        editNomeCliente = findViewById(R.id.textNomeCliente);
        editNomeFantasia = findViewById(R.id.textNomeFantasia);
        editCnpj = findViewById(R.id.textCnpj);
        editEndereco = findViewById(R.id.textEndereco);
        editCidade = findViewById(R.id.textCidade);
        buttonCadastrar = findViewById(R.id.buttonCadastrar);
        buttonExcluir = findViewById(R.id.buttonExcluir);
        progressBar = findViewById(R.id.progressBar);

        // Pegando valores passados pela intent
        if (getIntent().getExtras() != null) {
            direcionamentoActivity = getIntent().getStringExtra("activity");
            flagCadastroAlteracao = getIntent().getStringExtra("flagCadastroAlteracao");

            if ("Alteracao".equals(flagCadastroAlteracao)) {
                idCliente = getIntent().getStringExtra("codigoCliente");
                editNomeCliente.setText(getIntent().getStringExtra("nomeCliente"));
                editNomeFantasia.setText(getIntent().getStringExtra("nomeFantasia"));
                editCnpj.setText(getIntent().getStringExtra("cnpj"));
                editEndereco.setText(getIntent().getStringExtra("endereco"));
                editCidade.setText(getIntent().getStringExtra("cidade"));

                toolbar.setTitle("Alterar Dados Cliente");
                buttonCadastrar.setText("Confirmar Alteração");
                buttonExcluir.setVisibility(View.VISIBLE);

                buttonExcluir.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Cliente cliente = new Cliente();
                        cliente.setCodigo(idCliente);
                        // Não adicionei as outras informações como nome, razao social etc pois nao é necessário

                        new AlertDialog.Builder(CadastrarClienteActivity.this)
                                .setTitle("Excluir Cliente")
                                .setMessage("Tem certeza que deseja excluir o cliente " + cliente.getNomeCliente() + "?" +
                                        " Os dados serão excluidos permanentemente")
                                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        cliente.excluirCliente(new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                                Toast.makeText(CadastrarClienteActivity.this, "Cliente excluido", Toast.LENGTH_SHORT).show();

                                                // Send broadcast to notify MeusClientesActivity about the deletion
                                                Intent intent = new Intent("CLIENTE_EXCLUIDO");
                                                sendBroadcast(intent);

                                                finish();
                                            }
                                        });
                                    }
                                })
                                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss(); // Fecha o diálogo
                                    }
                                })
                                .create()
                                .show();
                    }
                });
            }
        }

        buttonCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validaCampos()){
                    progressBar.setVisibility(View.VISIBLE);

                    Cliente cliente = new Cliente();
                    cliente.setNomeCliente(nomeCliente);
                    cliente.setNomeFantasia(nomeFantasia);
                    cliente.setCpnj(cpnj);
                    cliente.setEndereco(endereco);
                    cliente.setCidade(cidade);

                    if(flagCadastroAlteracao.equals("Criacao")){ // Fazendo cadastro do cliente

                        // Gera o código para novos clientes
                        cliente.setCodigo(GeraCodigo.gerarCodigoUnico());

                        cliente.salvarCliente(new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                if(error != null){
                                    Toast.makeText(CadastrarClienteActivity.this, "Ocorreu um erro ao cadastrar cliente", Toast.LENGTH_SHORT).show();
                                    Log.e("ERRO CADASTRO CLIENTE", error.getMessage());
                                    progressBar.setVisibility(View.GONE);
                                }else{
                                    Toast.makeText(CadastrarClienteActivity.this, "Cliente cadastrado com sucesso", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);

                                    if(direcionamentoActivity.equals("MeusClientes")){
                                        startActivity(new Intent(CadastrarClienteActivity.this, MeusClientesActivity.class));
                                    }else if(direcionamentoActivity.equals("SelecionarCliente")){
                                        startActivity(new Intent(CadastrarClienteActivity.this, SelecionaClienteActivity.class));
                                    }
                                }
                            }
                        });
                    }else if(flagCadastroAlteracao.equals("Alteracao")){ // Fazendo alteração do cliente

                        cliente.setCodigo(idCliente);
                        cliente.alterarCliente(new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                if(error != null){
                                    Toast.makeText(CadastrarClienteActivity.this, "Ocorreu um erro ao alterar cliente", Toast.LENGTH_SHORT).show();
                                    Log.e("ERRO ALTERAR CLIENTE", error.getMessage());
                                    progressBar.setVisibility(View.GONE);
                                }else{
                                    Toast.makeText(CadastrarClienteActivity.this, "Cliente alterado com sucesso", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);

                                    if(direcionamentoActivity.equals("MeusClientes")){
                                        startActivity(new Intent(CadastrarClienteActivity.this, MeusClientesActivity.class));
                                    }else if(direcionamentoActivity.equals("SelecionarCliente")){
                                        startActivity(new Intent(CadastrarClienteActivity.this, SelecionaClienteActivity.class));
                                    }
                                }
                            }
                        });
                    }

                }
            }
        });


    }

    private boolean validaCampos(){
        boolean camposCorretos = true;

        nomeCliente = editNomeCliente.getText().toString();
        nomeFantasia = editNomeFantasia.getText().toString();
        cpnj = editCnpj.getText().toString();
        endereco = editEndereco.getText().toString();
        cidade = editCidade.getText().toString();

        if(nomeCliente.isEmpty()){
            Toast.makeText(this, "Campo 'Nome Cliente' precisa ser preenchido", Toast.LENGTH_SHORT).show();
            camposCorretos = false;
        }else if(nomeFantasia.isEmpty()){
            Toast.makeText(this, "Campo 'Nome Fantasia' precisa ser preenchido", Toast.LENGTH_SHORT).show();
            camposCorretos = false;
        }else if(cpnj.isEmpty()){
            Toast.makeText(this, "Campo 'CNPJ' precisa ser preenchido", Toast.LENGTH_SHORT).show();
            camposCorretos = false;
        }else if(endereco.isEmpty()){
            Toast.makeText(this, "Campo 'Endereço' precisa ser preenchido", Toast.LENGTH_SHORT).show();
            camposCorretos = false;
        }else if(cidade.isEmpty()){
            Toast.makeText(this, "Campo 'Cidade' precisa ser preenchido", Toast.LENGTH_SHORT).show();
            camposCorretos = false;
        }

        return camposCorretos;
    }
}