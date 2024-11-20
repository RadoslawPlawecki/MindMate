package com.application.mindmate.games

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.application.common.ActivityUtils
import com.application.enums.UserRole
import com.application.other.ParseStringList
import com.application.games.craftRhymeGame.CraftRhymeGameManager
import com.application.games.craftRhymeGame.CraftRhymeGameState
import com.application.mindmate.R

class CraftRhymeActivity : AppCompatActivity() {
    private val gameManager = CraftRhymeGameManager()
    private lateinit var titleTextView: TextView
    private lateinit var triesCounterTextView: TextView
    private lateinit var givenWordTextView: TextView
    private lateinit var userWordEditText: EditText
    private lateinit var correctTextView: TextView
    private lateinit var incorrectTextView: TextView
    private lateinit var submitImageView: ImageView
    private lateinit var refreshGameImageView: ImageView
    private lateinit var bottomTextView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_craft_rhyme)
        ActivityUtils.actionBarSetup(this, UserRole.PATIENT)
        titleTextView = findViewById(R.id.title_craft_rhyme)
        triesCounterTextView = findViewById(R.id.number_of_tries)
        givenWordTextView = findViewById(R.id.given_word)
        userWordEditText = findViewById(R.id.user_word)
        correctTextView = findViewById(R.id.guessed_correctly)
        incorrectTextView = findViewById(R.id.guessed_incorrectly)
        submitImageView = findViewById(R.id.submit)
        refreshGameImageView = findViewById(R.id.restart_game)
        bottomTextView = findViewById(R.id.bottom_text_view)
        refreshGameImageView.setOnClickListener {
            startNewGame()
            userWordEditText.setText("")
        }
        val gameState = gameManager.startNewGame()
        updateUI(gameState)
        submitImageView.setOnClickListener {
            val gameState = gameManager.play(userWordEditText.text.toString())
            updateUI(gameState)
            userWordEditText.setText("")
        }
    }

    private fun updateUI(gameState: CraftRhymeGameState) {
        when (gameState) {
            is CraftRhymeGameState.Lost -> showGameLost(gameState.givenWord, gameState.rhymingWords)
            is CraftRhymeGameState.Won -> showGameWon(gameState.givenWord, gameState.rhymingWords)
            is CraftRhymeGameState.Running -> {
                givenWordTextView.text = gameState.givenWord
                bottomTextView.text = "Words used: ${gameState.wordsUsed}"
                if (gameState.currentTries == 6) {
                    triesCounterTextView.text = getString(R.string.last_try)
                    triesCounterTextView.setTextColor(Color.parseColor("#C53434"))
                } else {
                    triesCounterTextView.text = "${gameState.currentTries} | ${gameState.maxTries}"
                    triesCounterTextView.setTextColor(Color.parseColor("#1B1B1B"))
                }
            }
        }
    }

    private fun showGameLost(givenWord: String, rhymingWords: List<String>) {
        givenWordTextView.text = givenWord
        titleTextView.visibility = View.GONE
        correctTextView.visibility = View.GONE
        triesCounterTextView.visibility = View.GONE
        incorrectTextView.visibility = View.VISIBLE
        submitImageView.visibility = View.GONE
        bottomTextView.text = ParseStringList().main("Examples of rhyming words: ", rhymingWords)
    }

    private fun showGameWon(givenWord: String, rhymingWords: List<String>) {
        givenWordTextView.text = givenWord
        titleTextView.visibility = View.GONE
        correctTextView.visibility = View.VISIBLE
        triesCounterTextView.visibility = View.GONE
        incorrectTextView.visibility = View.GONE
        submitImageView.visibility = View.GONE
        bottomTextView.text = ParseStringList().main("Examples of rhyming words: ", rhymingWords)
    }

    private fun startNewGame() {
        val gameState = gameManager.startNewGame()
        submitImageView.visibility = View.VISIBLE
        titleTextView.visibility = View.VISIBLE
        correctTextView.visibility = View.GONE
        triesCounterTextView.visibility = View.VISIBLE
        incorrectTextView.visibility = View.GONE
        updateUI(gameState)
    }
}