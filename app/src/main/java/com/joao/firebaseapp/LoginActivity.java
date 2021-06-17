package com.joao.firebaseapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private Button btnCadastrar;
    private Button btnLogin;
    private EditText editSenha, editEmail;

    private FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnCadastrar = findViewById(R.id.login_btn_cadastrar);
        btnLogin = findViewById(R.id.login_btn_logar);
        editEmail = findViewById(R.id.login_edit_email);
        editSenha = findViewById(R.id.login_edit_senha);


        btnCadastrar.setOnClickListener(v -> {
                Intent intent = new Intent(getApplicationContext(), CadastroActivity.class);
                startActivity(intent);


        });

        btnLogin.setOnClickListener(v -> {
            logar();
        });

    }

    public void logar(){
        String email= editEmail.getText().toString();
        String senha= editSenha.getText().toString();
        if (email.isEmpty() || senha.isEmpty()){
            Toast.makeText(this, "Preencha os campos", Toast.LENGTH_SHORT).show();
            return;
        }
        // t -> É uma tarefa para logar
        Task<AuthResult> t = auth.signInWithEmailAndPassword(email,senha);

        //Listener de sucesso
        t.addOnSuccessListener(authResult -> {
            Toast.makeText(this, "Bem vindo", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        });

        //Listener de falha
        t.addOnFailureListener(e -> {
           //Parametro e -> Exception
           Toast.makeText(this, "Erro", Toast.LENGTH_SHORT).show();
        });
    }






}
