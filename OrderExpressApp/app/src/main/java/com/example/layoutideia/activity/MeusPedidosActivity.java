package com.example.layoutideia.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.layoutideia.R;
import com.example.layoutideia.adapter.AdapterClientes;
import com.example.layoutideia.adapter.AdapterPedido;
import com.example.layoutideia.config.ConfiguracaoFirebase;
import com.example.layoutideia.helper.RecyclerViewClick;
import com.example.layoutideia.helper.VendedorFirebase;
import com.example.layoutideia.model.Cliente;
import com.example.layoutideia.model.Pedido;
import com.example.layoutideia.viewmodel.CarrinhoViewModel;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

public class MeusPedidosActivity extends AppCompatActivity {

    private RecyclerView recyclerPedidos;
    private ProgressBar progressBarMeusPedidos;
    private TextView textAvisoErro;
    private ImageView imageAvisoErro;
    private AdapterPedido adapterPedido;
    private List<Pedido> listaPedidos = new ArrayList<>();

    private DatabaseReference database;
    private DatabaseReference pedidosRef;
    private ChildEventListener childEventListenerPedidos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_pedidos);

        progressBarMeusPedidos = findViewById(R.id.progressBarMeusPedidos);
        progressBarMeusPedidos.setVisibility(View.VISIBLE);

        // Config. Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Meus Pedidos");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Pegando pedidos do Firebase
        database = ConfiguracaoFirebase.getFirebaseDatabase();
        pedidosRef = database
                .child("todosPedidosVendedor")
                .child(VendedorFirebase.getIdVendedorLogado());

        recuperarPedidos();

        recyclerPedidos = findViewById(R.id.recyclerPedidos);

        // Config. Adapter
        adapterPedido = new AdapterPedido(listaPedidos);

        // Config. RecyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerPedidos.setLayoutManager(layoutManager);
        recyclerPedidos.setHasFixedSize(true);
        recyclerPedidos.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));
        recyclerPedidos.setAdapter(adapterPedido);
        configurandoClickRecyclerView();

        // Definindo um timer para que se não recuperar os clientes em 5 segundos aparecer uma mensagem
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if(progressBarMeusPedidos.getVisibility() == View.VISIBLE){
                    textAvisoErro = findViewById(R.id.textAvisoErro);
                    imageAvisoErro = findViewById(R.id.imageAvisoErro);

                    textAvisoErro.setVisibility(View.VISIBLE);
                    imageAvisoErro.setVisibility(View.VISIBLE);
                    progressBarMeusPedidos.setVisibility(View.GONE);
                }
            }
        };
        handler.postDelayed(runnable, 3500);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.toolbar_meus_pedidos, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.menu_pesquisa_pedido){
            SearchView searchView = (SearchView) item.getActionView();
            searchView.setQueryHint("Pesquisar");

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    if(newText != null && !newText.isEmpty()){
                        pesquisarPedido(newText.toLowerCase());
                    }

                    if(newText.isEmpty()){
                        restaurarPedidos();
                    }

                    return false;
                }
            });
        }

        return super.onOptionsItemSelected(item);
    }

    private void recuperarPedidos(){
        listaPedidos.clear();

        childEventListenerPedidos = pedidosRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Pedido pedido = snapshot.getValue(Pedido.class);
                listaPedidos.add(pedido);
                adapterPedido.notifyDataSetChanged();
                progressBarMeusPedidos.setVisibility(View.GONE);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                // Removendo o pedido do Firebase
                Pedido pedidoRemovido = snapshot.getValue(Pedido.class);
                listaPedidos.removeIf(pedido -> pedido.getId().equals(pedidoRemovido.getId()));
                adapterPedido.notifyDataSetChanged();

                if(listaPedidos.isEmpty()){
                    progressBarMeusPedidos.setVisibility(View.VISIBLE);

                    // Definindo um timer para que se não recuperar os clientes em 5 segundos aparecer uma mensagem
                    Handler handler = new Handler();
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            if(progressBarMeusPedidos.getVisibility() == View.VISIBLE){
                                textAvisoErro = findViewById(R.id.textAvisoErro);
                                imageAvisoErro = findViewById(R.id.imageAvisoErro);

                                textAvisoErro.setVisibility(View.VISIBLE);
                                imageAvisoErro.setVisibility(View.VISIBLE);
                                progressBarMeusPedidos.setVisibility(View.GONE);
                            }
                        }
                    };
                    handler.postDelayed(runnable, 3500);
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void configurandoClickRecyclerView(){
        recyclerPedidos.addOnItemTouchListener(new RecyclerViewClick(getApplicationContext(), recyclerPedidos, new RecyclerViewClick.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                List<Pedido> listaPedidosAtualizada = adapterPedido.getListaPedido();
                Pedido pedidoSelecionado = listaPedidosAtualizada.get(position);

                Intent intent = new Intent(MeusPedidosActivity.this, ConsultaPedidoActivity.class);
                // intent.putExtra("itens", (CharSequence) pedidoSelecionado.getItens());
                intent.putExtra("clickFragment", true);
                intent.putExtra("telaAnterior", 1);
                intent.putExtra("PedidoId", pedidoSelecionado.getId());
                intent.putExtra("Cliente", pedidoSelecionado.getCliente());
                intent.putExtra("FormaPagamento", pedidoSelecionado.getFormaPagamento());
                intent.putExtra("OperacaoVenda", pedidoSelecionado.getOperacaoVenda());
                intent.putExtra("Total", pedidoSelecionado.getTotal());
                intent.putExtra("Descricao", pedidoSelecionado.getDescricao());
                intent.putExtra("Data", pedidoSelecionado.getDataPedido());

                // CarrinhoViewModel.limpaItensCarrinhoConsulta();
                CarrinhoViewModel.setItensCarrinhoConsulta(pedidoSelecionado.getItens());

                startActivity(intent);
            }

            @Override
            public void onLongItemClick(View view, int position) {
                List<Pedido> listaPedidosAtualizados = adapterPedido.getListaPedido();
                Pedido pedidoSelecionado = listaPedidosAtualizados.get(position);

                new AlertDialog.Builder(MeusPedidosActivity.this)
                        .setTitle("Excluir Pedido")
                        .setMessage("Tem certeza que deseja excluir o pedido? Os dados serão excluidos permanentemente")
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                pedidoSelecionado.excluirPedido(new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                        listaPedidosAtualizados.remove(position);
                                        Toast.makeText(MeusPedidosActivity.this, "Pedido excluido", Toast.LENGTH_SHORT).show();
                                        adapterPedido.notifyDataSetChanged();
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

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }));
    }

    public void pesquisarPedido(String texto){
        List<Pedido> listaPedidosBusca = new ArrayList<>();

        for(Pedido pedido: listaPedidos){
            String nomeCliente = pedido.getCliente().getNomeCliente().toLowerCase();

            if(nomeCliente.contains(texto)){
                listaPedidosBusca.add(pedido);
            }
        }

        // configurarClickRecycler(listaClientesBusca);
        adapterPedido = new AdapterPedido(listaPedidosBusca);
        recyclerPedidos.setAdapter(adapterPedido);
        adapterPedido.notifyDataSetChanged();
    }

    public void restaurarPedidos(){
        adapterPedido = new AdapterPedido(listaPedidos);
        recyclerPedidos.setAdapter(adapterPedido);
        adapterPedido.notifyDataSetChanged();
    }
}