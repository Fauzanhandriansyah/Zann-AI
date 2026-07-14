package com.example.ui

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Base64
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.ChatDatabase
import com.example.data.db.Flashcard
import com.example.data.db.FlashcardDeck
import com.example.data.repository.FlashcardRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FlashcardViewModel(application: Application) : AndroidViewModel(application) {

    private val db = ChatDatabase.getDatabase(application)
    private val repository = FlashcardRepository(db.flashcardDao())

    val allDecks: StateFlow<List<FlashcardDeck>> = repository.allDecks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI generation inputs
    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    private val _deckTitleInput = MutableStateFlow("")
    val deckTitleInput: StateFlow<String> = _deckTitleInput.asStateFlow()

    private val _cardCountInput = MutableStateFlow(10)
    val cardCountInput: StateFlow<Int> = _cardCountInput.asStateFlow()

    private val _deckTypeInput = MutableStateFlow("biasa") // "biasa" or "pilihan_ganda"
    val deckTypeInput: StateFlow<String> = _deckTypeInput.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _generateError = MutableStateFlow<String?>(null)
    val generateError: StateFlow<String?> = _generateError.asStateFlow()

    // Attached Image references for generation
    private val _attachedFileBase64 = MutableStateFlow<String?>(null)
    val attachedFileBase64: StateFlow<String?> = _attachedFileBase64.asStateFlow()

    private val _attachedFileMimeType = MutableStateFlow<String?>(null)
    val attachedFileMimeType: StateFlow<String?> = _attachedFileMimeType.asStateFlow()

    private val _attachedFileName = MutableStateFlow<String?>(null)
    val attachedFileName: StateFlow<String?> = _attachedFileName.asStateFlow()

    private val _attachedFilePath = MutableStateFlow<String?>(null)
    val attachedFilePath: StateFlow<String?> = _attachedFilePath.asStateFlow()

    // Active deck study states
    private val _activeDeck = MutableStateFlow<FlashcardDeck?>(null)
    val activeDeck: StateFlow<FlashcardDeck?> = _activeDeck.asStateFlow()

    private val _activeCards = MutableStateFlow<List<Flashcard>>(emptyList())
    val activeCards: StateFlow<List<Flashcard>> = _activeCards.asStateFlow()

    private val _currentCardIndex = MutableStateFlow(0)
    val currentCardIndex: StateFlow<Int> = _currentCardIndex.asStateFlow()

    private val _isCardFlipped = MutableStateFlow(false)
    val isCardFlipped: StateFlow<Boolean> = _isCardFlipped.asStateFlow()

    // Study session evaluation progress map (id or index to difficulty status: "Paham" or "Perlu Belajar")
    private val _cardStatuses = MutableStateFlow<Map<Long, String>>(emptyMap())
    val cardStatuses: StateFlow<Map<Long, String>> = _cardStatuses.asStateFlow()

    private val _isStudySessionFinished = MutableStateFlow(false)
    val isStudySessionFinished: StateFlow<Boolean> = _isStudySessionFinished.asStateFlow()

    // NEW FEATURES INJECTED AS REQUESTED
    // Feature 1: Persistent Starred flashcard IDs
    private val starredPrefs = application.getSharedPreferences("starred_flashcards", Context.MODE_PRIVATE)
    private val _starredCardIds = MutableStateFlow<Set<Long>>(emptySet())
    val starredCardIds: StateFlow<Set<Long>> = _starredCardIds.asStateFlow()

    // Feature 2: Only study starred cards mode configuration
    private val _onlyStudyStarred = MutableStateFlow(false)
    val onlyStudyStarred: StateFlow<Boolean> = _onlyStudyStarred.asStateFlow()

    // Feature 3: Persistent Study Session History
    data class StudyHistoryItem(
        val deckId: Long,
        val deckTitle: String,
        val timestamp: Long,
        val scorePercent: Int,
        val totalCards: Int
    )
    private val historyPrefs = application.getSharedPreferences("study_history", Context.MODE_PRIVATE)
    private val _studyHistory = MutableStateFlow<List<StudyHistoryItem>>(emptyList())
    val studyHistory: StateFlow<List<StudyHistoryItem>> = _studyHistory.asStateFlow()

    // Feature A & E: Study Streaks & Daily Goal Tracker
    private val streakPrefs = application.getSharedPreferences("study_streaks_goals", Context.MODE_PRIVATE)

    private val _studyStreak = MutableStateFlow(0)
    val studyStreak: StateFlow<Int> = _studyStreak.asStateFlow()

    private val _dailyGoal = MutableStateFlow(10) // Customizable goal card review count
    val dailyGoal: StateFlow<Int> = _dailyGoal.asStateFlow()

    private val _dailyReviewedCards = MutableStateFlow(0)
    val dailyReviewedCards: StateFlow<Int> = _dailyReviewedCards.asStateFlow()

    init {
        loadStarredCards()
        loadHistory()
        loadStreakAndGoals()
    }

    private fun loadStreakAndGoals() {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        val todayStr = sdf.format(java.util.Date())
        val lastStudyStr = streakPrefs.getString("last_study_date", "") ?: ""
        
        val currentStreak = streakPrefs.getInt("study_streak", 0)
        _dailyGoal.value = streakPrefs.getInt("daily_goal", 10)
        
        if (lastStudyStr == todayStr) {
            _studyStreak.value = currentStreak
            _dailyReviewedCards.value = streakPrefs.getInt("reviewed_today", 0)
        } else {
            // Check if streak was broken (last study was more than 1 day ago)
            var activeStreak = currentStreak
            if (lastStudyStr.isNotEmpty()) {
                try {
                    val todayDate = sdf.parse(todayStr)
                    val lastDate = sdf.parse(lastStudyStr)
                    if (todayDate != null && lastDate != null) {
                        val diffMs = todayDate.time - lastDate.time
                        val diffDays = diffMs / (1000 * 60 * 60 * 24)
                        if (diffDays > 1L) {
                            activeStreak = 0 // broke streak
                            streakPrefs.edit().putInt("study_streak", 0).apply()
                        }
                    } else {
                        activeStreak = 0
                    }
                } catch (e: Exception) {
                    activeStreak = 0
                }
            } else {
                activeStreak = 0
            }
            _studyStreak.value = activeStreak
            _dailyReviewedCards.value = 0
        }
    }

    fun recordStudyActivity() {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        val todayStr = sdf.format(java.util.Date())
        val lastStudyStr = streakPrefs.getString("last_study_date", "") ?: ""
        
        if (lastStudyStr == todayStr) {
            val reviewedToday = _dailyReviewedCards.value + 1
            _dailyReviewedCards.value = reviewedToday
            streakPrefs.edit().putInt("reviewed_today", reviewedToday).apply()
            return
        }
        
        val currentStreak = streakPrefs.getInt("study_streak", 0)
        val updatedStreak = if (lastStudyStr.isNotEmpty()) {
            try {
                val todayDate = sdf.parse(todayStr)
                val lastDate = sdf.parse(lastStudyStr)
                if (todayDate != null && lastDate != null) {
                    val diffMs = todayDate.time - lastDate.time
                    val diffDays = diffMs / (1000 * 60 * 60 * 24)
                    if (diffDays == 1L) {
                        currentStreak + 1
                    } else if (diffDays > 1L) {
                        1
                    } else {
                        currentStreak
                    }
                } else 1
            } catch (e: Exception) {
                1
            }
        } else {
            1
        }
        
        _studyStreak.value = updatedStreak
        _dailyReviewedCards.value = 1
        
        streakPrefs.edit()
            .putInt("study_streak", updatedStreak)
            .putString("last_study_date", todayStr)
            .putInt("reviewed_today", 1)
            .apply()
    }

    fun setDailyGoal(goalCount: Int) {
        _dailyGoal.value = goalCount
        streakPrefs.edit().putInt("daily_goal", goalCount).apply()
    }

    private fun loadStarredCards() {
        val saved = starredPrefs.getStringSet("starred_ids", emptySet()) ?: emptySet()
        _starredCardIds.value = saved.mapNotNull { it.toLongOrNull() }.toSet()
    }

    fun toggleStarCard(cardId: Long) {
        val current = _starredCardIds.value
        val updated = if (current.contains(cardId)) {
            current - cardId
        } else {
            current + cardId
        }
        _starredCardIds.value = updated
        starredPrefs.edit().putStringSet("starred_ids", updated.map { it.toString() }.toSet()).apply()
    }

    fun toggleOnlyStudyStarred() {
        _onlyStudyStarred.value = !_onlyStudyStarred.value
    }

    private fun loadHistory() {
        val raw = historyPrefs.getString("items", "") ?: ""
        if (raw.isNotEmpty()) {
            val items = raw.split("||").mapNotNull { line ->
                val parts = line.split("|")
                if (parts.size >= 5) {
                    StudyHistoryItem(
                        deckId = parts[0].toLongOrNull() ?: 0L,
                        deckTitle = parts[1],
                        timestamp = parts[2].toLongOrNull() ?: 0L,
                        scorePercent = parts[3].toIntOrNull() ?: 0,
                        totalCards = parts[4].toIntOrNull() ?: 0
                    )
                } else null
            }
            _studyHistory.value = items.sortedByDescending { it.timestamp }
        }
    }

    fun saveStudySessionItem(deckId: Long, deckTitle: String, scorePercent: Int, totalCards: Int) {
        val item = StudyHistoryItem(deckId, deckTitle, System.currentTimeMillis(), scorePercent, totalCards)
        val currentList = _studyHistory.value + item
        _studyHistory.value = currentList.sortedByDescending { it.timestamp }
        
        val serialized = currentList.joinToString("||") { 
            "${it.deckId}|${it.deckTitle}|${it.timestamp}|${it.scorePercent}|${it.totalCards}"
        }
        historyPrefs.edit().putString("items", serialized).apply()
    }

    fun clearAllHistory() {
        _studyHistory.value = emptyList()
        historyPrefs.edit().remove("items").apply()
    }

    fun updateInputText(text: String) {
        _inputText.value = text
    }

    fun updateDeckTitleInput(title: String) {
        _deckTitleInput.value = title
    }

    fun updateCardCountInput(count: Int) {
        _cardCountInput.value = count
    }

    fun updateDeckTypeInput(type: String) {
        _deckTypeInput.value = type
    }

    fun clearError() {
        _generateError.value = null
    }

    fun attachFile(name: String, mimeType: String, base64: String, path: String? = null) {
        _attachedFileName.value = name
        _attachedFileMimeType.value = mimeType
        _attachedFileBase64.value = base64
        _attachedFilePath.value = path
    }

    fun clearAttachedFile() {
        _attachedFileName.value = null
        _attachedFileMimeType.value = null
        _attachedFileBase64.value = null
        _attachedFilePath.value = null
    }

    fun selectDeckForStudy(deck: FlashcardDeck) {
        _activeDeck.value = deck
        _currentCardIndex.value = 0
        _isCardFlipped.value = false
        _isStudySessionFinished.value = false
        _cardStatuses.value = emptyMap()
        _activeCards.value = emptyList()

        viewModelScope.launch {
            repository.getCardsForDeck(deck.id).collectLatest { cards ->
                val filtered = if (_onlyStudyStarred.value) {
                    val starred = cards.filter { _starredCardIds.value.contains(it.id) }
                    if (starred.isEmpty()) {
                        // Safe fallback: if user checked only-starred option but didn't star any card, show all
                        cards
                    } else {
                        starred
                    }
                } else {
                    cards
                }
                _activeCards.value = filtered
            }
        }
    }

    fun closeActiveStudy() {
        _activeDeck.value = null
        _activeCards.value = emptyList()
        _currentCardIndex.value = 0
        _isCardFlipped.value = false
        _isStudySessionFinished.value = false
        _cardStatuses.value = emptyMap()
    }

    fun flipCard() {
        _isCardFlipped.value = !_isCardFlipped.value
    }

    fun setCardDifficulty(card: Flashcard, status: String) {
        val updatedStatuses = _cardStatuses.value + (card.id to status)
        _cardStatuses.value = updatedStatuses
        
        // Record studious metrics activity for Streak & Daily goals on action
        recordStudyActivity()

        // Persist to database
        viewModelScope.launch {
            repository.updateCard(card.copy(difficulty = status))
        }

        // Proceed to next card
        val currentIndex = _currentCardIndex.value
        val totalCards = _activeCards.value.size
        
        if (currentIndex < totalCards - 1) {
            viewModelScope.launch {
                kotlinx.coroutines.delay(200) // Small delay for elegant feel
                _currentCardIndex.value = currentIndex + 1
                _isCardFlipped.value = false
            }
        } else {
            _isStudySessionFinished.value = true
        }
    }

    fun resetStudySession() {
        _currentCardIndex.value = 0
        _isCardFlipped.value = false
        _isStudySessionFinished.value = false
        _cardStatuses.value = emptyMap()
    }

    fun deleteDeck(deck: FlashcardDeck) {
        viewModelScope.launch {
            repository.deleteDeck(deck.id)
            if (_activeDeck.value?.id == deck.id) {
                closeActiveStudy()
            }
        }
    }

    fun toggleDeckType(deck: FlashcardDeck) {
        viewModelScope.launch {
            val newType = if (deck.deckType == "biasa") "pilihan_ganda" else "biasa"
            repository.updateDeckType(deck.id, newType)
            if (_activeDeck.value?.id == deck.id) {
                _activeDeck.value = _activeDeck.value?.copy(deckType = newType)
            }
        }
    }

    fun resetDeckProgress(deckId: Long) {
        viewModelScope.launch {
            repository.resetDeckProgress(deckId)
        }
    }

    fun generateAndSaveDeck() {
        val title = _deckTitleInput.value.trim()
        val text = _inputText.value.trim()
        val count = _cardCountInput.value
        
        if (title.isEmpty()) {
            _generateError.value = "Judul Dek tidak boleh kosong."
            return
        }

        if (text.isEmpty() && _attachedFileBase64.value == null) {
            _generateError.value = "Silakan masukkan materi teks pelajaran atau lampirkan foto catatan pelajaran Anda."
            return
        }

        _isGenerating.value = true
        _generateError.value = null

        viewModelScope.launch {
            try {
                val cards = repository.generateFlashcardsMedia(
                    sourceText = text,
                    fileBase64 = _attachedFileBase64.value,
                    fileMimeType = _attachedFileMimeType.value,
                    count = count
                )

                val generatedDeckId = repository.createDeckWithCards(
                    title = title,
                    description = if (text.length > 80) text.take(77) + "..." else text,
                    sourceMaterial = text,
                    deckType = _deckTypeInput.value,
                    cards = cards
                )

                // Select this deck immediately for study!
                val newDeck = FlashcardDeck(
                    id = generatedDeckId,
                    title = title,
                    description = if (text.length > 80) text.take(77) + "..." else text,
                    sourceMaterial = text,
                    deckType = _deckTypeInput.value
                )
                
                // Clear inputs
                _inputText.value = ""
                _deckTitleInput.value = ""
                _deckTypeInput.value = "biasa"
                clearAttachedFile()
                
                selectDeckForStudy(newDeck)
            } catch (e: Exception) {
                _generateError.value = e.message ?: "Terjadi kesalahan tidak terduga saat membuat kartu."
            } finally {
                _isGenerating.value = false
            }
        }
    }

    fun deleteCard(card: Flashcard) {
        viewModelScope.launch {
            repository.deleteCard(card)
            val currentCards = _activeCards.value
            val currentIndex = _currentCardIndex.value
            if (currentCards.size <= 1) {
                _isStudySessionFinished.value = true
            } else {
                if (currentIndex >= currentCards.size - 1) {
                    _currentCardIndex.value = currentCards.size - 2
                }
                _isCardFlipped.value = false
            }
        }
    }

    fun editCard(card: Flashcard, newQuestion: String, newAnswer: String, newExplanation: String?) {
        viewModelScope.launch {
            val updated = card.copy(
                question = newQuestion,
                answer = newAnswer,
                explanation = if (newExplanation.isNullOrBlank()) null else newExplanation
            )
            repository.updateCard(updated)
        }
    }

    fun loadSampleMaterial() {
        _deckTitleInput.value = "Sistem Tata Surya (Materi IPA)"
        _inputText.value = """
            Sistem Tata Surya kita terdiri atas matahari sebagai pusatnya, delapan planet utama, satelit alami, asteroid, meteoroid, dan komet.
            
            Matahari adalah bintang raksasa kuning yang menghasilkan gravitasinya sendiri untuk mengikat seluruh elemen tata surya.
            Pluto dahulunya dianggap sebagai planet kesembilan, namun pada tahun 2006 IAU mengklasifikasikannya sebagai planet kerdil karena tidak dapat membersihkan orbitnya dari benda lain.
            
            Planet-planet dibagi menjadi dua kelompok besar:
            1. Planet Terestrial (kebumian/berbatu): Merkurius, Venus, Bumi, dan Mars. Merkurius adalah planet terkecil dan terdekat. Venus adalah planet terpanas karena efek rumah kaca ekstrem, sering dijuluki bintang fajar. Bumi adalah satu-satunya planet yang diketahui menyokong kehidupan dengan air cair melimpah. Mars sering dijuluki planet merah karena kandungan besi oksida di permukaannya.
            2. Planet Jovian (raksasa gas): Jupiter, Saturnus, Uranus, dan Neptunus. Jupiter adalah planet terbesar dengan bintik merah besar (badai raksasa). Saturnus terkenal dengan cincin es spektakulernya yang sangat tebal dan indah. Uranus berwarna biru kehijauan karena metana di atmosfernya dan berputar miring hampir 90 derajat. Neptunus adalah planet paling luar dengan angin terkencang di tata surya kita.
            
            Sabuk asteroid utama terletak di antara orbit planet Mars dan Jupiter, memisahkan planet dalam dengan planet luar. Komet adalah benda es kecil yang menghasilkan ekor gas bersinar saat mendekati matahari.
        """.trimIndent()
    }
}
