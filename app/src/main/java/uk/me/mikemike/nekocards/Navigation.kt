package uk.me.mikemike.nekocards

import androidx.navigation.NavHostController

fun navigateToEditDeck(deckId: Long, nav: NavHostController) {
    nav.navigate("$deckId/edit")
}

fun navigateToQuiz(deckId: Long, nav: NavHostController) {
    nav.navigate("$deckId/quiz")
}


