package com.application.games.craftRhymeGame

class CheckIfRhymes(private val firstWord: String, private val secondWord: String) {
    fun doWordsRhyme(): Boolean {
        val firstWordPhonetic = getPhoneticRepresentation(firstWord)
        val secondWordPhonetic = getPhoneticRepresentation(secondWord)
        val firstWordStressed = getStressedPart(firstWordPhonetic)
        val secondWordStressed = getStressedPart(secondWordPhonetic)
        return firstWordStressed == secondWordStressed
    }

    private fun getPhoneticRepresentation(word: String): String {
        return word.lowercase()
    }

    private fun getStressedPart(word: String): String {
        val vowels = "aeiou"
        for (i in word.length - 1 downTo 0) {
            if (word[i] in vowels) {
                return word.substring(i)
            }
        }
        return word
    }
}