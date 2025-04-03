package com.example.expensetracker.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.expensetracker.R
import java.text.NumberFormat
import java.util.Currency

class ExpenseDetailsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_expense_details, container, false)

        // Retrieve expense details from arguments
        val args = requireArguments()
        val expenseName = args.getString("expenseName") ?: "No Name"
        val expenseAmount = args.getDouble("expenseAmount", 0.0)
        val expenseDate = args.getString("expenseDate") ?: "No Date"
        val expenseCurrency = args.getString("currency") ?: "CAD"
        val convertedCost = args.getDouble("convertedCost", 0.0)

        view.findViewById<TextView>(R.id.expenseNameTextView).text = expenseName

        val originalFormatted = NumberFormat.getCurrencyInstance().apply {
            currency = Currency.getInstance(expenseCurrency)
        }.format(expenseAmount)
        view.findViewById<TextView>(R.id.expenseAmountTextView).text = "Original Amount: $originalFormatted"

        view.findViewById<TextView>(R.id.expenseDateTextView).text = "Date: $expenseDate"

        val convertedCostTextView = view.findViewById<TextView>(R.id.expenseConvertedCostTextView)
        if (convertedCost != expenseAmount) {
            val cadFormatted = NumberFormat.getCurrencyInstance().apply {
                currency = Currency.getInstance("CAD")
            }.format(convertedCost)
            convertedCostTextView.text = "Converted Cost (CAD): $cadFormatted"
        } else {
            convertedCostTextView.text = "No Conversion Applied"
        }

        val backButton = view.findViewById<Button>(R.id.backToHomeButton)
        backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        return view
    }
}
