package com.cursoandroid.organizze.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cursoandroid.organizze.R
import com.cursoandroid.organizze.config.ConfiguracaoFirebase
import com.cursoandroid.organizze.helper.Base64Custom
import com.cursoandroid.organizze.model.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlinx.android.synthetic.main.activity_cadastro.*

class CadastroActivity : AppCompatActivity() {

    private lateinit var autenticacao: FirebaseAuth
    private lateinit var usuario: Usuario

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro)

        buttonCadastrar.setOnClickListener {
            val textoNome = editNomeCadastro.text.toString()
            val textoEmail = editEmailCadastro.text.toString()
            val textoSenha = editSenhaCadastro.text.toString()

            //Validar se o campos foram preenchidos
            if (textoNome.isNotEmpty()) {
                if (textoEmail.isNotEmpty()) {
                    if (textoSenha.isNotEmpty()) {

                        this.usuario = Usuario().apply {
                            nome = textoNome
                            email = textoEmail
                            senha = textoSenha
                        }
                        cadastrarUsuario()

                    } else {
                        Toast.makeText(this, "Preencha a senha", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Preencha o email", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Preencha o nome!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun cadastrarUsuario() {

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao()
        autenticacao.createUserWithEmailAndPassword(usuario.email, usuario.senha)
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    val idUsuario = Base64Custom.codificarBase64(usuario.email)
                    usuario.idUsuario = idUsuario
                    usuario.salvar()
                    finish()

                }else {
                    val execao: String
                    try {
                        throw task.exception!!
                    } catch (e: FirebaseAuthWeakPasswordException) {
                        execao = "Digite uma senha mais forte!"
                    } catch (e: FirebaseAuthInvalidCredentialsException) {
                        execao = "Por favor, digite um email válido!"
                    } catch (e: FirebaseAuthUserCollisionException) {
                        execao = "Essa conta já foi cadastrada!"
                    } catch (e: Exception) {
                        execao = "Erro ao cadastrar usuário!" + e.message
                        e.printStackTrace()
                    }

                    Toast.makeText(applicationContext, execao, Toast.LENGTH_SHORT).show()
                }
            }
    }
}

