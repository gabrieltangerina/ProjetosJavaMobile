package com.example.whatsapp.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsapp.R;
import com.example.whatsapp.adapter.MensagensAdapter;
import com.example.whatsapp.config.ConfiguracaoFirebase;
import com.example.whatsapp.databinding.ActivityChatBinding;
import com.example.whatsapp.helper.Base64Custom;
import com.example.whatsapp.helper.UsuarioFirebase;
import com.example.whatsapp.model.Conversa;
import com.example.whatsapp.model.Grupo;
import com.example.whatsapp.model.Mensagem;
import com.example.whatsapp.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private static final int SELECAO_CAMERA = 100;
    private AppBarConfiguration appBarConfiguration;
    private ActivityChatBinding binding;
    private Usuario usuarioDestino;
    private String idUsuarioDestinatario;
    private String idUsuarioRemetente;
    private DatabaseReference mensagensRef;
    private ChildEventListener childEventListenerMensagens;
    private DatabaseReference database;
    private StorageReference storage;
    private Grupo grupo;
    private Usuario usuarioRemetente;

    private CircleImageView circleImageViewFotoChat;
    private TextView textViewNomeChat;
    private EditText editMensagem;
    private RecyclerView recyclerMensagens;
    private ImageView imageCamera;

    private MensagensAdapter adapter;
    private List<Mensagem> mensagens = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        MaterialToolbar toolbar = binding.toolbar;
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        textViewNomeChat = findViewById(R.id.textViewNomeChat);
        circleImageViewFotoChat = findViewById(R.id.circleImageFotoChat);
        editMensagem = findViewById(R.id.editMensagem);

        // Recuperar os dados passados no onClick de ContatosFragment
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if(bundle.containsKey("chatGrupo")){

                grupo = (Grupo) bundle.getSerializable("chatGrupo");
                idUsuarioDestinatario = grupo.getId();
                textViewNomeChat.setText(grupo.getNome());

                String foto = grupo.getFoto();
                if(foto != null){
                    Uri url = Uri.parse(foto);
                    Glide.with(ChatActivity.this).load(url).into(circleImageViewFotoChat);
                }else{
                    circleImageViewFotoChat.setImageResource(R.drawable.padrao);
                }

            }else{
                usuarioDestino = (Usuario) bundle.getSerializable("chatContato");
                textViewNomeChat.setText(usuarioDestino.getNome());
                String foto = usuarioDestino.getFoto();
                if (foto != null) {
                    Uri url = Uri.parse(foto);
                    Glide.with(ChatActivity.this)
                            .load(url)
                            .into(circleImageViewFotoChat);
                } else {
                    circleImageViewFotoChat.setImageResource(R.drawable.padrao);
                }

                // Recupera dados do usuário destinatário
                idUsuarioDestinatario = Base64Custom.codificarBase64(usuarioDestino.getEmail());
            }
        }

        // Recupera dados do usuário remetente
        idUsuarioRemetente = UsuarioFirebase.getIdUser();

        recyclerMensagens = findViewById(R.id.recyclerMensagens);

        // Configurando Adapter
        adapter = new MensagensAdapter(mensagens, getApplicationContext());

        // Configurando RecyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerMensagens.setLayoutManager(layoutManager);
        recyclerMensagens.setHasFixedSize(true);
        recyclerMensagens.setAdapter(adapter);

        storage = ConfiguracaoFirebase.getFirebaseStorage();

        // Pegando referencia para listar mensagens
        database = ConfiguracaoFirebase.getFirebaseDatabase();
        mensagensRef = database.child("mensagens")
                .child(idUsuarioRemetente)
                .child(idUsuarioDestinatario);

        imageCamera = findViewById(R.id.imageCamera);
        imageCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(i, SELECAO_CAMERA);
