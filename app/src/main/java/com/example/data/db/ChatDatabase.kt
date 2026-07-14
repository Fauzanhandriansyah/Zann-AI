package com.example.data.db

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "chat_sessions")
data class ChatSession(
    @PrimaryKey val sessionId: String,
    val title: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: String,
    val sender: String, // "user" or "model"
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val fileBase64: String? = null,
    val fileMimeType: String? = null,
    val fileName: String? = null,
    val filePath: String? = null
)

@Entity(tableName = "learned_knowledge")
data class LearnedKnowledge(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sourceSessionId: String,
    val type: String, // e.g. "Koreksi", "Pengetahuan", "Preferensi", "Gaya Bahasa"
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_messages WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    fun getMessagesForSession(sessionId: String): Flow<List<ChatMessage>>

    @Insert
    suspend fun insertMessage(message: ChatMessage)

    @Query("UPDATE chat_messages SET text = :newText WHERE id = :messageId")
    suspend fun updateMessageText(messageId: Long, newText: String)

    @Query("DELETE FROM chat_messages WHERE sessionId = :sessionId AND id > :messageId")
    suspend fun deleteMessagesAfter(sessionId: String, messageId: Long)

    @Query("DELETE FROM chat_messages WHERE sessionId = :sessionId")
    suspend fun clearMessagesForSession(sessionId: String)

    @Query("SELECT * FROM chat_sessions ORDER BY timestamp DESC")
    fun getAllSessions(): Flow<List<ChatSession>>

    @Insert
    suspend fun insertSession(session: ChatSession)

    @Query("UPDATE chat_sessions SET title = :newTitle WHERE sessionId = :sessionId")
    suspend fun updateSessionTitle(sessionId: String, newTitle: String)

    @Query("DELETE FROM chat_sessions WHERE sessionId = :sessionId")
    suspend fun deleteSession(sessionId: String)

    @Query("SELECT * FROM learned_knowledge ORDER BY timestamp DESC")
    fun getAllKnowledge(): Flow<List<LearnedKnowledge>>

    @Query("SELECT * FROM learned_knowledge ORDER BY timestamp DESC LIMIT 50")
    suspend fun getRecentKnowledgeList(): List<LearnedKnowledge>

    @Insert
    suspend fun insertKnowledge(knowledge: LearnedKnowledge)

    @Query("DELETE FROM learned_knowledge WHERE id = :id")
    suspend fun deleteKnowledge(id: Long)

    @Query("DELETE FROM learned_knowledge")
    suspend fun clearAllKnowledge()
}

@Database(entities = [ChatMessage::class, ChatSession::class, LearnedKnowledge::class, FlashcardDeck::class, Flashcard::class, StudyGoal::class, StudyTask::class], version = 7, exportSchema = false)
abstract class ChatDatabase : RoomDatabase() {
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun flashcardDao(): FlashcardDao
    abstract fun plannerDao(): PlannerDao

    companion object {
        @Volatile
        private var INSTANCE: ChatDatabase? = null

        fun getDatabase(context: Context): ChatDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ChatDatabase::class.java,
                    "chat_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
