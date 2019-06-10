package com.android.whatsapp.whatsappclone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.whatsapp.whatsappclone.R;
import com.android.whatsapp.whatsappclone.model.Contato;

import java.util.ArrayList;
import java.util.List;

public class ContatoAdapter extends ArrayAdapter<Contato> {

    private ArrayList<Contato> contatos;
    private Context context;

    public ContatoAdapter(Context c,ArrayList<Contato> objects) {
        super(c,0, objects);
        this.contatos = objects;
        this.context = c;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;

        //verifica se a lista está vazia
        if(contatos != null){
            //inicializar o objeto para montagem da view
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            //montar a view a partir do xml

            view = inflater.inflate(R.layout.lista_contato, parent, false);

            //recupera elemento para exibição

            TextView nomeContato = (TextView) view.findViewById(R.id.tv_nome);
            TextView emailContato = (TextView) view.findViewById(R.id.tv_email);

            Contato contato = contatos.get(position);
            nomeContato.setText(contato.getNome());
            emailContato.setText(contato.getEmail());

        }

        return view;
    }
}
