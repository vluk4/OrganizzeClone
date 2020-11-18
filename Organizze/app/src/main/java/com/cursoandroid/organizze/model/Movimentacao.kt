package com.cursoandroid.organizze.model

import com.cursoandroid.organizze.config.ConfiguracaoFirebase
import com.cursoandroid.organizze.helper.Base64Custom
import com.cursoandroid.organizze.helper.DateCustom
import com.google.firebase.auth.FirebaseAuth

data class Movimentacao(var data:String = "", var categoria: String = "", var descricao: String = "", var tipo: String = "", var valor: Double = 0.00, var key: String = "") {
    fun salvar() {

        val firebaseReference = ConfiguracaoFirebase.getFirebaseDatabase()

        val autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao()
        val idUsuario = Base64Custom.codificarBase64(autenticacao.currentUser?.email!!)
        val mesAno = DateCustom.mesAno(data)

        firebaseReference.child("movimentacao")
            .child(idUsuario)
            .child(mesAno)
            .push()
            .setValue(this)
    }

}