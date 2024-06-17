package com.application.games.guessWordGame

sealed class GuessWordGameState {
    class Running(val lettersUsed: String,
                  val underscoreWord: String,
                    val currentTries: Number,
                    val maxTries: Number) : GuessWordGameState()
    class Lost(val wordToGuess: String, val descriptionOfWordToGuess: String) : GuessWordGameState()
    class Won(val wordToGuess: String, val descriptionOfWordToGuess: String) : GuessWordGameState()
}