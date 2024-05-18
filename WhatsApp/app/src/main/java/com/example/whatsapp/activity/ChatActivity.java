package com.example.whatsapp.activity;

import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.whatsapp.model.Usuario;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.whatsapp.databinding.ActivityChatBinding;

import com.example.whatsapp.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityChatBinding binding;
    private Usuario usuarioDestino;

    private CircleImageView circleImageViewFotoChat;
    private TextView textViewNomeChat;

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

        // Recuperar os dados passados no onClick de ContatosFragment
        Bundle bundle = getIntent().getExtras();

        if(bundle != null){
            usuarioDestino = (Usuario) bundle.getSerializable("chatContato");
            textViewNomeChat.setText(usuarioDestino.getNome());
            String foto = usuarioDestino.getFoto();

            if(foto != null){
                Uri url = Uri.parse(foto);
                Glide.with(ChatActivity.this)
                        .load(url)
                        .into(circleImageViewFotoChat);
            }else{
                circleImageViewFotoChat.setImageResource(R.drawable.padrao);
            }
        }

    }

}