package com.joao.firebaseapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;

public class CadastroActivity extends AppCompatActivity {

    private Button btnCadastrar;
    private EditText editEmail, editName, editSenha;

    //Referência para autenticaçao
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        btnCadastrar = findViewById(R.id.cadastro_btn_cadastrar);
        editEmail =findViewById(R.id.cadastro_edit_email);
        editName =findViewById(R.id.cadastro_edit_nome);
        editSenha =findViewById(R.id.cadastro_edit_senha);


        btnCadastrar.setOnClickListener(v -> {
            cadastrar();
        });

    }

    public void cadastrar(){
        String email= editEmail.getText().toString();
        String senha= editSenha.getText().toString();
        String nome = editName.getText().toString();
        if (email.isEmpty() || senha.isEmpty() || nome.isEmpty() ){
            Toast.makeText(this, "Preencha os campos", Toast.LENGTH_SHORT).show();
            return;
        }
        //Criar um usuario com email e senha
        Task<AuthResult> t = auth.createUserWithEmailAndPassword(email, senha);
        t.addOnCompleteListener(task -> {
           //Listener executado com sucesso ou com falha
           if (task.isSuccessful()){
               Toast.makeText(getApplicationContext(),"Usuario criado com sucesso!", Toast.LENGTH_SHORT).show();
               finish();
           } else {
               Toast.makeText(getApplicationContext(), "Erro", Toast.LENGTH_SHORT).show();
           }
        });

       t.addOnSuccessListener(authResult -> {
           //Request para mudar nome do Usuario
           UserProfileChangeRequest update = new UserProfileChangeRequest.Builder().setDisplayName(nome).build();
          //Setando nome do usuario
          authResult.getUser().updateProfile(update);
       });
    }





}







