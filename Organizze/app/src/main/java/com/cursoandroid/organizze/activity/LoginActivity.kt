package com.cursoandroid.organizze.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.cursoandroid.organizze.R
import com.cursoandroid.organizze.config.ConfiguracaoFirebase
import com.cursoandroid.organizze.model.Usuario
import com.google.firebase.auth.*
import kotlinx.android.synthetic.main.activity_cadastro.*
import kotlinx.android.synthetic.main.activity_login.*
import java.lang.Exception

class LoginActivity : AppCompatActivity() {

    private lateinit var usuario: Usuario
    private lateinit var autenticacao: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        buttonEntrar.setOnClickListener {

            val textoEmail = editNomeAcesso.text.toString()
            val textoSenha = editSenhaAcesso.text.toString()

            if (textoEmail.isNotEmpty()) {
                if (textoSenha.isNotEmpty()) {
                    usuario = Usuario().apply {
                        email = textoEmail
                        senha = textoSenha
                    }
                    validarLogin()

                } else {
                    Toast.makeText(this, "Preencha a senha", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Preencha o email", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun validarLogin() {
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao()
        autenticacao.signInWithEmailAndPassword(usuario.email, usuario.senha)
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    abrirTelaPrincipal()

                } else {
                    var execao: String = ""
                    try {
                        throw task.exception!!
                    } catch (e: FirebaseAuthInvalidUserException) {
                        execao = "Usuário não está cadastrado."
                    } catch (e: FirebaseAuthInvalidCredentialsException) {
                        execao = "Email e senha não correspodem ao usuário cadastradp"
                    } catch (e: Exception) {
                        execao = "Erro ao fazer login" + e.message
                        e.printStackTrace()
                    }

                    Toast.makeText(applicationContext, execao, Toast.LENGTH_SHORT).show()
                }

            }
    }

    private fun abrirTelaPrincipal() {
        startActivity(Intent(applicationContext, PrincipalActivity::class.java))
        finish()
    }
}