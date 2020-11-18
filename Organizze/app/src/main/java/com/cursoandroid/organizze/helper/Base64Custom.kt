package com.cursoandroid.organizze.helper

import android.util.Base64

class Base64Custom {

    companion object{

        fun codificarBase64(texto: String): String{
            return Base64.encodeToString(texto.encodeToByteArray(), Base64.NO_WRAP).replace("(\\n|\\r)", "")
        }

        fun decodificarBase64(textoCodificado: String): String{
            return Base64.decode(textoCodificado, Base64.NO_WRAP).toString()
        }

    }
}