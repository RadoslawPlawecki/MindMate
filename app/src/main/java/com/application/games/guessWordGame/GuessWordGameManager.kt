package com.application.games.guessWordGame

import android.util.Log
import android.widget.Toast
import com.application.constants.GameConstantsDescriptions
import com.application.constants.GameConstantsWords
import com.application.constants.GuessWordDescriptions
import com.application.constants.GuessWordWords
import java.lang.StringBuilder
import kotlin.random.Random

class GuessWordGameManager {
    private var lettersUsed: String = ""
    private lateinit var underscoreWord: String
    private lateinit var wordToGuess: String
    private lateinit var descriptionOfWordToGuess: String
    private val maxTries = 7
    private var currentTries = 0
    fun startNewGame(wordCategory: String): GuessWordGameState {
        lettersUsed = ""
        currentTries = 0
        selectRandomWordByCategory(wordCategory)
        generateUnderscores(wordToGuess)
        return getGameState()
    }

    fun generateUnderscores(word: String) {
        val sb = StringBuilder()
        word.forEach { _ ->
            sb.append("_")
        }
        underscoreWord = sb.toString()
    }

    fun play(letter: Char): GuessWordGameState {
        if (lettersUsed.contains(letter)) {
            return GuessWordGameState.Running(lettersUsed, underscoreWord, currentTries, maxTries)
        }
        lettersUsed += "$letter"
        val indexes = mutableListOf<Int>()
        wordToGuess.forEachIndexed {index, char ->
            if (char.equals(letter, true)) {
                indexes.add(index)
            }
        }
        var finalUnderscoreWord = "" + underscoreWord
        indexes.forEach { index ->
            val sb = StringBuilder(finalUnderscoreWord).also {it.setCharAt(index, letter)}
            finalUnderscoreWord = sb.toString()
        }
        if (indexes.isEmpty()) {
            currentTries++
        }
        underscoreWord = finalUnderscoreWord
        return getGameState()
    }

    private fun getGameState(): GuessWordGameState {
        if (underscoreWord.equals(wordToGuess, true)) {
            return GuessWordGameState.Won(wordToGuess, descriptionOfWordToGuess)
        }
        if (currentTries == maxTries) {
            return GuessWordGameState.Lost(wordToGuess, descriptionOfWordToGuess)
        }
        return GuessWordGameState.Running(lettersUsed, underscoreWord, currentTries, maxTries)
    }

    private fun selectRandomWordByCategory(wordCategory: String) {
        when (wordCategory) {
            "fruit" -> {
                val randomIndex = Random.nextInt(GuessWordWords.fruits.size)
                wordToGuess = GuessWordWords.fruits[randomIndex]
                descriptionOfWordToGuess = GuessWordDescriptions.fruits[randomIndex]
            }

            "vegetable" -> {
                val randomIndex = Random.nextInt(GuessWordWords.vegetables.size)
                wordToGuess = GuessWordWords.vegetables[randomIndex]
                descriptionOfWordToGuess = GuessWordDescriptions.vegetables[randomIndex]
            }

            "country" -> {
                val randomIndex = Random.nextInt(GuessWordWords.countries.size)
                wordToGuess = GuessWordWords.countries[randomIndex]
                descriptionOfWordToGuess = GuessWordDescriptions.countries[randomIndex]
            }

            "furniture" -> {
                val randomIndex = Random.nextInt(GuessWordWords.furniture.size)
                wordToGuess = GuessWordWords.furniture[randomIndex]
                descriptionOfWordToGuess = GuessWordDescriptions.furniture[randomIndex]
            }

            "animal" -> {
                val randomIndex = Random.nextInt(GuessWordWords.animals.size)
                wordToGuess = GuessWordWords.animals[randomIndex]
                descriptionOfWordToGuess = GuessWordDescriptions.animals[randomIndex]
            }

            "color" -> {
                val randomIndex = Random.nextInt(GuessWordWords.colors.size)
                wordToGuess = GuessWordWords.colors[randomIndex]
                descriptionOfWordToGuess = GuessWordDescriptions.colors[randomIndex]
            }

            "instrument" -> {
                val randomIndex = Random.nextInt(GuessWordWords.instruments.size)
                wordToGuess = GuessWordWords.instruments[randomIndex]
                descriptionOfWordToGuess = GuessWordDescriptions.instruments[randomIndex]
            }

            "sport" -> {
                val randomIndex = Random.nextInt(GuessWordWords.sports.size)
                wordToGuess = GuessWordWords.sports[randomIndex]
                descriptionOfWordToGuess = GuessWordDescriptions.sports[randomIndex]
            }

            else -> {
                Log.e("GuessWordActivity", "Category not found!")
            }
        }
    }
}