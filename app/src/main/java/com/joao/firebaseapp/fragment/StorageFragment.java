package com.joao.firebaseapp.fragment;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.joao.firebaseapp.R;
import com.joao.firebaseapp.model.Upload;
import com.joao.firebaseapp.util.LoadingDialog;

import java.io.ByteArrayOutputStream;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class StorageFragment extends Fragment {

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private Button btnupload, btnGaleria;
    private ImageView imageView;
    private Uri imageUri=null;
    private EditText editNome;
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    // Referência para um Nó RealtTimeDB
    private DatabaseReference database = FirebaseDatabase.getInstance().getReference("uploads");


    public StorageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        database = FirebaseDatabase.getInstance().getReference("uploads").child(auth.getUid());

        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_storage, container, false);

        editNome= layout.findViewById(R.id.storage_edit_nome);
        btnupload = layout.findViewById(R.id.storage_btn_upload);
        btnGaleria = layout.findViewById(R.id.storage_btn_galeria);
        imageView = layout.findViewById(R.id.storage_image_cel);

        btnupload.setOnClickListener(v -> {
            if (editNome.getText().toString().isEmpty()){
                Toast.makeText(getActivity(), "Digite um nome para Imagem", Toast.LENGTH_SHORT).show();
                return;
            }
            if (imageUri != null) {
                uploadImagemUri();
            } else {
                uploadImagemByte();
            }
        });

        btnGaleria.setOnClickListener(v -> {
            Intent intent = new Intent();
            //intent implicita -> pegar um arquivo do celular
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            // Inicia uma Activity, e espera o retorno(Foto)
            startActivityForResult(intent,112);
        });

        return layout;
    }

    private void uploadImagemUri() {

        LoadingDialog dialog = new LoadingDialog(getActivity(), R.layout.custom_dialog);
        dialog.startLoadingDialog();

        String tipo = getFileExtesion(imageUri);
        //Referencia do arquivo no firebase
        Date d = new Date();
        String nome = editNome.getText().toString();
        //Criando Referencia da imagem no Storage
        StorageReference imagemRef = storage.getReference().child("imagens/"+nome+"-"+d.getTime()+"."+tipo);

        imagemRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            Toast.makeText(getActivity(), "Upload feito com Sucesso ", Toast.LENGTH_SHORT).show();

            //Inserir dados da imagem no RealtimeDatabase

            //pegar a url da imagem
            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                // Inserir no database

                // Criando refere(database) do (Upload)
                DatabaseReference refUpload = database.push();
                String id = refUpload.getKey();

                Upload upload = new Upload(id,nome,uri.toString());
                //salvando upLoad no DB
                refUpload.setValue(upload)
                        .addOnSuccessListener(aVoid -> {
                            dialog.dismissDialog();
                            Toast.makeText(getActivity(), "Upload Sucesso!", Toast.LENGTH_SHORT).show();

                            NavController navController = Navigation.findNavController(getActivity(),R.id.nav_host_fragment);

                            //Voltar para fragment incial
                            navController.navigateUp();
                        });
            });
        }).addOnFailureListener(e -> {
            e.printStackTrace();
        });

    }

    //Retorna o tipo (.png, .jpg) da image,
    private String getFileExtesion(Uri imageUri) {
        ContentResolver cr = getActivity().getContentResolver();
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(cr.getType(imageUri));
    }

    // Resultado da startActivityResult()
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("RESULT", "requestCode: "+ resultCode + ",resultCode: "+ resultCode);

        if (requestCode== 112 && resultCode== Activity.RESULT_OK){
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
                    Toast.makeText(getActivity(), "Upload feito com sucesso", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                });

        //storage.getReference().putBytes();

    }




}
