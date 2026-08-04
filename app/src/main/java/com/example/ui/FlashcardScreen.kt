package com.example.ui

import android.content.Context
import android.net.Uri
import android.widget.Toast
import android.speech.tts.TextToSpeech
import java.util.Locale
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.Flashcard
import com.example.data.db.FlashcardDeck
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardScreen(
    viewModel: FlashcardViewModel,
    modifier: Modifier = Modifier,
    onNavigateToChat: () -> Unit,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Initialize TTS
    val tts = remember {
        var ttsInstance: TextToSpeech? = null
        ttsInstance = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                ttsInstance?.language = Locale("id", "ID")
            }
        }
        ttsInstance
    }

    DisposableEffect(Unit) {
        onDispose {
            tts?.stop()
            tts?.shutdown()
        }
    }

    val speakOut: (String) -> Unit = remember(tts) {
        { text ->
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    // ViewModel Flows
    val allDecks by viewModel.allDecks.collectAsState()
    val inputText by viewModel.inputText.collectAsState()
    val deckTitleInput by viewModel.deckTitleInput.collectAsState()
    val cardCountInput by viewModel.cardCountInput.collectAsState()
    val deckTypeInput by viewModel.deckTypeInput.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val generateError by viewModel.generateError.collectAsState()

    // Persistent new states for Feature 1, 2, 5
    val starredCardIds by viewModel.starredCardIds.collectAsState()
    val onlyStudyStarred by viewModel.onlyStudyStarred.collectAsState()
    val studyHistory by viewModel.studyHistory.collectAsState()
    val studyStreak by viewModel.studyStreak.collectAsState()
    val dailyGoal by viewModel.dailyGoal.collectAsState()
    val dailyReviewedCards by viewModel.dailyReviewedCards.collectAsState()

    var showFullEditDialog by remember { mutableStateOf(false) }

    // Selection/Active states
    val activeDeck by viewModel.activeDeck.collectAsState()
    val activeCards by viewModel.activeCards.collectAsState()
    val currentCardIndex by viewModel.currentCardIndex.collectAsState()
    val isCardFlipped by viewModel.isCardFlipped.collectAsState()
    val cardStatuses by viewModel.cardStatuses.collectAsState()
    val isStudySessionFinished by viewModel.isStudySessionFinished.collectAsState()

    // Image upload states
    val attachedFileName by viewModel.attachedFileName.collectAsState()
    val attachedFileMimeType by viewModel.attachedFileMimeType.collectAsState()
    val attachedFileBase64 by viewModel.attachedFileBase64.collectAsState()

    var activeTab by remember { mutableStateOf(0) } // 0: Buat Baru, 1: Koleksi Dek

    // Feature 1: Dynamic Search Query and Type Filtering
    var searchQuery by remember { mutableStateOf("") }
    var selectedTypeFilter by remember { mutableStateOf("Semua") } // "Semua", "biasa", "pilihan_ganda"

    val filteredDecks = remember(allDecks, searchQuery, selectedTypeFilter) {
        allDecks.filter { deck ->
            val matchQuery = deck.title.contains(searchQuery, ignoreCase = true) ||
                    deck.description.contains(searchQuery, ignoreCase = true)
            val matchType = if (selectedTypeFilter == "Semua") true else deck.deckType == selectedTypeFilter
            matchQuery && matchType
        }
    }

    // Photo/Image pickers
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val base64 = getUriBase64(context, uri)
            val name = getFileName(context, uri) ?: "catatan_pelajaran.jpg"
            val mimeType = context.contentResolver.getType(uri) ?: "image/jpeg"
            if (base64 != null) {
                viewModel.attachFile(name, mimeType, base64)
                Toast.makeText(context, "Gambar catatan berhasil dilampirkan!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Style,
                            contentDescription = "Flashcard",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Flashcard Zann AI",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp,
                            letterSpacing = 0.5.sp
                        )
                    }
                },
                navigationIcon = {
                    if (activeDeck != null) {
                        IconButton(onClick = { viewModel.closeActiveStudy() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Kembali ke Beranda"
                            )
                        }
                    } else {
                        IconButton(onClick = onNavigateToChat) {
                            Icon(
                                imageVector = Icons.Default.Chat,
                                contentDescription = "Masuk Obrolan AI"
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onToggleTheme) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Ganti Tema"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            AnimatedContent(
                targetState = activeDeck,
                transitionSpec = {
                    slideInHorizontally { width -> width } + fadeIn() togetherWith
                            slideOutHorizontally { width -> -width } + fadeOut()
                },
                label = "ActiveViewTransition"
            ) { targetSelection ->
                if (targetSelection != null) {
                    // Study Mode View
                    StudySessionView(
                        deck = targetSelection,
                        cards = activeCards,
                        currentIndex = currentCardIndex,
                        isFlipped = isCardFlipped,
                        cardStatuses = cardStatuses,
                        isFinished = isStudySessionFinished,
                        starredCardIds = starredCardIds,
                        onToggleStarCard = { viewModel.toggleStarCard(it) },
                        onSaveSessionHistory = { score -> viewModel.saveStudySessionItem(targetSelection.id, targetSelection.title, score, activeCards.size) },
                        onFlip = { viewModel.flipCard() },
                        onAnswering = { card, status -> viewModel.setCardDifficulty(card, status) },
                        onReset = { viewModel.resetStudySession() },
                        onClose = { viewModel.closeActiveStudy() },
                        onDeleteCard = { viewModel.deleteCard(it) },
                        onEditCard = { card, q, a, e -> viewModel.editCard(card, q, a, e) },
                        onSpeak = speakOut
                    )
                } else {
                    // Maker & Gallery View
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Adaptive Streaks and Daily Goal Dashboard
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Streak text & Fire Badge
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Whatshot,
                                            contentDescription = "Streak Belajar",
                                            tint = if (studyStreak > 0) Color(0xFFFF6D00) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (studyStreak > 0) "$studyStreak Hari Belajar!" else "Mulai Streak Belajarmu!",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }

                                    // Daily Goal Progress Fraction
                                    Text(
                                        text = "$dailyReviewedCards / $dailyGoal Kartu Hari Ini",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Progress Bar
                                val progressFraction = if (dailyGoal > 0) (dailyReviewedCards.toFloat() / dailyGoal).coerceIn(0f, 1f) else 0f
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    LinearProgressIndicator(
                                        progress = progressFraction,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(8.dp)
                                            .clip(RoundedCornerShape(4.dp)),
                                        color = if (progressFraction >= 1f) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary,
                                        trackColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
                                    )
                                    
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = if (progressFraction >= 1f) "Sasaran Harian Tercapai! 🎉" else "${(progressFraction * 100).toInt()}% Selesai",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = if (progressFraction >= 1f) Color(0xFF388E3C) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                        )
                                        
                                        // Edit Goal label/button
                                        var showGoalDialog by remember { mutableStateOf(false) }
                                        Text(
                                            text = "Ubah Target",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier
                                                .clickable { showGoalDialog = true }
                                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                        )

                                        if (showGoalDialog) {
                                            var tempGoalInput by remember { mutableStateOf(dailyGoal.toString()) }
                                            AlertDialog(
                                                onDismissRequest = { showGoalDialog = false },
                                                title = { Text("Ubah Target Harian", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
                                                text = {
                                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                                        Text("Setel berapa kartu flashcard yang ingin Anda review setiap harinya:", fontSize = 12.sp)
                                                        OutlinedTextField(
                                                            value = tempGoalInput,
                                                            onValueChange = { tempGoalInput = it.filter { char -> char.isDigit() } },
                                                            singleLine = true,
                                                            placeholder = { Text("10") },
                                                            modifier = Modifier.fillMaxWidth()
                                                        )
                                                    }
                                                },
                                                confirmButton = {
                                                    Button(
                                                        onClick = {
                                                            val finalVal = tempGoalInput.toIntOrNull() ?: 10
                                                            viewModel.setDailyGoal(if (finalVal > 0) finalVal else 10)
                                                            showGoalDialog = false
                                                        }
                                                    ) {
                                                        Text("Simpan")
                                                    }
                                                },
                                                dismissButton = {
                                                    TextButton(onClick = { showGoalDialog = false }) {
                                                        Text("Batal")
                                                    }
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Dynamic Tab indicators
                        TabRow(
                            selectedTabIndex = activeTab,
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.primary
                        ) {
                            Tab(
                                selected = activeTab == 0,
                                onClick = { activeTab = 0 },
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(Icons.Default.AddBox, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Text("Buat Dek Baru", fontWeight = FontWeight.Bold)
                                    }
                                }
                            )
                            Tab(
                                selected = activeTab == 1,
                                onClick = { activeTab = 1 },
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(Icons.Default.FolderSpecial, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Text("Koleksi Dek (${allDecks.size})", fontWeight = FontWeight.Bold)
                                    }
                                }
                            )
                        }

                        if (showFullEditDialog) {
                            var tempText by remember { mutableStateOf(inputText) }
                            AlertDialog(
                                onDismissRequest = { showFullEditDialog = false },
                                title = {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Icon(Icons.Default.Edit, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                        Text("Editor Materi Pelajaran", fontWeight = FontWeight.Bold)
                                    }
                                },
                                text = {
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text("Edit materi pelajaran Anda secara bebas di bawah ini untuk mengoptimalkan output pembuatan flashcard kuis AI:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        OutlinedTextField(
                                            value = tempText,
                                            onValueChange = { tempText = it },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(260.dp),
                                            shape = RoundedCornerShape(10.dp),
                                            placeholder = { Text("Tempel materi di sini...") }
                                        )
                                    }
                                },
                                confirmButton = {
                                    Button(
                                        onClick = {
                                            viewModel.updateInputText(tempText)
                                            showFullEditDialog = false
                                        }
                                    ) {
                                        Text("Simpan")
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showFullEditDialog = false }) {
                                        Text("Batal")
                                    }
                                },
                                shape = RoundedCornerShape(16.dp)
                            )
                        }

                        if (activeTab == 0) {
                            // Workspace for generating new Decks
                            FlashcardGeneratorWorkspace(
                                inputText = inputText,
                                deckTitleInput = deckTitleInput,
                                countInput = cardCountInput,
                                deckTypeInput = deckTypeInput,
                                isGenerating = isGenerating,
                                generateError = generateError,
                                attachedFileName = attachedFileName,
                                attachedFileBase64 = attachedFileBase64,
                                onInputTextChange = { viewModel.updateInputText(it) },
                                onTitleChange = { viewModel.updateDeckTitleInput(it) },
                                onCountChange = { viewModel.updateCardCountInput(it) },
                                onTypeChange = { viewModel.updateDeckTypeInput(it) },
                                showFullEditDialog = showFullEditDialog,
                                onShowFullEditDialogChange = { showFullEditDialog = it },
                                onPickPhoto = {
                                    photoPickerLauncher.launch(
                                        androidx.activity.result.PickVisualMediaRequest(
                                            ActivityResultContracts.PickVisualMedia.ImageOnly
                                        )
                                    )
                                },
                                onClearAttachment = { viewModel.clearAttachedFile() },
                                onGenerateAction = { viewModel.generateAndSaveDeck() },
                                onLoadSample = { viewModel.loadSampleMaterial() },
                                onDismissError = { viewModel.clearError() }
                            )
                        } else {
                            // Gallery lists with Feature 1 (Search, Type Filter), Feature 2 (Study Starered Option), and Feature 5 (Study History) built-in
                            FlashcardGallery(
                                decks = filteredDecks,
                                searchQuery = searchQuery,
                                onSearchQueryChange = { searchQuery = it },
                                selectedFilter = selectedTypeFilter,
                                onFilterChange = { selectedTypeFilter = it },
                                studyHistory = studyHistory,
                                onClearHistory = { viewModel.clearAllHistory() },
                                onlyStudyStarred = onlyStudyStarred,
                                onToggleOnlyStudyStarred = { viewModel.toggleOnlyStudyStarred() },
                                onSelectDeck = { viewModel.selectDeckForStudy(it) },
                                onDeleteDeck = { viewModel.deleteDeck(it) },
                                onGoToCreateTab = { activeTab = 0 },
                                onToggleDeckType = { viewModel.toggleDeckType(it) },
                                onResetDeckProgress = { viewModel.resetDeckProgress(it) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FlashcardGeneratorWorkspace(
    inputText: String,
    deckTitleInput: String,
    countInput: Int,
    deckTypeInput: String,
    isGenerating: Boolean,
    generateError: String?,
    attachedFileName: String?,
    attachedFileBase64: String?,
    onInputTextChange: (String) -> Unit,
    onTitleChange: (String) -> Unit,
    onCountChange: (Int) -> Unit,
    onTypeChange: (String) -> Unit,
    showFullEditDialog: Boolean,
    onShowFullEditDialogChange: (Boolean) -> Unit,
    onPickPhoto: () -> Unit,
    onClearAttachment: () -> Unit,
    onGenerateAction: () -> Unit,
    onLoadSample: () -> Unit,
    onDismissError: () -> Unit
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcoming card
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "AI",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Konversi Materi Belajar dengan AI",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Gunakan Zann AI untuk secara otomatis mengekstrak fakta penting, definisi, dan kuis tanya-jawab interaktif langsung dari teks materi pelajaran, berkas digital, atau foto catatan kelas Anda.",
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
        }

        // Title textfield
        OutlinedTextField(
            value = deckTitleInput,
            onValueChange = onTitleChange,
            label = { Text("Judul Dek Flashcard") },
            placeholder = { Text("Contoh: Bab 4 Atmosfer Bumi, Kosakata Mandarim, dll") },
            leadingIcon = { Icon(Icons.Default.Book, contentDescription = null) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("deck_title_input"),
            shape = RoundedCornerShape(10.dp)
        )

        // Text input area
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Materi Pelajaran / Catatan Teks:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                TextButton(
                    onClick = onLoadSample,
                    modifier = Modifier.heightIn(max = 36.dp)
                ) {
                    Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Gunakan Contoh", fontSize = 12.sp)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = inputText,
                onValueChange = { onInputTextChange(it) },
                placeholder = { Text("Tempel di sini materi pelajaran, kumpulan rumus, artikel berita, kliping riset, atau ringkasan catatan Anda untuk dianalisis oleh AI...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .testTag("material_text_input"),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = { onShowFullEditDialogChange(true) },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit Luas", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                TextButton(
                    onClick = {
                        if (inputText.isNotEmpty()) {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                            val clip = android.content.ClipData.newPlainText("Materi Pelajaran", inputText)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Selesai disalin ke clipboard!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Kandungan materi kosong!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.secondary),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Salin Teks", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                TextButton(
                    onClick = {
                        if (inputText.isNotEmpty()) {
                            onInputTextChange("")
                            Toast.makeText(context, "Materi dibersihkan!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Kandungan materi sudah kosong!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(Icons.Default.DeleteOutline, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Hapus", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Attachment Section (Responsive visual upload desk)
        Card(
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Attachment,
                            contentDescription = "Lampiran",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Ekstraksi dari Gambar Catatan (Opsional)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (attachedFileName == null) {
                        Button(
                            onClick = onPickPhoto,
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.heightIn(max = 34.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        ) {
                            Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Pilih Foto", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                if (attachedFileName != null) {
                    val bitmap = remember(attachedFileBase64) {
                        try {
                            if (attachedFileBase64 != null) {
                                val bytes = android.util.Base64.decode(attachedFileBase64, android.util.Base64.DEFAULT)
                                android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
                            } else null
                        } catch (e: Exception) {
                            null
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (bitmap != null) {
                            Image(
                                bitmap = bitmap,
                                contentDescription = "Pratinjau Foto",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .border(1.dp, MaterialTheme.colorScheme.secondary, RoundedCornerShape(6.dp))
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = attachedFileName,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Text(
                                text = "Foto catatan tersemat, AI akan membaca tulisan di dalamnya",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                            )
                        }

                        IconButton(
                            onClick = onClearAttachment,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Cancel,
                                contentDescription = "Hapus Gambar",
                                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { onPickPhoto() }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Belum ada gambar terpilih. Ketuk untuk memotret/memilih.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }

        // Selected Flashcard Type Selector (Classic card vs Interactive Multiple Choice quiz)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Jenis Flashcard Kuis:",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Option 1: Classic Card Flipping
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(84.dp)
                        .clickable { onTypeChange("biasa") }
                        .border(
                            width = if (deckTypeInput == "biasa") 2.dp else 1.dp,
                            color = if (deckTypeInput == "biasa") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (deckTypeInput == "biasa") MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.FlipToBack,
                            contentDescription = null,
                            tint = if (deckTypeInput == "biasa") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Biasa (Balik Kartu)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (deckTypeInput == "biasa") MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Option 2: Multiple Choice Quiz
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(84.dp)
                        .clickable { onTypeChange("pilihan_ganda") }
                        .border(
                            width = if (deckTypeInput == "pilihan_ganda") 2.dp else 1.dp,
                            color = if (deckTypeInput == "pilihan_ganda") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (deckTypeInput == "pilihan_ganda") MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Quiz,
                            contentDescription = null,
                            tint = if (deckTypeInput == "pilihan_ganda") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Pilihan Ganda",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (deckTypeInput == "pilihan_ganda") MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // Card Count Customizer (Slider)
        Card(
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Jumlah Kartu Kuis:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$countInput Kartu",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Slider(
                    value = countInput.toFloat(),
                    onValueChange = { onCountChange(it.toInt()) },
                    valueRange = 5f..20f,
                    steps = 14,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        if (generateError != null) {
            AlertDialog(
                onDismissRequest = onDismissError,
                title = { Text("Pembuatan Gagal") },
                text = { Text(generateError) },
                confirmButton = {
                    TextButton(onClick = onDismissError) {
                        Text("Dimengerti")
                    }
                },
                shape = RoundedCornerShape(16.dp)
            )
        }

        // Action Trigger
        Button(
            onClick = onGenerateAction,
            enabled = !isGenerating,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("generate_deck_button"),
            shape = RoundedCornerShape(12.dp)
        ) {
            if (isGenerating) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.5.dp
                    )
                    Text(
                        text = "Menganalisis Materi Belajar...",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        letterSpacing = 0.2.sp
                    )
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null)
                    Text(
                        text = "Generate Flashcards!",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        letterSpacing = 0.2.sp
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
fun CustomFilterChip(
    selected: Boolean,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(32.dp),
        color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        contentColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
        border = BorderStroke(1.dp, if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.08f)),
        modifier = modifier
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun FlashcardGallery(
    decks: List<FlashcardDeck>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedFilter: String,
    onFilterChange: (String) -> Unit,
    studyHistory: List<com.example.ui.FlashcardViewModel.StudyHistoryItem>,
    onClearHistory: () -> Unit,
    onlyStudyStarred: Boolean,
    onToggleOnlyStudyStarred: () -> Unit,
    onSelectDeck: (FlashcardDeck) -> Unit,
    onDeleteDeck: (FlashcardDeck) -> Unit,
    onGoToCreateTab: () -> Unit,
    onToggleDeckType: (FlashcardDeck) -> Unit,
    onResetDeckProgress: (Long) -> Unit
) {
    var showHistoryDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize()) {
        // Control Row: Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = { Text("Cari judul dek atau deskripsi pelajaran...", fontSize = 13.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(20.dp)) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(Icons.Default.Close, contentDescription = "Bersihkan", modifier = Modifier.size(18.dp))
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        // Filtration Badges
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CustomFilterChip(
                selected = selectedFilter == "Semua",
                label = "Semua Tipe",
                onClick = { onFilterChange("Semua") }
            )
            CustomFilterChip(
                selected = selectedFilter == "biasa",
                label = "Balik Kartu (Biasa)",
                onClick = { onFilterChange("biasa") }
            )
            CustomFilterChip(
                selected = selectedFilter == "pilihan_ganda",
                label = "Pilihan Ganda (Kuis)",
                onClick = { onFilterChange("pilihan_ganda") }
            )
        }

        // Feature 2 & 5 Row Buttons: Study Starred Toggle & History Viewer
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Heart Star Button
            Surface(
                onClick = onToggleOnlyStudyStarred,
                shape = RoundedCornerShape(10.dp),
                color = if (onlyStudyStarred) Color(0xFFFFB300).copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface,
                contentColor = if (onlyStudyStarred) Color(0xFFE65100) else MaterialTheme.colorScheme.onSurfaceVariant,
                border = BorderStroke(1.dp, if (onlyStudyStarred) Color(0xFFFFB300) else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
                modifier = Modifier.weight(1.1f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (onlyStudyStarred) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = if (onlyStudyStarred) Color(0xFFFFB300) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (onlyStudyStarred) "Hanya Kuis Bintang (Aktif)" else "Saring Kartu Bintang",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // View Study History Logs button
            Surface(
                onClick = { showHistoryDialog = true },
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
                modifier = Modifier.weight(0.9f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "Riwayat",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Riwayat Skor kuis",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        // Show Decks empty or list state
        if (decks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.widthIn(max = 300.dp)
                ) {
                    Icon(
                        imageVector = if (searchQuery.isNotEmpty()) Icons.Default.Search else Icons.Default.School,
                        contentDescription = "Dek Kosong",
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                        modifier = Modifier.size(72.dp)
                    )
                    Text(
                        text = if (searchQuery.isNotEmpty()) "Hasil Tidak Ditemukan" else "Koleksi Dek Masih Kosong",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        text = if (searchQuery.isNotEmpty()) {
                            "Tidak ada dek kuis dengan kriteria pencarian '${searchQuery}' di dalam koleksi Anda. Silakan bersihkan pencarian atau saring tipe lainnya."
                        } else {
                            "Anda belum membuat dek kuis flashcard. Silakan tulis atau tempel materi di tab 'Buat Dek Baru' untuk mulai mengonversinya dengan AI secara otomatis."
                        },
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                    if (searchQuery.isEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = onGoToCreateTab,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Buat Dek Baru Sekarang")
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            Icons.Default.HistoryEdu, 
                            contentDescription = null, 
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Dek Pembelajaran Anda (${decks.size})",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }

                items(decks) { deck ->
                    var showDeleteConfirm by remember { mutableStateOf(false) }
                    var showResetConfirm by remember { mutableStateOf(false) }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(2.dp, RoundedCornerShape(12.dp))
                            .clickable { onSelectDeck(deck) }
                            .testTag("deck_card_${deck.id}"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = deck.title,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = deck.description,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Layers,
                                            contentDescription = "Jumlah kartu",
                                            tint = if (deck.deckType == "pilihan_ganda") MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = if (deck.deckType == "pilihan_ganda") "Pilihan Ganda" else "Balik Kartu",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (deck.deckType == "pilihan_ganda") MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                                        )
                                    }

                                    val dateString = remember(deck.timestamp) {
                                        val date = java.util.Date(deck.timestamp)
                                        val format = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault())
                                        format.format(date)
                                    }
                                    Text(
                                        text = dateString,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                    )
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                // Toggle Deck Type Button
                                IconButton(
                                    onClick = {
                                        onToggleDeckType(deck)
                                        val nextType = if (deck.deckType == "biasa") "Pilihan Ganda" else "Balik Kartu"
                                        Toast.makeText(context, "Tipe kuis '${deck.title}' diubah ke: $nextType", Toast.LENGTH_SHORT).show()
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.SwapHoriz,
                                        contentDescription = "Ganti Tipe Dek",
                                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                                    )
                                }

                                // Reset Study Progress Button
                                IconButton(
                                    onClick = { showResetConfirm = true }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Reset Progress",
                                        tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f)
                                    )
                                }

                                // Delete Deck Button
                                IconButton(
                                    onClick = { showDeleteConfirm = true }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteSweep,
                                        contentDescription = "Hapus Dek",
                                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f)
                                    )
                                }
                            }
                        }
                    }

                    if (showDeleteConfirm) {
                        AlertDialog(
                            onDismissRequest = { showDeleteConfirm = false },
                            title = { Text("Hapus Dek Flashcard?") },
                            text = { Text("Apakah Anda yakin ingin menghapus dek kuis '${deck.title}'? Tindakan ini tidak dapat dibatalkan.") },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        onDeleteDeck(deck)
                                        showDeleteConfirm = false
                                    },
                                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                                ) {
                                    Text("Hapus")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showDeleteConfirm = false }) {
                                    Text("Batal")
                                }
                            },
                            shape = RoundedCornerShape(16.dp)
                        )
                    }

                    if (showResetConfirm) {
                        AlertDialog(
                            onDismissRequest = { showResetConfirm = false },
                            title = { Text("Setel Ulang Kemajuan Belajar?") },
                            text = { Text("Apakah Anda yakin ingin menghapus status pemahaman untuk semua kartu di dalam dek '${deck.title}'? Semua kartu akan kembali berstatus 'Belum Diuji'.") },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        onResetDeckProgress(deck.id)
                                        showResetConfirm = false
                                        Toast.makeText(context, "Status belajar untuk dek '${deck.title}' berhasil disetel ulang!", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                                ) {
                                    Text("Setel Ulang", fontWeight = FontWeight.Bold)
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showResetConfirm = false }) {
                                    Text("Batal")
                                }
                            },
                            shape = RoundedCornerShape(16.dp)
                        )
                    }
                }
            }
        }
    }

    // Historical score stats dialog popup
    if (showHistoryDialog) {
        AlertDialog(
            onDismissRequest = { showHistoryDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "Riwayat",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text("Riwayat Skor Belajar AI", fontWeight = FontWeight.Black)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 350.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (studyHistory.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Belum ada materi pengerjaan kuis. Selesaikan salah satu kuis dek Anda sampai akhir untuk merekam hasil skor di sini secara otomatis!",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                lineHeight = 18.sp
                            )
                        }
                    } else {
                        Text(
                            text = "Berikut adalah log penyelesaian kuis dek terakhir Anda:",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(vertical = 4.dp)
                        ) {
                            items(studyHistory) { log ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(
                                            modifier = Modifier.weight(1f),
                                            verticalArrangement = Arrangement.spacedBy(2.dp)
                                        ) {
                                            Text(
                                                text = log.deckTitle,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            val logDate = remember(log.timestamp) {
                                                val date = java.util.Date(log.timestamp)
                                                val format = java.text.SimpleDateFormat("dd MMM, HH:mm", java.util.Locale.getDefault())
                                                format.format(date)
                                            }
                                            Text(
                                                text = "$logDate • ${log.totalCards} Kartu",
                                                fontSize = 10.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                            )
                                        }

                                        // Badge Score representation
                                        val badgeColor = when {
                                            log.scorePercent >= 85 -> Color(0xFF1B5E20)
                                            log.scorePercent >= 60 -> Color(0xFFE65100)
                                            else -> Color(0xFFB71C1C)
                                        }
                                        Box(
                                            modifier = Modifier
                                                .background(badgeColor.copy(alpha = 0.12f), RoundedCornerShape(6.dp))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = "${log.scorePercent}%",
                                                fontWeight = FontWeight.Black,
                                                color = badgeColor,
                                                fontSize = 14.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showHistoryDialog = false }) {
                    Text("Tutup")
                }
            },
            dismissButton = {
                if (studyHistory.isNotEmpty()) {
                    TextButton(
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
                        onClick = {
                            onClearHistory()
                            showHistoryDialog = false
                        }
                    ) {
                        Text("Hapus Riwayat")
                    }
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun StudySessionView(
    deck: FlashcardDeck,
    cards: List<Flashcard>,
    currentIndex: Int,
    isFlipped: Boolean,
    cardStatuses: Map<Long, String>,
    isFinished: Boolean,
    starredCardIds: Set<Long>,
    onToggleStarCard: (Long) -> Unit,
    onSaveSessionHistory: (Int) -> Unit,
    onFlip: () -> Unit,
    onAnswering: (Flashcard, String) -> Unit,
    onReset: () -> Unit,
    onClose: () -> Unit,
    onDeleteCard: (Flashcard) -> Unit,
    onEditCard: (Flashcard, String, String, String?) -> Unit,
    onSpeak: (String) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Feature 2: Support shuffling/randomizing cards locally inside study session
    var shuffledCards by remember(cards) { mutableStateOf(cards) }

    // Feature 4: Active study session elapsed stopwatch timer
    var sessionElapsedTime by remember { mutableStateOf(0) }
    LaunchedEffect(isFinished) {
        if (!isFinished) {
            while (true) {
                kotlinx.coroutines.delay(1000L)
                sessionElapsedTime++
            }
        }
    }
    val timeText = remember(sessionElapsedTime) {
        val mins = sessionElapsedTime / 60
        val secs = sessionElapsedTime % 60
        String.format("%02d:%02d", mins, secs)
    }

    if (shuffledCards.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    if (isFinished) {
        // Study Completed Summary Card
        val pahamCount = cardStatuses.values.count { it == "Paham" }
        val perluCount = cardStatuses.values.count { it == "Perlu Belajar" }
        val scorePercent = ((pahamCount.toFloat() / shuffledCards.size.toFloat()) * 100).toInt()

        // Feature 5: Save completing session to database/preferences histories
        LaunchedEffect(isFinished) {
            onSaveSessionHistory(scorePercent)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Celebration,
                contentDescription = "Tamat",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Sesi Selesai! 🎉",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Sesi kuis untuk dek '${deck.title}' telah selesai.",
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Score Dashboard representation
            Card(
                modifier = Modifier.widthIn(max = 400.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Skor Pemahaman Anda",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(100.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                shape = CircleShape
                            )
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$scorePercent%",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Paham",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(modifier = Modifier.size(8.dp).background(Color(0xFF2E7D32), CircleShape))
                                Text("Paham", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Text("$pahamCount / ${shuffledCards.size}", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF2E7D32))
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(modifier = Modifier.size(8.dp).background(Color(0xFFD84315), CircleShape))
                                Text("Perlu Belajar", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Text("$perluCount / ${shuffledCards.size}", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFD84315))
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))

                    // Helpful Advice description
                    val adviceText = if (scorePercent >= 80) {
                        "Luar biasa! Pemahaman Anda sangat tinggi untuk materi ini. Siap untuk menerjang ujian langsung!"
                    } else if (scorePercent >= 50) {
                        "Kerja bagus! Pemahaman Anda cukup solid. Pelajari kembali beberapa kartu bertanda 'Perlu Belajar' agar nilai Anda sempurna."
                    } else {
                        "Semangat! Anda masih membutuhkan sedikit latihan pada materi ini. Ulangi kembali sesi ini untuk memperkuat retensi memori."
                    }
                    Text(
                        text = adviceText,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
            ) {
                OutlinedButton(
                    onClick = onReset,
                    modifier = Modifier.height(48.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Ulangi Belajar")
                }
                
                Button(
                    onClick = onClose,
                    modifier = Modifier.height(48.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Home, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Selesai & Keluar")
                }
            }
        }
        return
    }

    val currentCard = shuffledCards[currentIndex]

    // States for Multiple Choice Quiz
    var chosenMcOption by remember(currentCard.id) { mutableStateOf<String?>(null) }
    var mcSubmitted by remember(currentCard.id) { mutableStateOf(false) }

    val mcOptions = remember(currentCard.id, shuffledCards) {
        val correct = currentCard.answer
        val distractors = shuffledCards
            .filter { it.answer != correct }
            .map { it.answer }
            .distinct()
            .shuffled()
            .take(3)
            .toMutableList()
        
        while (distractors.size < 3) {
            val templates = listOf(
                "Informasi tidak tersedia di teks",
                "Semua pernyataan di atas salah",
                "Pertanyaan tidak relevan",
                "Opsi alternatif materi pelajaran",
                "Pernyataan pengalihan opsional",
                "Jawaban umum materi pelengkap"
            )
            val fill = templates.shuffled().firstOrNull { it != correct && !distractors.contains(it) } ?: "Jawaban Lainnya"
            distractors.add(fill)
        }
        distractors.add(correct)
        distractors.shuffled()
    }

    // States for active card operations: Delete
    var showCardDeleteDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top statistics header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = deck.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Feature 4: Stopwatch elapsed timer badge in green-themed wrapper
                Box(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = "Stopwatch",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = timeText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Kartu ${currentIndex + 1}/${shuffledCards.size}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Actions Bar: Shuffling & Star Toggling Options (Feature 2, Feature 3)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Shuffle Action Button
            TextButton(
                onClick = {
                    shuffledCards = shuffledCards.shuffled()
                    Toast.makeText(context, "Urutan kartu diacak!", Toast.LENGTH_SHORT).show()
                },
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Shuffle,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Acak Urutan",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Interactive Delete Card Actions
                IconButton(
                    onClick = { showCardDeleteDialog = true },
                    modifier = Modifier.size(36.dp).testTag("delete_card_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Hapus Kartu",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Star Toggler Button
                val hasStarred = starredCardIds.contains(currentCard.id)
                IconButton(
                    onClick = { onToggleStarCard(currentCard.id) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (hasStarred) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = "Bintang",
                        tint = if (hasStarred) Color(0xFFFFB300) else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        // Progress Text & Visual Column showing reviewed cards
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val reviewedCount = cardStatuses.size
            val totalCount = shuffledCards.size
            val progressPercent = if (totalCount > 0) (reviewedCount * 100) / totalCount else 0

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Review Status",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Diulas: $reviewedCount dari $totalCount kartu",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "$progressPercent%",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            LinearProgressIndicator(
                progress = { if (totalCount > 0) reviewedCount.toFloat() / totalCount.toFloat() else 0f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Spacer(modifier = Modifier.height(16.dp))

        // Awesome interactively flipping study card or Quiz Multiple Choice option
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            if (deck.deckType == "pilihan_ganda") {
                MultipleChoiceQuizView(
                    card = currentCard,
                    options = mcOptions,
                    chosenOption = chosenMcOption,
                    isSubmitted = mcSubmitted,
                    onOptionSelected = { option ->
                        if (!mcSubmitted) {
                            chosenMcOption = option
                            mcSubmitted = true
                        }
                    },
                    onSpeak = onSpeak
                )
            } else {
                FlippableFlashcard(
                    card = currentCard,
                    isFlipped = isFlipped,
                    onFlip = onFlip,
                    onSpeak = onSpeak
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Evaluation Action panel
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (deck.deckType == "pilihan_ganda") {
                if (mcSubmitted) {
                    if (currentCard.explanation != null) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier.fillMaxWidth(0.92f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("💡 Penjelasan:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(currentCard.explanation!!, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface, textAlign = TextAlign.Center)
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(0.92f),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onClose,
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .shadow(1.dp, RoundedCornerShape(12.dp)),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        ) {
                            Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Tidak (Keluar)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                val status = if (chosenMcOption == currentCard.answer) "Paham" else "Perlu Belajar"
                                onAnswering(currentCard, status)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .shadow(2.dp, RoundedCornerShape(12.dp)),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text("Lanjut", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                } else {
                    Text(
                        text = "Pilihlah salah satu jawaban yang menurut Anda paling benar.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                if (!isFlipped) {
                    Button(
                        onClick = onFlip,
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .height(52.dp)
                            .shadow(4.dp, RoundedCornerShape(12.dp)),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.FlipCameraAndroid,
                            contentDescription = "Balik Kartu"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Ketuk untuk Membalik Kartu",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.2.sp
                        )
                    }
                } else {
                    Text(
                        text = "Apakah Anda memahami kuis tanya-jawab di atas?",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(0.9f),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { onAnswering(currentCard, "Perlu Belajar") },
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                                .shadow(2.dp, RoundedCornerShape(12.dp)),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFA13D2D),
                                contentColor = Color.White
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(18.dp))
                                Text("Perlu Belajar", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Button(
                            onClick = { onAnswering(currentCard, "Paham") },
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                                .shadow(2.dp, RoundedCornerShape(12.dp))
                                .testTag("paham_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2E7D32),
                                contentColor = Color.White
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                                Text("Paham ✔️", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    // Delete Active Card popup
    if (showCardDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showCardDeleteDialog = false },
            title = { Text("Hapus Kartu Kuis?") },
            text = { Text("Apakah Anda yakin ingin menghapus kartu ini dari dek? Tindakan ini bersifat permanen.") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteCard(currentCard)
                        showCardDeleteDialog = false
                        Toast.makeText(context, "Kartu dihapus dari dek", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCardDeleteDialog = false }) {
                    Text("Batal")
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun MultipleChoiceQuizView(
    card: Flashcard,
    options: List<String>,
    chosenOption: String?,
    isSubmitted: Boolean,
    onOptionSelected: (String) -> Unit,
    onSpeak: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(0.92f)
            .heightIn(min = 350.dp)
            .shadow(4.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ),
        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "PERTANYAAN PILIHAN GANDA",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )
                }

                // TTS speak speaker button
                IconButton(
                    onClick = { onSpeak(card.question) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Speak Question",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Question Text centered
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = card.question,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Choices list (4 buttons stacked)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                options.forEach { option ->
                    val isCurrentChoice = chosenOption == option
                    val isCorrectChoice = option == card.answer
                    
                    val bColor = when {
                        !isSubmitted -> MaterialTheme.colorScheme.surface
                        isCorrectChoice -> Color(0xFFE8F5E9)
                        isCurrentChoice -> Color(0xFFFFEBEE)
                        else -> MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                    }
                    
                    val borderCol = when {
                        !isSubmitted -> MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        isCorrectChoice -> Color(0xFF2E7D32)
                        isCurrentChoice -> Color(0xFFC62828)
                        else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.05f)
                    }

                    val textColor = when {
                        !isSubmitted -> MaterialTheme.colorScheme.onSurface
                        isCorrectChoice -> Color(0xFF2E7D32)
                        isCurrentChoice -> Color(0xFFC62828)
                        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 44.dp)
                            .clickable(enabled = !isSubmitted) { onOptionSelected(option) }
                            .border(1.dp, borderCol, RoundedCornerShape(8.dp)),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = bColor)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = option,
                                fontSize = 12.sp,
                                fontWeight = if (isCurrentChoice || (isSubmitted && isCorrectChoice)) FontWeight.Bold else FontWeight.Medium,
                                color = textColor,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 8.dp)
                            )
                            
                            if (isSubmitted) {
                                if (isCorrectChoice) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = "Benar", tint = Color(0xFF2E7D32), modifier = Modifier.size(16.dp))
                                } else if (isCurrentChoice) {
                                    Icon(Icons.Default.Cancel, contentDescription = "Salah", tint = Color(0xFFC62828), modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FlippableFlashcard(
    card: Flashcard,
    isFlipped: Boolean,
    onFlip: () -> Unit,
    onSpeak: (String) -> Unit
) {
    // High-fidelity back-elastic cubic curve mimicking classic CSS transitions (easeOutBack)
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 650, easing = CubicBezierEasing(0.175f, 0.885f, 0.32f, 1.275f)),
        label = "rotationAnimation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .height(340.dp)
            .shadow(6.dp, RoundedCornerShape(20.dp), clip = false)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 16 * density // Gorgeous deep 3D perspective projection

                // Real-time active physical calculations for depth scaling and vertical lift
                val rad = Math.toRadians(rotation.toDouble())
                val sinVal = Math.abs(Math.sin(rad)).toFloat()

                // Shrink card dynamically at the 90 degree peak of its flip (CSS scale transform mimic)
                scaleX = 1f - 0.08f * sinVal
                scaleY = 1f - 0.08f * sinVal

                // Vertically elevate/lift the card as it turns in flight to create airiness
                translationY = -28 * density * sinVal
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onFlip
            )
            .testTag("flippable_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isFlipped) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            }
        ),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .graphicsLayer {
                    // Mirroring the content back to match rotation
                    if (rotation > 90f) {
                        rotationY = 180f
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            if (rotation <= 90f) {
                // Front Side - The Question
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .background(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "PERTANYAAN",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary,
                                letterSpacing = 1.sp
                            )
                        }

                        // TTS speaker button
                        IconButton(
                            onClick = { onSpeak(card.question) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Dengarkan Pertanyaan",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Text(
                        text = card.question,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        lineHeight = 26.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.TouchApp,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Ketuk kartu untuk balik",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    }
                }
            } else {
                // Back Side - The Answer & explanation
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = Color(0xFF2E7D32).copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(6.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "JAWABAN",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF2E7D32),
                                    letterSpacing = 1.sp
                                )
                            }

                            // TTS speaker button for answer text
                            IconButton(
                                onClick = { onSpeak(card.answer) },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = "Dengarkan Jawaban",
                                    tint = Color(0xFF2E7D32),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Text(
                            text = card.answer,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center,
                            lineHeight = 26.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    if (card.explanation != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Column(
                            modifier = Modifier
                                .weight(1f, fill = false)
                                .verticalScroll(rememberScrollState())
                                .padding(horizontal = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.12f),
                                thickness = 1.dp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Penjelasan Tambahan:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                            )
                            Text(
                                text = card.explanation,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 18.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                            )
                        }
                    }
                    
                    Text(
                        text = "Ketuk untuk kembali ke pertanyaan",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.4f),
                        modifier = Modifier.padding(top = 10.dp)
                    )
                }
            }
        }
    }
}
