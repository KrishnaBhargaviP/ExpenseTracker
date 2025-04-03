package com.example.expensetracker.fragments

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.expensetracker.R
import com.example.expensetracker.models.Expense
import com.example.expensetracker.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.util.Currency

class AddExpenseFragment : Fragment() {

    private var convertedAmount: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_add_expense, container, false)

        val expenseNameEditText: EditText = view.findViewById(R.id.expenseName)
        val expenseAmountEditText: EditText = view.findViewById(R.id.expenseAmount)
        val expenseDateEditText: EditText = view.findViewById(R.id.expenseDate)
        val costConversionCheckBox: CheckBox = view.findViewById(R.id.checkbox_conversion_needed)
        val spinnerCurrency: Spinner = view.findViewById(R.id.spinner_currency)
        val textConvertedCost: TextView = view.findViewById(R.id.text_converted_cost)
        val saveButton: Button = view.findViewById(R.id.saveButton)

        // Set up the date picker
        expenseDateEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    expenseDateEditText.setText("%02d/%02d/%04d".format(dayOfMonth, month + 1, year))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // Populate spinner with available currency codes and set default to "CAD"
        val currencies = Currency.getAvailableCurrencies().map { it.currencyCode }.sorted()
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, currencies)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCurrency.adapter = adapter
        val defaultIndex = currencies.indexOf("CAD")
        if (defaultIndex >= 0) spinnerCurrency.setSelection(defaultIndex)

        // When saving, perform conversion if needed and pass back the expense details
        saveButton.setOnClickListener {
            val name = expenseNameEditText.text.toString()
            val amount = expenseAmountEditText.text.toString().toDoubleOrNull() ?: 0.0
            val date = expenseDateEditText.text.toString()
            val selectedCurrency = spinnerCurrency.selectedItem.toString()

            if (costConversionCheckBox.isChecked && !selectedCurrency.equals("CAD", ignoreCase = true)) {
                lifecycleScope.launch {
                    convertedAmount = performCurrencyConversion(amount, selectedCurrency)
                    textConvertedCost.text = formatAmount(convertedAmount, "CAD")
                    createAndReturnExpense(name, amount, date, convertedAmount, selectedCurrency)
                }
            } else {
                convertedAmount = amount
                textConvertedCost.text = formatAmount(amount, selectedCurrency)
                createAndReturnExpense(name, amount, date, convertedAmount, selectedCurrency)
            }
        }

        return view
    }

    private suspend fun performCurrencyConversion(amount: Double, selectedCurrency: String): Double {
        return withContext(Dispatchers.IO) {
            if (selectedCurrency.equals("CAD", ignoreCase = true)) {
                amount
            } else {
                try {
                    // Fetch exchange rates with CAD as the base currency
                    val response = RetrofitClient.instance.getRates("cad")
                    val rate = response.cad[selectedCurrency.lowercase()]
                    // Convert the amount from the selected currency to CAD: amount / rate
                    if (rate != null && rate != 0.0) {
                        amount / rate
                    } else {
                        amount
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    amount
                }
            }
        }
    }

    private fun formatAmount(amount: Double, currencyCode: String): String {
        val formatted = NumberFormat.getCurrencyInstance().apply {
            currency = Currency.getInstance(currencyCode)
        }.format(amount)
        return "Amount: $formatted"
    }

    private fun createAndReturnExpense(name: String, amount: Double, date: String, converted: Double, selectedCurrency: String) {

        val expenseCurrency = if (converted != amount) "CAD" else selectedCurrency
        val newExpense = Expense(
            id = (System.currentTimeMillis() / 1000).toInt(),
            expenseName = name,
            expenseAmount = amount,
            expenseDate = date,
            currency = expenseCurrency,
            convertedCost = converted
        )

        val bundle = Bundle().apply {
            putInt("expenseId", newExpense.id.toInt())
            putString("expenseName", newExpense.expenseName)
            putFloat("expenseAmount", newExpense.expenseAmount.toFloat())
            putString("expenseDate", newExpense.expenseDate)
            putString("currency", newExpense.currency)
            putDouble("convertedCost", newExpense.convertedCost)
        }
        findNavController().previousBackStackEntry?.savedStateHandle?.set("newExpense", bundle)
        findNavController().popBackStack()

    }
}
