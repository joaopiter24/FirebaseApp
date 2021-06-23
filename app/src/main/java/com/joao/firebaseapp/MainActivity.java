package com.joao.firebaseapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.joao.firebaseapp.adapter.ImageAdapter;
import com.joao.firebaseapp.model.Upload;
import com.joao.firebaseapp.util.LoadingDialog;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private Button btnLogout, btnStorage;
    private DatabaseReference database = FirebaseDatabase.getInstance().getReference("uploads");

    private ArrayList<Upload> listaUploads = new ArrayList<>();

    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLogout = findViewById(R.id.main_btn_logout);
        btnStorage = findViewById(R.id.main_btn_storage);
        recyclerView = findViewById(R.id.main_recycler);

        imageAdapter = new ImageAdapter(getApplication(), listaUploads);
        imageAdapter.setListener(new ImageAdapter.OnItemClickListener() {
            @Override
            public void onDeleteClick(int position) {
                Upload upload = listaUploads.get(position);
                deleteUpload(upload);
            }

            @Override
            public void onUpdateClick(int position) {

            }
        });

        recyclerView.setLayoutManager( new LinearLayoutManager(getApplicationContext())
        );

        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(imageAdapter);

        btnLogout.setOnClickListener( v -> {
            //Deslogar usuario
            auth.signOut();
            finish();
        });

        btnStorage.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), StorageActivity.class);
            startActivity(intent);
        });

        TextView  textEmail = findViewById(R.id.main_text_email);
        textEmail.setText(auth.getCurrentUser().getEmail());

        TextView  textNome = findViewById(R.id.main_text_user);
        textNome.setText(auth.getCurrentUser().getDisplayName());
    }

    @Override
    protected void onStart() {
        // inStart:
        /*  - Faz parte do ciclo de vida da Activity, depois do onCreate()
        *   - É executado quando app Inicia,
        *   - E quando volta do background
        * */
        super.onStart();
        getData();

    }

    public void deleteUpload(Upload upload){
        LoadingDialog dialog = new LoadingDialog(this,R.layout.custom_dialog);

        dialog.startLoadingDialog();

        // Deletar a imagem no Storage
        StorageReference imageRef = FirebaseStorage
                                        .getInstance()
                                        .getReferenceFromUrl(upload.getUrl());
        imageRef.delete()
        .addOnSuccessListener(aVoid -> {
            // Deletar imagem no DataBase
            database.child(upload.getId()).removeValue()
            .addOnSuccessListener(aVoid1 -> {
                Toast.makeText(getApplicationContext(), "Item deletado!", Toast.LENGTH_SHORT).show();
                dialog.dismissDialog();
            });
        });
    }

    public void getData(){
        // Listener para o nó Uploads
        /*  - caso ocorra alguma alteração -> retorna TODOS os dados */
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for( DataSnapshot no_filho: snapshot.getChildren()){
                    Upload upload = no_filho.getValue(Upload.class);
                    listaUploads.add(upload);
                    Log.i("DATABASE","id: "+ upload.getId() + ",nome: " + upload.getNomeImagem() );

                }
                imageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }

}















