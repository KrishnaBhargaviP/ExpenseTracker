package com.example.expensetracker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.expensetracker.R

class ExpenseDetailsActivity : AppCompatActivity() {
    private lateinit var backToHomeButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense_details)

        // Retrieve the expense data passed from MainActivity via intent extras
        val expenseName = intent.getStringExtra("expenseName") ?: "No Name"
        val expenseAmount = intent.getDoubleExtra("expense_amount", 0.0)
        val expenseDate = intent.getStringExtra("expense_date") ?: "No Date"

        // Display the received data in the UI
        findViewById<TextView>(R.id.expenseName).text = expenseName
        findViewById<TextView>(R.id.expenseAmount).text = expenseAmount.toString()
        findViewById<TextView>(R.id.expenseDate).text = expenseDate

        backToHomeButton = findViewById(R.id.backToHomeButton)
        backToHomeButton.setOnClickListener {
            val intent = Intent(this@ExpenseDetailsActivity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }


    }
}
