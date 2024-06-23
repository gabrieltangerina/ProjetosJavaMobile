package com.example.layoutideia.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.layoutideia.R;
import com.example.layoutideia.activity.DadosPedidoActivity;
import com.example.layoutideia.adapter.AdapterProdutos;
import com.example.layoutideia.config.ConfiguracaoFirebase;
import com.example.layoutideia.helper.RecyclerViewClick;
import com.example.layoutideia.model.Produto;
import com.example.layoutideia.viewmodel.CarrinhoViewModel;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

public class ItensPedidoFragment extends Fragment {
    private RecyclerView recyclerProdutos;
    private AdapterProdutos adapter;
    private List<Produto> listaProdutos = new ArrayList<>();
    private InputMethodManager inputMethodManager;
    private CarrinhoViewModel carrinhoViewModel;
    private DadosPedidoActivity activity;

    private DatabaseReference database;
    private DatabaseReference produtosRef;
    private ChildEventListener childEventListenerProdutos;
    private ProgressBar progressBarProdutos;
    private TextView textAvisoErro;
    private ImageView imageAvisoErro;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_itens_pedido2, container, false);

        progressBarProdutos = rootView.findViewById(R.id.progressBarProdutos);

        carrinhoViewModel = new ViewModelProvider(this).get(CarrinhoViewModel.class);

        database = ConfiguracaoFirebase.getFirebaseDatabase();
        produtosRef = database
                .child("produtos");

        recuperarProdutos();

        adapter = new AdapterProdutos(listaProdutos, carrinhoViewModel);

        recyclerProdutos = rootView.findViewById(R.id.recyclerProdutos);
        configurandoRecyclerProdutos();

        // Pega o serviço do sistema responsável pera interação com o teclado virtual
        inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        // Pegando a referência da activity DadosPedido
        activity = (DadosPedidoActivity) getActivity();

        // Definindo um timer para que se não recuperar os produtos em alguns segundos aparecer uma mensagem
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if(progressBarProdutos.getVisibility() == View.VISIBLE){
                    textAvisoErro = rootView.findViewById(R.id.textAvisoErro);
                    imageAvisoErro = rootView.findViewById(R.id.imageAvisoErro);

                    textAvisoErro.setVisibility(View.VISIBLE);
                    imageAvisoErro.setVisibility(View.VISIBLE);
                    progressBarProdutos.setVisibility(View.GONE);
                }
            }
        };
        handler.postDelayed(runnable, 3500);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        activity.atualizarSubtituloToolbar(carrinhoViewModel.calcularTotalPedido());
        atualizarAdapter();
    }

    public List<Produto> getListaProdutos(){
        return this.listaProdutos;
    }

    public void pesquisarProduto(String texto){
        List<Produto> listaProdutosBusca = new ArrayList<>();

        for(Produto produto: listaProdutos){
            String nomeProduto = produto.getNome().toLowerCase();
            String codProduto = produto.getCodigo();

            if(nomeProduto.contains(texto) || codProduto.contains(texto)){
                listaProdutosBusca.add(produto);
            }
        }

        //adapter.atualizarLista(listaProdutosBusca);
        adapter = new AdapterProdutos(listaProdutosBusca, carrinhoViewModel);
        recyclerProdutos.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        super.onStart();
        restaurarProdutos();
    }

    @Override
    public void onStop() {
        super.onStop();
        produtosRef.removeEventListener(childEventListenerProdutos);
    }

    private  void restaurarProdutos(){
        adapter = new AdapterProdutos(listaProdutos);
        recyclerProdutos.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void recuperarProdutos(){
        progressBarProdutos.setVisibility(View.VISIBLE);
        listaProdutos.clear();

        childEventListenerProdutos = produtosRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Produto produto = snapshot.getValue(Produto.class);
                listaProdutos.add(produto);
                adapter.notifyDataSetChanged();
                progressBarProdutos.setVisibility(View.GONE);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void configurandoRecyclerProdutos(){

        // Config. Adapter
        adapter = new AdapterProdutos(listaProdutos, carrinhoViewModel);

        // Config. RecyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerProdutos.setLayoutManager(layoutManager);
        recyclerProdutos.setHasFixedSize(true);
        recyclerProdutos.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayout.VERTICAL));
        recyclerProdutos.setAdapter(adapter);

        // Config. Click RecyclerView
        recyclerProdutos.addOnItemTouchListener(new RecyclerViewClick(
                getActivity(),
                recyclerProdutos,
                new RecyclerViewClick.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        List<Produto> listaProdutosAtualizada = adapter.getListaProdutos();
                        Produto produtoSelecionado = listaProdutosAtualizada.get(position);
                        abrirMenuDadosProduto(produtoSelecionado);
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

    public void abrirMenuDadosProduto(Produto produto){
        // Inflando o layout personalizado
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.carrinho_alert_dialog, null);

        // Criando o AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
        // Para o cursor começar no final da linha, fincando mais facil de alterar o preço
        // editPrecoUnidade.setSelection(editPrecoUnidade.getText().length());

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
                    Toast.makeText(getActivity(), "Informe a quantidade", Toast.LENGTH_SHORT).show();
                    return;
                }else if(textoPreco.isEmpty()){
                    Toast.makeText(getActivity(), "Informe o preço", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (carrinhoViewModel.buscaPorId(produto.getCodigo())) {
                    carrinhoViewModel.atualizarItemCarrinho(produto.getCodigo(), textoQuantidade, textoPreco);
                    Toast.makeText(getActivity(), "Valores do produto alterados", Toast.LENGTH_SHORT).show();
                    activity.atualizarSubtituloToolbar(carrinhoViewModel.calcularTotalPedido());
                    adapter.notifyDataSetChanged();
                    dialog.dismiss();
                    return;
                }

                // Adicionando produto
                produto.setPreco(Double.parseDouble(textoPreco));
                produto.setQuantidade(Integer.parseInt(textoQuantidade));
                carrinhoViewModel.adicionarItemCarrinho(produto);
                Toast.makeText(getActivity(), "'" + produto.getNome() + "' adicionado ao pedido", Toast.LENGTH_SHORT).show();
                activity.atualizarSubtituloToolbar(carrinhoViewModel.calcularTotalPedido());

                adapter.notifyDataSetChanged();

                // Fecha o AlertDialog
                dialog.dismiss();

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
                        Toast.makeText(getActivity(), "Informe a quantidade", Toast.LENGTH_SHORT).show();
                        return false;
                    }else if(textoPreco.isEmpty()){
                        Toast.makeText(getActivity(), "Informe o preço", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    if (carrinhoViewModel.buscaPorId(produto.getCodigo())) {
                        carrinhoViewModel.atualizarItemCarrinho(produto.getCodigo(), textoQuantidade, textoPreco);
                        Toast.makeText(getActivity(), "Valores do produto alterados", Toast.LENGTH_SHORT).show();
                        activity.atualizarSubtituloToolbar(carrinhoViewModel.calcularTotalPedido());
                        dialog.dismiss();
                        return false;
                    }

                    // Adicionando preco e quantidade personalizada
                    produto.setPreco(Double.parseDouble(textoPreco));
                    produto.setQuantidade(Integer.parseInt(textoQuantidade));
                    // Adicionando produto
                    carrinhoViewModel.adicionarItemCarrinho(produto);
                    Toast.makeText(getActivity(), "'" + produto.getNome() + "' adicionado ao pedido", Toast.LENGTH_SHORT).show();
                    activity.atualizarSubtituloToolbar(carrinhoViewModel.calcularTotalPedido());

                    adapter.notifyDataSetChanged();

                    dialog.dismiss();

                    return true;
                }
                return false;
            }
        });

        dialog.show();
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

    public void excluirItemCarrinho(String codigo, AlertDialog dialogProduto){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Confirmação de Exclusão");
        builder.setMessage("Tem certeza que deseja excluir este item do carrinho?");
        builder.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String retorno = carrinhoViewModel.removeItem(codigo);

                if (!retorno.equals("")) {
                    Toast.makeText(getActivity(), retorno, Toast.LENGTH_SHORT).show();
                    dialogProduto.dismiss();
                    atualizarAdapter();
                    activity.atualizarSubtituloToolbar(carrinhoViewModel.calcularTotalPedido());
                } else {
                    Toast.makeText(getActivity(), "Ocorreu um erro ao excluir o produto", Toast.LENGTH_SHORT).show();
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

    public void atualizarAdapter(){
        adapter.notifyDataSetChanged();
    }

    public void listaProdutos(){
        Produto produto1 = new Produto("Panela de Pressão", "789", 79.90, 20);
        Produto produto2 = new Produto("Faca Chef", "456", 129.99, 15);
        Produto produto3 = new Produto("Liquidificador", "123", 199.00, 10);
        Produto produto4 = new Produto("Tábua de Corte", "987", 39.99, 30);
        Produto produto5 = new Produto("Conjunto de Facas", "654", 149.90, 25);
        Produto produto6 = new Produto("Garrafa Térmica", "321", 49.99, 40);
        Produto produto7 = new Produto("Cafeteira Elétrica", "567", 179.99, 15);
        Produto produto8 = new Produto("Espátula de Silicone", "234", 15.00, 50);
        Produto produto9 = new Produto("Afiador de Facas", "890", 29.99, 30);
        Produto produto10 = new Produto("Descascador de Legumes", "543", 9.99, 100);
        Produto produto11 = new Produto("Pote de Vidro", "210", 24.90, 80);
        Produto produto12 = new Produto("Escorredor de Macarrão", "876", 34.99, 20);

        listaProdutos.add(produto1);
        listaProdutos.add(produto2);
        listaProdutos.add(produto3);
        listaProdutos.add(produto4);
        listaProdutos.add(produto5);
        listaProdutos.add(produto6);
        listaProdutos.add(produto7);
        listaProdutos.add(produto8);
        listaProdutos.add(produto9);
        listaProdutos.add(produto10);
        listaProdutos.add(produto11);
        listaProdutos.add(produto12);
    }


}