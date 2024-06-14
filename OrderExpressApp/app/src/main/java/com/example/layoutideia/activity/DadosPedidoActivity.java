package com.example.layoutideia.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.layoutideia.R;
import com.example.layoutideia.databinding.ActivityDadosPedidoBinding;
import com.example.layoutideia.fragments.InformacoesPedidoFragment;
import com.example.layoutideia.fragments.ItensPedidoFragment;
import com.example.layoutideia.model.Cliente;
import com.example.layoutideia.model.Produto;
import com.example.layoutideia.model.Vendedor;
import com.example.layoutideia.viewmodel.CarrinhoViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class DadosPedidoActivity extends AppCompatActivity {

    private ActivityDadosPedidoBinding binding;
    private NavHostFragment navHostFragment;
    private NavController navController;
    private String flagToolbar;

    private InputMethodManager inputMethodManager; // Armazena o serviço do sistema que lida com a interação com o teclado virtual

    private List<Produto> listaProdutos = new ArrayList<>();
    private Toolbar toolbar;
    private SearchView searchView;
    private CarrinhoViewModel carrinhoViewModel;
    private ItensPedidoFragment itensPedidoFragment;
    private FloatingActionButton fabSalvarPedido;
    private BottomNavigationView bottom_navigation;
    private InformacoesPedidoFragment informacoesPedidoFragment;

    // Dados para Cadastrar o pedido
    private Cliente cliente;
    private Vendedor vendedor;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDadosPedidoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initNavigation();

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Dados do Pedido");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        configurandoToolbar(toolbar);

        // Pega o serviço do sistema responsável pera interação com o teclado virtual
        inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);

        carrinhoViewModel = new ViewModelProvider(this).get(CarrinhoViewModel.class);

        if(getIntent().getExtras() != null){
            Intent intent = getIntent();
            cliente = (Cliente) intent.getSerializableExtra("Cliente");
        }

        fabSalvarPedido = findViewById(R.id.fabSalvarPedido);
        fabSalvarPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(carrinhoViewModel.getItensCarrinho().isEmpty()){
                    Toast.makeText(DadosPedidoActivity.this, "Adicione ao menos um item no carrinho para realizar o pedido", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(DadosPedidoActivity.this, ConsultaPedidoActivity.class);
                startActivity(intent);
            }
        });

        // Deixando Invisível o BottomNavigation quando abre o teclado
        bottom_navigation = findViewById(R.id.bottom_navigation);
        final View rootView = findViewById(android.R.id.content);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Código para ser executado quando ocorrer uma mudança no layout global
                Rect r = new Rect();
                rootView.getWindowVisibleDisplayFrame(r);
                int screenHeight = rootView.getRootView().getHeight();
                int keypadHeight = screenHeight - r.bottom;

                if (keypadHeight > screenHeight * 0.15) {
                    bottom_navigation.setVisibility(View.GONE);
                } else {
                    bottom_navigation.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void initNavigation(){
        navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_dados_pedido);
        navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController);
    }

    public void configurandoToolbar(Toolbar toolbar){
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController navController, @NonNull NavDestination navDestination, @Nullable Bundle bundle) {
                if (navDestination.getLabel().equals("fragment_itens_pedido2")) {
                    toolbar.setTitle("Itens do pedido");
                    flagToolbar = "I";
                } else if (navDestination.getLabel().equals("fragment_financeiro2")) {
                    toolbar.setTitle("Financeiro");
                    flagToolbar = "F";
                } else if (navDestination.getLabel().equals("fragment_cliente2")) {
                    toolbar.setTitle("Informações Cliente");
                    flagToolbar = "C";
                } else if (navDestination.getLabel().equals("fragment_informacoes_pedido2")) {
                    toolbar.setTitle("Informações Pedido");
                    flagToolbar = "P";
                } else if (navDestination.getLabel().equals("fragment_compras_recentes")) {
                    toolbar.setTitle("Últimas Compras");
                    flagToolbar = "UC";
                }

                // Para forçar a reinflação do menu
                invalidateOptionsMenu();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();

        if (flagToolbar.equals("I")) {
            inflater.inflate(R.menu.toolbar_itens_pedido, menu);
            toolbar.setSubtitle(carrinhoViewModel.calcularTotalPedido());
        } else if (flagToolbar.equals("F")) {
            inflater.inflate(R.menu.toolbar_financeiro, menu);
            toolbar.setSubtitle("");
        } else if (flagToolbar.equals("C")) {
            inflater.inflate(R.menu.toolbar_informacoes_cliente, menu);
            toolbar.setSubtitle("");
        } else if (flagToolbar.equals("P")) {
            inflater.inflate(R.menu.toolbar_informacoes_pedido, menu);
            toolbar.setSubtitle("");
        } else if (flagToolbar.equals("UC")) {
            inflater.inflate(R.menu.toolbar_compras_recentes, menu);
            toolbar.setSubtitle("");
        }

        return super.onCreateOptionsMenu(menu);
    }

    // Mensagem de aviso ao tentar voltar para a activity SelecionarCliente
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        showExitConfirmationDialog();
    }

    private void showExitConfirmationDialog() {

        if(!carrinhoViewModel.getItensCarrinho().isEmpty()){
            new AlertDialog.Builder(this)
                    .setTitle("Confirmação de Saída")
                    .setMessage("Tem certeza que deseja sair da aba do pedido? Os dados do pedido atual serão apagados.")
                    .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            carrinhoViewModel.limparItensCarrinho(); // Limpa os itens do carrinho
                            navigateUp(); // Volta para a SelecionarClienteActivity
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
        }else{
            finish();
        }

    }

    private void navigateUp() {
        Intent intent = new Intent(DadosPedidoActivity.this, SelecionaClienteActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // Finaliza a atividade atual
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            showExitConfirmationDialog();
            return true; // Consome o evento para evitar o comportamento padrão
        }else if (item.getItemId() == R.id.menu_pesquisa_produto) {
            searchView = (SearchView) item.getActionView();
            searchView.setQueryHint("Pesquisar produto...");

            // Pegando os dados do fragment ItensPedido
            Fragment currentFragment = navHostFragment.getChildFragmentManager().getPrimaryNavigationFragment();
            itensPedidoFragment = (ItensPedidoFragment) currentFragment;

            // listaProdutos.addAll(itensPedidoFragment.getListaProdutos());

            // Limpe a lista antes de adicionar os produtos
            listaProdutos.clear();
            listaProdutos.addAll(itensPedidoFragment.getListaProdutos());

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    if(!query.isEmpty()){
                        for(Produto produto: listaProdutos){
                            if(produto.getCodigo().equals(query)){
                                buscarProduto(produto);
                            }
                        }
                    }

                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    if (currentFragment instanceof ItensPedidoFragment) {
                        itensPedidoFragment.pesquisarProduto(newText.toLowerCase());
                    }

                    return false;
                }
            });
        }else if(item.getItemId() == R.id.menu_carrinho){
            Intent i = new Intent(DadosPedidoActivity.this, CarrinhoActivity.class);
            // i.putExtra("itensPedido", (Serializable) produtosCarrinho);
            startActivity(i);
        }else if(item.getItemId() == android.R.id.home){
            // Se clicar na seta de voltar para a activity de selecionar pedido a lista de itens
            // do carrinho será resetada
            if (!carrinhoViewModel.getItensCarrinho().isEmpty()) {
                carrinhoViewModel.limparItensCarrinho();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void buscarProduto(Produto produto){

        // Inflando o layout personalizado
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.carrinho_alert_dialog, null);

        // Criando o AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(DadosPedidoActivity.this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        // Criando referência aos campos no layout
        TextView nomeProduto = dialogView.findViewById(R.id.textNomeProduto);
        EditText editQuantidade = dialogView.findViewById(R.id.editQuantidade);
        EditText editPrecoUnidade = dialogView.findViewById(R.id.editPrecoUnidade);
        TextView textTotal = dialogView.findViewById(R.id.textTotalPedido);
        ImageButton buttonSair = dialogView.findViewById(R.id.buttonSair);
        Button buttonSalvar = dialogView.findViewById(R.id.buttonSalvar);
        Button buttonExcluir = dialogView.findViewById(R.id.buttonExcluir);

        // Adicionando valores e eventos nos campos do layout
        nomeProduto.setText(produto.getNome());

        if (carrinhoViewModel.buscaPorId(produto.getCodigo())) {
            Produto produtoCarrinho = carrinhoViewModel.buscaPorIdRetornaProduto(produto.getCodigo());
            editQuantidade.setText(String.valueOf(produtoCarrinho.getQuantidade()));
            editQuantidade.setSelection(editQuantidade.getText().length());
            buttonExcluir.setVisibility(View.VISIBLE);
            buttonExcluir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    excluirItemCarrinho(produto.getCodigo(), dialog);
                }
            });
        }

        editPrecoUnidade.setText(produto.getPreco().toString());
        editPrecoUnidade.setSelection(editPrecoUnidade.getText().length());// Para o cursor começar no final da linha, fincando mais facil de alterar o preço

        if(!editQuantidade.getText().toString().equals("") && !editPrecoUnidade.getText().toString().equals("")){
            double total = Double.parseDouble(editQuantidade.getText().toString()) * Double.parseDouble(editPrecoUnidade.getText().toString());
            textTotal.setText(String.format("Total: R$%.2f", total));
        }else{
            textTotal.setText("Total: R$0.00");
        }

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
                if (textoQuantidade.isEmpty() || textoQuantidade.equals("0")){
                    Toast.makeText(DadosPedidoActivity.this, "Informe a quantidade", Toast.LENGTH_SHORT).show();
                    return;
                }else if(textoPreco.isEmpty()){
                    Toast.makeText(DadosPedidoActivity.this, "Informe o preço", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (carrinhoViewModel.buscaPorId(produto.getCodigo())) {
                    carrinhoViewModel.atualizarItemCarrinho(produto.getCodigo(), textoQuantidade, textoPreco);
                    Toast.makeText(DadosPedidoActivity.this, "Valores do produto alterados", Toast.LENGTH_SHORT).show();
                    atualizarSubtituloToolbar(carrinhoViewModel.calcularTotalPedido());
                    dialog.dismiss();
                    return;
                }

                // Adicionando produto
                produto.setPreco(Double.parseDouble(textoPreco));
                produto.setQuantidade(Integer.parseInt(textoQuantidade));
                produto.setSelecionado(true);
                carrinhoViewModel.adicionarItemCarrinho(produto);
                Toast.makeText(DadosPedidoActivity.this, "'" + produto.getNome() + "' adicionado ao pedido", Toast.LENGTH_SHORT).show();
                itensPedidoFragment.atualizarAdapter();
                toolbar.setSubtitle(carrinhoViewModel.calcularTotalPedido());

                // Limpa o texto do SearchView
                searchView.setQuery("", false);

                // Aciona o foco novamente
                searchView.requestFocus();

                // Fecha o AlertDialog
                dialog.dismiss();

                // Sobe o teclado para buscar outro produto
                inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

            }
        });

        // Adicionando foco no input de quantidade e subindo o teclado
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        editQuantidade.requestFocus();
                        inputMethodManager.showSoftInput(editQuantidade, InputMethodManager.SHOW_IMPLICIT);
                    }
                }, 200);
            }
        });

        // Adicionando ouvinte ao fechar o AlertDialog clicando no enter do teclado
        editPrecoUnidade.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {

                    // Validando campos
                    String textoPreco = editPrecoUnidade.getText().toString();
                    String textoQuantidade = editQuantidade.getText().toString();
                    if (textoQuantidade.isEmpty() || textoQuantidade.equals("0")){
                        Toast.makeText(DadosPedidoActivity.this, "Informe a quantidade", Toast.LENGTH_SHORT).show();
                        return false;
                    }else if(textoPreco.isEmpty()){
                        Toast.makeText(DadosPedidoActivity.this, "Informe o preço", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    if (carrinhoViewModel.buscaPorId(produto.getCodigo())) {
                        carrinhoViewModel.atualizarItemCarrinho(produto.getCodigo(), textoQuantidade, textoPreco);
                        Toast.makeText(DadosPedidoActivity.this, "Valores do produto alterados", Toast.LENGTH_SHORT).show();
                        toolbar.setSubtitle(carrinhoViewModel.calcularTotalPedido());
                        dialog.dismiss();
                        return false;
                    }

                    // Adicionando produto
                    produto.setPreco(Double.parseDouble(textoPreco));
                    produto.setQuantidade(Integer.parseInt(textoQuantidade));
                    produto.setSelecionado(true);
                    carrinhoViewModel.adicionarItemCarrinho(produto);
                    Toast.makeText(DadosPedidoActivity.this, "'" + produto.getNome() + "' adicionado ao pedido", Toast.LENGTH_SHORT).show();
                    itensPedidoFragment.atualizarAdapter();
                    toolbar.setSubtitle(carrinhoViewModel.calcularTotalPedido());

                    // Limpa o texto do SearchView
                    searchView.setQuery("", false);

                    // Aciona o foco novamente
                    searchView.requestFocus();

                    // Fecha o AlertDialog
                    dialog.dismiss();

                    // Sobe o teclado para buscar outro produto
                    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

                    return true;
                }
                return false;
            }
        });

        dialog.show();
    }

    public void excluirItemCarrinho(String codigo, AlertDialog dialogProduto){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmação de Exclusão");
        builder.setMessage("Tem certeza que deseja excluir este item do carrinho?");
        builder.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String retorno = carrinhoViewModel.removeItem(codigo);

                if (!retorno.equals("")) {
                    Toast.makeText(DadosPedidoActivity.this, retorno, Toast.LENGTH_SHORT).show();
                    itensPedidoFragment.atualizarAdapter();
                    toolbar.setSubtitle(carrinhoViewModel.calcularTotalPedido());
                } else {
                    Toast.makeText(DadosPedidoActivity.this, "Ocorreu um erro ao excluir o produto", Toast.LENGTH_SHORT).show();
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

    public void atualizarSubtituloToolbar(String subTitulo){
        toolbar.setSubtitle(subTitulo);
    }

    public Cliente getCliente(){
        return this.cliente;
    }
}