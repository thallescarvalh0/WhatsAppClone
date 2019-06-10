package com.android.whatsapp.whatsappclone.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.whatsapp.whatsappclone.R;
import com.android.whatsapp.whatsappclone.activity.activity.ConversaActivity;
import com.android.whatsapp.whatsappclone.adapter.ContatoAdapter;
import com.android.whatsapp.whatsappclone.adapter.ConversaAdapter;
import com.android.whatsapp.whatsappclone.config.ConfiguracaoFirebase;
import com.android.whatsapp.whatsappclone.helpers.Base64Custom;
import com.android.whatsapp.whatsappclone.helpers.Preferencias;
import com.android.whatsapp.whatsappclone.model.Contato;
import com.android.whatsapp.whatsappclone.model.Conversa;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConversasFragment extends Fragment {

    private ListView listView;

    private ArrayAdapter<Conversa> adapter;
    private ArrayList<Conversa> conversas;

    private DatabaseReference firebase;
    private ValueEventListener valueEventListenerConversas;


    public ConversasFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_conversas, container, false);

        //instanciar objetos
        conversas = new ArrayList<>();

        //monta uma list view

        listView = (ListView) view.findViewById(R.id.lv_conversas);

        adapter = new ConversaAdapter(getActivity(), conversas);
        listView.setAdapter(adapter);

        //recuperar as conversas cadastrados par ao usuario logado

        Preferencias preferencias = new Preferencias((getActivity()));
        String idUsuarioLogado = preferencias.getIdentificador();

        firebase = ConfiguracaoFirebase.getFirebase()
                .child("conversas")
                .child(idUsuarioLogado);

        valueEventListenerConversas = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //limpar lista
                conversas.clear(); //para não acumular conversas

                //listar contatos
                for (DataSnapshot dados: dataSnapshot.getChildren()){//percorre os filhos dos dados que temos na estrutura do firebase
                    Conversa conversa = dados.getValue(Conversa.class);
                    conversas.add(conversa);
                }

                adapter.notifyDataSetChanged(); //definimos para o adapter que os dados mudaram
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getActivity(), ConversaActivity.class);

                //recuperar dados a serem passados par aactivity
                Conversa conversa = conversas.get(position);

                //enviando dados para conversasactivitu

                intent.putExtra("nome", conversa.getNome());
                String email = Base64Custom.decodificarBase64(conversa.getIdUsuario());
                intent.putExtra("email", email);

                startActivity(intent);


            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        firebase.addValueEventListener(valueEventListenerConversas);
    }

    @Override
    public void onStop() {
        super.onStop();
        firebase.removeEventListener(valueEventListenerConversas);
    }
}
