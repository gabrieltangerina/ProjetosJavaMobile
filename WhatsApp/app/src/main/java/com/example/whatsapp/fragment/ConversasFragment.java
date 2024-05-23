package com.example.whatsapp.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.example.whatsapp.R;
import com.example.whatsapp.activity.ChatActivity;
import com.example.whatsapp.adapter.ConversasAdapter;
import com.example.whatsapp.config.ConfiguracaoFirebase;
import com.example.whatsapp.helper.RecyclerItemClickListener;
import com.example.whatsapp.helper.UsuarioFirebase;
import com.example.whatsapp.model.Conversa;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ConversasFragment extends Fragment {
    private RecyclerView recyclerListaConversas;
    private List<Conversa> listaConversa = new ArrayList<>();
    private ConversasAdapter adapter;
    private DatabaseReference database;
    private DatabaseReference conversasRef;
    private ChildEventListener childEventListenerConversas;



    public ConversasFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversas, container, false);

        recyclerListaConversas = view.findViewById(R.id.recyclerListaConversas);

        // Config. Adapter
        adapter = new ConversasAdapter(listaConversa, getActivity());

        // Config. RecyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerListaConversas.setLayoutManager(layoutManager);
        recyclerListaConversas.setHasFixedSize(true);
        recyclerListaConversas.setAdapter(adapter);

        // Config. Evento de click no RecyclerView
        recyclerListaConversas.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), recyclerListaConversas, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Conversa conversa = listaConversa.get(position);

                if(conversa.getIsGroup().equals("true")){
                    Intent i = new Intent(getActivity(), ChatActivity.class);
                    i.putExtra("chatGrupo", conversa.getGrupo());
                    startActivity(i);
                }else{
                    Intent i = new Intent(getActivity(), ChatActivity.class);
                    i.putExtra("chatContato", conversa.getUsuarioExibicao());
                    startActivity(i);
                }

            }

            @Override
            public void onLongItemClick(View view, int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }));

        // Config. Referencias para o Firebase
        String idUsuario = UsuarioFirebase.getIdUser();
        database = ConfiguracaoFirebase.getFirebaseDatabase();
        conversasRef = database.child("conversas").child(idUsuario);

        return view;
    }

    public void recarregarConversas(){
        adapter = new ConversasAdapter(listaConversa, getActivity());
        recyclerListaConversas.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void pesquisarConversas(String texto){
        List<Conversa> listaConversasBusca = new ArrayList<>();

        for(Conversa conversa: listaConversa){
            String nome = conversa.getUsuarioExibicao().getNome().toLowerCase();
            String ultumaMsg = conversa.getUltimaMensagem().toLowerCase();

            if(nome.contains(texto) || ultumaMsg.contains(texto)){
                listaConversasBusca.add(conversa);
            }
        }

        adapter = new ConversasAdapter(listaConversasBusca, getActivity());
        recyclerListaConversas.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarConversas();
    }

    @Override
    public void onStop() {
        super.onStop();
        conversasRef.removeEventListener(childEventListenerConversas);
    }

    public void recuperarConversas(){
        childEventListenerConversas = conversasRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Conversa conversa = snapshot.getValue(Conversa.class);
                listaConversa.add(conversa);
                adapter.notifyDataSetChanged();
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
}