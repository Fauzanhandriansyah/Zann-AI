package com.example.data.repository

import com.example.BuildConfig
import com.example.data.api.Content
import com.example.data.api.GenerateContentRequest
import com.example.data.api.Part
import com.example.data.api.RetrofitClient
import com.example.data.api.Tool
import com.example.data.db.ChatMessage
import com.example.data.db.ChatMessageDao
import com.example.data.db.ChatSession
import kotlinx.coroutines.flow.Flow
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import org.json.JSONArray
import java.net.URLEncoder
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers

class ChatRepository(private val chatMessageDao: ChatMessageDao) {

    val allSessions: Flow<List<ChatSession>> = chatMessageDao.getAllSessions()

    fun getMessagesForSession(sessionId: String): Flow<List<ChatMessage>> {
        return chatMessageDao.getMessagesForSession(sessionId)
    }

    suspend fun insertSession(session: ChatSession) {
        chatMessageDao.insertSession(session)
    }

    suspend fun updateSessionTitle(sessionId: String, newTitle: String) {
        chatMessageDao.updateSessionTitle(sessionId, newTitle)
    }

    suspend fun deleteSession(sessionId: String) {
        chatMessageDao.deleteSession(sessionId)
        chatMessageDao.clearMessagesForSession(sessionId)
    }

    suspend fun insertMessage(message: ChatMessage) {
        chatMessageDao.insertMessage(message)
    }

    suspend fun updateMessageText(messageId: Long, newText: String) {
        chatMessageDao.updateMessageText(messageId, newText)
    }

    suspend fun deleteMessagesAfter(sessionId: String, messageId: Long) {
        chatMessageDao.deleteMessagesAfter(sessionId, messageId)
    }

    suspend fun clearMessagesForSession(sessionId: String) {
        chatMessageDao.clearMessagesForSession(sessionId)
    }

    suspend fun getGeminiResponse(
        history: List<ChatMessage>,
        prompt: String,
        model: String,
        fileBase64: String? = null,
        fileMimeType: String? = null,
        enableGoogleSearch: Boolean = true
    ): String {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return "Zann AI Error: API Key Gemini belum dikonfigurasi di panel Secrets AI Studio. Silakan konfigurasi kunci Anda dengan nama GEMINI_API_KEY."
        }

        // Map database history into Gemini Contents API format (using "user" and "model")
        // To guarantee alternating roles, consecutive identical roles are merged into a single Content turn.
        val formattedContents = mutableListOf<Content>()
        history.forEach { msg ->
            val role = if (msg.sender == "user") "user" else "model"
            if (formattedContents.isNotEmpty() && formattedContents.last().role == role) {
                // Merge parts if the role matches the previous turn
                val updatedParts = formattedContents.last().parts.toMutableList()
                updatedParts.add(Part(text = msg.text))
                formattedContents[formattedContents.lastIndex] = Content(role = role, parts = updatedParts)
            } else {
                formattedContents.add(Content(role = role, parts = listOf(Part(text = msg.text))))
            }
        }
        
        // Add current prompt + file/audio if available
        val promptParts = mutableListOf<Part>()
        if (fileBase64 != null && fileMimeType != null) {
            promptParts.add(Part(inlineData = com.example.data.api.InlineData(mimeType = fileMimeType, data = fileBase64)))
        }
        promptParts.add(Part(text = prompt))
        
        if (formattedContents.isNotEmpty() && formattedContents.last().role == "user") {
            // Merge into previous user turn if the last turn was also user
            val updatedParts = formattedContents.last().parts.toMutableList()
            updatedParts.addAll(promptParts)
            formattedContents[formattedContents.lastIndex] = Content(role = "user", parts = updatedParts)
        } else {
            formattedContents.add(Content(role = "user", parts = promptParts))
        }

