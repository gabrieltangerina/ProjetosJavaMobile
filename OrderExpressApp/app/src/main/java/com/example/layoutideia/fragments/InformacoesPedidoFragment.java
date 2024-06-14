package com.example.layoutideia.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.layoutideia.R;
import com.example.layoutideia.activity.DadosPedidoActivity;
import com.example.layoutideia.model.Cliente;
import com.example.layoutideia.viewmodel.CarrinhoViewModel;
import com.google.android.material.textfield.TextInputEditText;

public class InformacoesPedidoFragment extends Fragment {

    private DadosPedidoActivity activity;

    private TextView editRazaoSocialInfPedido;
    private Spinner spinnerOpVenda;
    private Spinner spinnerFormaPagamento;
    private EditText editTextDescricao;

    private Cliente cliente;
    private CarrinhoViewModel carrinhoViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_informacoes_pedido2, container, false);

        activity = (DadosPedidoActivity) getActivity();
        // cliente = activity.getCliente();

        carrinhoViewModel = new ViewModelProvider(this).get(CarrinhoViewModel.class);
        cliente = carrinhoViewModel.getCliente();

        spinnerOpVenda = rootView.findViewById(R.id.spinnerOpVenda);
        spinnerFormaPagamento = rootView.findViewById(R.id.spinnerFormaPagamento);

        editRazaoSocialInfPedido = rootView.findViewById(R.id.textNomeCliente);
        editRazaoSocialInfPedido.setText(carrinhoViewModel.getCliente().getNomeCliente());

        editTextDescricao = rootView.findViewById(R.id.editTextDescricao);
        editTextDescricao.setText(CarrinhoViewModel.getDescricao());
        editTextDescricao.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                carrinhoViewModel.setDescricao(editTextDescricao.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Config. Spinner Op. Venda
        ArrayAdapter<CharSequence> adapterOpVenda = ArrayAdapter.createFromResource(getActivity(),
                R.array.op_venda_opcoes, android.R.layout.simple_spinner_item);
        adapterOpVenda.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOpVenda.setAdapter(adapterOpVenda);

        // Config. Spinner Forma Pagamento
        ArrayAdapter<CharSequence> adapterFormaPagamento = ArrayAdapter.createFromResource(getActivity(),
                R.array.forma_pagamento_opcoes, android.R.layout.simple_spinner_item);
        adapterFormaPagamento.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFormaPagamento.setAdapter(adapterFormaPagamento);

        // Definir listener para seleção de itens
        spinnerFormaPagamento.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) parent.getItemAtPosition(position);
                carrinhoViewModel.setFormaPagamento(selectedItem);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerOpVenda.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) parent.getItemAtPosition(position);
                carrinhoViewModel.setOperacaoVenda(selectedItem);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return rootView;
    }
}