package com.example.flashcardapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    lateinit var flashcardDatabase: FlashcardDatabase
    var allFlashcards = mutableListOf<Flashcard>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        flashcardDatabase = FlashcardDatabase(this)
        allFlashcards = flashcardDatabase.getAllCards().toMutableList()

        val flashcardQuestion = findViewById<TextView>(R.id.flashcard_question)
        val flashcardAnswer = findViewById<TextView>(R.id.flashcard_answer)
        val addAddButton = findViewById<ImageView>(R.id.add_question_button)
        val nextQuestion = findViewById<ImageView>(R.id.next_question)
        var currentCardDisplayedIndex = 0

        if (allFlashcards.size > 0) {
            flashcardQuestion.text = allFlashcards[0].question
            flashcardAnswer.text = allFlashcards[0].answer
        }

        flashcardDatabase = FlashcardDatabase(this)

        flashcardAnswer.visibility = View.INVISIBLE
        flashcardQuestion.setOnClickListener {
            flashcardQuestion.animate()
                .rotationY(90f)
                .setDuration(200)
                .withEndAction(
                    Runnable {
                        flashcardQuestion.setVisibility(View.INVISIBLE)
                        flashcardAnswer.visibility = View.VISIBLE
                        // second quarter turn
                        flashcardAnswer.rotationY = -90f
                        flashcardAnswer.animate()
                            .rotationY(0f)
                            .setDuration(200)
                            .start()
                    }
                ).start()
        }

        flashcardAnswer.setOnClickListener {
            flashcardAnswer.animate()
                .rotationY(90f)
                .setDuration(200)
                .withEndAction(
                    Runnable {
                        flashcardAnswer.setVisibility(View.INVISIBLE)
                        flashcardQuestion.visibility = View.VISIBLE
                        // second quarter turn
                        flashcardQuestion.rotationY = -90f
                        flashcardQuestion.animate()
                            .rotationY(0f)
                            .setDuration(200)
                            .start()
                    }
                ).start()
        }

        nextQuestion.setOnClickListener {
            val leftOutAnim = AnimationUtils.loadAnimation(this, R.anim.left_out)
            val rightInAnim = AnimationUtils.loadAnimation(this, R.anim.right_in)
            findViewById<View>(R.id.flashcard_question).startAnimation(leftOutAnim)
            findViewById<View>(R.id.flashcard_question).startAnimation(rightInAnim)
            findViewById<View>(R.id.flashcard_answer).startAnimation(leftOutAnim)
            findViewById<View>(R.id.flashcard_answer).startAnimation(rightInAnim)
            if (allFlashcards.size == 0) {
                // return here, so that the rest of the code in this onClickListener doesn't execute
                return@setOnClickListener
            }
            // advance our pointer index so we can show the next card
            currentCardDisplayedIndex++
            // make sure we don't get an IndexOutOfBoundsError if we are viewing the last indexed card in our list
            if(currentCardDisplayedIndex >= allFlashcards.size) {
                    findViewById<TextView>(R.id.flashcard_question) // This should be the TextView for displaying your flashcard question
                currentCardDisplayedIndex = 0
            }
            // set the question and answer TextViews with data from the database
            allFlashcards = flashcardDatabase.getAllCards().toMutableList()
            val (question, answer) = allFlashcards[currentCardDisplayedIndex]

            flashcardAnswer.text = answer
            flashcardQuestion.text = question
        }

        val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data: Intent? = result.data

            if (data != null) { // Check that we have data returned
                val question =
                    data.getStringExtra("card_question") // 'string1' needs to match the key we used when we put the string in the Intent
                val answer = data.getStringExtra("card_answer")

                flashcardQuestion.text = question
                flashcardAnswer.text = answer
                // Log the value of the strings for easier debugging
                Log.i("MainActivity", "question: $question")
                Log.i("MainActivity", "answer: $answer")

                if (question != null && answer != null) {
                    flashcardDatabase.insertCard(Flashcard(question, answer))
                    // Update set of flashcards to include new card
                    allFlashcards = flashcardDatabase.getAllCards().toMutableList()
                } else {
                    Log.e(
                        "TAG",
                        "Missing question or answer to input into database. Question is $question and answer is $answer"
                    )
                }
            } else {
                Log.i("MainActivity", "Returned null data from AddCardActivity")
            }
        }

        addAddButton.setOnClickListener {
            val intent = Intent(this, AddCardActivity::class.java)
            intent.putExtra("question", flashcardQuestion.text);
            intent.putExtra("answer", flashcardAnswer.text);
            resultLauncher.launch(intent)
            overridePendingTransition(R.anim.right_in, R.anim.left_out)
        }
    }
}