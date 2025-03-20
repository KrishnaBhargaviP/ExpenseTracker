package com.example.expensetracker

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: Adapter
    private lateinit var expenseNameInput: EditText
    private lateinit var expenseAmountInput: EditText
    private lateinit var addExpenseButton: Button

    private val expensesList: MutableList<Expense> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Views
        recyclerView = findViewById(R.id.recyclerView)
        expenseNameInput = findViewById(R.id.editTextText)
        expenseAmountInput = findViewById(R.id.editTextText2)
        addExpenseButton = findViewById(R.id.button)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = Adapter(expensesList) { position -> deleteExpense(position) }
        recyclerView.adapter = adapter

        addExpenseButton.setOnClickListener { addExpense() }
    }

    private fun addExpense() {
        val name = expenseNameInput.text.toString().trim()
        val amountText = expenseAmountInput.text.toString().trim()

        if (name.isEmpty() || amountText.isEmpty()) {
            Toast.makeText(this, "Please enter a valid name and amount", Toast.LENGTH_SHORT).show()
            return
        }

        val amount = amountText.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            Toast.makeText(this, "Enter a valid amount", Toast.LENGTH_SHORT).show()
            return
        }

        val newExpense = Expense(name, amount)
//        println("After Adding: $newExpense")
        expensesList.add(newExpense)
//        println("expensesList now: $expensesList")
        adapter.notifyItemInserted(expensesList.size - 1)

        // Reset Fields
        expenseNameInput.text.clear()
        expenseAmountInput.text.clear()

        Toast.makeText(this, "Expense Added!", Toast.LENGTH_SHORT).show()
    }

    private fun deleteExpense(position: Int) {
//        println("Before deletion: $expensesList")

        expensesList.removeAt(position)
        adapter.notifyItemRemoved(position)
        adapter.notifyItemRangeChanged(position, expensesList.size)
//        println("After deletion: $expensesList")

    }
}
