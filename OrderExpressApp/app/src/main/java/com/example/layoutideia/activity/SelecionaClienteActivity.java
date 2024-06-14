package com.example.layoutideia.activity;

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

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;

import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.layoutideia.R;
import com.example.layoutideia.adapter.AdapterClientes;
import com.example.layoutideia.config.ConfiguracaoFirebase;
import com.example.layoutideia.helper.RecyclerViewClick;
import com.example.layoutideia.helper.VendedorFirebase;
import com.example.layoutideia.model.Cliente;
import com.example.layoutideia.viewmodel.CarrinhoViewModel;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

public class SelecionaClienteActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TextView textAvisoErro;
    private ImageView imageAvisoErro;
    private List<Cliente> clientes = new ArrayList<>();
    private AdapterClientes adapter;
    private CarrinhoViewModel carrinhoViewModel;

    private DatabaseReference database;
    private DatabaseReference clientesRef;
    private ChildEventListener childEventListenerClientes;
    private ProgressBar progressBarSelecionarCliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fazer_pedido_seleciona_cliente);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Config. Barra de Loading
        progressBarSelecionarCliente = findViewById(R.id.progressBarSelecionarCliente);
        progressBarSelecionarCliente.setVisibility(View.VISIBLE);

        // Config. Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Selecione o cliente");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Config. Listagem de Clientes
        database = ConfiguracaoFirebase.getFirebaseDatabase();
        clientesRef = database
                .child("clientes")
                .child(VendedorFirebase.getIdVendedorLogado());

        // Config. RecyclerView
        recyclerView = findViewById(R.id.recyclerListaClientes);
        configurarRecyclerView();

        // Config. Click RecyclerView
        configurarClickRecycler(clientes);

        CarrinhoViewModel.limparDadosViewModel();

        // Definindo um timer para que se não recuperar os clientes em 5 segundos aparecer uma mensagem
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if(progressBarSelecionarCliente.getVisibility() == View.VISIBLE){
                    textAvisoErro = findViewById(R.id.textAvisoErro);
                    imageAvisoErro = findViewById(R.id.imageAvisoErro);


                    textAvisoErro.setVisibility(View.VISIBLE);
                    imageAvisoErro.setVisibility(View.VISIBLE);
                    progressBarSelecionarCliente.setVisibility(View.GONE);
                }
            }
        };
        handler.postDelayed(runnable, 3500);
    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarClientes();
    }

    @Override
    protected void onStop() {
        super.onStop();
        clientesRef.removeEventListener(childEventListenerClientes);
    }

    public void recuperarClientes(){
        clientes.clear();

        childEventListenerClientes = clientesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Cliente cliente = snapshot.getValue(Cliente.class);
                clientes.add(cliente);
                adapter.notifyDataSetChanged();
                progressBarSelecionarCliente.setVisibility(View.GONE);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Cliente clienteRemovido = snapshot.getValue(Cliente.class);
                clientes.removeIf(cliente -> cliente.getCodigo().equals(clienteRemovido.getCodigo()));
                adapter.notifyDataSetChanged();

                if(clientes.isEmpty()) {
                    progressBarSelecionarCliente.setVisibility(View.VISIBLE);

                    // Definindo um timer para que se não recuperar os clientes em 5 segundos aparecer uma mensagem
                    Handler handler = new Handler();
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            if (progressBarSelecionarCliente.getVisibility() == View.VISIBLE) {
                                textAvisoErro = findViewById(R.id.textAvisoErro);
                                imageAvisoErro = findViewById(R.id.imageAvisoErro);


                                textAvisoErro.setVisibility(View.VISIBLE);
                                imageAvisoErro.setVisibility(View.VISIBLE);
                                progressBarSelecionarCliente.setVisibility(View.GONE);
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

    @Override
    protected void onResume() {
        super.onResume();

        // Limpando carrinho de pedidos
        carrinhoViewModel = new ViewModelProvider(this).get(CarrinhoViewModel.class);
        carrinhoViewModel.limparItensCarrinho();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.toolbar_selecionar_cliente, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menu_pesquisa_cliente){
            SearchView searchView = (SearchView) item.getActionView();
            searchView.setQueryHint("Pesquisar");

            // Tratando mudança de letras no searchView
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    if(newText != null && !newText.isEmpty()){
                        pesquisarCliente(newText.toLowerCase());
                    }

                    if(newText.isEmpty()){
                        restaurarClientes();
                    }

                    return false;
                }
            });
        }else if(item.getItemId() == R.id.menu_cliente_adicionar){
            Intent intent = new Intent(SelecionaClienteActivity.this, CadastrarClienteActivity.class);
            intent.putExtra("activity", "SelecionarCliente");
            intent.putExtra("flagCadastroAlteracao", "Criacao");
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public void restaurarClientes(){
        adapter = new AdapterClientes(clientes, getApplication());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void pesquisarCliente(String texto){
        List<Cliente> listaClientesBusca = new ArrayList<>();

        for(Cliente cliente: clientes){
            String nomeCliente = cliente.getNomeCliente().toLowerCase();
            String nomeFantasia = cliente.getNomeFantasia().toLowerCase();

            if(nomeCliente.contains(texto) || nomeFantasia.contains(texto)){
                listaClientesBusca.add(cliente);
            }
        }

        // configurarClickRecycler(listaClientesBusca);
        adapter = new AdapterClientes(listaClientesBusca, getApplication());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void configurarClickRecycler(List<Cliente> lista){
        recyclerView.addOnItemTouchListener(new RecyclerViewClick(getApplicationContext(), recyclerView, new RecyclerViewClick.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                List<Cliente> listaClienteAtualizado = adapter.getListaClientes();
                Cliente cliente = listaClienteAtualizado.get(position);

                Intent intent = new Intent(SelecionaClienteActivity.this, DadosPedidoActivity.class);
                intent.putExtra("Cliente", cliente);
                carrinhoViewModel.setCliente(cliente);
                intent.putExtra("flagCadastroAlteracao", "Criacao");
                startActivity(intent);
            }

            @Override
            public void onLongItemClick(View view, int position) {
            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        }));
    }

    public void configurarRecyclerView(){

        // Configurar Adapter
        adapter = new AdapterClientes(clientes, getApplication());

        // Configurar RecyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));
        recyclerView.setAdapter(adapter);
        configurarClickRecycler(clientes);
    }
}