//                if(i.resolveActivity(getPackageManager()) != null){
//                }
            }
        });

        usuarioRemetente = UsuarioFirebase.getDadosUsuarioLogado();

    }

    // Capturnado o resultado do "startActivityForResult(i, SELECAO_CAMERA);"
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Verificando se não ocorreu nenhum erro
        if(resultCode == RESULT_OK){
            Bitmap imagem = null;

            try{

                if(requestCode == SELECAO_CAMERA){
                    imagem = (Bitmap) data.getExtras().get("data");
                }

                if(imagem != null){

                    // Recuperando dados da imagem para o Firebase
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    // Criar nome da imagem
                    String nomeImagem = UUID.randomUUID().toString();

                    // Configurando referência
                    final StorageReference imagemRef = storage.child("imagens")
                            .child("fotos")
                            .child(idUsuarioRemetente)
                            .child(nomeImagem + ".jpeg");

                    // Fazendo upload da imagem no Storage
                    UploadTask uploadTask = imagemRef.putBytes(dadosImagem);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("Erro", "Erro ao fazer upload: " + e.getMessage());
                            Toast.makeText(ChatActivity.this, "Erro ao fazer upload da imagem", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imagemRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    Uri url = task.getResult();
                                    Mensagem mensagem = new Mensagem();
                                    mensagem.setIdRemetente(idUsuarioRemetente);
                                    mensagem.setImagem(url.toString());

                                    // Texto padrão para ser salvo no firebase
                                    mensagem.setMensagem("imagem.jpeg");

                                    // Salvando mensagem no Database (para o remetente)
                                    salvarMensagem(idUsuarioRemetente, idUsuarioDestinatario, mensagem);

                                    // Salvando mensagem para o destinatário
                                    salvarMensagem(idUsuarioDestinatario, idUsuarioRemetente, mensagem);

                                    Toast.makeText(ChatActivity.this, "Sucesso ao enviar imagem", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }

            }catch(Exception e){
                e.printStackTrace();
            }

        }
    }

    private void salvarConversa(String idRemetente, String idDestinaraio, Usuario usuarioExibicao, Mensagem mensagem, boolean isGroup){
        Conversa conversaRemetente = new Conversa();
        conversaRemetente.setIdRemetente(idRemetente);
        conversaRemetente.setIdDestinatario(idDestinaraio);
        conversaRemetente.setUltimaMensagem(mensagem.getMensagem());

        if(isGroup){
            // Conversa em grupo
            conversaRemetente.setIsGroup("true");
            conversaRemetente.setGrupo(grupo);
        }else{
            // Conversa entre duas pessoas
            conversaRemetente.setUsuarioExibicao(usuarioExibicao);
        }

        conversaRemetente.salvar();
    }

    public void enviarMensagem(View view) {
        String textoMensagem = editMensagem.getText().toString();
        if (!textoMensagem.isEmpty()) {

            if(usuarioDestino != null){
                Mensagem mensagem = new Mensagem();
                mensagem.setIdRemetente(idUsuarioRemetente);
                mensagem.setMensagem(textoMensagem);

                // Salvando mensagem para o remetente
                salvarMensagem(idUsuarioRemetente, idUsuarioDestinatario, mensagem);

                // Salvando mensagem para o destinatário
                salvarMensagem(idUsuarioDestinatario, idUsuarioRemetente, mensagem);

                // Salvando conversa para o remetente (para aparecer no fragment de conversas)
                salvarConversa(idUsuarioRemetente, idUsuarioDestinatario, usuarioDestino, mensagem, false);

                // Salvando conversa para o destinatário
                salvarConversa(idUsuarioDestinatario, idUsuarioRemetente, usuarioRemetente, mensagem, false);
            }else{

                for(Usuario membro: grupo.getMembros()){
                    String idRemetenteGrupo = Base64Custom.codificarBase64(membro.getEmail());
                    String idUsuarioLogadoGrupo = UsuarioFirebase.getIdUser();

                    Mensagem mensagem = new Mensagem();
                    mensagem.setIdRemetente(idUsuarioLogadoGrupo);
                    mensagem.setMensagem(textoMensagem);
                    mensagem.setNome(usuarioRemetente.getNome());

                    // Salvar mensagem
                    salvarMensagem(idRemetenteGrupo, idUsuarioDestinatario, mensagem);

                    // Salvar conversa
                    salvarConversa(idRemetenteGrupo, idUsuarioDestinatario, usuarioDestino, mensagem, true);
                }

            }

        }
    }

    private void salvarMensagem(String idRemetente, String idDestinatario, Mensagem mensagem) {
        DatabaseReference database = ConfiguracaoFirebase.getFirebaseDatabase();
        mensagensRef = database.child("mensagens");

        mensagensRef
                .child(idRemetente)
                .child(idDestinatario)
                .push()
                .setValue(mensagem);

        editMensagem.setText("");
    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarMensagens();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mensagensRef.removeEventListener(childEventListenerMensagens);
    }

    private void recuperarMensagens(){
        childEventListenerMensagens = mensagensRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Mensagem mensagem = snapshot.getValue(Mensagem.class);
                mensagens.add(mensagem);
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
