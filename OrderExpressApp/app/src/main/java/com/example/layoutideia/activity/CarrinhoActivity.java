package com.example.layoutideia.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.layoutideia.R;
import com.example.layoutideia.adapter.AdapterItensCarrinho;
import com.example.layoutideia.helper.RecyclerViewClick;
import com.example.layoutideia.model.Produto;
import com.example.layoutideia.viewmodel.CarrinhoViewModel;

import java.util.ArrayList;
import java.util.List;

public class CarrinhoActivity extends AppCompatActivity {

    private RecyclerView recyclerCarrinho;
    private AdapterItensCarrinho adapterItensCarrinho;
    private List<Produto> itensCarrinho;
    private SearchView searchView;
    private InputMethodManager inputMethodManager;
    private CarrinhoViewModel carrinhoViewModel;
    private Toolbar toolbar;
    private TextView textAvisoErro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrinho);

        carrinhoViewModel = new ViewModelProvider(this).get(CarrinhoViewModel.class);

        if(carrinhoViewModel.getItensCarrinho().isEmpty()){
            textAvisoErro = findViewById(R.id.textAvisoErro);
            textAvisoErro.setVisibility(View.VISIBLE);
        }

        // Config. Toolbar
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Carrinho");
        toolbar.setSubtitle(carrinhoViewModel.calcularTotalPedido());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Pega o serviço do sistema responsável pera interação com o teclado virtual
        inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);

        // Recuperando dados passados pela activity DadosPedidoActivity
        if(getIntent().getExtras() != null){
            List<Produto> listaProdutos = (List<Produto>) getIntent().getExtras().getSerializable("itensPedido");
            itensCarrinho.addAll(listaProdutos);
        }

        recyclerCarrinho = findViewById(R.id.recyclerCarrinho);

        // Config. Adapter
        // adapterItensCarrinho = new AdapterItensCarrinho(itensCarrinho, getApplication());

        itensCarrinho = carrinhoViewModel.getItensCarrinho();
        adapterItensCarrinho = new AdapterItensCarrinho(itensCarrinho,this);

        // Config. RecyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerCarrinho.setLayoutManager(layoutManager);
        recyclerCarrinho.setHasFixedSize(true);
        recyclerCarrinho.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));
        recyclerCarrinho.setAdapter(adapterItensCarrinho);

        // Config. Click RecyclerView
        configurarClickRecycler(itensCarrinho);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_carrinho, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.menu_pesquisa_carrinho){
            searchView = (SearchView) item.getActionView();
            searchView.setQueryHint("Buscar produto...");

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    if(newText != null && !newText.isEmpty()){
                        pesquisarItemCarrinho(newText.toLowerCase());
                    }

                    if(newText.isEmpty()){
                        restaurarItensCarrinho();
                    }

                    return false;
                }
            });
        }

        return super.onOptionsItemSelected(item);
    }

    public void restaurarItensCarrinho(){
        adapterItensCarrinho = new AdapterItensCarrinho(itensCarrinho,this);
        recyclerCarrinho.setAdapter(adapterItensCarrinho);
        adapterItensCarrinho.notifyDataSetChanged();
    }

    public void pesquisarItemCarrinho(String texto){
        List<Produto> listaItensBusca = new ArrayList<>();

        for(Produto produto: itensCarrinho){
            String nomeProduto = produto.getNome().toLowerCase();
            String codProduto = produto.getCodigo();

            if(nomeProduto.contains(texto) || codProduto.contains(texto)){
                listaItensBusca.add(produto);
            }
        }

        adapterItensCarrinho = new AdapterItensCarrinho(listaItensBusca,this);
        recyclerCarrinho.setAdapter(adapterItensCarrinho);
        adapterItensCarrinho.notifyDataSetChanged();
    }

    public void configurarClickRecycler(List<Produto> listaProdutos){
        recyclerCarrinho.addOnItemTouchListener(new RecyclerViewClick(
                getApplicationContext(),
                recyclerCarrinho,
                new RecyclerViewClick.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        List<Produto> listaProdutosAtualizada = adapterItensCarrinho.getItensCarrinho();
                        Produto produto = listaProdutosAtualizada.get(position);

                        // Inflando o layout personalizado
                        LayoutInflater inflater = getLayoutInflater();
                        View dialogView = inflater.inflate(R.layout.carrinho_alert_dialog, null);

                        // Criando o AlertDialog
                        AlertDialog.Builder builder = new AlertDialog.Builder(CarrinhoActivity.this);
                        builder.setView(dialogView);
                        AlertDialog dialog = builder.create();

                        // Criando referência aos campos no layout
                        TextView nomeProduto = dialogView.findViewById(R.id.textNomeProduto);
                        EditText editQuantidade = dialogView.findViewById(R.id.editQuantidade);
                        TextView editPrecoUnidade = dialogView.findViewById(R.id.editPrecoUnidade);
                        TextView textTotal = dialogView.findViewById(R.id.textTotalPedido);
                        ImageButton buttonSair = dialogView.findViewById(R.id.buttonSair);
                        Button buttonSalvar = dialogView.findViewById(R.id.buttonSalvar);
                        Button buttonExcluir = dialogView.findViewById(R.id.buttonExcluir);
                        buttonExcluir.setVisibility(View.VISIBLE);
                        buttonExcluir.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                excluirItemCarrinho(produto.getCodigo(), dialog);
                            }
                        });

                        // Adicionando valores e eventos nos campos do layout
                        nomeProduto.setText(produto.getNome());
                        editQuantidade.setText(String.valueOf(produto.getQuantidade()));
                        editPrecoUnidade.setText(String.valueOf(produto.getPreco()));

