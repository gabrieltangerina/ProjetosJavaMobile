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
    private FloatingActionButton buttonSalvarImagem;
    private ProgressBar progressBar;
    private EditText editPerfilNome;

    private StorageReference storageReference;
    private String idUsuario;
    private Bitmap imagem;
    private FirebaseUser usuario;
    private Uri urlImagemAtual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);

        // Validando permissões
        // Permissao.validarPermissoes(permissoesNecessarios, this, 1);
        // alertaValidacaoPermissao();

        fotoPerfil = findViewById(R.id.fotoPerfil);
        editPerfilNome = findViewById(R.id.editPerfilNome);

        // Recuperando dados do usuário (nome e foto de perfil)
        usuario = UsuarioFirebase.getUsuarioAtual();
        Uri url = usuario.getPhotoUrl();

        // Recuperando foto de perfil do usuario ao iniciar activity
        if(url != null){
            Glide.with(ConfiguracoesActivity.this)
                    .load(url)
                    .into(fotoPerfil);

            urlImagemAtual = url;
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
        buttonSalvarImagem = findViewById(R.id.buttonSalvarImagem);
        buttonSalvarImagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvarDadosPerfil(v);
            }
        });

        progressBar = findViewById(R.id.progressBar);
    }

    private void salvarDadosPerfil(View v){
        /*
        * Esse método atualiza tanto o nome quanto a imagem dependendo da primeira verificação,
        * se a imagem for nula ele atualiza apenas o nome, se a imagem nao for nula ele atualiza
        * o nome e a imagem
        */

        if(imagem != null){
            progressBar.setVisibility(View.VISIBLE);
            // Recuperando dados da imagem para o Firebase
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
            byte[] dadosImagem = baos.toByteArray();

            // Salvando imagem no Firebase
            final StorageReference imagemRef = storageReference
                    .child("imagens")
                    .child("perfil")
                    // .child(idUsuario)
                    .child(idUsuario + ".jpeg");

            UploadTask uploadTask = imagemRef.putBytes(dadosImagem);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ConfiguracoesActivity.this, "Erro ao fazer upload da imagem", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    if(!editPerfilNome.getText().toString().isEmpty()){
                        // Salvando nome no Firebase
                        if (!String.valueOf(editPerfilNome.getText()).isEmpty()) {
                            // Salvando nome no Firebase (Auth)
                            progressBar.setVisibility(View.VISIBLE);
                            boolean nomeAtualizadoComSucesso = UsuarioFirebase.atualizarNomeUsuario(String.valueOf(editPerfilNome.getText()));
                            if (nomeAtualizadoComSucesso) {

                            } else {
                                Toast.makeText(ConfiguracoesActivity.this, "Falha ao atualizar o nome", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }else{
                        Toast.makeText(ConfiguracoesActivity.this, "Nome inválido", Toast.LENGTH_SHORT).show();
                    }

                    Toast.makeText(ConfiguracoesActivity.this, "Dados atualizados com sucesso", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);

                    // Adicionando a imagem para o storage do usuário para conseguir acessar com .getPhotoUrl()
                    imagemRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            Uri url = task.getResult();

                            // Atualizando foto no perfil do usuário (Auth)
                            atualizaFotoUsuario(url);
                        }
                    });
                }
            });
        }else if(!editPerfilNome.getText().toString().isEmpty()){
            // Salvando nome no Firebase
            if (!String.valueOf(editPerfilNome.getText()).isEmpty()) {
                // Salvando nome no Firebase (Auth)
                progressBar.setVisibility(View.VISIBLE);
                boolean nomeAtualizadoComSucesso = UsuarioFirebase.atualizarNomeUsuario(String.valueOf(editPerfilNome.getText()));
                if (nomeAtualizadoComSucesso) {
                    Toast.makeText(ConfiguracoesActivity.this, "Nome atualizado com sucesso", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ConfiguracoesActivity.this, "Falha ao atualizar o nome", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }
        }else{
            Toast.makeText(ConfiguracoesActivity.this, "Nome inválido", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        }

    }

    public void atualizaFotoUsuario(Uri url){
        UsuarioFirebase.atualizarFotoUsuario(url);
    }

    // Método para coletar retorno da ActivityForResult
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            try{

                if(requestCode == SELECAO_CAMERA){
                    imagem = (Bitmap) data.getExtras().get("data");
                }else if (requestCode == SELECAO_GALERIA){
                    Uri localImagemSelecionada = data.getData();
                    imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagemSelecionada);
                }

                if(imagem != null){
                    fotoPerfil.setImageBitmap(imagem);
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
