package com.joao.firebaseapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

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

        //Caso usuario Logado
        if (auth.getCurrentUser()!=null){
            String email = auth.getCurrentUser().getEmail();
            Intent intent = new Intent(getApplicationContext(), NavigationActivity.class);

            intent.putExtra("email", email);
            startActivity(intent);
        }

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
         auth.signInWithEmailAndPassword(email,senha)

        .addOnSuccessListener(authResult -> {
            Toast.makeText(this, "Bem vindo", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        })

        //Listener de falha
        .addOnFailureListener(e -> {
           //Parametro e -> Exception
           Toast.makeText(this, "Erro" + e.getClass().toString(), Toast.LENGTH_SHORT).show();

           Log.e("Erro","Mensagem" + e.getMessage() + "classe: "+ e.getClass().toString() );
           try {
               //Exceção para email invalido
               throw e;
           }catch (FirebaseAuthInvalidUserException userException){
               //Exceção p/ senha incorreta
               Toast.makeText(this, "", Toast.LENGTH_SHORT).show();

           }catch (FirebaseAuthInvalidCredentialsException credException){
               //Exceção genérica
               Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
           }catch (Exception ex){
               Toast.makeText(this, "Erro", Toast.LENGTH_SHORT).show();
           }

        });
    }






}
