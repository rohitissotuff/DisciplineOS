package com.disciplineos.app.domain

object FeedbackEngine {

    fun messageFor(score: Int): String = when {
        score >= 90 -> "Executed. No excuses left on the table."
        score >= 80 -> "Solid day. Don't get comfortable."
        score >= 70 -> "Bare minimum cleared. Raise the floor."
        score >= 50 -> "Half-measures. You know what you skipped."
        score >= 30 -> "Weak day. The score doesn't lie."
        else -> "You failed the day. Own it and reset tomorrow."
    }

    fun verdictFor(score: Int): String = when {
        score >= 80 -> "PRODUCTIVE"
        score >= 70 -> "ACCEPTABLE"
        score >= 50 -> "BELOW STANDARD"
        else -> "FAILED"
    }
}
