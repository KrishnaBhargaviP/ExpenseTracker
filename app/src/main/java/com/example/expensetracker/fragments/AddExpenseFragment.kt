package com.example.expensetracker.fragments

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.os.bundleOf
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

    private var expenseToEdit: Expense? = null
    private var selectedCurrency: String = "CAD"
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
        val textCurrencyLabel: TextView = view.findViewById(R.id.currency_label)
        val textConvertedCost: TextView = view.findViewById(R.id.text_converted_cost)
        val saveButton: Button = view.findViewById(R.id.saveButton)

        textCurrencyLabel.text = "Select Currency"

        // Show Date Picker
        expenseDateEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    val formattedDate = "%02d/%02d/%04d".format(dayOfMonth, month + 1, year)
                    expenseDateEditText.setText(formattedDate)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // Populate Spinner using ICU Currency
        val currencyCodes = Currency.getAvailableCurrencies().map { it.currencyCode }.sorted()
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, currencyCodes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCurrency.adapter = adapter

        val defaultIndex = currencyCodes.indexOf("CAD")
        if (defaultIndex >= 0) spinnerCurrency.setSelection(defaultIndex)

        // Load data if editing
        arguments?.let {
            val id = it.getInt("expenseId", -1)
            if (id != -1) {
                val currencyCode = it.getString("currency") ?: "CAD"
                val currency = Currency.getInstance(currencyCode)
                val name = it.getString("expenseName", "")
                val amount = it.getDouble("expenseAmount", 0.0)
                val date = it.getString("expenseDate", "")
                val converted = it.getDouble("convertedCost", 0.0)

                expenseNameEditText.setText(name)
                expenseAmountEditText.setText(amount.toString())
                expenseDateEditText.setText(date)
                spinnerCurrency.setSelection(currencyCodes.indexOf(currency.currencyCode))

                val formatted = NumberFormat.getCurrencyInstance().apply {
                    this.currency = currency
                }.format(converted)
                textConvertedCost.text = "Converted: $formatted"
            }
        }

        costConversionCheckBox.setOnCheckedChangeListener { _, isChecked ->
            val userAmountString = expenseAmountEditText.text.toString()
            val userAmount = userAmountString.toDoubleOrNull() ?: 0.0

            if (isChecked) {
                performCurrencyConversion(expenseAmountEditText, textConvertedCost)
            } else {
                convertedAmount = userAmount
                val formatted = NumberFormat.getCurrencyInstance().format(userAmount)
                textConvertedCost.text = "Amount: $formatted"
            }
        }

        spinnerCurrency.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View?, position: Int, id: Long
            ) {
                selectedCurrency = parent.getItemAtPosition(position).toString()
                // If conversion is enabled, update the conversion
                if (costConversionCheckBox.isChecked) {
                    performCurrencyConversion(expenseAmountEditText, textConvertedCost)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        saveButton.setOnClickListener {
            val name = expenseNameEditText.text.toString()
            val amount = expenseAmountEditText.text.toString().toDoubleOrNull() ?: 0.0
            val date = expenseDateEditText.text.toString()
            val id = expenseToEdit?.id ?: (System.currentTimeMillis() / 1000).toInt()

            // Use CAD as currency if conversion was applied (and selected currency is not CAD)
            val expenseCurrency = if (costConversionCheckBox.isChecked && !selectedCurrency.equals("CAD", ignoreCase = true)) "CAD" else selectedCurrency

            val newExpense = Expense(id, name, amount, date, expenseCurrency, convertedAmount)

            findNavController().previousBackStackEntry?.savedStateHandle?.set(
                "newExpense", bundleOf(
                    "expenseId" to newExpense.id,
                    "expenseName" to newExpense.expenseName,
                    "expenseAmount" to newExpense.expenseAmount,
                    "expenseDate" to newExpense.expenseDate,
                    "currency" to newExpense.currency,
                    "convertedCost" to newExpense.convertedCost
                )
            )

            findNavController().popBackStack()
        }

        return view
    }

    private fun performCurrencyConversion(amountInput: EditText, output: TextView) {
        val inputAmount = amountInput.text.toString().toDoubleOrNull()
        if (inputAmount == null) {
            Toast.makeText(requireContext(), "Enter a valid amount first", Toast.LENGTH_SHORT).show()
            return
        }

        val currentCurrency = selectedCurrency

        lifecycleScope.launch {
            try {
                val conversionFactor = withContext(Dispatchers.IO) {
                    Log.d("CurrencyConversion", "Selected currency: $currentCurrency")
                    if (currentCurrency.equals("CAD", ignoreCase = true)) {
                        1.0
                    } else {

                        val response = RetrofitClient.instance.getRates("cad")
                        Log.d("Current currency", currentCurrency)
                        val rateFromCadToSelected = response.cad[currentCurrency.lowercase()]
                        if (rateFromCadToSelected != null && rateFromCadToSelected != 0.0) {
                            1.0 / rateFromCadToSelected
                        } else {
                            1.0
                        }
                    }
                }

                convertedAmount = inputAmount * conversionFactor

                val formatted = NumberFormat.getCurrencyInstance().apply {
                    currency = Currency.getInstance("CAD")
                }.format(convertedAmount)

                output.text = "Converted: $formatted"
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Conversion error: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}
