package com.android.whatsapp.whatsappclone.activity.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.whatsapp.whatsappclone.R;
import com.android.whatsapp.whatsappclone.config.ConfiguracaoFirebase;
import com.android.whatsapp.whatsappclone.helpers.Base64Custom;
import com.android.whatsapp.whatsappclone.helpers.Preferencias;
import com.android.whatsapp.whatsappclone.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;

public class CadastroUsuarioActivity extends AppCompatActivity {

    private EditText nome;
    private EditText email;
    private EditText senha;
    private Button botaoCadastrar;
    private Usuario usuario;

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_usuario);

        nome = (EditText) findViewById(R.id.edit_cadastro_nome);
        email = (EditText) findViewById(R.id.edit_cadastro_email);
        senha = (EditText) findViewById(R.id.edit_cadastro_senha);
        botaoCadastrar = (Button) findViewById(R.id.botao_cadastrar);

        botaoCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ((email.getText().toString().equals(""))||(senha.getText().toString().equals(""))||(nome.getText().toString().equals(""))){
                    Toast.makeText(getApplicationContext(), "O campo e-mail, senha ou nome não podem ser vazios !", Toast.LENGTH_SHORT).show();
                }else {

                    usuario = new Usuario();
                    usuario.setNome(nome.getText().toString());
                    usuario.setEmail(email.getText().toString());
                    usuario.setSenha(senha.getText().toString());

                    cadastrarUsuario();
                }
            }
        });
    }
    private void cadastrarUsuario(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        autenticacao.createUserWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(CadastroUsuarioActivity.this, new OnCompleteListener<AuthResult>() {
            @Override //metodo que verifica se relamente foi feito o cadastro do usuario
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    Toast.makeText(CadastroUsuarioActivity.this, "Sucesso ao cadastrar", Toast.LENGTH_SHORT).show();

                    String identificarUsuario = Base64Custom.codificarBase64(usuario.getEmail());
                    usuario.setId(identificarUsuario); // precupenrado id cadastrado

                    usuario.salvar();


                    Preferencias preferencias = new Preferencias(CadastroUsuarioActivity.this);
                    preferencias.salvarDados(identificarUsuario, usuario.getNome());

                   abrirLoginUsuario();

                }else{

                    String erroException = "";
                    try{
                        throw task.getException(); //lançar exception
                    } catch (FirebaseAuthWeakPasswordException e) {
                        erroException = "Digite uma senha mais forte, contendo mais caracteres! ";
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        erroException = "O e-mail digitado é inválido, digite um novo e-mail! ";
                    } catch (FirebaseAuthUserCollisionException e) {
                        erroException = "O usuário já está em uso! ";
                    } catch (Exception e) {
                        erroException = "Erro ao efetuar cadastro! ";
                        e.printStackTrace();
                    }

                    Toast.makeText(CadastroUsuarioActivity.this, "Falha ao cadastrar: "+erroException, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void abrirLoginUsuario(){
        Intent intent = new Intent(CadastroUsuarioActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();

        //autenticacao.signOut();// método antigo para slavar os dados o usuário precisar estar logado
        //finish();
    }
}