        val learnedList = chatMessageDao.getRecentKnowledgeList()
        val learnedContext = if (learnedList.isNotEmpty()) {
            "\n\n[MEMORI PEMBELAJARAN BERKELANJUTAN - KNOWLEDGE BASE ANDA]:\n" +
            "Berikut adalah pengetahuan/koreksi penting yang telah diajarkan pengguna kepada Anda sebelumnya. Anda WAJIB mematuhinya secara konsisten dalam obrolan saat ini:\n" +
            learnedList.joinToString("\n") { "- [Kategori: ${it.type}]: ${it.content}" }
        } else ""

        val lowerPrompt = prompt.lowercase()
        var injectedRealTimeContext = ""

        if (lowerPrompt.contains("cuaca") || lowerPrompt.contains("weather") || lowerPrompt.contains("suhu ")) {
            val words = prompt.split(" ", "?", ",")
            val indexDi = words.indexOfFirst { it.equals("di", ignoreCase = true) }
            val indexIn = words.indexOfFirst { it.equals("in", ignoreCase = true) }
            val targetIndex = if (indexDi != -1) indexDi + 1 else if (indexIn != -1) indexIn + 1 else -1
            
            var cityName = ""
            if (targetIndex != -1 && targetIndex < words.size) {
                cityName = words[targetIndex].trim().replace(Regex("[^a-zA-Z0-9]"), "")
            }
            
            if (cityName.isBlank() || cityName.length <= 2) {
                val filtered = words.filter { 
                    val w = it.lowercase()
                    w != "cuaca" && w != "hari" && w != "ini" && w != "suhu" && w != "bagaimana" && w != "apakah" && w != "esok" && w != "besok" && w != "sekarang" && w != "weather" && w != "forecast" && w != "info" && w != "informasi" && w.isNotBlank()
                }
                if (filtered.isNotEmpty()) {
                    cityName = filtered.joinToString(" ")
                }
            }
            
            if (cityName.isNotBlank() && cityName.length > 2) {
                val weatherData = withContext(Dispatchers.IO) {
                    RealTimeDataFetcher.fetchWeather(cityName)
                }
                if (weatherData != null) {
                    injectedRealTimeContext = "\n\n[DATA CUACA REAL-TIME SEKETIKA UNTUK PROMPT: $cityName (SUMBER: OPEN-METEO)]:\n" +
                        "Berikut adalah informasi cuaca aktual untuk lokasi tersebut. Gunakan data asli di bawah ini secara langsung dan integrasikan secara natural ke respons Anda:\n" +
                        "$weatherData\n\n"
                }
            }
        } else if (lowerPrompt.contains("berita") || lowerPrompt.contains("news") || lowerPrompt.contains("headline") || lowerPrompt.contains("kabar terkini") || lowerPrompt.contains("kejadian hari ini")) {
            var category = "general"
            if (lowerPrompt.contains("teknologi") || lowerPrompt.contains("tech") || lowerPrompt.contains("gadget")) {
                category = "technology"
            } else if (lowerPrompt.contains("bisnis") || lowerPrompt.contains("business") || lowerPrompt.contains("saham") || lowerPrompt.contains("ekonomi")) {
                category = "business"
            } else if (lowerPrompt.contains("olahraga") || lowerPrompt.contains("sports") || lowerPrompt.contains("bola")) {
                category = "sports"
            } else if (lowerPrompt.contains("sains") || lowerPrompt.contains("science") || lowerPrompt.contains("iptek")) {
                category = "science"
            } else if (lowerPrompt.contains("hiburan") || lowerPrompt.contains("entertainment") || lowerPrompt.contains("film") || lowerPrompt.contains("musik")) {
                category = "entertainment"
            } else if (lowerPrompt.contains("kesehatan") || lowerPrompt.contains("health") || lowerPrompt.contains("medis")) {
                category = "health"
            }
            
            val newsData = withContext(Dispatchers.IO) {
                RealTimeDataFetcher.fetchNews(category)
            }
            
            if (newsData != null) {
                injectedRealTimeContext = "\n\n[RINGKASAN DATA BERITA TERBARU & TERPANTAU REAL-TIME (KATEGORI: ${category.uppercase()})]:\n" +
                    "Berikut adalah daftar berita utama berita terbaru hari ini:\n" +
                    newsData + "\n\n" +
                    "Tolong buat ringkasan ulasan berita utama/headlines tersebut secara ramah, informatif, dan sangat menarik bagi pengguna.\n\n"
            }
        }

