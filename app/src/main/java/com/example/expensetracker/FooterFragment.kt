package com.example.expensetracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class FooterFragment : Fragment() {

    private lateinit var expenseTotalTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_footer, container, false)
        expenseTotalTextView = view.findViewById(R.id.expenseTotalTextView)
        return view
    }

    fun updateExpenseTotal(total: Double) {
        expenseTotalTextView.text = "Total Expenses: $${"%.2f".format(total)}"
    }
}
