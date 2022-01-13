package uk.me.mikemike.nekocards

import android.app.Application

class NekoCardsApplication: Application() {

    val database  by lazy{ NekoCardsDatabase.getDatabase(this)}
    val deckDataRepository by lazy { DeckDataRepository(database.getDeckDataDao())}

}