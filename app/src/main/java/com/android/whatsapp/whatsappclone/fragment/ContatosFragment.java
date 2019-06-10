package com.android.whatsapp.whatsappclone.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.whatsapp.whatsappclone.R;
import com.android.whatsapp.whatsappclone.activity.activity.ConversaActivity;
import com.android.whatsapp.whatsappclone.adapter.ContatoAdapter;
import com.android.whatsapp.whatsappclone.config.ConfiguracaoFirebase;
import com.android.whatsapp.whatsappclone.helpers.Preferencias;
import com.android.whatsapp.whatsappclone.model.Contato;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.prefs.PreferenceChangeEvent;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContatosFragment extends Fragment {

    private ListView listView;
    private ArrayAdapter adapter;
    private ArrayList<Contato> contatos;
    private DatabaseReference firebase;
    private ValueEventListener valueEventListenerContatos;

    public ContatosFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        firebase.addValueEventListener(valueEventListenerContatos);
        Log.i("valueEventListener", "onStart");
    }

    @Override
    public void onStop() {
        super.onStop();
        firebase.removeEventListener(valueEventListenerContatos);
        Log.i("valueEventListener", "onStop");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        //instanciar objetos
        contatos = new ArrayList<>();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contatos, container, false);

        listView = (ListView) view.findViewById(R.id.lv_contatos);
        /*adapter = new ArrayAdapter(
                getActivity(),
                R.layout.lista_contato,
                contatos
        );*/

        adapter = new ContatoAdapter(getActivity(), contatos);
        listView.setAdapter(adapter);

        //recuperar os contatos cadastrados par ao usuario logado

        Preferencias preferencias = new Preferencias((getActivity()));
        String identificadorLogado = preferencias.getIdentificador();

        firebase = ConfiguracaoFirebase.getFirebase()
                   .child("contatos")
                   .child(identificadorLogado);

        valueEventListenerContatos = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //limpar lista
                contatos.clear(); //para não acumular contatos

                //listar contatos
                for (DataSnapshot dados: dataSnapshot.getChildren()){//percorre os filhos dos dados que temos na estrutura do firebase
                    Contato contato = dados.getValue(Contato.class);
                    contatos.add(contato);
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
                Contato contato = contatos.get(position);

                //enviando dados para conversasactivitu

                intent.putExtra("nome", contato.getNome());
                intent.putExtra("email", contato.getEmail());

                startActivity(intent);


            }
        });

        return view;
    }

}
