package com.joao.firebaseapp.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.joao.firebaseapp.R;
import com.joao.firebaseapp.adapter.UserAdapter;
import com.joao.firebaseapp.model.User;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {

    private RecyclerView recyclerContatos;
    private UserAdapter userAdapter;
    private DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
    private DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference("requests");
    private User userLogged;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private ArrayList<User> listaContatos = new ArrayList<>();

    public MainFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_main, container, false);
        userLogged = new User(
                auth.getCurrentUser().getUid(),
                auth.getCurrentUser().getEmail(),
                auth.getCurrentUser().getDisplayName()
        );

        recyclerContatos = layout.findViewById(R.id.frag_main_recycler_user);

        userAdapter = new  UserAdapter(getContext(),listaContatos);

        userAdapter.setListener(new UserAdapter.ClickAdapterUser() {
            @Override
            public void adicionarContato(int position) {
                User u = listaContatos.get(position);

                requestRef.child(userLogged.getId())
                        .child("send")
                        .child(u.getId())
                        .setValue(u);
                // Request receive
                requestRef.child(u.getId())
                        .child("receive")
                        .child(userLogged.getId())
                        .setValue(userLogged);

                // tirar o usuario solicitado
                listaContatos.get(position).setReceiveRequest(true);
                userAdapter.notifyDataSetChanged();
            }
        });

        recyclerContatos.setLayoutManager( new LinearLayoutManager(getContext()));

        recyclerContatos.setAdapter(userAdapter);

        return layout;
    }

    public void onStart(){
        super.onStart();
        getUsersDatabase();
    }

    public void getUsersDatabase(){
        usersRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
            listaContatos.clear();

            for(DataSnapshot filho : snapshot.getChildren()){
                User u = filho.getValue(User.class);
                // Comparar com u
                if(!userLogged.equals(u)){
                    /*if (cont%2==0){
                        u.setReceiveRequest(true);
                    }else {
                        u.setReceiveRequest(false);
                    }*/
                    listaContatos.add(u);
                }

            }

            //verifivar quais contatos ja foram adicionados
            requestRef
                    .child(userLogged.getId()).child("send").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot no_filho : snapshot.getChildren() ){
                        User usuarioSolicitado = no_filho.getValue(User.class);
                        for(int i= 0; i< listaContatos.size(); i++){
                            if(listaContatos.get(i).equals(usuarioSolicitado)){
                                listaContatos.get(i).setReceiveRequest(true);
                            }
                        }
                    }
                    userAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}










