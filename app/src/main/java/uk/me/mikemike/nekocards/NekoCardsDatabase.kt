package uk.me.mikemike.nekocards

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Card::class, Deck::class], version = 2, exportSchema = false)
abstract class NekoCardsDatabase : RoomDatabase() {

    abstract fun getDeckDataDao(): DeckDataDao

    companion object {

        private var Instance : NekoCardsDatabase? =  null

        fun getDatabase(context: Context): NekoCardsDatabase {
                return Instance ?: synchronized(this){
                    val instance = Room.databaseBuilder(context.applicationContext,
                                NekoCardsDatabase::class.java,
                                "neko_cards").fallbackToDestructiveMigration().build()
                    Instance = instance
                    instance
                }

        }
    }
}