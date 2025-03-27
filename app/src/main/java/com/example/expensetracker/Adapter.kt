package com.example.expensetracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class Adapter(
    private val expensesList: List<Expense>,
    private val onDeleteClick: (Int) -> Unit,
    private val onShowDetailsClick: (Expense) -> Unit
) : RecyclerView.Adapter<Adapter.ExpenseViewHolder>() {

    inner class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewExpenseName: TextView = itemView.findViewById(R.id.textView1)
        val textViewExpenseAmount: TextView = itemView.findViewById(R.id.textView2)
        val btnDelete: Button = itemView.findViewById(R.id.button3)
        val btnShowDetails: Button = itemView.findViewById(R.id.button2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_view_design, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expensesList[position]
        holder.textViewExpenseName.text = expense.expenseName
        holder.textViewExpenseAmount.text = expense.expenseAmount.toString()

        // Handle Delete button click
        holder.btnDelete.setOnClickListener {
            onDeleteClick(position)
        }

        // Handle Show Details button click
        holder.btnShowDetails.setOnClickListener {
            onShowDetailsClick(expense)
        }
    }

    override fun getItemCount(): Int = expensesList.size
}
