package com.application.common

sealed class GameState {
    class Running(val lettersUsed: String,
                  val underscoreWord: String,
                    val currentTries: Number,
                    val maxTries: Number) : GameState()
    class Lost(val wordToGuess: String, val descriptionOfWordToGuess: String) : GameState()
    class Won(val wordToGuess: String, val descriptionOfWordToGuess: String) : GameState()
}