package com.cursoandroid.organizze.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.RecyclerView
import com.cursoandroid.organizze.R
import com.cursoandroid.organizze.activity.PrincipalActivity
import com.cursoandroid.organizze.model.Movimentacao
import kotlinx.android.synthetic.main.adapter_movimentacao.view.*

class MyAdapter(private val movimentacoes: MutableList<Movimentacao>, var context: Context): RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(movimentacao: Movimentacao){
            with(movimentacao){
                itemView.textAdapterTitulo.text = movimentacao.descricao
                itemView.textAdapterCategoria.text = movimentacao.categoria
                itemView.textAdapterValor.text = movimentacao.valor.toString()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_movimentacao, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val movimentacao: Movimentacao = movimentacoes[position]
        holder.bind(movimentacao)
        holder.itemView.findViewById<TextView>(R.id.textAdapterValor).setTextColor(context.resources.getColor(R.color.colorPrimaryReceita))
        if (movimentacao.tipo == "d"){
            holder.itemView.findViewById<TextView>(R.id.textAdapterValor).setTextColor(context.resources.getColor(R.color.colorAccent))
            holder.itemView.findViewById<TextView>(R.id.textAdapterValor).text = "-" + movimentacao.valor
        }

    }

    override fun getItemCount() = movimentacoes.size
}