package uk.me.mikemike.nekocards

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException


@RunWith(AndroidJUnit4::class)
class DeckDaoTest {
    private lateinit var database: NekoCardsDatabase
    private lateinit var deckDataDao: DeckDataDao


    @Before
    fun create() {
        val context: Context = ApplicationProvider.getApplicationContext()
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        database = Room.inMemoryDatabaseBuilder(context, NekoCardsDatabase::class.java)
            // Allowing main thread queries, just for testing.
            .allowMainThreadQueries()
            .build()
        deckDataDao = database.getDeckDataDao()
    }

    @Test
    @Throws(Exception::class)
    fun addDeckAndGetSameDeck() = runBlocking {
        var deck = Deck(0,"Test", "Description", "sidea", "sideb")
        deckDataDao.insertDeck(deck)
        assertEquals(true, deckDataDao.getAllDecks().first()[0].name.equals("Test"))
    }

    @Test
    @Throws(Exception::class)
    fun addMultipleDecksAndGetAll() = runBlocking {
        var decks = listOf(Deck(0, "Deck1", "Description1", "sidea", "sideb"),
                                Deck(0, "Deck2", "Description2", "sidea", "sideb"),
                            Deck(0, "Deck3", "Description3", "sidea", "sideb"))
        assertEquals(deckDataDao.insertDeck(decks).size, decks.size)
        assertEquals(deckDataDao.getAllDecks().first().size, decks.size)
    }


    @Test
    @Throws(Exception::class)
    fun addCardAndGetSameCard() = runBlocking {
        // we need a valid deck
        val id = deckDataDao.insertDeck(Deck(0, "deck", "deckdesc", "sidea", "sideb"))
        val card = Card(0, id, "a", "b")
        assertEquals(true, deckDataDao.insertCard(card) > 0)
        assertEquals(1, deckDataDao.getAllCards().first().size)
    }

    @Test
    @Throws(Exception::class)
    fun addDeckWithCardsAndGet() = runBlocking {
        val deck = DeckWithCards(Deck(0, "deck1", "deck2", "sidea", "sideb"),
                       listOf(Card(0, 0, "a", "b"),
                               Card(0, 0, "a2", "b2")))
        val id = deckDataDao.insert(deck)
        assertEquals(true, id > 0)
        assertEquals(2, deckDataDao.getAllCards().first().size)
        assertEquals(1, deckDataDao.getAllDecks().first().size)
    }


    @Test
    @Throws(Exception::class)
    fun addDeckWithCardsAndGetDeckWithCards() = runBlocking {
        val deck = DeckWithCards(Deck(0, "deck1", "deck2", "sidea", "sideb"),
            listOf(Card(0, 0, "a", "b"),
                Card(0, 0, "a2", "b2")))
        val id = deckDataDao.insert(deck)
        assertEquals(true, id > 0)
        val cards = deckDataDao.getAllCards().first()

        assertEquals(2,cards.size )
        for (card in cards){
            assertEquals("Inserted Card's deck id is wrong", card.deckId, id)
        }
        assertEquals(1, deckDataDao.getAllDecks().first().size)

        val deck2 = deckDataDao.getDeckWithCardsById(id).first()
        assertEquals(2, deck2!!.cards.size)
    }



    @Test
    @Throws(Exception::class)
    fun insertAndGetSingleDeck() = runBlocking{
        val d1 = Deck(0, "deck1", "deck1description", "sidea", "sideb")
        val d2 = Deck(0, "deck2", "deck2description", "sidea", "sideb")
        deckDataDao.insertDeck(d1)
        val id = deckDataDao.insertDeck(d2)

        val getDeck = deckDataDao.getDeckWithCardsById(id).first()
        assertNotNull(getDeck)
        assertEquals(getDeck?.deck?.deckId, id)

    }




    @After
    @Throws(IOException::class)
    fun close() {
        database.close()
    }


}