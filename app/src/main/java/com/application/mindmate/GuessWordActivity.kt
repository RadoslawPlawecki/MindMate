package com.application.mindmate

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import com.application.games.guessWordGame.GuessWordGameManager
import com.application.games.guessWordGame.GuessWordGameState

class GuessWordActivity : AppCompatActivity() {
    private val gameManager = GuessWordGameManager()
    private lateinit var openMenu: ImageView
    private lateinit var alarmImageView: ImageView
    private lateinit var wordTextView: TextView
    private lateinit var lettersUsedTextView: TextView
    private lateinit var newGameButton: Button
    private lateinit var neutralTextView: TextView
    private lateinit var correctTextView: TextView
    private lateinit var incorrectTextView: TextView
    private lateinit var wordToGuessDescription: TextView
    private lateinit var triesCounterTextView: TextView
    private lateinit var lettersLayout: ConstraintLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guess_word)
        openMenu = findViewById(R.id.image_bars)
        openMenu.setOnClickListener {
            val intent = Intent(this@GuessWordActivity, MenuActivity::class.java)
            startActivity(intent)
        }
        alarmImageView = findViewById(R.id.alarm)
        alarmImageView.setOnClickListener {
            AlarmActivity(this).useAlarm()
        }
        wordTextView = findViewById(R.id.unknown_word)
        lettersUsedTextView = findViewById(R.id.letters_used)
        neutralTextView = findViewById(R.id.title_guess_word)
        correctTextView = findViewById(R.id.guessed_correctly)
        incorrectTextView = findViewById(R.id.guessed_incorrectly)
        newGameButton = findViewById(R.id.start_new_game_button)
        wordToGuessDescription = findViewById(R.id.word_to_guess_description)
        triesCounterTextView = findViewById(R.id.counter_text)
        lettersLayout = findViewById(R.id.letters_layout)
        newGameButton.setOnClickListener {
            startNewGame()
        }
        val gameState = gameManager.startNewGame()
        updateUI(gameState)
        lettersLayout.children.forEach { letterView ->
            if (letterView is TextView) {
                letterView.setOnClickListener {
                    val gameState = gameManager.play((letterView).text[0])
                    updateUI(gameState)
                    letterView.visibility = View.GONE
                }
            }
        }
    }

    private fun updateUI(gameState: GuessWordGameState) {
        when (gameState) {
            is GuessWordGameState.Lost -> showGameLost(gameState.wordToGuess, gameState.descriptionOfWordToGuess)
            is GuessWordGameState.Running -> {
                wordTextView.text = gameState.underscoreWord
                lettersUsedTextView.text = "Letters used: \n ${gameState.lettersUsed}"
                if (gameState.currentTries == 6) {
                    triesCounterTextView.text = getString(R.string.last_try)
                    triesCounterTextView.setTextColor(Color.parseColor("#C53434"))
                } else {
                    triesCounterTextView.text = "${gameState.currentTries} | ${gameState.maxTries}"
                    triesCounterTextView.setTextColor(Color.parseColor("#1B1B1B"))
                }
            }
            is GuessWordGameState.Won -> showGameWon(gameState.wordToGuess, gameState.descriptionOfWordToGuess)
        }
    }

    private fun showGameLost(wordToGuess: String, descriptionOfWordToGuess: String) {
        wordTextView.text = wordToGuess
        lettersLayout.visibility = View.GONE
        wordToGuessDescription.visibility = View.VISIBLE
        wordToGuessDescription.text = descriptionOfWordToGuess
        neutralTextView.visibility = View.GONE
        correctTextView.visibility = View.GONE
        triesCounterTextView.visibility = View.GONE
        incorrectTextView.visibility = View.VISIBLE
    }

    private fun showGameWon(wordToGuess: String, descriptionOfWordToGuess: String) {
        wordTextView.text = wordToGuess
        lettersLayout.visibility = View.GONE
        wordToGuessDescription.visibility = View.VISIBLE
        wordToGuessDescription.text = descriptionOfWordToGuess
        neutralTextView.visibility = View.GONE
        correctTextView.visibility = View.VISIBLE
        triesCounterTextView.visibility = View.GONE
        incorrectTextView.visibility = View.GONE
    }

    private fun startNewGame() {
        val gameState = gameManager.startNewGame()
        lettersLayout.visibility = View.VISIBLE
        neutralTextView.visibility = View.VISIBLE
        correctTextView.visibility = View.GONE
        triesCounterTextView.visibility = View.VISIBLE
        incorrectTextView.visibility = View.GONE
        wordToGuessDescription.visibility = View.GONE
        lettersLayout.children.forEach { letterView ->
            letterView.visibility = View.VISIBLE
        }
        updateUI(gameState)
    }
}