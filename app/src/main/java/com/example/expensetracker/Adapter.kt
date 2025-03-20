package com.example.expensetracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class Adapter(
    private val list: MutableList<Expense>,
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<Adapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view_design, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.expenseNameText.text = item.expenseName
        holder.expenseAmountText.text = "$${String.format("%.2f", item.expenseAmount)}"

        // delete callback;
        holder.deleteButton.setOnClickListener {
            onDeleteClick(position)
        }
    }

    override fun getItemCount(): Int = list.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val expenseNameText: TextView = itemView.findViewById(R.id.textView1)
        val expenseAmountText: TextView = itemView.findViewById(R.id.textView2)
        val deleteButton: Button = itemView.findViewById(R.id.button3)
    }
}
