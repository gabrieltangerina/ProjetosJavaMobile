package com.example.whatsapp.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.whatsapp.R;
import com.example.whatsapp.config.ConfiguracaoFirebase;
import com.example.whatsapp.helper.Base64Custom;
import com.example.whatsapp.helper.Permissao;
import com.example.whatsapp.helper.UsuarioFirebase;
import com.example.whatsapp.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class ConfiguracoesActivity extends AppCompatActivity {

    private String[] permissoesNecessarios = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    private static final int SELECAO_CAMERA = 100;
    private static final int SELECAO_GALERIA = 200;

    private ImageButton imageButtonCamera, imageButtonGaleria;
    private ImageView fotoPerfil;
    private FloatingActionButton buttonSalvarNome;
    private ProgressBar progressBar;
    private EditText editPerfilNome;

    private StorageReference storageReference;
    private String idUsuario;
    private Bitmap imagem;
    private Usuario usuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);

        // Validando permissões
        // Permissao.validarPermissoes(permissoesNecessarios, this, 1);
        // alertaValidacaoPermissao();

        fotoPerfil = findViewById(R.id.fotoPerfil);
        editPerfilNome = findViewById(R.id.editPerfilNome);

        buttonSalvarNome = findViewById(R.id.buttonSalvarNome);
        buttonSalvarNome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvarNome(v);
            }
        });

        // Recuperando dados do usuário logado
        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();

        // Recuperando dados do usuário (nome e foto de perfil)
        FirebaseUser usuario = UsuarioFirebase.getUsuarioAtual();
        Uri url = usuario.getPhotoUrl();

        // Recuperando foto de perfil do usuario ao iniciar activity
        if(url != null){
            // Glide - biblioteca do Firebase para exibir a imagem (está nas dependências)
            Glide.with(ConfiguracoesActivity.this)
                    .load(url)
                    .into(fotoPerfil);
        }else{
            fotoPerfil.setImageResource(R.drawable.padrao);
        }

        // Recuperando nome do usuário
        editPerfilNome.setText(usuario.getDisplayName());

        // Config. Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Configurações");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Config. Seta de Voltar

        // Config. Icones Foto Perfil
        imageButtonCamera = findViewById(R.id.imageButtonCamera);
        imageButtonGaleria = findViewById(R.id.imageButtonGaleria);

        imageButtonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

//                if(i.resolveActivity(getPackageManager()) != null){
//                    startActivityForResult(i, SELECAO_CAMERA);
//                }

                startActivityForResult(i, SELECAO_CAMERA);

            }
        });

        imageButtonGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, SELECAO_GALERIA);
            }
        });

        storageReference = ConfiguracaoFirebase.getFirebaseStorage();
        idUsuario = UsuarioFirebase.getIdUser();

        progressBar = findViewById(R.id.progressBar);
    }

    public void salvarNome(View view){
        String nome = editPerfilNome.getText().toString();
        boolean nomeAtualizadoComSucesso = UsuarioFirebase.atualizarNomeUsuario(nome);

        if(nomeAtualizadoComSucesso){

            // Adicionou um novo nome para o usuário e atualizou no Firebase.
            usuarioLogado.setNome(nome);
            usuarioLogado.atualizar();

            Toast.makeText(this, "Nome alterado com sucesso", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Falha ao alterar nome", Toast.LENGTH_SHORT).show();
        }
    }

    public void atualizaFotoUsuario(Uri url){
        // Salva a foto no usuário do Firebase do usuário
        boolean fotoAtualizadaComSucesso = UsuarioFirebase.atualizarFotoUsuario(url);

        if(fotoAtualizadaComSucesso){
            // Salva a foto no Database do usuário
            usuarioLogado.setFoto(url.toString());
            usuarioLogado.atualizar();
            //Toast.makeText(this, "Foto alterada", Toast.LENGTH_SHORT).show();
        } else{
            //Toast.makeText(this, "Falha ao alterar foto", Toast.LENGTH_SHORT).show();
        }

    }

    // Método para coletar retorno da ActivityForResult, salva a imagem no Firebase e add url da imagem no perfil do usuario
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            try{
                progressBar.setVisibility(View.VISIBLE);
                if(requestCode == SELECAO_CAMERA){
                    imagem = (Bitmap) data.getExtras().get("data");
                }else if (requestCode == SELECAO_GALERIA){
                    Uri localImagemSelecionada = data.getData();
                    imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagemSelecionada);
                }

                if(imagem != null){

                    // Ao tirar ou selecionar foto ela será mostrada na foto de perfil
                    fotoPerfil.setImageBitmap(imagem);

                    // Recuperando dados da imagem para o Firebase
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    // Caminho no Firebase para salvar a imagem
                    final StorageReference imagemRef = storageReference.child("imagens")
                            .child("perfil")
                            .child(idUsuario + ".jpeg");

                    // Salvando imagem no Firebase
                    UploadTask uploadTask = imagemRef.putBytes(dadosImagem);

                    // Verificando se a imagem foi salva corretamente
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ConfiguracoesActivity.this, "Falha ao fazer upload da imagem", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(ConfiguracoesActivity.this, "Sucesso ao fazer upload da imagem", Toast.LENGTH_SHORT).show();

                            // Recuperando a url da imagem, para salvar a foto no Usuario do Firebase do usuário
                            imagemRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    Uri url = task.getResult();
                                    atualizaFotoUsuario(url);
                                }
                            });

                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }

            }catch (Exception e){
                e.printStackTrace();
            }


        }else{
            Toast.makeText(this, "Ocorreu um erro ao coletar o retorno", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Tratando a negação da permissão das solicitações
        for(int permissaoResultado : grantResults){
            if(permissaoResultado == PackageManager.PERMISSION_DENIED){
                alertaValidacaoPermissao();
            }
        }
    }

    private void alertaValidacaoPermissao(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões Negadas");
        builder.setCancelable(false); // Para não fechar o alert ao clicar fora dele
        builder.setMessage("Para utilizar o app é necessário aceitar as permissões");

        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
