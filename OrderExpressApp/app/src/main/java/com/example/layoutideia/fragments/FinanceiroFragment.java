package com.example.layoutideia.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.layoutideia.R;
import com.example.layoutideia.activity.ConsultaPedidoActivity;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FinanceiroFragment extends Fragment {

    private RecyclerView recyclerFinanceiro;
    private ProgressBar progressBarPedidosRecentes;
    private TextView textAvisoErro;
    private ImageView imageAvisoErro;
    private AdapterPedido adapter;
    private List<Pedido> listaPedido = new ArrayList<>();
    private DatabaseReference pedidosRef;
    private DatabaseReference database;
    private CarrinhoViewModel carrinhoViewModel;
    private ChildEventListener childEventListenerPedidos;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_financeiro2, container, false);

        progressBarPedidosRecentes = rootView.findViewById(R.id.progressBarPedidosRecentes);
        progressBarPedidosRecentes.setVisibility(View.VISIBLE);


        recyclerFinanceiro = rootView.findViewById(R.id.recyclerFinanceiro);

        carrinhoViewModel = new ViewModelProvider(this).get(CarrinhoViewModel.class);

        // Config. Listagem Pedidos do Cliente
        database = ConfiguracaoFirebase.getFirebaseDatabase();
        pedidosRef = database
                .child("pedidos")
                .child(VendedorFirebase.getIdVendedorLogado())
                .child(carrinhoViewModel.getCliente().getCodigo());

        // Config. Adapter
        adapter = new AdapterPedido(listaPedido);

        // Config. Recycler
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerFinanceiro.setLayoutManager(layoutManager);
        recyclerFinanceiro.setHasFixedSize(true);
        recyclerFinanceiro.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayout.VERTICAL));
        recyclerFinanceiro.setAdapter(adapter);
        configurarClickRecycler();

        // Definindo um timer para que se não recuperar os clientes em 5 segundos aparecer uma mensagem
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if(progressBarPedidosRecentes.getVisibility() == View.VISIBLE){
                    textAvisoErro = rootView.findViewById(R.id.textAvisoErro);
                    imageAvisoErro = rootView.findViewById(R.id.imageAvisoErro);

                    textAvisoErro.setVisibility(View.VISIBLE);
                    imageAvisoErro.setVisibility(View.VISIBLE);
                    progressBarPedidosRecentes.setVisibility(View.GONE);
                }
            }
        };
        handler.postDelayed(runnable, 3500);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarPedidos();
    }

    @Override
    public void onStop() {
        super.onStop();
        pedidosRef.removeEventListener(childEventListenerPedidos);
    }

    public void recuperarPedidos(){
        listaPedido.clear();

        childEventListenerPedidos = pedidosRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Pedido pedido = snapshot.getValue(Pedido.class);
                listaPedido.add(pedido);
                adapter.notifyDataSetChanged();
                progressBarPedidosRecentes.setVisibility(View.GONE);
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

    private void configurarClickRecycler(){
        recyclerFinanceiro.addOnItemTouchListener(new RecyclerViewClick(
                getActivity(),
                recyclerFinanceiro,
                new RecyclerViewClick.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        List<Pedido> listaPedidosAtualizada = adapter.getListaPedido();
                        Pedido pedidoSelecionado = listaPedidosAtualizada.get(position);

                        Intent intent = new Intent(getActivity(), ConsultaPedidoActivity.class);
                        intent.putExtra("clickFragment", true);
                        intent.putExtra("telaAnterior", 0);
                        intent.putExtra("Cliente", pedidoSelecionado.getCliente());
                        intent.putExtra("FormaPagamento", pedidoSelecionado.getFormaPagamento());
                        intent.putExtra("OperacaoVenda", pedidoSelecionado.getOperacaoVenda());
                        intent.putExtra("Total", pedidoSelecionado.getTotal());
                        intent.putExtra("Descricao", pedidoSelecionado.getDescricao());
                        intent.putExtra("Data", pedidoSelecionado.getDataPedido());

                        CarrinhoViewModel.limpaItensCarrinhoConsulta();
                        CarrinhoViewModel.setItensCarrinhoConsulta(pedidoSelecionado.getItens());

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

}