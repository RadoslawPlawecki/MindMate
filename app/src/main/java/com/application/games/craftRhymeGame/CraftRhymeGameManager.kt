package com.application.games.craftRhymeGame

import com.application.constants.GameConstantsRhymingWords
import com.application.constants.GameConstantsWords
import kotlin.random.Random

class CraftRhymeGameManager {
    private var wordsUsed: String = ""
    private lateinit var givenWord: String
    private lateinit var rhymingWords: List<String>
    private val maxTries = 7
    private var currentTries = 0

    fun startNewGame(): CraftRhymeGameState {
        wordsUsed = ""
        currentTries = 0
        val randomIndex = Random.nextInt(0, GameConstantsWords.words.size)
        givenWord = GameConstantsWords.words[randomIndex]
        rhymingWords = GameConstantsRhymingWords.rhymingWords[randomIndex]
        return getGameState("")
    }

    fun play(userWord: String): CraftRhymeGameState {
        wordsUsed += if (currentTries != 6) {
            "$userWord, "
        } else {
            userWord
        }
        currentTries++
        return getGameState(userWord)
    }

    private fun getGameState(userWord: String): CraftRhymeGameState {
        if (CheckIfRhymes(givenWord, userWord).doWordsRhyme() && givenWord.lowercase() != userWord.lowercase()) {
            return CraftRhymeGameState.Won(givenWord, rhymingWords)
        }
        if (currentTries == maxTries) {
            return CraftRhymeGameState.Lost(givenWord, rhymingWords)
        }
        return CraftRhymeGameState.Running(wordsUsed, givenWord, currentTries, maxTries)
    }
}