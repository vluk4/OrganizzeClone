package com.cursoandroid.organizze.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.cursoandroid.organizze.R
import com.cursoandroid.organizze.config.ConfiguracaoFirebase
import com.cursoandroid.organizze.helper.Base64Custom
import com.cursoandroid.organizze.helper.DateCustom
import com.cursoandroid.organizze.model.Movimentacao
import com.cursoandroid.organizze.model.Usuario
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_despesas.*

class DespesasActivity : AppCompatActivity() {

    private lateinit var movimentacao: Movimentacao
    private val firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase()
    private val autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao()
    private var despesaTotal: Double = 0.00
    private var despesaAtualizada: Double = 0.00

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_despesas)

        //Preenche o campo data com a data atual
        editData.setText(DateCustom.dataAtual())
        Log.i("INFO", editData.text.toString())

        recuperarDespesaTotal()
    }

    fun salvarDespesa(view: View) {
        if (validarCamposDespesa()) {

            val valorRecuperado = textValor.text.toString().toDouble()

            movimentacao = Movimentacao().apply {
                valor = valorRecuperado
                categoria = editCategoria.text.toString()
                descricao = editDescricao.text.toString()
                data = editData.text.toString()
                tipo = "d"
            }

            despesaAtualizada = despesaTotal + valorRecuperado
            atualizarDespesa(despesaAtualizada)

            movimentacao.salvar()

            finish()
        }
    }

    fun validarCamposDespesa(): Boolean {

        val textoValor = textValor.text.toString()
        val textoData = editData.text.toString()
        val textoDescricao = editDescricao.text.toString()
        val textoCategoria = editCategoria.text.toString()

        //Validar se o campos foram preenchidos
        if (textoValor.isNotEmpty()) {
            if (textoData.isNotEmpty()) {
                if (textoCategoria.isNotEmpty()) {
                    if (textoDescricao.isNotEmpty()) {
                        return true
                    } else {
                        Toast.makeText(this, "Preencha a descrição", Toast.LENGTH_SHORT).show()
                        return false
                    }
                } else {
                    Toast.makeText(this, "Preencha a categoria", Toast.LENGTH_SHORT).show()
                    return false
                }
            } else {
                Toast.makeText(this, "Preencha a data", Toast.LENGTH_SHORT).show()
                return false
            }
        } else {
            Toast.makeText(this, "Preencha o valor", Toast.LENGTH_SHORT).show()
            return false
        }
    }

    fun recuperarDespesaTotal() {

        val emailUsuario = autenticacao.currentUser!!.email
        val idUsuario = Base64Custom.codificarBase64(emailUsuario.toString())
        val usuarioRef = firebaseRef.child("usuarios")
            .child(idUsuario)

        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val usuario = snapshot.getValue(Usuario::class.java)
                despesaTotal = usuario!!.despesaTotal
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }

        usuarioRef.addValueEventListener(postListener)
    }

    fun atualizarDespesa(despesa: Double) {

        val emailUsuario = autenticacao.currentUser!!.email
        val idUsuario = Base64Custom.codificarBase64(emailUsuario.toString())

        val usuarioRef = firebaseRef.child("usuarios")
            .child(idUsuario)

        usuarioRef.child("despesaTotal").setValue(despesa)
    }
}