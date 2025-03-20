package com.example.expensetracker

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.expensetracker.R

class ExpenseDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense_details)

        // Retrieve the expense data passed from MainActivity via intent extras
        val expenseName = intent.getStringExtra("expenseName") ?: "No Name"
        val expenseAmount = intent.getFloatExtra("expense_amount", 0.0f)
        val expenseDate = intent.getStringExtra("expense_date") ?: "No Date"

        // Display the received data in the UI
        findViewById<TextView>(R.id.expenseName).text = "$expenseName"
        findViewById<TextView>(R.id.expenseAmount).text = "$$expenseAmount"
        findViewById<TextView>(R.id.expenseDate).text = "$expenseDate"

    }
}
