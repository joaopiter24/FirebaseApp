package com.joao.firebaseapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Date;

public class StorageActivity extends AppCompatActivity {
    //Firebase Storage
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private Button btnupload, btnGaleria;
    private ImageView imageView;
    private Uri imageUri=null;
    private EditText editNome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage);

        editNome= findViewById(R.id.storage_edit_nome);
        btnupload = findViewById(R.id.storage_btn_upload);
        btnGaleria = findViewById(R.id.storage_btn_galeria);
        imageView = findViewById(R.id.storage_image_cel);

        btnupload.setOnClickListener(v -> {
            if (editNome.getText().toString().isEmpty()){
                Toast.makeText(this, "Digite um nome para Imagem", Toast.LENGTH_SHORT).show();
                return;
            }
            if (imageUri != null) {
                uploadImagamUri();
            } else {
                uploadImagemByte();
            }
        });

        btnGaleria.setOnClickListener(v -> {
            Intent intent = new Intent();
            //intent implicita -> pegar um arquivo do celular
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent,111);
        });
    }

    private void uploadImagamUri() {
        String tipo = getFileExtesion(imageUri);
        //Referencia do arquivo no firebase
        Date d = new Date();
        String nome = editNome.getText().toString();
        StorageReference imagemRef = storage.getReference().child("imagens/"+nome+"."+tipo);

       imagemRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
           Toast.makeText(this, "Sucesso ", Toast.LENGTH_SHORT).show();

       }).addOnFailureListener(e -> {
           e.printStackTrace();
       });

    }

    //Retorna o tipo (.png, .jpg) da image,
    private String getFileExtesion(Uri imageUri) {
        ContentResolver cr = getContentResolver();
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(cr.getType(imageUri));
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

    public byte[] convertImage2byte(ImageView imageView){
        //Converter ImageView -> byte[]
        Bitmap bitmap = ( (BitmapDrawable) imageView.getDrawable() ).getBitmap();
        //objeto baos ->
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100, baos);
        return baos.toByteArray();

    }

    //Fazer o upload de uma imagem convertida para bytes
    public void uploadImagemByte(){
       byte[] data = convertImage2byte(imageView);
       //Criar uma referencia p/ imagem no Storage
        StorageReference imageRef = storage.getReference().child("imagens/01.jpeg");

        //Realizar o upload da imagem
        imageRef.putBytes(data)
        .addOnSuccessListener(taskSnapshot -> {
            Toast.makeText(this, "Upoad feito com sucesso", Toast.LENGTH_SHORT).show();
        })
        .addOnFailureListener(e -> {
             e.printStackTrace();
        });

        //storage.getReference().putBytes();

    }

}
