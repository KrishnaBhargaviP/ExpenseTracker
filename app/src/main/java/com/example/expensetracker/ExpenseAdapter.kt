package com.example.expensetracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetracker.models.Expense

class ExpenseAdapter(
    private val expenseList: MutableList<Expense>,
    private val listener: ExpenseItemListener
) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    interface ExpenseItemListener {
        fun onEditClick(expense: Expense)
        fun onDeleteClick(expense: Expense)
        fun onViewClick(expense: Expense)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.expense_item, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenseList[position]
        holder.bind(expense)
    }

    override fun getItemCount(): Int = expenseList.size

    inner class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val expenseNameTextView: TextView = itemView.findViewById(R.id.expenseNameTextView)
        private val expenseAmountTextView: TextView = itemView.findViewById(R.id.expenseAmountTextView)
        private val expenseDateTextView: TextView = itemView.findViewById(R.id.expenseDateTextView)
        private val editButton: Button = itemView.findViewById(R.id.editButton)
        private val deleteButton: Button = itemView.findViewById(R.id.deleteButton)
        private val viewButton: Button = itemView.findViewById(R.id.viewButton)

        fun bind(expense: Expense) {
            val sym = java.util.Currency.getInstance(expense.currency.uppercase()).symbol
            expenseNameTextView.text = expense.expenseName
            expenseAmountTextView.text = "Amount: ${sym} ${"%.2f".format(expense.convertedCost)} "
            expenseDateTextView.text = "Date: ${expense.expenseDate}"

            editButton.setOnClickListener {
                listener.onEditClick(expense)
            }

            deleteButton.setOnClickListener {
                listener.onDeleteClick(expense)
            }

            viewButton.setOnClickListener {
                listener.onViewClick(expense)
            }
        }
    }
}
