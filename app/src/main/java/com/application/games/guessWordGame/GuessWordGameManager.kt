package com.application.games.guessWordGame

import com.application.constants.GameConstantsDescriptions
import com.application.constants.GameConstantsWords
import java.lang.StringBuilder
import kotlin.random.Random

class GuessWordGameManager {
    private var lettersUsed: String = ""
    private lateinit var underscoreWord: String
    private lateinit var wordToGuess: String
    private lateinit var descriptionOfWordToGuess: String
    private val maxTries = 7
    private var currentTries = 0
    fun startNewGame(): GuessWordGameState {
        lettersUsed = ""
        currentTries = 0
        val randomIndex = Random.nextInt(0, GameConstantsWords.words.size)
        wordToGuess = GameConstantsWords.words[randomIndex]
        descriptionOfWordToGuess = GameConstantsDescriptions.descriptions[randomIndex]
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
}