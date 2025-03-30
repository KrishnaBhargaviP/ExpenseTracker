package com.example.expensetracker

import android.content.Context
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
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: Adapter
    private lateinit var expenseNameInput: EditText
    private lateinit var expenseAmountInput: EditText
    private lateinit var addExpenseButton: Button
    private lateinit var expenseDateInput: CalendarView
    private lateinit var btnFinancialTips: Button

    private val expensesList: MutableList<Expense> = mutableListOf()
    private var selectedDate: String = ""

    private val FILE_NAME = "expenses.json"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("ActivityLifecycle", "onCreate called")

        // Dynamically add header and footer fragments if needed.
        supportFragmentManager.beginTransaction()
            .replace(R.id.header_container, HeaderFragment())
            .commit()

        supportFragmentManager.beginTransaction()
            .replace(R.id.footer_container, FooterFragment(), "FOOTER_TAG")
            .commit()

        // Initialize Views
        recyclerView = findViewById(R.id.recyclerView)
        expenseNameInput = findViewById(R.id.editTextText)
        expenseAmountInput = findViewById(R.id.editTextText2)
        addExpenseButton = findViewById(R.id.button)
        expenseDateInput = findViewById(R.id.calendarView)
        btnFinancialTips = findViewById(R.id.btnFinancialTips)

        // Initialize the selected date using CalendarView's current date.
        selectedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(expenseDateInput.date))
        expenseDateInput.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedDate = "$dayOfMonth/${month + 1}/$year"
        }

        // Setup Financial Tips button.
        btnFinancialTips.setOnClickListener {
            val url = "https://www.investopedia.com/"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            val chooser = Intent.createChooser(intent, "Open URL with")
            startActivity(chooser)
        }

        // Setup the RecyclerView adapter.
        adapter = Adapter(expensesList,
            onDeleteClick = { position -> deleteExpense(position) },
            onShowDetailsClick = { expense ->
                // Create intent and pass expense details to ExpenseDetailsActivity.
                val intent = Intent(this, ExpenseDetailsActivity::class.java).apply {
                    putExtra("expenseName", expense.expenseName)
                    putExtra("expense_amount", expense.expenseAmount) // Passed as a Double.
                    putExtra("expense_date", expense.expenseDate)
                }
                startActivity(intent)
            }
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Load saved expenses from file, if any.
        expensesList.addAll(loadTasksFromFile())
        adapter.notifyDataSetChanged()


        // Setup Add Expense button.
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
        // Optionally, save expenses when the activity pauses.
        saveTasksToFile(expensesList)
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

        // Create a new Expense with the selected date.
        val newExpense = Expense(name, amount, selectedDate)
        expensesList.add(newExpense)
        adapter.notifyItemInserted(expensesList.size - 1)

        // Clear the input fields.
        expenseNameInput.text.clear()
        expenseAmountInput.text.clear()

        updateTotalExpenses()
        saveTasksToFile(expensesList)
        Toast.makeText(this, "Expense Added!", Toast.LENGTH_SHORT).show()
    }

    private fun deleteExpense(position: Int) {
        expensesList.removeAt(position)
        adapter.notifyItemRemoved(position)
        adapter.notifyItemRangeChanged(position, expensesList.size)
        updateTotalExpenses()
        saveTasksToFile(expensesList)
    }

    private fun calculateTotal(): Double {
        return expensesList.sumOf { it.expenseAmount }
    }

    private fun updateTotalExpenses() {
        val total = calculateTotal()
        val footer = supportFragmentManager.findFragmentByTag("FOOTER_TAG") as? FooterFragment
        footer?.updateExpenseTotal(total)
    }

    private fun saveTasksToFile(taskList: List<Expense>) {
        try {
            val json = Gson().toJson(taskList)
            openFileOutput(FILE_NAME, Context.MODE_PRIVATE).use { output ->
                output.write(json.toByteArray())
            }
        } catch (e: IOException) {
            Log.e("FileStorage", "Error saving tasks: ${e.message}")
        }
    }


    private fun loadTasksFromFile(): MutableList<Expense> {
        val taskList: MutableList<Expense> = mutableListOf()
        try {
            val file = File(filesDir, FILE_NAME)

            if (!file.exists()) return taskList

            val json = file.readText()
            val type = object : TypeToken<List<Expense>>() {}.type
            val loadedTasks: List<Expense> = Gson().fromJson(json, type)
            taskList.addAll(loadedTasks)

            Log.d("FileStorage", "Tasks loaded successfully")
        } catch (e: FileNotFoundException) {
            Log.e("FileStorage", "File not found: ${e.message}")
        } catch (e: IOException) {
            Log.e("FileStorage", "Error reading file: ${e.message}")
        }
        return taskList
    }
}
