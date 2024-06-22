package com.example.layoutideia.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.layoutideia.R;
import com.example.layoutideia.adapter.AdapterMeusProdutos;
import com.example.layoutideia.adapter.AdapterPedido;
import com.example.layoutideia.config.ConfiguracaoFirebase;
import com.example.layoutideia.helper.VendedorFirebase;
import com.example.layoutideia.model.Pedido;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.util.ArrayList;
import java.util.List;

public class MinhasVendasActivity extends AppCompatActivity {

    private MaterialCalendarView calendarView;
    private RecyclerView recyclerVendas;
    private AdapterPedido adapterPedidos;
    private List<Pedido> listaPedidos = new ArrayList<>();
    private DatabaseReference vendasRef;
    private String mesAnoSelecionado;
    private ValueEventListener valueEventListenerVendas;
    private Toolbar toolbar;

    private TextView textAvisoErro;
    private ImageView imageAvisoErro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minhas_vendas);

        // Config. Toolbar
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Vendas");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        calendarView = findViewById(R.id.calendarView);
        configuraCalendario();

        recyclerVendas = findViewById(R.id.recyclerVendas);

        // Config. Adapter
        adapterPedidos = new AdapterPedido(listaPedidos);

        // Config. RecyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerVendas.setLayoutManager(layoutManager);
        recyclerVendas.setHasFixedSize(true);
        recyclerVendas.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));
        recyclerVendas.setAdapter(adapterPedidos);

    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarVendas();
    }

    private void atualizarTotal(){
        Double total = 0.00;
        for(Pedido pedido: listaPedidos){
            total += pedido.getTotal();
        }

        toolbar.setSubtitle("Total mês: R$" + total);
    }

    private void recuperarVendas(){
        listaPedidos.clear();

        DatabaseReference databaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        vendasRef = databaseRef.child("vendas")
                .child(VendedorFirebase.getIdVendedorLogado())
                .child(mesAnoSelecionado); // A data salva será no fomato mes e ano: 062024

        valueEventListenerVendas = vendasRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaPedidos.clear();

                for(DataSnapshot dados : snapshot.getChildren()){
                    Pedido pedido = dados.getValue(Pedido.class);
                    pedido.setId(dados.getKey());
                    listaPedidos.add(pedido);
                }

                atualizarTotal();
                adapterPedidos.notifyDataSetChanged(); // Notifica o adapter que os dados mudaram


                textAvisoErro = findViewById(R.id.textAvisoErro);
                imageAvisoErro = findViewById(R.id.imageAvisoErro);

                if(listaPedidos.isEmpty()){
                    textAvisoErro.setVisibility(View.VISIBLE);
                    imageAvisoErro.setVisibility(View.VISIBLE);
                }else{
                    textAvisoErro.setVisibility(View.GONE);
                    imageAvisoErro.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void configuraCalendario(){
        // Personalizando nome dos meses
        CharSequence meses[] = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};
        calendarView.setTitleMonths(meses);

        // Personalizando nome dos dias da semana
        CharSequence diasSemana[] = {"Seg", "Ter", "Qua", "Qui", "Sex", "Sab", "Dom"};
        calendarView.setWeekDayLabels(diasSemana);

        // Data que o calendário inicia, ou seja, quando ele abre e nao ocorre o onMonthChanged
        CalendarDay dataCalendario = calendarView.getCurrentDate();
        String mesSelecionado = String.format("%02d", (dataCalendario.getMonth() + 1)); // Adicionando um 0 antes do numero para ficar 022024 e nao 22024
        mesAnoSelecionado = String.valueOf(mesSelecionado + "" + dataCalendario.getYear());

        calendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                String mesSelecionado = String.format("%02d", (date.getMonth() + 1)); // Adicionando um 0 antes do numero para ficar 022024 e nao 22024
                mesAnoSelecionado = mesSelecionado + date.getYear();

                vendasRef.removeEventListener(valueEventListenerVendas); // O "recuperarMovimentacoes()" adiciona um Listener, para evitar add vários Listener é removido o anterior
                recuperarVendas();
            }
        });
    }

}