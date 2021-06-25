package com.joao.firebaseapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.joao.firebaseapp.model.Upload;
import com.joao.firebaseapp.util.LoadingDialog;

import java.util.Date;

public class UpdateActivity extends AppCompatActivity {

    //Firebase Storage
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private Button btnupload, btnGaleria;
    private ImageView imageView;
    private Uri imageUri=null;
    private EditText editNome;
    // Referência para um Nó RealtTimeDB
    private DatabaseReference database = FirebaseDatabase.getInstance().getReference("uploads");
    private Upload upload;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        editNome= findViewById(R.id.update_edit_nome);
        btnupload = findViewById(R.id.update_btn_upload);
        btnGaleria = findViewById(R.id.update_btn_galeria);
        imageView = findViewById(R.id.update_image_cel);

        // Recuperar o upload selecionado
        upload = (Upload) getIntent().getSerializableExtra("upload");
        editNome.setText(upload.getNomeImagem());
        Glide.with(this).load(upload.getUrl()).into(imageView);

        btnGaleria.setOnClickListener(v -> {
            Intent intent = new Intent();
            //intent implicita -> pegar um arquivo do celular
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            // Inicia uma Activity, e espera o retorno(Foto)
            startActivityForResult(intent,111);
        });

        btnupload.setOnClickListener(v -> {
            if (editNome.getText().toString().isEmpty()){
                Toast.makeText(this, "SEM NOME", Toast.LENGTH_SHORT).show();
                return;
            }
            // Caso a imagem não tenha sido atualizada
            if (imageUri==null){
                // Atualizar o nome da imagem
                String nome  = editNome.getText().toString();
                upload.setNomeImagem(nome);
                database.child(upload.getId()).setValue(upload).addOnSuccessListener(aVoid -> {
                    finish();
                });
                return;
            }
            atualizarImagem();

        });
    }
    public void atualizarImagem(){
        // Deletar a imagem antiga no Storage
        storage.getReferenceFromUrl( upload.getUrl()).delete();

        // Fazer upload da imagem atualizada no Storage
        uploadImagemUri();
        // Recuperar a Url da imagem no storage

        //Atualizar no Database
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("RESULT", "requestCode: "+ resultCode + ",resultCode: "+ resultCode);

        if (requestCode== 111 && resultCode== Activity.RESULT_OK){
            //caso o usuario selecionou uma imagem da galeria

            //endereço da imagem selecionada
            imageUri = data.getData();
            imageView.setImageURI(imageUri);

        }
    }

    private void uploadImagemUri() {

        LoadingDialog dialog = new LoadingDialog(this, R.layout.custom_dialog);
        dialog.startLoadingDialog();

        String tipo = getFileExtesion(imageUri);
        //Referencia do arquivo no firebase
        Date d = new Date();
        String nome = editNome.getText().toString();
        //Criando Referencia da imagem no Storage
        StorageReference imagemRef = storage.getReference().child("imagens/"+nome+"-"+d.getTime()+"."+tipo);

        imagemRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            Toast.makeText(this, "Upload feito com Sucesso ", Toast.LENGTH_SHORT).show();

            //Inserir dados da imagem no RealtimeDatabase

            //pegar a url da imagem
            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                // atualizar no database


                //atualiuzar  o objeto upload
                upload.setUrl(uri.toString());
                upload.setNomeImagem( editNome.getText().toString());

                database.child(upload.getId()).setValue(upload).addOnSuccessListener(aVoid -> {
                    dialog.dismissDialog();
                    finish();
                });
            });
        }).addOnFailureListener(e -> {
            e.printStackTrace();
        });

    }

    //Retorna o tipo (.png, .jpg) da image,
    private String getFileExtesion(Uri imageUri) {
        ContentResolver cr = getContentResolver();
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(cr.getType(imageUri));
    }




}
