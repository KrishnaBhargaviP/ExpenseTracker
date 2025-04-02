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
    ): View? {
        val view = inflater.inflate(R.layout.fragment_expense_details, container, false)

        val expenseName = arguments?.getString("expenseName") ?: "No Name"
        val expenseAmount = arguments?.getDouble("expenseAmount") ?: 0.0
        val expenseDate = arguments?.getString("expenseDate") ?: "No Date"

        view.findViewById<TextView>(R.id.expenseNameTextView).text = expenseName
        view.findViewById<TextView>(R.id.expenseAmountTextView).text = "Amount: $${"%.2f".format(expenseAmount)}"
        view.findViewById<TextView>(R.id.expenseDateTextView).text = expenseDate

        val backButton = view.findViewById<Button>(R.id.backToHomeButton)
        backButton.setOnClickListener {
            findNavController().popBackStack()
        }
        return view
    }
}