package com.example.organizze.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.example.organizze.adapter.AdapterMovimentacao;
import com.example.organizze.config.ConfiguracaoFirebase;
import com.example.organizze.helper.Base64Custom;
import com.example.organizze.model.Movimentacao;
import com.example.organizze.model.Usuario;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.organizze.databinding.ActivityPrincipalBinding;

import com.example.organizze.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class PrincipalActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityPrincipalBinding binding;
    private MaterialCalendarView calendarView;
    private TextView textoSaudacao, textoSaldo;
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
    private Double despesaTotal = 0.0, receitaTotal = 0.0, resumoUsuario = 0.0;
    private DatabaseReference usuarioRef;
    private ValueEventListener valueEventListenerUsuario;
    private RecyclerView recyclerView;
    private AdapterMovimentacao adapterMovimentacao;
    private List<Movimentacao> movimentacaos = new ArrayList<>();
    private DatabaseReference movimentacaoRef;
    private String mesAnoSeleciona;
    private ValueEventListener valueEventListenerMovimentacoes;
    private Movimentacao movimentacaoExclusao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPrincipalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.setTitle("");
        binding.toolbar.setElevation(0);
        setSupportActionBar(binding.toolbar);

        textoSaldo = findViewById(R.id.textSaldo);
        textoSaudacao = findViewById(R.id.textSaudacao);

        calendarView = findViewById(R.id.calendarView);
        configuraCalendario();

        recyclerView = findViewById(R.id.recyclerView);

        // Conf. Adapter
        adapterMovimentacao = new AdapterMovimentacao(movimentacaos, this);

        //Conf. RecyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapterMovimentacao);

        swipe();
    }

    public void atualizaSaldo(){
        if(movimentacaoExclusao.getTipo().equalsIgnoreCase("r")){
            receitaTotal = receitaTotal - movimentacaoExclusao.getValor();

            String emailUsuario = autenticacao.getCurrentUser().getEmail();
            String idUsuario = Base64Custom.codificarBase64(emailUsuario);
            usuarioRef = firebaseRef.child("usuarios").child(idUsuario);

            usuarioRef.child("receitaTotal").setValue(receitaTotal);
        }else if(movimentacaoExclusao.getTipo().equalsIgnoreCase("d")){
            despesaTotal = despesaTotal - movimentacaoExclusao.getValor();
            usuarioRef.child("despesaTotal").setValue(despesaTotal);
        }
    }

    public void excluirMovimentacao(RecyclerView.ViewHolder viewHolder){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Conf. AlerDialog
        alertDialog.setTitle("Excluir movimentação da conta");
        alertDialog.setMessage("Tem certeza que deseja excluir essa movimentação da sua conta?");
        alertDialog.setCancelable(false);

        alertDialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int posicaoItem = viewHolder.getAdapterPosition();
                movimentacaoExclusao = movimentacaos.get(posicaoItem);

                // Pegando os dados e adicionando referencia para remover o nó que foi acionado no evento de arrastar
                String emailUsuario = autenticacao.getCurrentUser().getEmail();
                String idUsuario = Base64Custom.codificarBase64(emailUsuario);
                movimentacaoRef = firebaseRef.child("movimentacao")
                        .child(idUsuario)
                        .child(mesAnoSeleciona)
                        .child(movimentacaoExclusao.getId());

                movimentacaoRef.removeValue();
                adapterMovimentacao.notifyItemRemoved(posicaoItem);
                atualizaSaldo();
            }
        });

        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                adapterMovimentacao.notifyDataSetChanged();
            }
        });

        AlertDialog alert = alertDialog.create();
        alert.show();
    }

    public void swipe(){
        ItemTouchHelper.Callback itemTouch = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                int dragFlags = ItemTouchHelper.ACTION_STATE_IDLE; // Eventos drag and drop ficarão inativos
                int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END; // Add evento de arrastar para o lado direito e esquerdo
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                excluirMovimentacao(viewHolder);
            }
        };

        new ItemTouchHelper(itemTouch).attachToRecyclerView(recyclerView); // Anexando o evento ao recyclerView
    }

    public void recuperarMovimentacoes(){

        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(emailUsuario);
        movimentacaoRef = firebaseRef.child("movimentacao")
                                     .child(idUsuario)
                                     .child(mesAnoSeleciona);

        valueEventListenerMovimentacoes = movimentacaoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                movimentacaos.clear();
                for(DataSnapshot dados : snapshot.getChildren()){
                    Movimentacao movimentacao = dados.getValue(Movimentacao.class);
                    movimentacao.setId(dados.getKey());
                    movimentacaos.add(movimentacao);
                }

                adapterMovimentacao.notifyDataSetChanged(); // Notifica o adapter que os dados mudaram
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // Anexando evento de Listener
    @Override
    protected void onStart() {
        super.onStart();
        recuperarResumo();
        recuperarMovimentacoes();
    }

    // Removendo o evento de Listener quando sai da tela Principal
    @Override
    protected void onStop() {
        super.onStop();
        usuarioRef.removeEventListener(valueEventListenerUsuario);
        movimentacaoRef.removeEventListener(valueEventListenerMovimentacoes);
    }

    // Pega os valores nome e saldo para adicionar aos campos
    public void recuperarResumo(){
        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(emailUsuario);
        usuarioRef = firebaseRef.child("usuarios").child(idUsuario);

        valueEventListenerUsuario = usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue(Usuario.class);
                despesaTotal = usuario.getDespesaTotal();
                receitaTotal = usuario.getReceitaTotal();
                resumoUsuario = receitaTotal - despesaTotal;

                DecimalFormat decimalFormat = new DecimalFormat("0.##");
                String resumoUsuarioFormatado = decimalFormat.format(resumoUsuario);

                textoSaudacao.setText("Olá, " + usuario.getNome());
                textoSaldo.setText("R$ " + resumoUsuarioFormatado);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menuSair){
            autenticacao.signOut();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void adicionarReceita(View view){
        startActivity(new Intent(this, ReceitasActivity.class));
    }

    public void adicionarDespesa(View view){
        startActivity(new Intent(this, DespesasActivity.class));
    }

    public void configuraCalendario(){
        // Personalizando nome dos meses
        CharSequence meses[] = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};
        calendarView.setTitleMonths(meses);

        // Personalizando nome dos dias da semana
        CharSequence diasSemana[] = {"Seg", "Ter", "Qua", "Qui", "Sex", "Sab", "Dom"};
        calendarView.setWeekDayLabels(diasSemana);

        // Data que o calendário inicia, ou seja, quando ele abre e nao ocorre o onMonthChanged
        CalendarDay dataCalendario = calendarView.getCurrentDate();
        String mesSelecionado = String.format("%02d", (dataCalendario.getMonth() + 1)); // Adicionando um 0 antes do numero para ficar 022024 e nao 22024
        mesAnoSeleciona = String.valueOf(mesSelecionado + "" + dataCalendario.getYear());

        calendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                String mesSelecionado = String.format("%02d", (date.getMonth() + 1)); // Adicionando um 0 antes do numero para ficar 022024 e nao 22024
                mesAnoSeleciona = String.valueOf(mesSelecionado + date.getYear());

                movimentacaoRef.removeEventListener(valueEventListenerMovimentacoes); // O "recuperarMovimentacoes()" adiciona um Listener, para evitar add vários Listener é removido o anterior
                recuperarMovimentacoes();
            }
        });
    }
}