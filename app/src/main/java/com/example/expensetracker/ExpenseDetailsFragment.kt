package com.example.expensetracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class ExpenseDetailsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_expense_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val expenseName = arguments?.getString("expenseName") ?: "No Name"
        val expenseAmount = arguments?.getString("expenseAmount") ?: "0.0"
        val expenseDate = arguments?.getString("expenseDate") ?: "No Priority"

        view.findViewById<TextView>(R.id.expenseName).text = expenseName
        view.findViewById<TextView>(R.id.expenseAmount).text = expenseAmount.toString()
        view.findViewById<TextView>(R.id.expenseDate).text = expenseDate

        view.findViewById<Button>(R.id.backToHomeButton).setOnClickListener {
            findNavController().navigateUp()
        }
    }
}
