package com.example.flashcardapp

import android.content.Intent
import android.graphics.drawable.Icon
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.snackbar.Snackbar

class AddCardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_card)
        val exitButton = findViewById<ImageView>(R.id.exit_question_button)
        val saveButton = findViewById<ImageView>(R.id.card_save)
        val cardQuestion = findViewById<EditText>(R.id.add_card_question)
        val cardAnswer = findViewById<EditText>(R.id.add_card_answer)

        saveButton.setOnClickListener {
            val data = Intent()
            data.putExtra(
                "card_question",
                cardQuestion.text.toString()
            )

            data.putExtra(
                "card_answer",
                cardAnswer.text.toString()
            )

            setResult(RESULT_OK, data)
            finish()
        }

        exitButton.setOnClickListener {
            finish()
        }
    }
}