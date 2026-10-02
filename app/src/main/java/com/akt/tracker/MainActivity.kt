package com.akt.tracker

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.akt.tracker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.caseIdInput.setText("AKT-2026-018")
        binding.summaryInput.setText("Suspicious scam relay detected during network navigation review.")

        val routeStatus = getRouteStatus(this)
        binding.routeStatus.text = routeStatus
        binding.routeStatus.setTextColor(
            if (routeStatus == getString(R.string.status_alert)) {
                getColor(android.R.color.holo_red_dark)
            } else {
                getColor(android.R.color.holo_green_dark)
            }
        )

        binding.analyzeButton.setOnClickListener {
            val caseId = binding.caseIdInput.text.toString().ifBlank { "UNSPECIFIED" }
            val summary = binding.summaryInput.text.toString().ifBlank { "No summary provided." }
            Toast.makeText(this, "Analyzing $caseId: $summary", Toast.LENGTH_SHORT).show()
        }

        binding.reportButton.setOnClickListener {
            val report = buildString {
                append("AKT TRACKER REPORT\n")
                append("Case ID: ${binding.caseIdInput.text}\n")
                append("Status: ${binding.routeStatus.text}\n")
                append("Summary: ${binding.summaryInput.text}\n")
            }
            Toast.makeText(this, report, Toast.LENGTH_LONG).show()
        }
    }

    private fun getRouteStatus(context: Context): String {
        val connectivityManager = context.getSystemService(ConnectivityManager::class.java)
        val network = connectivityManager?.activeNetwork ?: return context.getString(R.string.status_ok)
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return context.getString(R.string.status_ok)

        val hasInternet = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        val hasValidated = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)

        return if (hasInternet && hasValidated) {
            context.getString(R.string.status_alert)
        } else {
            context.getString(R.string.status_ok)
        }
    }
}
