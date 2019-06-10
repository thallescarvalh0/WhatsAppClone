package com.android.whatsapp.whatsappclone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.whatsapp.whatsappclone.R;
import com.android.whatsapp.whatsappclone.model.Mensagem;
import com.android.whatsapp.whatsappclone.helpers.Preferencias;

import java.util.ArrayList;

public class MensagemAdapter extends ArrayAdapter<Mensagem> {

    private Context context;
    private ArrayList<Mensagem> mensagens;

    public MensagemAdapter(Context c, ArrayList<Mensagem> objects) {
        super(c, 0,objects);
        this.context = c;
        this.mensagens= objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;

        //verificar se a lista está preenchida
        if(mensagens != null){
            //inicialliza objeto par amontagem layout

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            //recupera dados do usuário remetente
            Preferencias preferencias = new Preferencias(context);
            String idUsuarioRemetente = preferencias.getIdentificador();

            //recuperar mensagem
            Mensagem mensagem = mensagens.get(position);

            //monta view
            if (idUsuarioRemetente.equals(mensagem.getIdUsuario()))
            {
                view = inflater.inflate(R.layout.item_layout_direita, parent, false);
            }else{
                view = inflater.inflate(R.layout.item_layout_esquerda, parent, false);
            }


            TextView textoMensagem = (TextView) view.findViewById(R.id.tv_mensagem);
            textoMensagem.setText(mensagem.getMensagem());
        }

        return view;

    }
}
