package com.example.expensetracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class ExpenseDetailsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_expense_details, container, false)

        val expenseName = arguments?.getString("expenseName") ?: "No Name"
        val expenseAmount = arguments?.getDouble("expenseAmount") ?: 0.0
        val expenseDate = arguments?.getString("expenseDate") ?: "No Date"

        view.findViewById<TextView>(R.id.expenseNameTextView).text = expenseName
        view.findViewById<TextView>(R.id.expenseAmountTextView).text = "Amount: $${"%.2f".format(expenseAmount)}"
        view.findViewById<TextView>(R.id.expenseDateTextView).text = expenseDate

        return view
    }
}