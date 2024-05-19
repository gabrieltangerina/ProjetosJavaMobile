package com.example.whatsapp.activity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.navigation.ui.AppBarConfiguration;

import com.bumptech.glide.Glide;
import com.example.whatsapp.R;
import com.example.whatsapp.config.ConfiguracaoFirebase;
import com.example.whatsapp.databinding.ActivityChatBinding;
import com.example.whatsapp.helper.Base64Custom;
import com.example.whatsapp.helper.UsuarioFirebase;
import com.example.whatsapp.model.Mensagem;
import com.example.whatsapp.model.Usuario;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.database.DatabaseReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityChatBinding binding;
    private Usuario usuarioDestino;
    private String idUsuarioDestinatario;
    private String idUsuarioRemetente;

    private CircleImageView circleImageViewFotoChat;
    private TextView textViewNomeChat;
    private EditText editMensagem;

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
        }

        // Recupera dados do usuário remetente
        idUsuarioRemetente = UsuarioFirebase.getIdUser();

        // Recupera dados do usuário destinatário
        idUsuarioDestinatario = Base64Custom.codificarBase64(usuarioDestino.getEmail());
    }

    public void enviarMensagem(View view) {
        String textoMensagem = editMensagem.getText().toString();
        if (!textoMensagem.isEmpty()) {
            Mensagem mensagem = new Mensagem();
            mensagem.setIdRemetente(idUsuarioRemetente);
            mensagem.setMensagem(textoMensagem);
            salvarMensagem(idUsuarioRemetente, idUsuarioDestinatario, mensagem);
            editMensagem.setText(""); // Limpar o campo de mensagem após enviar
        }
    }

    private void salvarMensagem(String idRemetente, String idDestinatario, Mensagem mensagem) {
        DatabaseReference database = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference mensagemRef = database.child("mensagens");
        mensagemRef
                .child(idRemetente)
                .child(idDestinatario)
                .push()
                .setValue(mensagem);

        editMensagem.setText("");
    }
}