        // System instructions detailing the model capabilities
        val customSystemSuffix = when (model) {
            "gemini-3.1-flash-lite-preview" -> "Berikan jawaban dengan sangat cepat, singkat, padat, langsung ke inti penjelasan, tapi tetap sangat jelas dan informatif."
            else -> "Berikan jawaban bantuan serbaguna yang handal, berimbang, ramah, dan membantu secara umum."
        }

        val systemInstruction = Content(
            role = "system",
            parts = listOf(Part(text = "Anda adalah Zann AI, asisten AI online cerdas ultra-modern yang mampu menjawab semua jenis pertanyaan secara real-time, akurat, informatif, mendalam, dan membantu. Anda ramah, komunikatif, dan interaktif. Anda harus mendeteksi bahasa (seperti Bahasa Indonesia, Jawa, Sunda, Inggris, Arab, dll.) dari input atau pertanyaan terakhir pengguna, dan menjawab menggunakan bahasa yang persis sama dengan yang digunakan pengguna secara otomatis dan natural. PENTING: Jangan pernah menyebutkan nama pembuat Anda (Fauzan Handriansyah) kecuali jika pengguna secara eksplisit menanyakannya kepada Anda. Jangan pernah menyertakan informasi pembuat ini di luar pertanyaan tersebut atau di akhir jawaban obrolan biasa. Hanya jika ditanya tentang pembuat, pengembang, atau perancang Anda, barulah Anda menyebutkan bahwa Anda dibuat oleh Fauzan Handriansyah, seorang pengembang perangkat lunak berdedikasi. $customSystemSuffix$learnedContext$injectedRealTimeContext"))
        )

        val request = GenerateContentRequest(
            contents = formattedContents,
            systemInstruction = systemInstruction,
            tools = if (enableGoogleSearch) listOf(Tool(googleSearch = emptyMap())) else null
        )

