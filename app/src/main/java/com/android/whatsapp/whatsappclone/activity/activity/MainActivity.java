package com.android.whatsapp.whatsappclone.activity.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.whatsapp.whatsappclone.R;
import com.android.whatsapp.whatsappclone.adapter.TabAdapter;
import com.android.whatsapp.whatsappclone.config.ConfiguracaoFirebase;
import com.android.whatsapp.whatsappclone.helpers.Base64Custom;
import com.android.whatsapp.whatsappclone.helpers.Preferencias;
import com.android.whatsapp.whatsappclone.helpers.SlidingTabLayout;
import com.android.whatsapp.whatsappclone.model.Contato;
import com.android.whatsapp.whatsappclone.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private android.support.v7.widget.Toolbar toolbar;
    private FirebaseAuth usuarioAutenticacao;

    private SlidingTabLayout slidingTabLayout;
    private ViewPager viewPager;
    private String identificadorContato;
    private DatabaseReference referenciaFirebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usuarioAutenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.stl_tabs);
        viewPager = (ViewPager) findViewById(R.id.vp_pagina);
        toolbar.setTitle("WhatsApp");
        setSupportActionBar(toolbar); //suporte para action bar

        //configurar sliding tab
        slidingTabLayout.setDistributeEvenly(true); // configurar par apreencher toda tela
        slidingTabLayout.setSelectedIndicatorColors(ContextCompat.getColor(this,R.color.colorAccent)); //configurar indicador de cor dos itens selecionados


        //configurar adapter para recuperar os fragmentos para a view pager

        TabAdapter tabAdapter = new TabAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabAdapter);

        slidingTabLayout.setViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater(); // classe utilizada para exibir menus em tela
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {// método para retornar item que foi selecionado na toolbar

        switch (item.getItemId()){
            case R.id.item_sair:
                deslogarUsuario();
                return true;
            case R.id.item_configuracoes:
                return true;
            case R.id.item_adicionar:
                abrirCadastroContato();
                return true;
            case R.id.item_pesquisa:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void abrirCadastroContato(){

        AlertDialog.Builder alertDialog  = new AlertDialog.Builder(MainActivity.this);

        //configurar dialog
        alertDialog.setTitle("Novo Contato");
        alertDialog.setMessage("E-mail do usuário: ");
        alertDialog.setCancelable(false);

        final EditText editText = new EditText(MainActivity.this); //colocar objseto view no dialog

        alertDialog.setView(editText);

        //configura botões

        alertDialog.setPositiveButton("Cadastrar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String emailContato = editText.getText().toString();

                //valida se user digitou
                if(emailContato.isEmpty()){
                    Toast.makeText(MainActivity.this, "Preencha o e-mail", Toast.LENGTH_SHORT).show();
                }
                else{
                    //verificar se o user está cadastrado no app
                    identificadorContato = Base64Custom.codificarBase64(emailContato);

                    //recuperar instancia firebase para consulta

                    referenciaFirebase = ConfiguracaoFirebase.getFirebase();

                    //consulta para ver se existe, abrindo uma nova instancia do firebase

                    referenciaFirebase = referenciaFirebase.child("usuarios").child(identificadorContato);

                    referenciaFirebase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if(dataSnapshot.getValue() != null){

                                //recuperar dados do contato a ser adicionado

                                Usuario usuarioContato = dataSnapshot.getValue(Usuario.class); // recebe retorno de um obj com os dados no firebase

                                //recuperar identificador

                                Preferencias preferencias = new Preferencias(MainActivity.this);
                                String identificadorUsuario = preferencias.getIdentificador();

                                //criar nova referencia firebase para armaezenar contatos
                                referenciaFirebase = ConfiguracaoFirebase.getFirebase();
                                referenciaFirebase = referenciaFirebase.child("contatos")
                                                                       .child(identificadorUsuario)
                                                                       .child(identificadorContato);

                                Contato contato = new Contato();
                                contato.setIdentificadorUsuario(identificadorContato);
                                contato.setEmail(usuarioContato.getEmail());
                                contato.setNome(usuarioContato.getNome());

                                referenciaFirebase.setValue(contato);

                            }else{
                                Toast.makeText(MainActivity.this, "Usuário não possui cadastro !", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });

        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialog.create();
        alertDialog.show();
    }

    private void deslogarUsuario(){
        usuarioAutenticacao.signOut();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
