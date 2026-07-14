package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.ChatDatabase
import com.example.data.db.ChatMessage
import com.example.data.db.ChatSession
import com.example.data.repository.ChatRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val db = ChatDatabase.getDatabase(application)
    private val repository = ChatRepository(db.chatMessageDao())

    val sessions: StateFlow<List<ChatSession>> = repository.allSessions
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _currentSessionId = MutableStateFlow<String?>(null)
    val currentSessionId: StateFlow<String?> = _currentSessionId.asStateFlow()

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Model and attachment states
    private val _selectedModel = MutableStateFlow("gemini-2.5-flash")
    val selectedModel: StateFlow<String> = _selectedModel.asStateFlow()

    private val _attachedFileBase64 = MutableStateFlow<String?>(null)
    val attachedFileBase64: StateFlow<String?> = _attachedFileBase64.asStateFlow()

    private val _attachedFileMimeType = MutableStateFlow<String?>(null)
    val attachedFileMimeType: StateFlow<String?> = _attachedFileMimeType.asStateFlow()

    private val _attachedFileName = MutableStateFlow<String?>(null)
    val attachedFileName: StateFlow<String?> = _attachedFileName.asStateFlow()

    private val _attachedFilePath = MutableStateFlow<String?>(null)
    val attachedFilePath: StateFlow<String?> = _attachedFilePath.asStateFlow()

    private val prefs = application.getSharedPreferences("zann_preferences", android.content.Context.MODE_PRIVATE)
    private val _isDarkTheme = MutableStateFlow(prefs.getBoolean("is_dark_theme", true))
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    fun toggleTheme() {
        val newValue = !_isDarkTheme.value
        _isDarkTheme.value = newValue
        prefs.edit().putBoolean("is_dark_theme", newValue).apply()
    }

    private val _appThemeId = MutableStateFlow(prefs.getString("app_theme_id", "cyber_fusion") ?: "cyber_fusion")
    val appThemeId: StateFlow<String> = _appThemeId.asStateFlow()

    fun setAppThemeId(themeId: String) {
        _appThemeId.value = themeId
        prefs.edit().putString("app_theme_id", themeId).apply()
    }

    private val _isGoogleSearchEnabled = MutableStateFlow(prefs.getBoolean("is_google_search_enabled", false))
    val isGoogleSearchEnabled: StateFlow<Boolean> = _isGoogleSearchEnabled.asStateFlow()

    fun toggleGoogleSearch() {
        val newValue = !_isGoogleSearchEnabled.value
        _isGoogleSearchEnabled.value = newValue
        prefs.edit().putBoolean("is_google_search_enabled", newValue).apply()
    }

    // Feature B & C: Persistent Starred Sessions, Chat Search queries, Star Filters
    private val _starredSessionIds = MutableStateFlow<Set<String>>(emptySet())
    val starredSessionIds: StateFlow<Set<String>> = _starredSessionIds.asStateFlow()

    private val _chatSearchQuery = MutableStateFlow("")
    val chatSearchQuery: StateFlow<String> = _chatSearchQuery.asStateFlow()

    private val _filterOnlyStarredChats = MutableStateFlow(false)
    val filterOnlyStarredChats: StateFlow<Boolean> = _filterOnlyStarredChats.asStateFlow()

    private fun loadStarredSessions() {
        val saved = prefs.getStringSet("starred_session_ids", emptySet()) ?: emptySet()
        _starredSessionIds.value = saved
    }

    fun toggleStarSession(sessionId: String) {
        val current = _starredSessionIds.value
        val updated = if (current.contains(sessionId)) {
            current - sessionId
        } else {
            current + sessionId
        }
        _starredSessionIds.value = updated
        prefs.edit().putStringSet("starred_session_ids", updated).apply()
    }

    fun setChatSearchQuery(query: String) {
        _chatSearchQuery.value = query
    }

    fun toggleFilterOnlyStarredChats() {
        _filterOnlyStarredChats.value = !_filterOnlyStarredChats.value
    }

    private var messageCollectionJob: Job? = null

    init {
        loadStarredSessions()
        // Start fresh with a new chat on application startup instead of restoring the previous session automatically
    }

    fun setModel(model: String) {
        _selectedModel.value = model
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

    fun selectSession(sessionId: String?) {
        _currentSessionId.value = sessionId
        messageCollectionJob?.cancel()
        if (sessionId != null) {
            messageCollectionJob = viewModelScope.launch {
                repository.getMessagesForSession(sessionId).collect { list ->
                    _messages.value = list
                }
            }
        } else {
            _messages.value = emptyList()
        }
    }

    fun createNewChat() {
        selectSession(null)
    }

    fun sendMessage(text: String) {
        val trimmedText = text.trim()
        val fileBase64 = _attachedFileBase64.value
        val fileMimeType = _attachedFileMimeType.value
        val fileName = _attachedFileName.value
        val filePath = _attachedFilePath.value

        if (trimmedText.isBlank() && fileBase64 == null) return

        viewModelScope.launch {
            var activeSessionId = _currentSessionId.value
            
            // Format descriptive title from the user's first prompt or file name
            val derivedTitle = if (trimmedText.isNotEmpty()) {
                if (trimmedText.length > 25) trimmedText.take(22) + "..." else trimmedText
            } else if (fileName != null) {
                fileName
            } else if (fileMimeType?.startsWith("audio/") == true) {
                "Pesan Suara"
            } else {
                "Percakapan Baru"
            }

            if (activeSessionId == null) {
                // Initialize new chat session dynamically
                val newId = UUID.randomUUID().toString()
                val newSession = ChatSession(
                    sessionId = newId,
                    title = derivedTitle
                )
                repository.insertSession(newSession)
                selectSession(newId)
                activeSessionId = newId
            }

            // 1. Get snapshot of current history BEFORE inserting the new message.
            val history = _messages.value.toList()

            _isLoading.value = true

            // Generate localized display format for local database storage
            val userMsgText = buildString {
                if (fileMimeType?.startsWith("audio/") == true) {
                    append("[voice_note:$filePath]")
                    if (trimmedText.isNotBlank()) {
                        append("\n\n$trimmedText")
                    }
                } else if (fileName != null) {
                    val label = if (fileMimeType?.startsWith("image/") == true) "Gambar" else "Berkas"
                    append("[$label: $fileName]")
                    if (trimmedText.isNotBlank()) {
                        append("\n\n$trimmedText")
                    }
                } else {
                    append(trimmedText)
                }
            }

            // 2. Insert user message in database
            val userMsg = ChatMessage(
                sessionId = activeSessionId,
                sender = "user",
                text = userMsgText,
                fileBase64 = fileBase64,
                fileMimeType = fileMimeType,
                fileName = fileName,
                filePath = filePath
            )
            repository.insertMessage(userMsg)

            // Clear the input attachment state in UI
            clearAttachedFile()

            // 3. Generate content reply using selected model and potential files
            val modelId = _selectedModel.value
            val reply = repository.getGeminiResponse(
                history = history,
                prompt = trimmedText.ifBlank { "Lihat lampiran dan berikan tanggapan." },
                model = modelId,
                fileBase64 = fileBase64,
                fileMimeType = fileMimeType,
                enableGoogleSearch = _isGoogleSearchEnabled.value
            )

            // 4. Save AI's response
            val modelMsg = ChatMessage(
                sessionId = activeSessionId,
                sender = "model",
                text = reply
            )
            repository.insertMessage(modelMsg)

            _isLoading.value = false
            runContinuousLearningAnalysis()
        }
    }

    suspend fun getLiveResponse(text: String): String {
        val trimmedText = text.trim()
        if (trimmedText.isBlank()) return ""

        var activeSessionId = _currentSessionId.value
        
        if (activeSessionId == null) {
            val newId = java.util.UUID.randomUUID().toString()
            val newSession = ChatSession(
                sessionId = newId,
                title = if (trimmedText.length > 25) trimmedText.take(22) + "..." else trimmedText
            )
            repository.insertSession(newSession)
            selectSession(newId)
            activeSessionId = newId
        }

        val history = _messages.value.toList()
        
        val userMsg = ChatMessage(
            sessionId = activeSessionId,
            sender = "user",
            text = trimmedText
        )
        repository.insertMessage(userMsg)

        val modelId = _selectedModel.value
        val reply = repository.getGeminiResponse(
            history = history,
            prompt = trimmedText,
            model = modelId,
            enableGoogleSearch = _isGoogleSearchEnabled.value
        )

        val modelMsg = ChatMessage(
            sessionId = activeSessionId,
            sender = "model",
            text = reply
        )
        repository.insertMessage(modelMsg)

        return reply
    }

    fun editUserMessage(messageId: Long, newText: String) {
        val sessionId = _currentSessionId.value ?: return
        val trimmedText = newText.trim()
        if (trimmedText.isBlank()) return

        viewModelScope.launch {
            _isLoading.value = true
            
            val allCurrentMessages = _messages.value
            val index = allCurrentMessages.indexOfFirst { it.id == messageId }
            if (index == -1) {
                _isLoading.value = false
                return@launch
            }
            
            val historyBefore = allCurrentMessages.take(index)
            
            val originalText = allCurrentMessages[index].text
            val finalUpdatedText = if (originalText.startsWith("[")) {
                val lineBreakIndex = originalText.indexOf("\n\n")
                if (lineBreakIndex != -1) {
                    val mediaHeader = originalText.substring(0, lineBreakIndex)
                    "$mediaHeader\n\n$trimmedText"
                } else {
                    "$originalText\n\n$trimmedText"
                }
            } else {
                trimmedText
            }

            repository.updateMessageText(messageId, finalUpdatedText)
            repository.deleteMessagesAfter(sessionId, messageId)
            
            val modelId = _selectedModel.value
            val reply = repository.getGeminiResponse(
                history = historyBefore,
                prompt = trimmedText,
                model = modelId,
                enableGoogleSearch = _isGoogleSearchEnabled.value
            )
            
            val modelMsg = ChatMessage(
                sessionId = sessionId,
                sender = "model",
                text = reply
            )
            repository.insertMessage(modelMsg)
            
            _isLoading.value = false
            runContinuousLearningAnalysis()
        }
    }

    fun editAiResponse(messageId: Long, newText: String) {
        val trimmedText = newText.trim()
        if (trimmedText.isBlank()) return
        viewModelScope.launch {
            repository.updateMessageText(messageId, trimmedText)
            runContinuousLearningAnalysis()
        }
    }

    fun regenerateResponse(modelMessageId: Long) {
        val sessionId = _currentSessionId.value ?: return
        viewModelScope.launch {
            _isLoading.value = true
            val allCurrentMessages = _messages.value
            val aiIndex = allCurrentMessages.indexOfFirst { it.id == modelMessageId }
            if (aiIndex == -1 || aiIndex == 0) {
                _isLoading.value = false
                return@launch
            }
            
            // Preceding message is the user prompt message
            val userMsg = allCurrentMessages[aiIndex - 1]
            if (userMsg.sender != "user") {
                _isLoading.value = false
                return@launch
            }
            
            val historyBefore = allCurrentMessages.take(aiIndex - 1)
            
            // Delete the incorrect/failed model message and any messages after it
            repository.deleteMessagesAfter(sessionId, userMsg.id)
            
            // Extract pure text prompt (strip media header if any)
            val originalText = userMsg.text
            val promptText = if (originalText.startsWith("[")) {
                val idx = originalText.indexOf("\n\n")
                if (idx != -1) originalText.substring(idx + 2) else ""
            } else {
                originalText
            }
            
            val modelId = _selectedModel.value
            val reply = repository.getGeminiResponse(
                history = historyBefore,
                prompt = promptText.ifBlank { "Lihat lampiran dan berikan tanggapan." },
                model = modelId,
                enableGoogleSearch = _isGoogleSearchEnabled.value
            )
            
            val modelMsg = ChatMessage(
                sessionId = sessionId,
                sender = "model",
                text = reply
            )
            repository.insertMessage(modelMsg)
            _isLoading.value = false
            runContinuousLearningAnalysis()
        }
    }

    fun clearChat() {
        val activeSessionId = _currentSessionId.value
        if (activeSessionId != null) {
            viewModelScope.launch {
                repository.clearMessagesForSession(activeSessionId)
            }
        }
    }

    fun deleteSession(sessionId: String) {
        viewModelScope.launch {
            repository.deleteSession(sessionId)
            if (_currentSessionId.value == sessionId) {
                val remaining = sessions.value.filter { it.sessionId != sessionId }
                if (remaining.isNotEmpty()) {
                    selectSession(remaining.first().sessionId)
                } else {
                    selectSession(null)
                }
            }
        }
    }

    fun updateSessionTitle(sessionId: String, newTitle: String) {
        viewModelScope.launch {
            repository.updateSessionTitle(sessionId, newTitle)
        }
    }

    fun deleteSessions(sessionIds: Collection<String>) {
        viewModelScope.launch {
            sessionIds.forEach { sessionId ->
                repository.deleteSession(sessionId)
            }
            if (_currentSessionId.value in sessionIds) {
                val remaining = sessions.value.filter { it.sessionId !in sessionIds }
                if (remaining.isNotEmpty()) {
                    selectSession(remaining.first().sessionId)
                } else {
                    selectSession(null)
                }
            }
        }
    }

    fun analyzeHomeworkPhoto(
        base64: String,
        mimeType: String,
        fileName: String,
        taskCategory: String,
        requestedAction: String,
        additionalDetail: String
    ) {
        if (base64.isBlank()) return

        viewModelScope.launch {
            var activeSessionId = _currentSessionId.value
            val label = "Analisis: $taskCategory"
            
            if (activeSessionId == null) {
                val newId = UUID.randomUUID().toString()
                val newSession = ChatSession(
                    sessionId = newId,
                    title = label
                )
                repository.insertSession(newSession)
                selectSession(newId)
                activeSessionId = newId
            }

            // 1. Get snapshot of current history BEFORE inserting the new message.
            val history = _messages.value.toList()

            _isLoading.value = true

            // Local user display message indicating attached homework and the task detail
            val userMsgText = buildString {
                append("[Gambar: $fileName]")
                append("\n\n📸 **Analisis Foto Tugas ($taskCategory)**")
                append("\n🎯 **Tindakan:** $requestedAction")
                if (additionalDetail.isNotBlank()) {
                    append("\n📝 **Catatan:** $additionalDetail")
                }
            }

            // 2. Insert user message in database
            val userMsg = ChatMessage(
                sessionId = activeSessionId,
                sender = "user",
                text = userMsgText,
                fileBase64 = base64,
                fileMimeType = mimeType,
                fileName = fileName
            )
            repository.insertMessage(userMsg)

            // Clear the input attachment state in UI
            clearAttachedFile()

            // 3. Generate content reply using selected model and potential files
            val modelId = _selectedModel.value
            
            // Build structured prompt instruct for Gemini
            val prompt = """
                Saya mengunggah foto tugas/pekerjaan dengan rincian berikut:
                - Kategori Tugas: $taskCategory (bisa berupa Soal Matematika, Kode Program, atau Dokumen)
                - Tindakan yang diminta: $requestedAction
                - Informasi/Catatan khusus tambahan dari saya: ${if (additionalDetail.isBlank()) "Tidak ada" else additionalDetail}

                Tolong bertindak sebagai asisten tutor akademik ahli Zann AI. Tanggapi foto tugas yang saya kirimkan dan berikan analisis selengkap mungkin sesuai dengan tindakan yang saya minta:
                1. Jelaskan isi soal/konten secara ringkas dan mudah dipahami.
                2. Berikan langkah penyelesaian secara runtut, logis, mendalam, dan terstruktur.
                3. Tunjukkan letak kesalahan secara tepat (jika ada kesalahan pada gambar/kode/pengerjaan tersebut) serta berikan pembetulan atau saran perbaikan yang akurat.

                Gunakan format Markdown yang sangat rapi (gunakan bold, bullet points, numbered lists, atau blok kode jika perlu) untuk memberikan tanggapan yang indah dan mudah dibaca. Jawablah menggunakan Bahasa Indonesia.
            """.trimIndent()

            val reply = repository.getGeminiResponse(
                history = history,
                prompt = prompt,
                model = modelId,
                fileBase64 = base64,
                fileMimeType = mimeType,
                enableGoogleSearch = _isGoogleSearchEnabled.value
            )

            // 4. Save AI's response
            val modelMsg = ChatMessage(
                sessionId = activeSessionId,
                sender = "model",
                text = reply
            )
            repository.insertMessage(modelMsg)

            _isLoading.value = false
            runContinuousLearningAnalysis()
        }
    }

    val learnedKnowledge: StateFlow<List<com.example.data.db.LearnedKnowledge>> = repository.allLearnedKnowledge
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _isAnalyzingLearning = MutableStateFlow(false)
    val isAnalyzingLearning: StateFlow<Boolean> = _isAnalyzingLearning.asStateFlow()

    fun deleteLearnedKnowledge(id: Long) {
        viewModelScope.launch {
            repository.deleteLearnedKnowledge(id)
        }
    }

    fun clearAllLearnedKnowledge() {
        viewModelScope.launch {
            repository.clearAllLearnedKnowledge()
        }
    }

    fun addNewManualKnowledge(type: String, content: String) {
        viewModelScope.launch {
            val sessionId = _currentSessionId.value ?: "manual"
            repository.insertLearnedKnowledge(
                com.example.data.db.LearnedKnowledge(
                    sourceSessionId = sessionId,
                    type = type,
                    content = content
                )
            )
        }
    }

    fun runContinuousLearningAnalysis(onComplete: (Int) -> Unit = {}) {
        val sessionId = _currentSessionId.value ?: return
        val currentMsgs = _messages.value
        if (currentMsgs.isEmpty()) return

        viewModelScope.launch {
            _isAnalyzingLearning.value = true
            try {
                // Compile the chat history into a string
                val chatLog = currentMsgs.joinToString("\n") { 
                    "${it.sender.uppercase()}: ${it.text}"
                }

                val prompt = """
                    Analisis riwayat obrolan berikut dan identifikasi apakah ada koreksi yang diberikan pengguna terhadap kesalahan Anda, fakta or informasi baru yang diajarkan atau dijelaskan pengguna kepada Anda, preferensi pengguna (seperti bahasa, gaya bahasa, topik favorit, atau nama pengguna), keinginan khusus pengguna, atau instruksi/cara merespon yang harus dipatuhi di masa depan.
                    
                    Riwayat obrolan:
                    $chatLog
                    
                    Format output Anda WAJIB menggunakan format berikut atau 'NONE' jika tidak ada sesuatu baru yang perlu dipelajari atau diperbaiki:
                    [Kategori]|[Informasi pengetahuan/koreksi ringkas dan berharga]
                    
                    Ketentuan:
                    - Gunakan persis salah satu kategori berikut untuk [Kategori]: "Koreksi", "Pengetahuan", "Preferensi", "Gaya Bahasa".
                    - Tiap item dipisahkan oleh baris baru.
                    - Tulis dalam bahasa Indonesia yang ringkas, objektif, dan padat (maksimal 1 kalimat per info belajar).
                    - Jika tidak ada yang perlu dipelajari, balaskan 'NONE'. Jangan output apa-apa selain format di atas.
                """.trimIndent()

                // Call Gemini to do the analysis
                val reply = repository.getGeminiResponse(
                    history = emptyList(),
                    prompt = prompt,
                    model = "gemini-2.5-flash",
                    enableGoogleSearch = false
                )

                var itemsCount = 0
                if (reply.trim() != "NONE" && !reply.contains("NONE", ignoreCase = true)) {
                    val lines = reply.split("\n")
                    lines.forEach { line ->
                        if (line.contains("|")) {
                            val parts = line.split("|", limit = 2)
                            val rawType = parts.firstOrNull()?.replace("[", "")?.replace("]", "")?.trim() ?: "Pengetahuan"
                            val content = parts.getOrNull(1)?.trim() ?: ""
                            if (content.isNotEmpty() && content.length > 5) {
                                repository.insertLearnedKnowledge(
                                    com.example.data.db.LearnedKnowledge(
                                        sourceSessionId = sessionId,
                                        type = rawType,
                                        content = content
                                    )
                                )
                                itemsCount++
                            }
                        }
                    }
                }
                onComplete(itemsCount)
            } catch (e: Exception) {
                e.printStackTrace()
                onComplete(0)
            } finally {
                _isAnalyzingLearning.value = false
            }
        }
    }

    suspend fun getGeminiResponseDirect(prompt: String): String {
        return repository.getGeminiResponse(
            history = emptyList(),
            prompt = prompt,
            model = _selectedModel.value,
            enableGoogleSearch = false
        )
    }
}
