package com.example.expensetracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class AddExpenseFragment : Fragment() {

    private var expenseToEdit: Expense? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_expense, container, false)

        val expenseNameEditText: EditText = view.findViewById(R.id.expenseName)
        val expenseAmountEditText: EditText = view.findViewById(R.id.expenseAmount)
        val expenseDateEditText: EditText = view.findViewById(R.id.expenseDate)
        val saveButton: Button = view.findViewById(R.id.saveButton)

        arguments?.let {
            val expenseId = it.getInt("expenseId", -1)
            val expenseName = it.getString("expenseName", "")
            val expenseAmount = it.getDouble("expenseAmount", 0.0)
            val expenseDate = it.getString("expenseDate", "")

            if (expenseId != -1) {
                expenseToEdit = Expense(expenseId, expenseName!!, expenseAmount, expenseDate!!)
                expenseNameEditText.setText(expenseName)
                expenseAmountEditText.setText(expenseAmount.toString())
                expenseDateEditText.setText(expenseDate)
            }
        }

        saveButton.setOnClickListener {
            val expenseName = expenseNameEditText.text.toString()
            val expenseAmount = expenseAmountEditText.text.toString().toDoubleOrNull() ?: 0.0
            val expenseDate = expenseDateEditText.text.toString()
            val expenseId = expenseToEdit?.id ?: (System.currentTimeMillis() / 1000).toInt()

            val bundle = Bundle().apply {
                putInt("expenseId", expenseId.toInt())
                putString("expenseName", expenseName)
                putDouble("expenseAmount", expenseAmount)
                putString("expenseDate", expenseDate)
            }

            findNavController().previousBackStackEntry?.savedStateHandle?.set("newExpense", bundle)
            findNavController().popBackStack()
        }

        return view
    }
}