        return try {
            val response = RetrofitClient.service.generateContent(model, apiKey, request)
            val candidate = response.candidates?.firstOrNull()
            var rawText = candidate?.content?.parts?.firstOrNull()?.text
                ?: "Maaf, Zann AI tidak dapat memproses atau menemukan jawaban untuk pertanyaan tersebut saat ini."
            
            // Extract grounding metadata search results if available
            val metadata = candidate?.groundingMetadata
            val chunks = metadata?.groundingChunks
            if (!chunks.isNullOrEmpty()) {
                val uniqueSources = chunks.mapNotNull { it.web }
                    .filter { !it.uri.isNullOrBlank() }
                    .distinctBy { it.uri }
                
                if (uniqueSources.isNotEmpty()) {
                    val citationBuilder = StringBuilder()
                    citationBuilder.append("\n\n---\n🌐 **Sitasi & Referensi:**\n")
                    uniqueSources.forEachIndexed { index, source ->
                        val title = if (!source.title.isNullOrBlank()) source.title else "Sumber Terverifikasi"
                        citationBuilder.append("${index + 1}. [${title}](${source.uri})\n")
                    }
                    rawText += citationBuilder.toString()
                }
            }
            rawText
        } catch (e: retrofit2.HttpException) {
            val modelName = when (model) {
                "gemini-3.1-flash-lite-preview" -> "Flash-Lite"
                "gemini-2.5-flash" -> "Flash"
                else -> model
            }

            if (enableGoogleSearch && request.tools != null) {
                try {
                    val fallbackRequest = GenerateContentRequest(
                        contents = formattedContents,
                        systemInstruction = systemInstruction,
                        tools = null
                    )
                    val response = RetrofitClient.service.generateContent(model, apiKey, fallbackRequest)
                    val candidate = response.candidates?.firstOrNull()
                    var rawText = candidate?.content?.parts?.firstOrNull()?.text
                        ?: "Maaf, Zann AI tidak dapat memproses atau menemukan jawaban untuk pertanyaan tersebut saat ini."
                    
                    rawText += "\n\n💡 *Catatan: Fitur Sitasi & Pencarian Web (Grounding) dinonaktifkan otomatis untuk pertanyaan ini karena model membatasi (Rate Limit) atau Kunci API Anda berada di Tingkat Gratis (Free Tier) yang tidak didukung untuk Google Search Tool.*"
                    rawText
                } catch (fallbackEx: Exception) {
                    when (e.code()) {
                        429 -> "⚠️ Zann AI Error: Batas kuota/limit pemakaian untuk model '$modelName' telah tercapai (Rate Limit).\n\n💡 Solusi: Silakan tunggu 1 hingga 2 menit sebelum mencoba mengirim pesan kembali, atau ganti pilihan model Anda di atas (misalnya ke model Flash-Lite)."
                        403 -> "⚠️ Zann AI Error: Akses Dilarang (HTTP 403) - API Key tidak valid. Silakan periksa kembali kunci API Anda di Secrets panel AI Studio."
                        400 -> "⚠️ Zann AI Error: Permintaan Tidak Valid (HTTP 400). Format tidak didukung atau input tidak sah."
                        else -> "Gagal terhubung dengan Zann AI (HTTP ${e.code()}). Terjadi kendala jaringan atau kesalahan sistem."
                    }
                }
            } else {
                when (e.code()) {
                    429 -> "⚠️ Zann AI Error: Batas kuota/limit pemakaian untuk model '$modelName' telah tercapai (Rate Limit).\n\n💡 Solusi: Silakan tunggu 1 hingga 2 menit sebelum mencoba mengirim pesan kembali, atau ganti pilihan model Anda di atas (misalnya ke model Flash-Lite)."
                    403 -> "⚠️ Zann AI Error: Akses Dilarang (HTTP 403) - API Key tidak valid. Silakan periksa kembali kunci API Anda di Secrets panel AI Studio."
                    400 -> "⚠️ Zann AI Error: Permintaan Tidak Valid (HTTP 400). Format tidak didukung atau input tidak sah."
                    else -> "Gagal terhubung dengan Zann AI (HTTP ${e.code()}). Terjadi kendala jaringan atau kesalahan sistem."
                }
            }
        } catch (e: Exception) {
            "Gagal terhubung dengan Zann AI: ${e.localizedMessage ?: "Terjadi kesalahan jaringan atau API Key tidak valid. Silakan periksa koneksi internet Anda."}"
        }
    }

    suspend fun generateImageBase64(prompt: String, aspectRatio: String): String? {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return null
        }

        val request = GenerateContentRequest(
            contents = listOf(
                Content(
                    role = "user",
                    parts = listOf(Part(text = prompt))
                )
            ),
            generationConfig = com.example.data.api.GenerationConfig(
                responseModalities = listOf("TEXT", "IMAGE"),
                imageConfig = com.example.data.api.ImageConfig(aspectRatio = aspectRatio, imageSize = "1K")
            )
        )

        return try {
            val response = RetrofitClient.service.generateContent("gemini-2.5-flash-image", apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull { it.inlineData != null }?.inlineData?.data
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    val allLearnedKnowledge: Flow<List<com.example.data.db.LearnedKnowledge>> = chatMessageDao.getAllKnowledge()

    suspend fun insertLearnedKnowledge(knowledge: com.example.data.db.LearnedKnowledge) {
        chatMessageDao.insertKnowledge(knowledge)
    }

    suspend fun deleteLearnedKnowledge(id: Long) {
        chatMessageDao.deleteKnowledge(id)
    }

    suspend fun clearAllLearnedKnowledge() {
        chatMessageDao.clearAllKnowledge()
    }
}

object RealTimeDataFetcher {
    private val client = OkHttpClient()

    fun fetchWeather(cityName: String): String? {
        try {
            val encodedCity = URLEncoder.encode(cityName, "UTF-8")
            // 1. Geocode lookup
            val geoUrl = "https://geocoding-api.open-meteo.com/v1/search?name=$encodedCity&count=1&language=id&format=json"
            val geoRequest = Request.Builder().url(geoUrl).build()
            client.newCall(geoRequest).execute().use { response ->
                if (!response.isSuccessful) return null
                val geoBody = response.body?.string() ?: return null
                val geoJson = JSONObject(geoBody)
                val results = geoJson.optJSONArray("results")
                if (results == null || results.length() == 0) return null
                
                val cityObj = results.getJSONObject(0)
                val lat = cityObj.optDouble("latitude")
                val lon = cityObj.optDouble("longitude")
                val resolvedName = cityObj.optString("name")
                val country = cityObj.optString("country")
                val adminArea = cityObj.optString("admin1")

                // 2. Weather forecast lookup
                val weatherUrl = "https://api.open-meteo.com/v1/forecast?latitude=$lat&longitude=$lon&current=temperature_2m,relative_humidity_2m,apparent_temperature,precipitation,rain,showers,weather_code,wind_speed_10m&timezone=auto"
                val weatherRequest = Request.Builder().url(weatherUrl).build()
                client.newCall(weatherRequest).execute().use { wResponse ->
                    if (!wResponse.isSuccessful) return null
                    val wBody = wResponse.body?.string() ?: return null
                    val wJson = JSONObject(wBody)
                    val current = wJson.optJSONObject("current") ?: return null
                    
                    val temp = current.optDouble("temperature_2m")
                    val appTemp = current.optDouble("apparent_temperature")
                    val humidity = current.optInt("relative_humidity_2m")
                    val windSpeed = current.optDouble("wind_speed_10m")
                    val rain = current.optDouble("rain")
                    val weatherCode = current.optInt("weather_code")

                    val condition = when (weatherCode) {
                        0 -> "Cerah (Clear sky)"
                        1, 2, 3 -> "Cerah Berawan / Berawan Sebagian"
                        45, 48 -> "Berkabut (Foggy)"
                        51, 53, 55 -> "Gerimis rintik-rintik (Drizzle)"
                        56, 57 -> "Gerimis Beku (Freezing Drizzle)"
                        61, 63, 65 -> "Hujan (Rain)"
                        66, 67 -> "Hujan Beku (Freezing Rain)"
                        71, 73, 75 -> "Salju rintik (Snow fall)"
                        77 -> "Butiran Salju (Snow grains)"
                        80, 81, 82 -> "Hujan Deras / Badai (Rain showers)"
                        85, 86 -> "Showers Salju (Snow showers)"
                        95 -> "Badai Petir (Thunderstorm)"
                        96, 99 -> "Badai Petir dengan Hujan Es"
                        else -> "Berawan / Tidak Diketahui"
                    }

                    return """
                        - Lokasi: $resolvedName, $adminArea, $country
                        - Koordinat: ($lat, $lon)
                        - Suhu Aktual: $temp °C (Terasa seperti $appTemp °C)
                        - Kondisi Cuaca: $condition
                        - Kelembaban: $humidity%
                        - Kecepatan Angin: $windSpeed km/jam
                        - Curah Hujan: $rain mm
                    """.trimIndent()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun fetchNews(category: String): String? {
        try {
            val url = "https://saurav.tech/NewsAPI/top-headlines/category/$category/us.json"
            val request = Request.Builder().url(url).build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return null
                val body = response.body?.string() ?: return null
                val json = JSONObject(body)
                val articles = json.optJSONArray("articles") ?: return null
                
                val limit = minOf(articles.length(), 5)
                val sb = java.lang.StringBuilder()
                for (i in 0 until limit) {
                    val art = articles.getJSONObject(i)
                    val title = art.optString("title")
                    val sourceObj = art.optJSONObject("source")
                    val source = sourceObj?.optString("name") ?: "Media Berita"
                    val desc = art.optString("description", "")
                    
                    sb.append("- **$title** (Sumber: $source)\n")
                    if (desc.isNotBlank() && desc != "null") {
                        sb.append("  Ringkasan: $desc\n")
                    }
                }
                return sb.trim().toString()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}
