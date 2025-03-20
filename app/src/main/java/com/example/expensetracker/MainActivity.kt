package com.example.expensetracker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CalendarView
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
    private lateinit var expenseDateInput: CalendarView
    private lateinit var btnFinancialTips: Button
    private lateinit var headerFragment: HeaderFragment
    private lateinit var footerFragment: FooterFragment

    private val expensesList: MutableList<Expense> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("ActivityLifecycle", "onCreate called")

        // Dynamically add
        headerFragment = HeaderFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.header_container, headerFragment)
            .commit()

        footerFragment = FooterFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.expenseTotalTextView, footerFragment)
            .commit()

        // Initialize Views
        recyclerView = findViewById(R.id.recyclerView)
        expenseNameInput = findViewById(R.id.editTextText)
        expenseAmountInput = findViewById(R.id.editTextText2)
        addExpenseButton = findViewById(R.id.button)
        expenseDateInput = findViewById(R.id.calendarView)

        //intent
        val btnShowDetails = findViewById<Button>(R.id.showDetailsButton)

        btnShowDetails.setOnClickListener {
            val expenseName = expenseNameInput.text.toString()
            val expenseAmount = expenseAmountInput.text.toString().toFloatOrNull() ?: 0.0f
            val expenseDate = expenseDateInput.toString()


            // Create intent and pass data to Expense DetailsActivity
            val intent = Intent(this, ExpenseDetailsActivity::class.java)
            intent.putExtra("expenseName", expenseName)
            intent.putExtra("expense_amount", expenseAmount)
            intent.putExtra("expense_date", expenseDate)

            startActivity(intent)
        }

        btnFinancialTips = findViewById(R.id.btnFinancialTips)
        btnFinancialTips.setOnClickListener {
            val url = "https://www.investopedia.com/"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            val chooser = Intent.createChooser(intent, "Open URL with")
            startActivity(chooser)
        }



        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = Adapter(expensesList) { position -> deleteExpense(position) }
        recyclerView.adapter = adapter

        addExpenseButton.setOnClickListener { addExpense() }
    }

    override fun onStart() {
        super.onStart()
        Log.d("ActivityLifecycle", "onStart called")
    }

    override fun onResume() {
        super.onResume()
        Log.d("ActivityLifecycle", "onResume called")
    }

    override fun onPause() {
        super.onPause()
        Log.d("ActivityLifecycle", "onPause called")
    }

    override fun onStop() {
        super.onStop()
        Log.d("ActivityLifecycle", "onStop called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("ActivityLifecycle", "onDestroy called")
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
