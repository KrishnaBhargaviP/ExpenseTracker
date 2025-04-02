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

class ExpenseDetailsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_expense_details, container, false)

        // Retrieve bundle from SavedStateHandle
        val savedStateHandle = findNavController().previousBackStackEntry?.savedStateHandle
        val bundle = savedStateHandle?.get<Bundle>("newExpense")

        val expenseName = bundle?.getString("expenseName") ?: "No Name"
        val expenseAmount = bundle?.getDouble("expenseAmount") ?: 0.0
        val expenseDate = bundle?.getString("expenseDate") ?: "No Date"

        view.findViewById<TextView>(R.id.expenseNameTextView).text = expenseName
        view.findViewById<TextView>(R.id.expenseAmountTextView).text = "Amount: $${"%.2f".format(expenseAmount)}"
        view.findViewById<TextView>(R.id.expenseDateTextView).text = expenseDate
        savedStateHandle?.remove<Bundle>("newExpense")

        val backButton = view.findViewById<Button>(R.id.backToHomeButton)
        backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        return view
    }
}
