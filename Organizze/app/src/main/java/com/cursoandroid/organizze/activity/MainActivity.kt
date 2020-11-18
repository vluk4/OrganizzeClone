package com.cursoandroid.organizze.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.cursoandroid.organizze.R
import com.cursoandroid.organizze.config.ConfiguracaoFirebase
import com.google.firebase.auth.FirebaseAuth
import com.heinrichreimersoftware.materialintro.app.IntroActivity
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide

class MainActivity : IntroActivity() {

    private lateinit var autenticacao: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)

        isButtonBackVisible = false
        isButtonNextVisible = false

        addSlide(
            FragmentSlide.Builder()
                .background(android.R.color.white)
                .fragment(R.layout.intro_1)
                .build()
        )

        addSlide(
            FragmentSlide.Builder()
                .background(android.R.color.white)
                .fragment(R.layout.intro_2)
                .build()
        )

        addSlide(
            FragmentSlide.Builder()
                .background(android.R.color.white)
                .fragment(R.layout.intro_3)
                .build()
        )

        addSlide(
            FragmentSlide.Builder()
                .background(android.R.color.white)
                .fragment(R.layout.intro_4)
                .build()
        )

        addSlide(
            FragmentSlide.Builder()
                .background(android.R.color.white)
                .fragment(R.layout.intro_cadastro)
                .canGoForward(false)
                .build()
        )
    }

    fun btEntrar(view: View) {
        startActivity(Intent(this, LoginActivity::class.java))
    }

    fun btCadastrar(view: View) {
        startActivity(Intent(this, CadastroActivity::class.java))
    }

    fun verificarUsuarioLogado() {
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao()
        //autenticacao.signOut()
        if (autenticacao.currentUser != null) {
            abrirTelaPrincipal()
        }
    }

    private fun abrirTelaPrincipal() {
        startActivity(Intent(applicationContext, PrincipalActivity::class.java))
    }

    override fun onStart() {
        super.onStart()
        verificarUsuarioLogado()
    }
}