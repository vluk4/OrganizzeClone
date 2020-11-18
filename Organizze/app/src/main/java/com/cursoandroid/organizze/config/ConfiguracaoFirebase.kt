package com.cursoandroid.organizze.config

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ConfiguracaoFirebase {

    companion object{

        var autenticacao: FirebaseAuth? = null
        private var firebase: DatabaseReference? = null

        //Retorna a instancia do FirebaseAuth
        fun getFirebaseAutenticacao(): FirebaseAuth{
            if (autenticacao == null){
                autenticacao = FirebaseAuth.getInstance()
            }
            return autenticacao as FirebaseAuth
        }

        fun getFirebaseDatabase(): DatabaseReference{
            if (firebase == null){
                firebase = FirebaseDatabase.getInstance().reference
            }
            return firebase as DatabaseReference
        }
    }

}