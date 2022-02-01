package uk.me.mikemike.nekocards

val String.Companion.Empty: String get()  {return ""}

typealias CardMethod = (Card)->Unit
typealias DeckMethod = (Deck) -> Unit

