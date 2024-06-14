package com.example.layoutideia.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.layoutideia.R;
import com.example.layoutideia.activity.DadosPedidoActivity;
import com.example.layoutideia.model.Cliente;
import com.example.layoutideia.viewmodel.CarrinhoViewModel;
import com.google.android.material.textfield.TextInputEditText;

public class ClienteFragment extends Fragment {

    private DadosPedidoActivity activity;
    private CarrinhoViewModel carrinhoViewModel;

    // Campos fragment
    private TextView editRazaoSocial;
    private TextView editNomeFantasia;
    private TextView editEndereco;
    private TextView editCnpj;
    private TextView editIe;

    // Inf. Cliente
    private Cliente cliente;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_cliente2, container, false);

        // Pegando a referência da activity DadosPedido
        activity = (DadosPedidoActivity) getActivity();

        carrinhoViewModel = new ViewModelProvider(this).get(CarrinhoViewModel.class);

        // Pegando cliente
        // cliente = activity.getCliente();
        cliente = carrinhoViewModel.getCliente();

        editRazaoSocial = rootView.findViewById(R.id.textRazaoSocial);
        editNomeFantasia = rootView.findViewById(R.id.textNomeFantasia);
        editEndereco = rootView.findViewById(R.id.textEndereco);
        editCnpj = rootView.findViewById(R.id.textCnpj);
        editIe = rootView.findViewById(R.id.textCidade);

        addValoresForm();

        return rootView;
    }

    private void addValoresForm(){
        editRazaoSocial.setText(cliente.getNomeCliente());
        editNomeFantasia.setText(cliente.getNomeFantasia());
        editEndereco.setText(cliente.getEndereco());
        editCnpj.setText(cliente.getCpnj());
        editIe.setText(cliente.getCidade());
    }
}