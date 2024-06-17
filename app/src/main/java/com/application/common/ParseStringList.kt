package com.application.common

class ParseStringList {
    fun main(title: String, listOfStrings: List<String>): String {
        var finalString = title
        for (string in listOfStrings) {
            finalString += if (listOfStrings.indexOf(string) != listOfStrings.size - 1) {
                "$string, "
            } else {
                "$string."
            }
        }
        return finalString
    }
}