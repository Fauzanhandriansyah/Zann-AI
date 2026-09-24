package com.example.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "flashcard_decks")
data class FlashcardDeck(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val sourceMaterial: String,
    val timestamp: Long = System.currentTimeMillis(),
    val deckType: String = "biasa" // "biasa" or "pilihan_ganda"
)

@Entity(tableName = "flashcards")
data class Flashcard(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val deckId: Long,
    val question: String,
    val answer: String,
    val explanation: String? = null,
    val difficulty: String = "Belum Diuji", // "Belum Diuji", "Paham", "Perlu Belajar"
    val timestamp: Long = System.currentTimeMillis()
)

@Dao
interface FlashcardDao {
    @Query("SELECT * FROM flashcard_decks ORDER BY timestamp DESC")
    fun getAllDecks(): Flow<List<FlashcardDeck>>

    @Query("SELECT * FROM flashcards WHERE deckId = :deckId ORDER BY id ASC")
    fun getCardsForDeck(deckId: Long): Flow<List<Flashcard>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeck(deck: FlashcardDeck): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCards(cards: List<Flashcard>)

    @Update
    suspend fun updateCard(card: Flashcard)

    @Delete
    suspend fun deleteCard(card: Flashcard)

    @Query("DELETE FROM flashcard_decks WHERE id = :deckId")
    suspend fun deleteDeck(deckId: Long)

    @Query("DELETE FROM flashcards WHERE deckId = :deckId")
    suspend fun deleteCardsForDeck(deckId: Long)

    @Query("UPDATE flashcard_decks SET deckType = :newType WHERE id = :deckId")
    suspend fun updateDeckType(deckId: Long, newType: String)

    @Query("UPDATE flashcards SET difficulty = 'Belum Diuji' WHERE deckId = :deckId")
    suspend fun resetDeckProgress(deckId: Long)
}
