package com.example.contactsmanager

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.content.ContentValues
import android.provider.ContactsContract
import androidx.activity.result.contract.ActivityResultContracts


class MainActivity : AppCompatActivity() {

    private lateinit var nameEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var addressEditText: EditText
    private lateinit var jobTitleEditText: EditText
    private lateinit var companyEditText: EditText
    private lateinit var websiteEditText: EditText
    private lateinit var imEditText: EditText

    private lateinit var showHideAdditionalFieldsButton: Button
    private lateinit var additionalFieldsContainer: LinearLayout


    private val contactsManagerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            Toast.makeText(this, "Contact salvat cu succes", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Operațiune anulată", Toast.LENGTH_SHORT).show()
        }
        finish() // opțional, la fel ca înainte
    }

    private val buttonClickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.show_hide_additional_fields -> {
                when (additionalFieldsContainer.visibility) {
                    View.VISIBLE -> {
                        showHideAdditionalFieldsButton.text =
                            getString(R.string.show_additional_fields)
                        additionalFieldsContainer.visibility = View.INVISIBLE
                    }

                    View.INVISIBLE -> {
                        showHideAdditionalFieldsButton.text =
                            getString(R.string.hide_additional_fields)
                        additionalFieldsContainer.visibility = View.VISIBLE
                    }

                    View.GONE -> {
                        // do nothing
                    }
                }
            }

            R.id.save_button -> {
                val name = nameEditText.text.toString()
                val phone = phoneEditText.text.toString()
                val email = emailEditText.text.toString()
                val address = addressEditText.text.toString()
                val jobTitle = jobTitleEditText.text.toString()
                val company = companyEditText.text.toString()
                val website = websiteEditText.text.toString()
                val im = imEditText.text.toString()

                val intent = Intent(ContactsContract.Intents.Insert.ACTION).apply {
                    type = ContactsContract.RawContacts.CONTENT_TYPE
                    putExtra(ContactsContract.Intents.Insert.NAME, name)
                    putExtra(ContactsContract.Intents.Insert.PHONE, phone)
                    putExtra(ContactsContract.Intents.Insert.EMAIL, email)
                    putExtra(ContactsContract.Intents.Insert.POSTAL, address)
                    putExtra(ContactsContract.Intents.Insert.JOB_TITLE, jobTitle)
                    putExtra(ContactsContract.Intents.Insert.COMPANY, company)
                }

                val contactData = ArrayList<ContentValues>()
                val websiteRow = ContentValues().apply {
                    put(
                        ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE
                    )
                    put(ContactsContract.CommonDataKinds.Website.URL, website)
                }
                contactData.add(websiteRow)

                val imRow = ContentValues().apply {
                    put(
                        ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE
                    )
                    put(ContactsContract.CommonDataKinds.Im.DATA, im)
                }
                contactData.add(imRow)

                intent.putParcelableArrayListExtra(
                    ContactsContract.Intents.Insert.DATA,
                    contactData
                )
                contactsManagerLauncher.launch(intent)
            }

            R.id.cancel_button -> {
                setResult(RESULT_CANCELED, Intent())
                finish()
            }
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        nameEditText = findViewById(R.id.name_edit_text)
        phoneEditText = findViewById(R.id.phone_number_edit_text)
        emailEditText = findViewById(R.id.email_edit_text)
        addressEditText = findViewById(R.id.address_edit_text)
        jobTitleEditText = findViewById(R.id.job_title_edit_text)
        companyEditText = findViewById(R.id.company_edit_text)
        websiteEditText = findViewById(R.id.website_edit_text)
        imEditText = findViewById(R.id.im_edit_text)

        showHideAdditionalFieldsButton = findViewById(R.id.show_hide_additional_fields)
        showHideAdditionalFieldsButton.setOnClickListener(buttonClickListener)
        findViewById<Button>(R.id.save_button).setOnClickListener(buttonClickListener)
        findViewById<Button>(R.id.cancel_button).setOnClickListener(buttonClickListener)

        additionalFieldsContainer = findViewById(R.id.additional_fields_container)

//        val intent = intent
//        if (intent != null) {
//            val phone = intent.getStringExtra("ro.pub.cs.systems.eim.lab04.contactsmanager.PHONE_NUMBER_KEY")
//            if (phone != null) {
//                phoneEditText.setText(phone)
//            } else {
//                Toast.makeText(this, getString(R.string.phone_error), Toast.LENGTH_LONG).show()
//            }
//        }

        intent?.getStringExtra("ro.pub.cs.systems.eim.lab04.contactsmanager.PHONE_NUMBER_KEY")?.let {
            phoneEditText.setText(it)
        } ?: run {
            Toast.makeText(this, getString(R.string.phone_error), Toast.LENGTH_LONG).show()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {

        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == CONTACTS_MANAGER_REQUEST_CODE) {
            setResult(resultCode, Intent())
            finish()
        }
    }

    companion object {
        const val CONTACTS_MANAGER_REQUEST_CODE = 2017
    }
}