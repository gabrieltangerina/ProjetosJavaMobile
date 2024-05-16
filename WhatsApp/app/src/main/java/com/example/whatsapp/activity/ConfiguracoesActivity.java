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
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.whatsapp.R;
import com.example.whatsapp.helper.Permissao;

public class ConfiguracoesActivity extends AppCompatActivity {

    private String[] permissoesNecessarios = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    private static final int SELECAO_CAMERA = 100;
    private static final int SELECAO_GALERIA = 200;

    private ImageButton imageButtonCamera, imageButtonGaleria;
    private ImageView fotoPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);

        // Validando permissões
        // Permissao.validarPermissoes(permissoesNecessarios, this, 1);
        // alertaValidacaoPermissao();

        // Config. Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Configurações");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Config. Seta de Voltar

        // Config. Icones Foto Perfil
        imageButtonCamera = findViewById(R.id.imageButtonCamera);
        imageButtonGaleria = findViewById(R.id.imageButtonGaleria);
        fotoPerfil = findViewById(R.id.fotoPerfil);

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
    }

    // Método para coletar retorno da ActivityForResult
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            Bitmap imagem = null;

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
