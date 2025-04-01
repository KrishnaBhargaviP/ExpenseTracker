package com.example.expensetracker

import android.app.DatePickerDialog
import android.icu.util.Calendar
import java.util.Currency
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import java.text.NumberFormat

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

                val currencyCodeStr = it.getString("currency") ?: "CAD"
                val currency = Currency.getInstance(currencyCodeStr)

                val name = it.getString("expenseName", "")
                val amount = it.getDouble("expenseAmount", 0.0)
                val date = it.getString("expenseDate", "")


                val converted = it.getDouble("convertedCost", 0.0)
                val code = currency.currencyCode
                expenseToEdit = Expense(id, name, amount, date, code, converted)

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
            if (isChecked) {
                val selected = spinnerCurrency.selectedItem?.toString() ?: "CAD"
                selectedCurrency = selected

                val amount = expenseAmountEditText.text.toString().toDoubleOrNull()
                if (amount == null) {
                    Toast.makeText(requireContext(), "Enter a valid amount first", Toast.LENGTH_SHORT).show()
                    costConversionCheckBox.isChecked = false
                    return@setOnCheckedChangeListener
                }

                val defaultRate = 1.0
                val converted = amount * defaultRate
                convertedAmount = converted

                val currency = Currency.getInstance(selectedCurrency)
                val formatted = NumberFormat.getCurrencyInstance().apply {
                    this.currency = currency
                }.format(converted)
                textConvertedCost.text = "Converted: $formatted"
            } else {
                convertedAmount = 0.0
                textConvertedCost.text = "Conversion disabled"
            }
        }

        saveButton.setOnClickListener {
            val name = expenseNameEditText.text.toString()
            val amount = expenseAmountEditText.text.toString().toDoubleOrNull() ?: 0.0
            val date = expenseDateEditText.text.toString()
            val id = expenseToEdit?.id ?: (System.currentTimeMillis() / 1000).toInt()
            val currency = Currency.getInstance(selectedCurrency)

            val newExpense = Expense(id, name, amount, date, currency.toString(), convertedAmount)

            findNavController().previousBackStackEntry?.savedStateHandle?.set("newExpense", bundleOf(
                "expenseId" to newExpense.id,
                "expenseName" to newExpense.expenseName,
                "expenseAmount" to newExpense.expenseAmount,
                "expenseDate" to newExpense.expenseDate,
                "currency" to newExpense.currency,
                "convertedCost" to newExpense.convertedCost
            ))

            findNavController().popBackStack()
        }

        return view
    }
}
