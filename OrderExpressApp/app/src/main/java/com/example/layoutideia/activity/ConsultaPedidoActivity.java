package com.example.layoutideia.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.layoutideia.R;
import com.example.layoutideia.adapter.AdapterItensCarrinho;
import com.example.layoutideia.adapter.AdapterProdutos;
import com.example.layoutideia.helper.GeraCodigo;
import com.example.layoutideia.helper.VendedorFirebase;
import com.example.layoutideia.model.Cliente;
import com.example.layoutideia.model.Pedido;
import com.example.layoutideia.model.Produto;
import com.example.layoutideia.model.Vendedor;
import com.example.layoutideia.viewmodel.CarrinhoViewModel;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ConsultaPedidoActivity extends AppCompatActivity {

    private Toolbar toolbar;


    // Informações passadas
    private Cliente cliente;
    private String formaPagamento;
    private String dataPedido;
    private String operacaoVenda;
    private String descricao;
    private List<Produto> listaProdutos = new ArrayList<>();
    private int telaAnterior = -1;

    private Vendedor vendedor;
    private Double total;
    private CarrinhoViewModel carrinhoViewModel;


    // Componentes do layout
    private TextView textNome;
    private TextView textTotal;
    private TextView textFormaPagamento;
    private TextView textDataPedido;
    private TextView textDescricao;
    private TextView textId;
    private TextView textOperacaoVenda;
    private RecyclerView recyclerItensPedidoFinal;
    private Button buttonConfirmar;
    private Button buttonExcluir;
    private AdapterItensCarrinho adapter;

    private DatabaseReference datatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido_final);

        // Config. Toolbar
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Dados do Pedido");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Pegando referência do carrinho
        carrinhoViewModel = new ViewModelProvider(this).get(CarrinhoViewModel.class);

        // Pegando lista de produtos
        listaProdutos = carrinhoViewModel.getItensCarrinho();

        // Pegando preço total do carrinho
        total = carrinhoViewModel.getValorTotalPedido();

        // Pegando Vendedor atual
        // vendedor = VendedorFirebase.getVendedorAtual();

        // Pegando o botão aqui para ele poder ser escondido no if do clickFragment
        buttonConfirmar = findViewById(R.id.buttonConfirmar);
        buttonExcluir = findViewById(R.id.buttonExcluir);

        // Pegando valores passados
        if(getIntent().getExtras() != null){

            boolean clickFragment = (boolean) getIntent().getSerializableExtra("clickFragment");
            telaAnterior = (int) getIntent().getSerializableExtra("telaAnterior");

            if(clickFragment){

                cliente = (Cliente) getIntent().getSerializableExtra("Cliente");
                String pedidoId = (String) getIntent().getSerializableExtra("PedidoId");
                vendedor = VendedorFirebase.getDadosVendedorLogado();
                formaPagamento = (String) getIntent().getExtras().getSerializable("FormaPagamento");
                operacaoVenda = (String) getIntent().getExtras().getSerializable("OperacaoVenda");
                listaProdutos = CarrinhoViewModel.getItensCarrinhoConsulta();
                total = (Double) getIntent().getExtras().getSerializable("Total");
                descricao = (String) getIntent().getExtras().getSerializable("Descricao");
                dataPedido = (String) getIntent().getExtras().getSerializable("Data");
                buttonConfirmar.setVisibility(View.GONE);
                buttonExcluir.setVisibility(View.VISIBLE);

                buttonExcluir.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Pedido pedido = new Pedido();
                        pedido.setId(pedidoId);
                        pedido.setVendedor(vendedor);
                        pedido.setCliente(cliente);

                        new AlertDialog.Builder(ConsultaPedidoActivity.this)
                                .setTitle("Excluir Pedido")
                                .setMessage("Tem certeza que deseja excluir o pedido? Os dados serão excluidos permanentemente")
                                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        pedido.excluirPedido(new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                                Toast.makeText(ConsultaPedidoActivity.this, "Pedido excluido", Toast.LENGTH_SHORT).show();
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
            
        }else{
            cliente = carrinhoViewModel.getCliente();
            formaPagamento = carrinhoViewModel.getFormaPagamento();
            operacaoVenda = carrinhoViewModel.getOperacaoVenda();
            vendedor = VendedorFirebase.getDadosVendedorLogado();
            dataPedido = getDataAtual();
            total = carrinhoViewModel.getValorTotalPedido();
            descricao = carrinhoViewModel.getDescricao();
        }


        // O ID do pedido vai ficar na toolbar

        // Config. Componentes
        textNome = findViewById(R.id.textNome);
        textTotal = findViewById(R.id.textTotal);
        textFormaPagamento = findViewById(R.id.textFormaPagamento);
        textDataPedido = findViewById(R.id.textDataPedido);
        textOperacaoVenda = findViewById(R.id.textOperacaoVenda);
        recyclerItensPedidoFinal = findViewById(R.id.recyclerItensPedidoFinal);
        textDescricao = findViewById(R.id.textDescricao);

        // Config. Adapter
        adapter = new AdapterItensCarrinho(listaProdutos, this);

        // Config. Recycler View
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerItensPedidoFinal.setLayoutManager(layoutManager);
        recyclerItensPedidoFinal.setHasFixedSize(true);
        recyclerItensPedidoFinal.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));
        recyclerItensPedidoFinal.setAdapter(adapter);

        // Passando dados para os TextView
        textNome.setText(cliente.getNomeCliente());
        String totalFormatado = String.format("Total: R$%.2f", total);
        textTotal.setText(totalFormatado);
        textFormaPagamento.setText(formaPagamento);
        textOperacaoVenda.setText(operacaoVenda);
        textDataPedido.setText(getDataAtual());
        textDescricao.setText(descricao);

        buttonConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pedido pedido = new Pedido();
                pedido.setCliente(cliente);
                pedido.setVendedor(vendedor);
                pedido.setItens(listaProdutos);
                pedido.setDataPedido(dataPedido);
                pedido.setId(GeraCodigo.gerarCodigoUnico());
                pedido.setDescricao(descricao);
                pedido.setTotal(total);
                pedido.setFormaPagamento(formaPagamento);
                pedido.setOperacaoVenda(operacaoVenda);

                pedido.salvarPedido(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        if(error != null){
                            Toast.makeText(ConsultaPedidoActivity.this, "Ocorreu um erro ao salvar pedido", Toast.LENGTH_SHORT).show();
                            Log.d("Erro Pedido", error.getMessage());
                        }else{
                            Toast.makeText(ConsultaPedidoActivity.this, "Pedido salvo com sucesso", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(ConsultaPedidoActivity.this, MainActivity.class));
                            finish();
                        }
                    }
                });
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }


    private String getDataAtual(){
        Date dataAtual = new Date();

        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        String dataFormatada = format.format(dataAtual);

        return dataFormatada;
    }
}