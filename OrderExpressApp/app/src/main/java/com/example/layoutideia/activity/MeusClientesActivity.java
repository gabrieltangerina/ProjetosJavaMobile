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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.example.layoutideia.config.ConfiguracaoFirebase;
import com.example.layoutideia.helper.RecyclerViewClick;
import com.example.layoutideia.helper.VendedorFirebase;
import com.example.layoutideia.model.Cliente;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

public class MeusClientesActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ProgressBar progressBarMeusClientes;
    private TextView textAvisoErro;
    private ImageView imageAvisoErro;

    private AdapterClientes adapterClientes;
    private RecyclerView recyclerClientes;
    private List<Cliente> clientes = new ArrayList<>();

    private DatabaseReference database;
    private DatabaseReference clientesRef;
    private ChildEventListener childEventListenerClientes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_clientes);

        progressBarMeusClientes = findViewById(R.id.progressBarMeusClientes);
        progressBarMeusClientes.setVisibility(View.VISIBLE);

        // Config. Toolbar
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Meus Clientes");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Config. Listagem de Clientes
        database = ConfiguracaoFirebase.getFirebaseDatabase();
        clientesRef = database
                .child("clientes")
                .child(VendedorFirebase.getIdVendedorLogado());

        recyclerClientes = findViewById(R.id.recyclerClientes);

        // Config. Adapter
        adapterClientes = new AdapterClientes(clientes, MeusClientesActivity.this);

        // Config. RecyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MeusClientesActivity.this);
        recyclerClientes.setLayoutManager(layoutManager);
        recyclerClientes.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));
        recyclerClientes.setHasFixedSize(true);
        recyclerClientes.setAdapter(adapterClientes);
        configurarClickRecyclerView();

        textAvisoErro = findViewById(R.id.textAvisoErro);
        imageAvisoErro = findViewById(R.id.imageAvisoErro);

        // Definindo um timer para que se não recuperar os clientes em 5 segundos aparecer uma mensagem
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if(progressBarMeusClientes.getVisibility() == View.VISIBLE){
                    textAvisoErro.setVisibility(View.VISIBLE);
                    imageAvisoErro.setVisibility(View.VISIBLE);
                    progressBarMeusClientes.setVisibility(View.GONE);
                }
            }
        };
        handler.postDelayed(runnable, 3500);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void configurarClickRecyclerView(){
        recyclerClientes.addOnItemTouchListener(new RecyclerViewClick(
                MeusClientesActivity.this,
                recyclerClientes,
                new RecyclerViewClick.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        List<Cliente> listaClientesAtualizado = adapterClientes.getListaClientes();
                        Cliente clienteSelecionado = listaClientesAtualizado.get(position);

                        Intent intent = new Intent(MeusClientesActivity.this, CadastrarClienteActivity.class);
                        intent.putExtra("codigoCliente", clienteSelecionado.getCodigo());
                        intent.putExtra("nomeCliente", clienteSelecionado.getNomeCliente());
                        intent.putExtra("nomeFantasia", clienteSelecionado.getNomeFantasia());
                        intent.putExtra("endereco", clienteSelecionado.getEndereco());
                        intent.putExtra("cnpj", clienteSelecionado.getCpnj());
                        intent.putExtra("cidade", clienteSelecionado.getCidade());
                        intent.putExtra("flagCadastroAlteracao", "Alteracao");
                        intent.putExtra("activity", "MeusClientes");
                        startActivity(intent);

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        List<Cliente> listaClientesAtualizado = adapterClientes.getListaClientes();
                        Cliente clienteSelecionado = listaClientesAtualizado.get(position);

                        new AlertDialog.Builder(MeusClientesActivity.this)
                                .setTitle("Excluir Cliente")
                                .setMessage("Tem certeza que deseja excluir o cliente " + clienteSelecionado.getNomeCliente() + "?" +
                                        " Os dados serão excluidos permanentemente")
                                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        clienteSelecionado.excluirCliente(new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                                listaClientesAtualizado.remove(clienteSelecionado);
                                                Toast.makeText(MeusClientesActivity.this, "Cliente excluido", Toast.LENGTH_SHORT).show();
                                                adapterClientes.notifyDataSetChanged();
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
                }
        ));
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
                adapterClientes.notifyDataSetChanged();
                progressBarMeusClientes.setVisibility(View.GONE);
                textAvisoErro.setVisibility(View.GONE);
                imageAvisoErro.setVisibility(View.GONE);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Cliente clienteRemovido = snapshot.getValue(Cliente.class);
                clientes.removeIf(cliente -> cliente.getCodigo().equals(clienteRemovido.getCodigo()));
                adapterClientes.notifyDataSetChanged();

                if(clientes.isEmpty()){
                    progressBarMeusClientes.setVisibility(View.VISIBLE);

                    // Definindo um timer para que se não recuperar os clientes em 5 segundos aparecer uma mensagem
                    Handler handler = new Handler();
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            if(progressBarMeusClientes.getVisibility() == View.VISIBLE){
                                textAvisoErro = findViewById(R.id.textAvisoErro);
                                imageAvisoErro = findViewById(R.id.imageAvisoErro);


                                textAvisoErro.setVisibility(View.VISIBLE);
                                imageAvisoErro.setVisibility(View.VISIBLE);
                                progressBarMeusClientes.setVisibility(View.GONE);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.toolbar_meus_clientes, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menu_cliente_adicionar){
            Intent intent = new Intent(MeusClientesActivity.this, CadastrarClienteActivity.class);
            intent.putExtra("activity", "MeusClientes");
            intent.putExtra("flagCadastroAlteracao", "Criacao");
            startActivity(intent);

        }else if(item.getItemId() == R.id.menu_pesquisa_cliente){
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
        }

        return super.onOptionsItemSelected(item);
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
        adapterClientes = new AdapterClientes(listaClientesBusca, getApplication());
        recyclerClientes.setAdapter(adapterClientes);
        adapterClientes.notifyDataSetChanged();
    }

    public void restaurarClientes(){
        adapterClientes = new AdapterClientes(clientes, getApplication());
        recyclerClientes.setAdapter(adapterClientes);
        adapterClientes.notifyDataSetChanged();
    }
}