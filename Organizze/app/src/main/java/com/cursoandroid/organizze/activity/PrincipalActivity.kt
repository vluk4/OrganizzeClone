package com.cursoandroid.organizze.activity

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cursoandroid.organizze.R
import com.cursoandroid.organizze.adapter.MyAdapter
import com.cursoandroid.organizze.config.ConfiguracaoFirebase
import com.cursoandroid.organizze.helper.Base64Custom
import com.cursoandroid.organizze.model.Movimentacao
import com.cursoandroid.organizze.model.Usuario
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.content_principal.*
import java.text.DecimalFormat


class PrincipalActivity : AppCompatActivity() {

    private lateinit var valueEventListenerUsuario: ValueEventListener
    private lateinit var valueEventListenerMovimentacoes: ValueEventListener

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    private val movimentacoes = mutableListOf<Movimentacao>()

    private val autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao()
    private val firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase()
    private lateinit var usuarioRef: DatabaseReference
    private lateinit var movimentacaoRef: DatabaseReference

    private var despesaTotal: Double = 0.0
    private var receitaTotal: Double = 0.0
    private var resumoUsuario: Double = 0.0
    private lateinit var mesAnoSelecionado: String
    private lateinit var movimentacao: Movimentacao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)
        setSupportActionBar(findViewById(R.id.toolbar))

        recyclerView = findViewById(R.id.recyclerMovimentos)
        configuraCaledarView()
        swipe()

        viewAdapter = MyAdapter(movimentacoes, applicationContext)
        viewManager = LinearLayoutManager(this)

        recyclerView = recyclerMovimentos.apply {
            adapter = viewAdapter
            layoutManager = viewManager
            setHasFixedSize(true)
        }
    }

    private fun swipe() {
        val itemTouch: ItemTouchHelper.Callback = object : ItemTouchHelper.Callback() {
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                val dragFlags = ItemTouchHelper.ACTION_STATE_IDLE
                val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
                return makeMovementFlags(dragFlags, swipeFlags)
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                excluirMovimentacao(viewHolder)
            }
        }
        ItemTouchHelper(itemTouch).attachToRecyclerView(recyclerView)
    }

    fun excluirMovimentacao(viewHolder: RecyclerView.ViewHolder) {
        val alertDialog = AlertDialog.Builder(this)

        //Configura AlertDialog
        alertDialog.setTitle("Excluir Movimentação da Conta")
        alertDialog.setMessage("Você tem certeza que deseja realmente excluir essa movimentação de sua conta?")
        alertDialog.setCancelable(false)
        alertDialog.setPositiveButton("Confirmar") { dialog, which ->
            val position = viewHolder.adapterPosition
            movimentacao = movimentacoes[position]
            val emailUsuario = autenticacao.currentUser!!.email
            val idUsuario = Base64Custom.codificarBase64(emailUsuario!!)
            movimentacaoRef = firebaseRef.child("movimentacao")
                .child(idUsuario)
                .child(mesAnoSelecionado)
            movimentacaoRef.child(movimentacao.key).removeValue()
            viewAdapter.notifyItemRemoved(position)
            atualizarSaldo()
        }
        alertDialog.setNegativeButton("Cancelar") { dialog, which ->
            Toast.makeText(
                this@PrincipalActivity,
                "Cancelado",
                Toast.LENGTH_SHORT
            ).show()
            viewAdapter.notifyDataSetChanged()
        }
        val alert: AlertDialog = alertDialog.create()
        alert.show()
    }

    private fun atualizarSaldo(){
        val emailUsuario = autenticacao.currentUser!!.email
        val idUsuario = Base64Custom.codificarBase64(emailUsuario.toString())
        usuarioRef = firebaseRef.child("usuarios")
            .child(idUsuario)

        if (movimentacao.tipo == "r"){
            receitaTotal -= movimentacao.valor
            usuarioRef.child("receitaTotal").setValue(receitaTotal)
        }

        if (movimentacao.tipo == "d"){
            despesaTotal -= movimentacao.valor
            usuarioRef.child("despesaTotal").setValue(despesaTotal)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_principal, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_sair -> {
                autenticacao.signOut()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }

        }
        return super.onOptionsItemSelected(item)
    }

    private fun recuperaResumo(){
        val emailUsuario = autenticacao.currentUser!!.email
        val idUsuario = Base64Custom.codificarBase64(emailUsuario.toString())
        usuarioRef = firebaseRef.child("usuarios")
            .child(idUsuario)

        valueEventListenerUsuario = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val usuario = snapshot.getValue(Usuario::class.java)

                despesaTotal = usuario!!.despesaTotal
                receitaTotal = usuario.receitaTotal
                resumoUsuario = receitaTotal - despesaTotal

                val decimalFormat = DecimalFormat("0.##")
                val resultadoFormatado = decimalFormat.format(resumoUsuario)

                textSaldo.text = "R$ $resultadoFormatado"
                textSaudacao.text = "Olá, ${usuario.nome}"

                Log.i("INFO", "sadjoiadjasjd")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        usuarioRef.addValueEventListener(valueEventListenerUsuario)
        Log.i("Evento", "evento foi adicionado!")
    }

    private fun recuperarMovimentacoes(){

        val emailUsuario = autenticacao.currentUser!!.email
        val idUsuario = Base64Custom.codificarBase64(emailUsuario.toString())

        movimentacaoRef = firebaseRef.child("movimentacao")
            .child(idUsuario)
            .child(mesAnoSelecionado)
        Log.i("dado", mesAnoSelecionado)


        valueEventListenerMovimentacoes = object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                movimentacoes.clear()

                for (dados in snapshot.children) {
                    val movimentacao = dados.getValue(Movimentacao::class.java)
                    movimentacoes.add(movimentacao!!)
                    movimentacao.key = dados.key.toString()
                    Log.i("Movimentacao", movimentacoes[0].descricao)
                }
                viewAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        movimentacaoRef.addValueEventListener(valueEventListenerMovimentacoes)
    }

    private fun configuraCaledarView() {
        val meses = arrayOf(
            "Janeiro",
            "Fevereiro",
            "Março",
            "Abril",
            "Maio",
            "Junho",
            "Julho",
            "Agosto",
            "Setembro",
            "Outubro",
            "Novembro",
            "Dezembro"
        )
        calendarView.setTitleMonths(meses)

        val dataAtual = calendarView.currentDate
        val mesSelecionado = String.format("%02d", dataAtual.month)
        mesAnoSelecionado = ("$mesSelecionado${dataAtual.year}")

        calendarView.setOnMonthChangedListener { _, date ->
            val mesSelecionado1 = String.format("%02d", date.month)
            mesAnoSelecionado = ("$mesSelecionado1${date.year}")

            movimentacaoRef.removeEventListener(valueEventListenerMovimentacoes)
            recuperarMovimentacoes()
        }
    }

    fun adicionarDespesa(view: View) {
        startActivity(Intent(this, DespesasActivity::class.java))
    }
    fun adicionarReceita(view: View) {
        startActivity(Intent(this, ReceitasActivity::class.java))
    }

    override fun onStart() {
        super.onStart()
        recuperaResumo()
        recuperarMovimentacoes()
    }

    override fun onStop() {
        super.onStop()
        Log.i("Evento", "evento foi removido!")
        usuarioRef.removeEventListener(valueEventListenerUsuario)
        movimentacaoRef.removeEventListener(valueEventListenerMovimentacoes)
    }
}