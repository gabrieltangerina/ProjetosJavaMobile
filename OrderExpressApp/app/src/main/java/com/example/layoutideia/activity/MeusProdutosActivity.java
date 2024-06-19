package com.example.layoutideia.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.layoutideia.R;

public class MeusProdutosActivity extends AppCompatActivity {

    private RecyclerView recyclerMeusProdutos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_produtos);

        // Config. Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Meus Produtos");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Config, RecyclerView
        recyclerMeusProdutos = findViewById(R.id.recyclerProdutos);
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
//            SearchView searchView = (SearchView) item.getActionView();
//            searchView.setQueryHint("Pesquisar");
//
//            // Tratando mudança de letras no searchView
//            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//                @Override
//                public boolean onQueryTextSubmit(String query) {
//                    return false;
//                }
//
//                @Override
//                public boolean onQueryTextChange(String newText) {
//                    if(newText != null && !newText.isEmpty()){
//                        pesquisarCliente(newText.toLowerCase());
//                    }
//
//                    if(newText.isEmpty()){
//                        restaurarClientes();
//                    }
//
//                    return false;
//                }
//            });
        }else if(item.getItemId() == R.id.menu_adicionar_produto){
            startActivity(new Intent(MeusProdutosActivity.this, CadastrarProdutoActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }
}