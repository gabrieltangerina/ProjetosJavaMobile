package com.example.whatsapp.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whatsapp.R;
import com.example.whatsapp.adapter.ContatoGrupoAdapter;
import com.example.whatsapp.config.ConfiguracaoFirebase;
import com.example.whatsapp.helper.UsuarioFirebase;
import com.example.whatsapp.model.Grupo;
import com.example.whatsapp.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CadastroGrupoActivity extends AppCompatActivity {

    private static final int SELECAO_GALERIA = 200;
    private List<Usuario> listaMembrosSelecionados = new ArrayList<>();
    private ContatoGrupoAdapter contatoGrupoAdapter;
    private StorageReference storageReference;
    private Grupo grupo;

    private Toolbar toolbar;
    private RecyclerView recyclerMembrosSelecionados;
    private CircleImageView imageGrupo;
    private TextView textTotalParticipantes;
    private FloatingActionButton fabSalvarGrupo;
    private EditText editNomeGrupo;

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

        grupo = new Grupo();

        storageReference = ConfiguracaoFirebase.getFirebaseStorage();

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

        imageGrupo = findViewById(R.id.imageGrupo);
        imageGrupo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, SELECAO_GALERIA);
            }
        });

        editNomeGrupo = findViewById(R.id.editNomeGrupo);
        fabSalvarGrupo = findViewById(R.id.fabSalvarGrupo);
        fabSalvarGrupo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nomeGrupo = editNomeGrupo.getText().toString();

                // Adiciona o usuário que está criando o grupo a lista de membros do grupo
                listaMembrosSelecionados.add(UsuarioFirebase.getDadosUsuarioLogado());
                grupo.setMembros(listaMembrosSelecionados);
                grupo.setNome(nomeGrupo);
                grupo.salvar();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            Bitmap imagem = null;

            try{

                Uri localImagemSelecioanda = data.getData();
                imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagemSelecioanda);

                if(imagem != null){
                    // Adicionando a imagem selecionada no CircleImageView
                    imageGrupo.setImageBitmap(imagem);

                    // Recuperando dados da imagem para adicionar ao Firebase
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    // Salvando imagem no Firebase
                    StorageReference imagemRef = storageReference
                            .child("imagens")
                            .child("grupos")
                            .child(grupo.getId() + ".jpeg");

                    UploadTask uploadTask = imagemRef.putBytes(dadosImagem);

                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CadastroGrupoActivity.this, "Erro ao fazer upload da imagem no Firebase", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            imagemRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    String url = task.getResult().toString();
                                    grupo.setFoto(url);
                                    Toast.makeText(CadastroGrupoActivity.this, "Upload da imagem feito com sucesso", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    });
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }
}