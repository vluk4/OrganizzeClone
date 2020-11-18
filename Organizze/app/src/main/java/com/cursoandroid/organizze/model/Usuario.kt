package com.cursoandroid.organizze.model

import android.util.Log
import android.widget.Toast
import com.cursoandroid.organizze.activity.PrincipalActivity
import com.cursoandroid.organizze.config.ConfiguracaoFirebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Exclude

class Usuario {
    private var _idUsuario: String = ""
    var idUsuario: String
        @Exclude get() = _idUsuario
        set(value) {_idUsuario = value}

    private var _nome: String = ""
    var nome: String
        get() = _nome
        set(value) {_nome = value}

    private var _email: String = ""
    var email: String
        get() = _email
        set(value) {_email = value}

    private var _senha: String = ""
    var senha: String
        @Exclude get() = _senha
        set(value) {_senha = value}

    private var _receitaTotal: Double = 0.00
    var receitaTotal: Double
        get() = _receitaTotal
        set(value) {_receitaTotal = value}

    private var _despesaTotal: Double = 0.00
    var despesaTotal: Double
        get() = _despesaTotal
        set(value) {_despesaTotal = value}

    fun salvar(){
        val firebase: DatabaseReference = ConfiguracaoFirebase.getFirebaseDatabase()
        firebase.child("usuarios")
            .child(this.idUsuario)
            .setValue(this)
        Log.d("salvar", "Salvo com sucesso!")
    }
}