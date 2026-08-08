package com.disciplineos.app.domain

import kotlin.random.Random

object MotivationQuotes {
    private val quotes = listOf(
        "Show up. Especially when you don't want to.",
        "Discipline is remembering what you want.",
        "Excuses don't build anything.",
        "Do it tired. Do it scared. Do it anyway.",
        "Your future self is watching.",
        "Comfort is the enemy today.",
        "One hard day beats ten soft ones.",
        "Stop negotiating with yourself.",
        "Pain is temporary. Quitting echoes.",
        "Be the person who finishes.",
        "No one is coming to save you.",
        "Standards over moods.",
        "You don't need motivation. You need movement.",
        "Make today undeniable.",
        "Weakness asks for later. Strength acts now.",
        "The work doesn't care how you feel.",
        "Earn your rest. Don't steal it.",
        "Consistency is a weapon.",
        "Small reps. Big respect.",
        "Silence the voice that bargains.",
        "Prove it in private.",
        "Hard choices. Easy life.",
        "Today's effort is tomorrow's proof.",
        "Get uncomfortable on purpose.",
        "Win the morning. Own the day.",
        "Discipline compounds. So does laziness.",
        "Be ruthless with your excuses.",
        "Action first. Feelings later.",
        "You are what you repeatedly do.",
        "Finish what you started.",
        "No half measures.",
        "Pressure builds the frame.",
        "Stay sharp. Stay honest.",
        "Outwork yesterday.",
        "Don't break the chain.",
        "Sweat now. Stand taller later.",
        "The standard is the standard.",
        "Lock in.",
        "Boring consistency beats flashy bursts.",
        "Respect the plan.",
        "Miss once, recover twice.",
        "Keep the promise you made to yourself.",
        "Train like it matters — because it does.",
        "Empty the tank.",
        "No audience required.",
        "Hard is the point.",
        "Build the identity, not the excuse.",
        "Clock in. No drama.",
        "Rise. Execute. Repeat.",
        "Make discipline look inevitable.",
    )

    fun forSession(seed: Long = System.currentTimeMillis()): String {
        return quotes[Random(seed).nextInt(quotes.size)]
    }
}
