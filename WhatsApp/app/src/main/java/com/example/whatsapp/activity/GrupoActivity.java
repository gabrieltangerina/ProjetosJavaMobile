package com.example.whatsapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.whatsapp.R;
import com.example.whatsapp.adapter.ContatoGrupoAdapter;
import com.example.whatsapp.adapter.ContatosAdapter;
import com.example.whatsapp.config.ConfiguracaoFirebase;
import com.example.whatsapp.helper.RecyclerItemClickListener;
import com.example.whatsapp.helper.UsuarioFirebase;
import com.example.whatsapp.model.Usuario;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GrupoActivity extends AppCompatActivity {
    private RecyclerView recyclerMembrosSelecionados, recyclerMembros;
    private ContatosAdapter contatosAdapter;
    private ContatoGrupoAdapter contatosGrupoAdapter;
    private List<Usuario> listaMembros = new ArrayList<>();
    private List<Usuario> listaMembrosSelecionados = new ArrayList<>();
    private ValueEventListener valueEventListenerMembros;
    private DatabaseReference usuarioRef;
    private FirebaseUser usuarioAtual;
    private Toolbar toolbar;
    private FloatingActionButton fabAvancarCadastro;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grupo);

        // Config. Toolbar
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Novo Grupo");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Config. Seta de Voltar

        // Config. FloatingActionButton
        fabAvancarCadastro = findViewById(R.id.fabAvancarCadastro);
        fabAvancarCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(listaMembrosSelecionados.isEmpty()){
                    Toast.makeText(GrupoActivity.this, "É necessário que o grupo tenha ao menos menos 2 participantes", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent i = new Intent(GrupoActivity.this, CadastroGrupoActivity.class);
                i.putExtra("membros", (Serializable) listaMembrosSelecionados); // Tem que converter para Serializable
                startActivity(i);
            }
        });

        recyclerMembrosSelecionados = findViewById(R.id.recyclerMembrosSelecionados);
        recyclerMembros = findViewById(R.id.recyclerMembros);

        // Config. Adapter (membros)
        contatosAdapter = new ContatosAdapter(listaMembros, getApplicationContext());

        // Config. RecyclerView para membros
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerMembros.setLayoutManager(layoutManager);
        recyclerMembros.setHasFixedSize(true);
        recyclerMembros.setAdapter(contatosAdapter);

        // Config. Adapter (membros selecionados)
        contatosGrupoAdapter = new ContatoGrupoAdapter(listaMembrosSelecionados, getApplicationContext());

        // Config. RecyclerView para membros selecionados
        RecyclerView.LayoutManager layoutManagerMembrosSelecionados = new LinearLayoutManager(
                getApplicationContext(),
                LinearLayoutManager.HORIZONTAL,
                false
        );
        recyclerMembrosSelecionados.setLayoutManager(layoutManagerMembrosSelecionados);
        recyclerMembrosSelecionados.setHasFixedSize(true);
        recyclerMembrosSelecionados.setAdapter(contatosGrupoAdapter);

        // Config. Referencia Database
        usuarioRef = ConfiguracaoFirebase.getFirebaseDatabase().child("usuarios");

        // Pegando Usuario Atual
        usuarioAtual = UsuarioFirebase.getUsuarioAtual();

        // Config. Evento de click no RecyclerView
        recyclerMembros.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), recyclerMembros, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Usuario usuarioSelecionado = listaMembros.get(position);

                // Removendo usuário da lista antiga
                listaMembros.remove(usuarioSelecionado);
                contatosAdapter.notifyDataSetChanged();

                // Adicionando usuário na lista de selecionados
                listaMembrosSelecionados.add(usuarioSelecionado);
                contatosGrupoAdapter.notifyDataSetChanged();
                atualizarMembrosToolbar();
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }));

        // Config. Evento de click no RecyclerViewSelecionados
        recyclerMembrosSelecionados.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), recyclerMembrosSelecionados, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Usuario usuarioSelecionado = listaMembrosSelecionados.get(position);

                // Removendo da lista de selecionados
                listaMembrosSelecionados.remove(usuarioSelecionado);
                contatosGrupoAdapter.notifyDataSetChanged();

                // Adicionando usuario a lista de todos os contatos
                listaMembros.add(usuarioSelecionado);
                contatosAdapter.notifyDataSetChanged();

                atualizarMembrosToolbar();
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }));
    }

    public void atualizarMembrosToolbar(){
        int totalSelecionado = listaMembrosSelecionados.size();
        int total = listaMembros.size() + totalSelecionado;

        toolbar.setSubtitle(totalSelecionado + " de " + total + " selecionados");
    }

    public void recuperarContatos(){
        valueEventListenerMembros = usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dados: snapshot.getChildren()){
                    Usuario usuario = dados.getValue(Usuario.class);

                    if(!usuarioAtual.getEmail().equals(usuario.getEmail())){
                        listaMembros.add(usuario);
                    }

                }

                contatosAdapter.notifyDataSetChanged();
                atualizarMembrosToolbar();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarContatos();
    }

    @Override
    protected void onStop() {
        super.onStop();
        usuarioRef.removeEventListener(valueEventListenerMembros);
    }
}