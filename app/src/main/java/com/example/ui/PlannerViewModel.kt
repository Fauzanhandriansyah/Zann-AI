package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.ChatDatabase
import com.example.data.db.StudyGoal
import com.example.data.db.StudyTask
import com.example.data.repository.PlannerRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.json.JSONArray

@OptIn(ExperimentalCoroutinesApi::class)
class PlannerViewModel(application: Application) : AndroidViewModel(application) {

    private val db = ChatDatabase.getDatabase(application)
    private val repository = PlannerRepository(db.plannerDao())

    val allGoals: StateFlow<List<StudyGoal>> = repository.allGoals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _activeGoal = MutableStateFlow<StudyGoal?>(null)
    val activeGoal: StateFlow<StudyGoal?> = _activeGoal.asStateFlow()

    val activeTasks: StateFlow<List<StudyTask>> = _activeGoal
        .flatMapLatest { goal ->
            if (goal != null) {
                repository.getTasksForGoal(goal.id)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI generation inputs
    private val _goalTitleInput = MutableStateFlow("")
    val goalTitleInput: StateFlow<String> = _goalTitleInput.asStateFlow()

    private val _goalSubjectInput = MutableStateFlow("")
    val goalSubjectInput: StateFlow<String> = _goalSubjectInput.asStateFlow()

    private val _goalDateInput = MutableStateFlow("Besok")
    val goalDateInput: StateFlow<String> = _goalDateInput.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _generateError = MutableStateFlow<String?>(null)
    val generateError: StateFlow<String?> = _generateError.asStateFlow()

    fun updateTitleInput(value: String) { _goalTitleInput.value = value }
    fun updateSubjectInput(value: String) { _goalSubjectInput.value = value }
    fun updateDateInput(value: String) { _goalDateInput.value = value }

    fun selectGoal(goal: StudyGoal?) {
        _activeGoal.value = goal
    }

    fun toggleTaskCompletion(task: StudyTask) {
        viewModelScope.launch {
            repository.updateTaskCompletion(task.id, !task.isCompleted)
        }
    }

    fun deleteGoal(goalId: Long) {
        viewModelScope.launch {
            repository.deleteGoal(goalId)
            if (_activeGoal.value?.id == goalId) {
                _activeGoal.value = null
            }
        }
    }

    fun addManualTask(goalId: Long, title: String, description: String) {
        viewModelScope.launch {
            repository.addManualSubTask(
                StudyTask(
                    goalId = goalId,
                    title = title,
                    description = description,
                    isCompleted = false
                )
            )
        }
    }

    fun deleteSubTask(task: StudyTask) {
        viewModelScope.launch {
            repository.deleteSubTask(task)
        }
    }

    fun createGoalWithAi(
        title: String,
        subject: String,
        targetDate: String,
        getGeminiResponse: suspend (String) -> String,
        onSuccess: () -> Unit
    ) {
        if (title.isBlank() || subject.isBlank()) {
            _generateError.value = "Judul dan subjek tidak boleh kosong."
            return
        }

        viewModelScope.launch {
            _isGenerating.value = true
            _generateError.value = null

            val prompt = """
                Pecahkan topik belajar berikut menjadi 5 subtopik atau langkah belajar yang terstruktur, konkret, sangat detail, dan berurutan dari dasar hingga mahir.
                
                Topik Belajar: "$title"
                Subjek: "$subject"
                Target Selesai: "$targetDate"
                
                HARAP berikan output hanya dan harus dalam format JSON raw array of objects (tanpa penjelasan tambahan di luar JSON) seperti berikut:
                [
                  {"title": "Nama subtopik 1", "description": "Ulasan singkat apa yang dipelajari"},
                  {"title": "Nama subtopik 2", "description": "Ulasan singkat apa yang dipelajari"},
                  ...
                ]
                
                Pastikan JSON valid dan berbahasa Indonesia yang baik dan menarik bagi pelajar.
            """.trimIndent()

            try {
                val response = getGeminiResponse(prompt)
                
                // Lenient cleaner for response (stripping markdown code blocks like ```json)
                var cleanJson = response.trim()
                if (cleanJson.contains("```")) {
                    val firstIndex = cleanJson.indexOf("[")
                    val lastIndex = cleanJson.lastIndexOf("]")
                    if (firstIndex != -1 && lastIndex != -1 && lastIndex > firstIndex) {
                        cleanJson = cleanJson.substring(firstIndex, lastIndex + 1)
                    }
                }

                val jsonArray = JSONArray(cleanJson)
                val subTasks = mutableListOf<StudyTask>()

                // Create goal first to get ID
                val goalId = repository.insertGoal(
                    StudyGoal(
                        title = title,
                        subject = subject,
                        targetDate = targetDate
                    )
                )

                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    val tTitle = obj.optString("title", "Langkah ${i + 1}")
                    val tDesc = obj.optString("description", "")
                    subTasks.add(
                        StudyTask(
                            goalId = goalId,
                            title = tTitle,
                            description = tDesc,
                            isCompleted = false,
                            orderIndex = i
                        )
                    )
                }

                repository.insertSubTasks(subTasks)
                
                // Clear state inputs
                _goalTitleInput.value = ""
                _goalSubjectInput.value = ""
                _goalDateInput.value = "Besok"
                
                _isGenerating.value = false
                onSuccess()
            } catch (e: Exception) {
                _isGenerating.value = false
                _generateError.value = "Gagal memproses respons AI: ${e.message}. Silakan coba lagi."
            }
        }
    }

    fun createGoalManual(
        title: String,
        subject: String,
        targetDate: String,
        onSuccess: () -> Unit
    ) {
        if (title.isBlank() || subject.isBlank()) {
            _generateError.value = "Judul dan subjek tidak boleh kosong."
            return
        }

        viewModelScope.launch {
            _generateError.value = null
            try {
                val goalId = repository.insertGoal(
                    StudyGoal(
                        title = title,
                        subject = subject,
                        targetDate = targetDate
                    )
                )

                // Add initial empty default subtopic
                repository.addManualSubTask(
                    StudyTask(
                        goalId = goalId,
                        title = "Pendahuluan",
                        description = "Baca materi awal mengenai topik $title",
                        isCompleted = false,
                        orderIndex = 0
                    )
                )

                _goalTitleInput.value = ""
                _goalSubjectInput.value = ""
                _goalDateInput.value = "Besok"

                onSuccess()
            } catch (e: Exception) {
                _generateError.value = "Gagal menyimpan: ${e.message}"
            }
        }
    }
}
