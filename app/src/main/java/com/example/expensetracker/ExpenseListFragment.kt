package com.example.expensetracker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ExpenseListFragment : Fragment() {

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_expense_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = view.findViewById(R.id.recyclerView)
        expenseNameInput = view.findViewById(R.id.editTextText)
        expenseAmountInput = view.findViewById(R.id.editTextText2)
        addExpenseButton = view.findViewById(R.id.button)
        expenseDateInput = view.findViewById(R.id.calendarView)
        btnFinancialTips = view.findViewById(R.id.btnFinancialTips)


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
                // Navigate to ExpenseDetailsFragment using the Navigation Component and SafeArgs.
                val action = ExpenseListFragmentDirections
                    .actionExpenseListFragmentToExpenseDetailsFragment(
                        expenseName = expense.expenseName,
                        expenseAmount = expense.expenseAmount.toString(),
                        expenseDate = expense.expenseDate
                    )
                Navigation.findNavController(view).navigate(action)
            }
        )
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // Load saved expenses.
        expensesList.addAll(loadTasksFromFile())
        adapter.notifyDataSetChanged()

        addExpenseButton.setOnClickListener { addExpense() }
    }

    private fun addExpense() {
        val name = expenseNameInput.text.toString().trim()
        val amountText = expenseAmountInput.text.toString().trim()

        if (name.isEmpty() || amountText.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter a valid name and amount", Toast.LENGTH_SHORT).show()
            return
        }
        val amount = amountText.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            Toast.makeText(requireContext(), "Enter a valid amount", Toast.LENGTH_SHORT).show()
            return
        }
        val newExpense = Expense(name, amount, selectedDate)
        expensesList.add(newExpense)
        adapter.notifyItemInserted(expensesList.size - 1)
        expenseNameInput.text.clear()
        expenseAmountInput.text.clear()
        updateTotalExpenses()
        saveTasksToFile(expensesList)
        Toast.makeText(requireContext(), "Expense Added!", Toast.LENGTH_SHORT).show()
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
        val footer = parentFragmentManager.findFragmentByTag("footerFragment") as? FooterFragment
        footer?.updateExpenseTotal(total)
        Log.d("footer",total.toString())
    }

    private fun saveTasksToFile(taskList: List<Expense>) {
        try {
            val json = Gson().toJson(taskList)
            requireContext().openFileOutput(FILE_NAME, android.content.Context.MODE_PRIVATE).use { output ->
                output.write(json.toByteArray())
            Log.d("FileStorage", "Tasks saved successfully")
            }
        } catch (e: IOException) {
            Log.e("FileStorage", "Error saving tasks: ${e.message}")
        }
    }

    private fun loadTasksFromFile(): MutableList<Expense> {
        val taskList: MutableList<Expense> = mutableListOf()
        try {
            val file = File(requireContext().filesDir, FILE_NAME)
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
