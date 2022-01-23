package uk.me.mikemike.nekocards


import androidx.room.*
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable

@Entity(
    foreignKeys = [ForeignKey(
        entity = Deck::class,
        parentColumns = arrayOf("deckId"),
        childColumns = arrayOf("deckId"),
        onDelete = ForeignKey.CASCADE
    )]
)

@Serializable
data class Card (
    @PrimaryKey(autoGenerate = true) var cardId: Long,
    @ColumnInfo(name="deckId", index = true)
    var deckId: Long,
    @ColumnInfo(name = "side_a") var sideA: String,
    @ColumnInfo(name = "side_b") var sideB: String
)

@Serializable
@Entity
data class Deck (
    @PrimaryKey(autoGenerate = true) var deckId: Long,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "description") var description: String,
    @ColumnInfo(name = "side_a_name") var sideAName: String,
    @ColumnInfo(name = "side_b_name") var sideBName: String
    ){

    /* HACK to force StateFlow to re emit this is as new value in QuizViewModel  */
    override fun equals(other: Any?): Boolean {
        return false
    }
}


@Serializable
data class DeckWithCards (
    @Embedded var deck : Deck,
    @Relation(
        parentColumn = "deckId",
        entityColumn = "deckId"
    )
    var cards: List<Card>
    )

@Dao
interface DeckDataDao {

    @Query("SELECT * FROM deck")
    fun getAllDecks() : Flow<List<Deck>>

    @Query("SELECT * FROM card WHERE deckId = :deckId")
    fun getAllCardsForDeck(deckId: Long): Flow<List<Card>>

    @Query("SELECT * FROM card")
    fun getAllCards(): Flow<List<Card>>

    @Transaction
    @Query("SELECT * FROM deck WHERE deckId = :id")
    fun getDeckWithCardsById(id: Long) : Flow<DeckWithCards?>

    @Insert
    suspend fun insertDeck(deck: Deck): Long
    @Insert
    suspend fun insertCard(card: Card): Long
    @Insert
    suspend fun insertCard(cards: List<Card>): List<Long>
    @Insert
    suspend fun insertDeck(decks: List<Deck>): List<Long>

    @Update
    suspend fun updateDeck(deck: Deck): Int
    @Update
    suspend fun updateCard(card: Card): Int

    @Delete
    suspend fun deleteDeck(decks: Deck): Int
    @Delete
    suspend fun deleteCard(card: Card): Int


    @Transaction
    suspend fun insert(deckAndCards: DeckWithCards): Long{
        val deckId = insertDeck(deckAndCards.deck)
        if(deckId < 0){
            return 0
        }
        deckAndCards.cards.forEach { i -> i.deckId = deckId }
        insertCard(deckAndCards.cards)
        return deckId
    }


}


class DeckDataRepository(private val deckDao: DeckDataDao) {
    val allDecks = deckDao.getAllDecks();
    fun getDeckWIthCardsById(id: Long) : Flow<DeckWithCards?>{
        return deckDao.getDeckWithCardsById(id)
    }

    fun getAllCardsForDeck(deckId: Long): Flow<List<Card>>
    {
        return deckDao.getAllCardsForDeck(deckId)
    }

    suspend fun insert(deck: DeckWithCards): Long{
        return deckDao.insert(deck)
    }

    suspend fun delete(card: Card): Int {
        return deckDao.deleteCard(card)
    }

    suspend fun insert(card: Card): Long {
        return deckDao.insertCard(card)
    }

    suspend fun updateDeck(deck: Deck): Int {
        return deckDao.updateDeck(deck)
    }

    suspend fun updateCard(card: Card): Int {
        return deckDao.updateCard(card)
    }

    suspend fun deleteDeck(deck: Deck): Int {
        return deckDao.deleteDeck(deck)
    }
}

fun createTestDeck(name: String, description: String, noOfCards: Int): DeckWithCards{
    val deck = Deck(1, name, description, "side a", "side b")
    var cards = mutableListOf<Card>()
    repeat(noOfCards){
        cards.add(Card( it.toLong(), deck.deckId, "side a $it", "side b $it"  ))
    }
  return DeckWithCards(deck, cards)
}