//                        editQuantidade.requestFocus();
//                        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
//
//                        // Adicionando foco no input de quantidade e subindo o teclado
//                        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
//                            @Override
//                            public void onShow(DialogInterface dialogInterface) {
//                                new Handler().postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        editQuantidade.requestFocus();
//                                        inputMethodManager.showSoftInput(editQuantidade, InputMethodManager.SHOW_IMPLICIT);
//                                    }
//                                }, 200);
//                            }
//                        });

                        double total = Double.parseDouble(editQuantidade.getText().toString()) * Double.parseDouble(editPrecoUnidade.getText().toString());
                        textTotal.setText(String.format("Total: R$%.2f", total));

                        TextWatcher textWatcher = new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                String quantidade = editQuantidade.getText().toString();
                                String preco = editPrecoUnidade.getText().toString();
                                String total = atualizarPreco(quantidade, preco);
                                textTotal.setText(total);
                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                            }
                        };

                        editQuantidade.addTextChangedListener(textWatcher);
                        editPrecoUnidade.addTextChangedListener(textWatcher);


                        buttonSair.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        buttonSalvar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                // Validando campos
                                String textoPreco = editPrecoUnidade.getText().toString();
                                String textoQuantidade = editQuantidade.getText().toString();
                                if (textoQuantidade.isEmpty()){
                                    Toast.makeText(CarrinhoActivity.this, "Informe a quantidade", Toast.LENGTH_SHORT).show();
                                    return;
                                }else if(textoPreco.isEmpty()){
                                    Toast.makeText(CarrinhoActivity.this, "Informe o preço", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                // Atualizando produto
                                produto.setPreco(Double.parseDouble(textoPreco));
                                produto.setQuantidade(Integer.parseInt(textoQuantidade));
                                adapterItensCarrinho.notifyDataSetChanged();

                                dialog.dismiss();
                                Toast.makeText(CarrinhoActivity.this, "Alterações salvas", Toast.LENGTH_SHORT).show();
                                toolbar.setSubtitle(carrinhoViewModel.calcularTotalPedido());
                            }
                        });

                        // Adicionando ouvinte ao fechar o AlertDialog clicando no enter do teclado
                        editPrecoUnidade.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                            @Override
                            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                                if (actionId == EditorInfo.IME_ACTION_DONE && event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

                                    // Validando campos
                                    String textoPreco = editPrecoUnidade.getText().toString();
                                    String textoQuantidade = editQuantidade.getText().toString();
                                    if (textoQuantidade.isEmpty()){
                                        Toast.makeText(CarrinhoActivity.this, "Informe a quantidade", Toast.LENGTH_SHORT).show();
                                        return false;
                                    }else if(textoPreco.isEmpty()){
                                        Toast.makeText(CarrinhoActivity.this, "Informe o preço", Toast.LENGTH_SHORT).show();
                                        return false;
                                    }

                                    // Adicionando produto
                                    produto.setPreco(Double.parseDouble(textoPreco));
                                    produto.setQuantidade(Integer.parseInt(textoQuantidade));
                                    adapterItensCarrinho.notifyDataSetChanged();
                                    Toast.makeText(CarrinhoActivity.this, "Alterações salvas", Toast.LENGTH_SHORT).show();
                                    toolbar.setSubtitle(carrinhoViewModel.calcularTotalPedido());

                                    dialog.dismiss();

                                    return true;
                                }
                                return false;
                            }
                        });

                        dialog.show();
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                }
        ));
    }

    public void excluirItemCarrinho(String codigo, AlertDialog dialogProduto){
        AlertDialog.Builder builder = new AlertDialog.Builder(CarrinhoActivity.this);
        builder.setTitle("Confirmação de Exclusão");
        builder.setMessage("Tem certeza que deseja excluir este item do carrinho?");
        builder.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String retorno = carrinhoViewModel.removeItem(codigo);

                if (!retorno.equals("")) {
                    Toast.makeText(CarrinhoActivity.this, retorno, Toast.LENGTH_SHORT).show();
                    dialogProduto.dismiss();
                    adapterItensCarrinho.notifyDataSetChanged();
                    toolbar.setSubtitle(carrinhoViewModel.calcularTotalPedido());
                } else {
                    Toast.makeText(CarrinhoActivity.this, "Ocorreu um erro ao excluir o produto", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    public String atualizarPreco(String quantidade, String preco) {
        if (numeroValido(quantidade) && numeroValido(preco)) {
            try {
                double quantidadeDouble = Double.parseDouble(quantidade);
                double precoDouble = Double.parseDouble(preco);
                double total = quantidadeDouble * precoDouble;
                return String.format("Total: R$%.2f", total);
            } catch (NumberFormatException e) {
                return "Total: R$0.00";
            }
        } else {
            return "Total: R$0.00";
        }
    }


    public boolean numeroValido(String num){
        if(num == null || num.isEmpty()){
            return false;
        }

        try{
            Double.parseDouble(num);
            return true;
        }catch (NumberFormatException e){
            return false;
        }
    }
}