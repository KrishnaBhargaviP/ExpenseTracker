package com.example.expensetracker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Add header and footer fragments as before.
        supportFragmentManager.beginTransaction()
            .replace(R.id.header_container, HeaderFragment())
            .commit()

        supportFragmentManager.beginTransaction()
            .replace(R.id.footer_container, FooterFragment(), "FOOTER_TAG")
            .commit()

        // Setup NavHostFragment (defined in activity_main.xml)
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        // No extra configuration is needed here.
    }
}
