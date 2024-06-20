package com.example.layoutideia.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.example.layoutideia.R;
import com.example.layoutideia.adapter.AdapterMeusProdutos;
import com.example.layoutideia.adapter.AdapterProdutos;
import com.example.layoutideia.config.ConfiguracaoFirebase;
import com.example.layoutideia.helper.RecyclerViewClick;
import com.example.layoutideia.model.Pedido;
import com.example.layoutideia.model.Produto;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

public class MeusProdutosActivity extends AppCompatActivity {

    private RecyclerView recyclerMeusProdutos;
    private List<Produto> listaProdutos = new ArrayList<>();
    private AdapterMeusProdutos adapterMeusProdutos;
    private ProgressBar progressBarMeusProdutos;

    private DatabaseReference database;
    private DatabaseReference produtosRef;
    private ChildEventListener childEventListenerProdutos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_produtos);

        progressBarMeusProdutos = findViewById(R.id.progressBarMeusProdutos);
        progressBarMeusProdutos.setVisibility(View.VISIBLE);

        // Config. Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Meus Produtos");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        database = ConfiguracaoFirebase.getFirebaseDatabase();
        produtosRef = database
                .child("produtos");

        recuperarProdutos();

        recyclerMeusProdutos = findViewById(R.id.recyclerProdutos);

        // Config. Adapter
        adapterMeusProdutos = new AdapterMeusProdutos(listaProdutos);

        // Config, RecyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerMeusProdutos.setLayoutManager(layoutManager);
        recyclerMeusProdutos.setHasFixedSize(true);
        recyclerMeusProdutos.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));
        recyclerMeusProdutos.setAdapter(adapterMeusProdutos);
        configurandoClickRecyclerView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        restaurarClientes();
    }

    @Override
    protected void onStop() {
        super.onStop();
        produtosRef.removeEventListener(childEventListenerProdutos);
    }

    private void recuperarProdutos(){
        listaProdutos.clear();

        childEventListenerProdutos = produtosRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Produto produto = snapshot.getValue(Produto.class);
                listaProdutos.add(produto);
                adapterMeusProdutos.notifyDataSetChanged();
                progressBarMeusProdutos.setVisibility(View.GONE);
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

    public void configurandoClickRecyclerView(){
        recyclerMeusProdutos.addOnItemTouchListener(new RecyclerViewClick(
                getApplicationContext(),
                recyclerMeusProdutos,
                new RecyclerViewClick.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        List<Produto> listaProdutosAtualizado = adapterMeusProdutos.getListaProdutos();
                        Produto produtoSelecionado = listaProdutosAtualizado.get(position);

                        Intent intent = new Intent(MeusProdutosActivity.this, CadastrarProdutoActivity.class);
                        intent.putExtra("idProduto", produtoSelecionado.getCodigo());
                        intent.putExtra("nomeProduto", produtoSelecionado.getNome());
                        intent.putExtra("precoProduto", produtoSelecionado.getPreco());
                        intent.putExtra("qtndProduto", produtoSelecionado.getQuantidade());
                        startActivity(intent);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.toolbar_meus_produtos, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.menu_pesquisa_produto){
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
                        pesquisarProduto(newText.toLowerCase());
                    }

                    if(newText.isEmpty()){
                        restaurarClientes();
                    }

                    return false;
                }
            });
        }else if(item.getItemId() == R.id.menu_adicionar_produto){
            startActivity(new Intent(MeusProdutosActivity.this, CadastrarProdutoActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    private void pesquisarProduto(String texto){
        List<Produto> listaProdutosBusca = new ArrayList<>();

        for(Produto produto: listaProdutos){
            String nomeProduto = produto.getNome().toLowerCase();

            if (nomeProduto.contains(texto)){
                listaProdutosBusca.add(produto);
            }

        }

        adapterMeusProdutos = new AdapterMeusProdutos(listaProdutosBusca);
        recyclerMeusProdutos.setAdapter(adapterMeusProdutos);
        adapterMeusProdutos.notifyDataSetChanged();
    }

    private void restaurarClientes(){
        adapterMeusProdutos = new AdapterMeusProdutos(listaProdutos);
        recyclerMeusProdutos.setAdapter(adapterMeusProdutos);
        adapterMeusProdutos.notifyDataSetChanged();
    }
}