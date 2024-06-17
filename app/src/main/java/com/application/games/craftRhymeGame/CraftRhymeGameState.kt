package com.application.games.craftRhymeGame

sealed class CraftRhymeGameState {
    class Running(val wordsUsed: String, val givenWord: String, val currentTries: Number, val maxTries: Number) : CraftRhymeGameState()
    class Lost(val givenWord: String, val rhymingWords: List<String>) : CraftRhymeGameState()
    class Won(val givenWord: String, val rhymingWords: List<String>) : CraftRhymeGameState()
}