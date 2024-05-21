package com.example.whatsapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.whatsapp.R;
import com.example.whatsapp.config.ConfiguracaoFirebase;
import com.example.whatsapp.fragment.ContatosFragment;
import com.example.whatsapp.fragment.ConversasFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private FragmentPagerItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();

        // Config. Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("WhatsApp");
        setSupportActionBar(toolbar); // Suporte a versões anteriores do android

        // Config. ViewPagerTab
         adapter = new FragmentPagerItemAdapter(getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add("Conversas", ConversasFragment.class)
                .add("Contatos", ContatosFragment.class)
                .create());

        ViewPager viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);

        SmartTabLayout viewPagerTag = findViewById(R.id.viewPagerTab);
        viewPagerTag.setViewPager(viewPager);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Verifica qual item de menu foi clicado

        if(item.getItemId() == R.id.menu_pesquisa){
            SearchView searchView = (SearchView) item.getActionView();
            searchView.setQueryHint(getString(R.string.menu_pesquisar));

            // Pare pegar todas as conversas ao fechar o searchView, no caso ao foco mudar do searchView
            searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    ConversasFragment fragment = (ConversasFragment) adapter.getPage(0);
                    // Recarrega todas as conversas ao fechar o searchView
                    fragment.recarregarConversas();
                }
            });

            // Lista para filtrar texto do searchView
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    ConversasFragment fragment = (ConversasFragment) adapter.getPage(0);

                    if(newText != null && !newText.isEmpty()){
                        fragment.pesquisarConversas(newText.toLowerCase());
                    }

                    if(newText.isEmpty()){
                        fragment.recarregarConversas();
                    }

                    return false;
                }
            });

        }else if(item.getItemId() == R.id.menu_sair){
            deslogarUsuario();
            finish();
        }else if(item.getItemId() == R.id.menu_configuracoes){
            abrirConfiguracoes();
        }

        return super.onOptionsItemSelected(item);
    }

    public void deslogarUsuario(){
        try{
            autenticacao.signOut();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void abrirConfiguracoes(){
        startActivity(new Intent(MainActivity.this, ConfiguracoesActivity.class));
    }
}