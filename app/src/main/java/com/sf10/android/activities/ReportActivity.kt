package com.sf10.android.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.core.Repo
import com.sf10.android.R
import com.sf10.android.databinding.ActivityMyProfileBinding
import com.sf10.android.databinding.ActivityReportBinding
import com.sf10.android.firebase.Firestore
import com.sf10.android.models.Report
import com.sf10.android.utils.Utils
import java.text.SimpleDateFormat
import java.util.*

class ReportActivity : BaseActivity() {
    private lateinit var binding: ActivityReportBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpActionBar()

        binding.btnReport.setOnClickListener {
            val issue: String = binding.etDescription.text.toString()
            hideKeyboard(this)
            if (issue.isNotEmpty()) {
                Firestore().submitReport(
                    this,
                    Report(
                        getCurrentUserID(),
                        Utils.getCurrentDateTime(),
                        issue
                    )
                )
            } else {
                showErrorSnackBar("Description cannot be empty!")
            }
        }
    }

    private fun setUpActionBar() {
        setSupportActionBar(binding.toolbarReportIssueActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_24)
            actionBar.title = resources.getString(R.string.report_issue_title)
        }

        binding.toolbarReportIssueActivity.setNavigationOnClickListener {
            onBackPressed()
        }
    }
}