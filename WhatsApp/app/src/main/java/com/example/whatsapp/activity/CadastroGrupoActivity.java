package com.example.whatsapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whatsapp.R;
import com.example.whatsapp.adapter.ContatoGrupoAdapter;
import com.example.whatsapp.model.Usuario;

import java.util.ArrayList;
import java.util.List;

public class CadastroGrupoActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private List<Usuario> listaMembrosSelecionados = new ArrayList<>();
    private TextView textTotalParticipantes;
    private ContatoGrupoAdapter contatoGrupoAdapter;
    private RecyclerView recyclerMembrosSelecionados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_grupo);

        // Config. Toolbar
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Novo Grupo");
        toolbar.setSubtitle("Defina o nome");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Recuperando lista de membros passada de GrupoActivity
        if(getIntent().getExtras() != null){
            List<Usuario> membros = (List<Usuario>) getIntent().getExtras().getSerializable("membros");
            listaMembrosSelecionados.addAll(membros);
        }

        textTotalParticipantes = findViewById(R.id.textTotalParticipantes);
        textTotalParticipantes.setText("Participantes: " + listaMembrosSelecionados.size());

        recyclerMembrosSelecionados = findViewById(R.id.recyclerMembrosGrupo);

        // Config. Adapter
        contatoGrupoAdapter = new ContatoGrupoAdapter(listaMembrosSelecionados, getApplicationContext());

        // Config. RecyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(
                getApplicationContext(),
                LinearLayoutManager.HORIZONTAL,
                false);

        recyclerMembrosSelecionados.setLayoutManager(layoutManager);
        recyclerMembrosSelecionados.setHasFixedSize(true);
        recyclerMembrosSelecionados.setAdapter(contatoGrupoAdapter);


    }
}