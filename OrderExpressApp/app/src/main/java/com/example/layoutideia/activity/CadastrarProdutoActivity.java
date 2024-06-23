package com.example.layoutideia.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.layoutideia.R;
import com.example.layoutideia.helper.GeraCodigo;
import com.example.layoutideia.model.Produto;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class CadastrarProdutoActivity extends AppCompatActivity {

    private TextInputEditText editNomeProduto;
    private TextInputEditText editPrecoProduto;
    private TextInputEditText editQuantidadeProduto;

    private Button btnCadastrar;
    private Button btnExcluir;
    private Button btnAlterar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_produto);

        // Config. Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Cadastro de Produtos");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editNomeProduto = findViewById(R.id.textNomeProduto);
        editPrecoProduto = findViewById(R.id.textPreco);
        editQuantidadeProduto = findViewById(R.id.textQuantidade);

        if(getIntent().getExtras() != null){ // Para quando for a alteração
            String idProduto = getIntent().getStringExtra("idProduto");
            String nomeProduto = getIntent().getStringExtra("nomeProduto");
            Double precoProduto = getIntent().getDoubleExtra("precoProduto", 0);
            Integer estoqueProduto = getIntent().getIntExtra("estoqueProduto", 0);

            editNomeProduto.setText(nomeProduto);
            editPrecoProduto.setText(precoProduto.toString());
            editQuantidadeProduto.setText(estoqueProduto.toString());

            btnCadastrar = findViewById(R.id.buttonCadastrar);
            btnCadastrar.setVisibility(View.GONE);
            btnExcluir = findViewById(R.id.buttonExcluir);
            btnAlterar = findViewById(R.id.buttonAlterar);

            btnAlterar.setVisibility(View.VISIBLE);
            btnExcluir.setVisibility(View.VISIBLE);
            btnAlterar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Produto produto = new Produto();
                    produto.setCodigo(idProduto);
                    produto.setNome(editNomeProduto.getText().toString());
                    produto.setPreco(Double.parseDouble(editPrecoProduto.getText().toString()));
                    produto.setEstoque(Integer.parseInt(editQuantidadeProduto.getText().toString()));

                    produto.atualizarProduto(new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                            if(error != null){
                                Toast.makeText(CadastrarProdutoActivity.this, "Erro ao atualizar o produto", Toast.LENGTH_SHORT).show();
                                finish();
                            }else{
                                Toast.makeText(CadastrarProdutoActivity.this, "Produto atualizado com sucesso", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    });
                }
            });

            btnExcluir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Produto produto = new Produto();
                    produto.setCodigo(idProduto);

                    new AlertDialog.Builder(CadastrarProdutoActivity.this)
                            .setTitle("Excluir Produto")
                            .setMessage("Tem certeza que deseja excluir o produto " + produto.getNome() + "?" +
                                    " Os dados serão excluidos permanentemente")
                            .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    produto.excluirProduto(new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                            if(error != null){
                                                Toast.makeText(CadastrarProdutoActivity.this, "Ocorreu um erro ao excluir o produto", Toast.LENGTH_SHORT).show();
                                                Log.e("ERRO EXCLUIR PRODUTO", error.getMessage());
                                                finish();
                                            }else{
                                                Toast.makeText(CadastrarProdutoActivity.this, "Produto excluido", Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
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

        }else{ // Para quando a criação de um produto
            btnCadastrar = findViewById(R.id.buttonCadastrar);
            btnCadastrar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String nomeProduto = editNomeProduto.getText().toString();
                    String precoProduto = editPrecoProduto.getText().toString();
                    String qtndProduto = editQuantidadeProduto.getText().toString();

                    if(validaCampos(nomeProduto, precoProduto, qtndProduto)){
                        Produto produto = new Produto();
                        produto.setCodigo(GeraCodigo.gerarCodigoNumerico());
                        produto.setNome(nomeProduto);
                        // String precoFormatado = formatarPreco(precoProduto);
                        // produto.setPreco(Double.parseDouble(precoFormatado));
                        produto.setPreco(Double.valueOf(precoProduto));
                        produto.setEstoque(Integer.parseInt(qtndProduto));

                        produto.salvarProduto(new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                if(error != null){
                                    Toast.makeText(CadastrarProdutoActivity.this, "Ocorreu um erro ao salvar o produto: " + produto.getNome(), Toast.LENGTH_SHORT).show();
                                    finish();
                                }else{
                                    Toast.makeText(CadastrarProdutoActivity.this, "Produto cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }
                        });
                    }
                }
            });
        }


    }

    private String formatarPreco(String precoProduto){
        // Remove separadores de milhar (vírgulas)
        String numeroSemMilhar = precoProduto.replace(",", "");

        // Converte o valor para BigDecimal para manipulação segura e precisa
        BigDecimal precoDecimal = new BigDecimal(numeroSemMilhar);

        // Arredonda o valor para duas casas decimais
        precoDecimal = precoDecimal.setScale(2, RoundingMode.HALF_UP);

        // Formata o valor com duas casas decimais
        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        return decimalFormat.format(precoDecimal);
    }

    private boolean validaCampos(String nome, String preco, String qtnd){
        if(nome.trim().equals("")){
            Toast.makeText(this, "Campo 'Nome' está inválido", Toast.LENGTH_SHORT).show();
            return false;
        }else if(preco.trim().equals("")){
            Toast.makeText(this, "Campo 'Preço' está inválido", Toast.LENGTH_SHORT).show();
            return false;
        }else if(qtnd.trim().equals("")){
            Toast.makeText(this, "Campo 'Quantidade' está inválido", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        return true;
    }
}