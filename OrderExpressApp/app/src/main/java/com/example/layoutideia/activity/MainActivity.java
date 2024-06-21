package com.example.layoutideia.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.layoutideia.R;
import com.example.layoutideia.config.ConfiguracaoFirebase;
import com.example.layoutideia.helper.VendedorFirebase;
import com.example.layoutideia.model.Cliente;
import com.example.layoutideia.model.Vendedor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private Button btnFazerPedido;
    private Button btnMeusClientes;
    private Button btnMeusPedidos;
    private Button btnMeusProdutos;
    private Button btnVendasMes;

    private TextView textApresentacao;
    private FirebaseAuth autenticacao;
    private FirebaseUser vendedorLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();

        // Config. Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("OrderExpress");
        setSupportActionBar(toolbar);

        textApresentacao = findViewById(R.id.textApresentacao);

        if(getIntent().getExtras() != null){
            String nomeVendedor = (String) getIntent().getSerializableExtra("NomeVendedor");
            textApresentacao.setText("Bem vindo, " + nomeVendedor);
        }else{
            vendedorLogado = VendedorFirebase.getVendedorAtual();
            textApresentacao.setText("Bem vindo, " + vendedorLogado.getDisplayName());
        }


        btnFazerPedido = findViewById(R.id.btnFazerPedido);
        configurandoClickFazerPedido();

        btnMeusClientes = findViewById(R.id.btnMeusClientes);
        configurandoClickMeusClientes();

        btnMeusPedidos = findViewById(R.id.btnMeusPedidos);
        configurandoClickMeusPedidos();

        btnMeusProdutos = findViewById(R.id.btnMeusProdutos);
        configurandoClickMeusProdutos();

        btnVendasMes = findViewById(R.id.btnVendasMes);
        configurandoClickVendasMes();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.toolbar_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_logout_main) {
            deslogarUsuario();
            return true; // Consome o evento
        }
        return super.onOptionsItemSelected(item);
    }

    private void deslogarUsuario() {
        try {
            autenticacao.signOut();
            // Redireciona para a LoginActivity
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Certifique-se de que a pilha de atividades está limpa
            startActivity(intent);
            finish(); // Finaliza a MainActivity
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Erro ao deslogar usuário: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void configurandoClickVendasMes(){
        btnVendasMes.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), MinhasVendasActivity.class);
            startActivity(intent);
        });
    }

    private void configurandoClickMeusPedidos(){
        btnMeusPedidos.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), MeusPedidosActivity.class);
            startActivity(intent);
        });
    }

    private void configurandoClickFazerPedido() {
        btnFazerPedido.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), SelecionaClienteActivity.class);
            startActivity(intent);
        });
    }

    private void configurandoClickMeusClientes() {
        btnMeusClientes.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, MeusClientesActivity.class)));
    }

    private void configurandoClickMeusProdutos(){
        btnMeusProdutos.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, MeusProdutosActivity.class)));
    }
}
