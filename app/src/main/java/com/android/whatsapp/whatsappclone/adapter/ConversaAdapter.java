package com.android.whatsapp.whatsappclone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.whatsapp.whatsappclone.R;
import com.android.whatsapp.whatsappclone.model.Contato;
import com.android.whatsapp.whatsappclone.model.Conversa;

import java.util.ArrayList;

public class ConversaAdapter extends ArrayAdapter<Conversa> {

    private ArrayList<Conversa> conversas;
    private Context context;

    public ConversaAdapter( Context c, ArrayList<Conversa> objects) {
        super(c, 0, objects);
        this.context = c;
        this.conversas = objects;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;

        if (conversas!= null){
            //inicializar o objeto para montagem da view
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            view = inflater.inflate(R.layout.lista_conversa, parent, false);

            //recupera elementos para exibição
            TextView nome = (TextView) view.findViewById( R.id.tv_titulo);
            TextView ultimamensagem = (TextView) view.findViewById( R.id.tv_subtitulo);

            Conversa conversa = conversas.get(position);
            nome.setText(conversa.getNome());
            ultimamensagem.setText(conversa.getMensagem());
        }

        return view;
    }
}
