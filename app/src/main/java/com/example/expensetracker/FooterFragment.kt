package com.example.expensetracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import java.text.NumberFormat
import java.util.Currency


class FooterFragment : Fragment() {

    private lateinit var expenseTotalTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_footer, container, false)
        expenseTotalTextView = view.findViewById(R.id.expenseTotalTextView)
        return view
    }
}
