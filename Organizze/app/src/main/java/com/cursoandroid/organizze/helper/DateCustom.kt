package com.cursoandroid.organizze.helper

import java.text.SimpleDateFormat

class DateCustom {

    companion object{
        fun dataAtual(): String{
            val data = System.currentTimeMillis()
            val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
            return simpleDateFormat.format(data)
        }
        fun mesAno(data: String): String {
            val retorno = data.split("/")
            return retorno[1] + retorno[2]
        }
    }
}