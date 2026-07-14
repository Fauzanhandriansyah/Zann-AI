package com.example.data.repository

import com.example.BuildConfig
import com.example.data.api.Content
import com.example.data.api.GenerateContentRequest
import com.example.data.api.Part
import com.example.data.api.InlineData
import com.example.data.api.RetrofitClient
import com.example.data.db.Flashcard
import com.example.data.db.FlashcardDao
import com.example.data.db.FlashcardDeck
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.json.JSONArray

import com.example.data.api.GenerationConfig
import org.json.JSONObject

class FlashcardRepository(private val flashcardDao: FlashcardDao) {

    val allDecks: Flow<List<FlashcardDeck>> = flashcardDao.getAllDecks()

    fun getCardsForDeck(deckId: Long): Flow<List<Flashcard>> {
        return flashcardDao.getCardsForDeck(deckId)
    }

    suspend fun createDeckWithCards(title: String, description: String, sourceMaterial: String, deckType: String = "biasa", cards: List<Flashcard>): Long = withContext(Dispatchers.IO) {
        val deckId = flashcardDao.insertDeck(
            FlashcardDeck(
                title = title,
                description = description,
                sourceMaterial = sourceMaterial,
                deckType = deckType
            )
        )
        val cardsWithId = cards.map { it.copy(deckId = deckId) }
        flashcardDao.insertCards(cardsWithId)
        deckId
    }

    suspend fun updateCard(card: Flashcard) = withContext(Dispatchers.IO) {
        flashcardDao.updateCard(card)
    }

    suspend fun deleteCard(card: Flashcard) = withContext(Dispatchers.IO) {
        flashcardDao.deleteCard(card)
    }

    suspend fun deleteDeck(deckId: Long) = withContext(Dispatchers.IO) {
        flashcardDao.deleteCardsForDeck(deckId)
        flashcardDao.deleteDeck(deckId)
    }

    suspend fun updateDeckType(deckId: Long, newType: String) = withContext(Dispatchers.IO) {
        flashcardDao.updateDeckType(deckId, newType)
    }

    suspend fun resetDeckProgress(deckId: Long) = withContext(Dispatchers.IO) {
        flashcardDao.resetDeckProgress(deckId)
    }

    suspend fun generateFlashcardsMedia(
        sourceText: String,
        fileBase64: String? = null,
        fileMimeType: String? = null,
        count: Int = 10
    ): List<Flashcard> = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            throw Exception("API Key Gemini belum dikonfigurasi di panel Secrets AI Studio.")
        }

        val promptText = """
            Anda adalah pakar pendidik dan pembuat media pembelajaran interaktif.
            Buatkan tepat $count kartu kuis kustom (flashcard) tanya-jawab yang komprehensif, edukatif, dan interaktif dari teks materi pelajaran, catatan, artikel, atau dokumen lampiran berikut ini.

            Materi Pembelajaran:
            $sourceText

            Persyaratan Pembuatan Kartu Kuis:
            1. Buat tepat $count kartu kuis yang mencakup konsep-konsep kunci dan detail penting dari materi di atas.
            2. Pertanyaan ("question") harus bervariasi, cerdas, menantang, dan mendidik (bisa berupa tebakan istilah, definisi, pertanyaan analitis, atau aplikasi konsep).
            3. Jawaban ("answer") harus singkat, padat, informatif, dan mudah dipahami.
            4. Berikan penjelasan tambahan ("explanation") singkat (maksimal 2 kalimat) untuk mengontekstualisasikan jawaban jika diperlukan.
            5. Gunakan bahasa Indonesia yang baik, ramah, profesional, dan mudah dipelajari.

            Formulasikan respon Anda dalam format JSON Array murni yang valid seperti ini, tanpa prefiks markdown atau '```json' atau apapun:
            [
              {
                "question": "Apa nama planet terdekat dari matahari?",
                "answer": "Merkurius",
                "explanation": "Merkurius memiliki orbit yang sangat dekat dengan matahari dan tidak memiliki atmosfer tebal untuk menahan panas."
              }
            ]
            
            Pastikan respon Anda murni JSON agar langsung bisa diparse dalam aplikasi. Jangan tambahkan kata pengantar atau penjelasan lain di luar JSON ini.
        """.trimIndent()

        val promptParts = mutableListOf<Part>()
        if (fileBase64 != null && fileMimeType != null) {
            promptParts.add(Part(inlineData = InlineData(mimeType = fileMimeType, data = fileBase64)))
        }
        promptParts.add(Part(text = promptText))

        val request = GenerateContentRequest(
            contents = listOf(
                Content(
                    role = "user",
                    parts = promptParts
                )
            ),
            generationConfig = GenerationConfig(
                responseMimeType = "application/json"
            )
        )

        var responseText: String? = null
        var lastErr: Exception? = null
        val modelsToTry = listOf("gemini-2.5-flash", "gemini-1.5-flash", "gemini-3.1-flash-lite-preview")

        for (m in modelsToTry) {
            try {
                val response = RetrofitClient.service.generateContent(
                    model = m,
                    apiKey = apiKey,
                    request = request
                )
                val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (!text.isNullOrBlank()) {
                    responseText = text
                    break
                }
            } catch (ex: Exception) {
                lastErr = ex
            }
        }

        if (responseText.isNullOrBlank()) {
            throw Exception("Gagal terhubung dengan Gemini API untuk menghasilkan flashcard. Kendala terakhir: ${lastErr?.localizedMessage ?: "Respon kosong."}")
        }

        // Clean JSON formatting robustly by extracting the JSON Array block
        val startArray = responseText.indexOf('[')
        val endArray = responseText.lastIndexOf(']')
        var cleanedText = if (startArray != -1 && endArray != -1 && endArray > startArray) {
            responseText.substring(startArray, endArray + 1).trim()
        } else {
            responseText.trim()
                .removePrefix("```json")
                .removePrefix("```")
                .removeSuffix("```")
                .trim()
        }

        val flashcards = mutableListOf<Flashcard>()
        try {
            var jsonArray: JSONArray? = null
            try {
                jsonArray = JSONArray(cleanedText)
            } catch (ex: Exception) {
                val startObj = responseText.indexOf('{')
                val endObj = responseText.lastIndexOf('}')
                if (startObj != -1 && endObj != -1 && endObj > startObj) {
                    val objText = responseText.substring(startObj, endObj + 1).trim()
                    val rootObj = JSONObject(objText)
                    val keys = rootObj.keys()
                    while (keys.hasNext()) {
                        val key = keys.next()
                        val possibleArray = rootObj.optJSONArray(key)
                        if (possibleArray != null) {
                            jsonArray = possibleArray
                            break
                        }
                    }
                }
            }

            if (jsonArray == null) {
                throw Exception("Respon tidak berisi array JSON yang valid.")
            }

            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val question = obj.optString("question", "").trim()
                val answer = obj.optString("answer", "").trim()
                val explanation = obj.optString("explanation", "").trim()
                
                if (question.isNotEmpty() && answer.isNotEmpty()) {
                    flashcards.add(
                        Flashcard(
                            deckId = 0,
                            question = question,
                            answer = answer,
                            explanation = if (explanation.isEmpty()) null else explanation
                        )
                    )
                }
            }
        } catch (e: Exception) {
            throw Exception("Gagal mem-parsing kartu kuis dari respon AI. Silakan coba lagi.\nRespon AI: $responseText\nDetail: ${e.localizedMessage}")
        }

        if (flashcards.isEmpty()) {
            throw Exception("Gagal membuat kartu kuis atau kartu kosong. Pastikan materi masukan berisi teks yang memadai.")
        }

        flashcards
    }
}
