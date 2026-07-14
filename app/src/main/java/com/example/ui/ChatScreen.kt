package com.example.ui

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.KeyboardVoice
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.TipsAndUpdates
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.ArrowOutward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.FilterAltOff
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.ui.draw.rotate
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Checkbox
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.material3.Switch
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.viewinterop.AndroidView
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.material.icons.filled.Visibility
import com.example.R
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.layout.ContentScale
import com.example.data.db.ChatMessage
import com.example.data.db.ChatSession
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

// Media Helper classes
class VoiceRecorder(private val context: Context) {
    private var mediaRecorder: MediaRecorder? = null
    private var outputFile: File? = null

    fun startRecording(fileName: String): File? {
        outputFile = File(context.cacheDir, "$fileName.m4a")
        
        return try {
            @Suppress("DEPRECATION")
            mediaRecorder = if (android.os.Build.VERSION.SDK_INT >= 31) {
                MediaRecorder(context)
            } else {
                MediaRecorder()
            }.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(outputFile?.absolutePath)
                prepare()
                start()
            }
            outputFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun stopRecording() {
        try {
            mediaRecorder?.stop()
            mediaRecorder?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mediaRecorder = null
    }
}

fun getFileBase64(file: File): String? {
    return try {
        val bytes = file.readBytes()
        Base64.encodeToString(bytes, Base64.NO_WRAP)
    } catch (e: Exception) {
        null
    }
}

fun getUriBase64(context: Context, uri: Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bytes = inputStream?.readBytes()
        inputStream?.close()
        if (bytes != null) Base64.encodeToString(bytes, Base64.NO_WRAP) else null
    } catch (e: Exception) {
        null
    }
}

fun getFileName(context: Context, uri: Uri): String? {
    var result: String? = null
    if (uri.scheme == "content") {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        try {
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (index >= 0) {
                    result = cursor.getString(index)
                }
            }
        } finally {
            cursor?.close()
        }
    }
    if (result == null) {
        result = uri.path
        val cut = result?.lastIndexOf('/')
        if (cut != null && cut != -1) {
            result = result.substring(cut + 1)
        }
    }
    return result
}

@Composable
fun ModelSelectorRow(
    selectedModel: String,
    onModelSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val models = listOf(
        ModelOption("Flash-Lite", "gemini-3.1-flash-lite-preview", "⚡ Cepat & Ringkas", MaterialTheme.colorScheme.secondary),
        ModelOption("Flash", "gemini-2.5-flash", "🤖 Serbaguna", MaterialTheme.colorScheme.primary)
    )
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.5f))
            .padding(vertical = 8.dp, horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        models.forEach { option ->
            val isSelected = option.id == selectedModel
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isSelected) option.color.copy(alpha = 0.15f) else Color.Transparent
                    )
                    .clickable { onModelSelected(option.id) }
                    .border(
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (isSelected) option.color else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(vertical = 8.dp, horizontal = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = option.label,
                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                        fontSize = 12.sp,
                        color = if (isSelected) option.color else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = option.tagline,
                        fontSize = 8.sp,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
    }
}

data class ModelOption(
    val label: String,
    val id: String,
    val tagline: String,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    modifier: Modifier = Modifier,
    onNavigateToFlashcards: () -> Unit = {},
    onNavigateToPlanner: () -> Unit = {},
    initialWidgetAction: String? = null,
    onHandledWidgetAction: () -> Unit = {}
) {
    val context = LocalContext.current
    val messages by viewModel.messages.collectAsState()
    val sessions by viewModel.sessions.collectAsState()
    val currentSessionId by viewModel.currentSessionId.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val selectedModel by viewModel.selectedModel.collectAsState()
    val isGoogleSearchEnabled by viewModel.isGoogleSearchEnabled.collectAsState()

    val attachedFileName by viewModel.attachedFileName.collectAsState()
    val attachedFileMimeType by viewModel.attachedFileMimeType.collectAsState()
    val attachedFileBase64 by viewModel.attachedFileBase64.collectAsState()

    val starredSessionIds by viewModel.starredSessionIds.collectAsState()
    val chatSearchQuery by viewModel.chatSearchQuery.collectAsState()
    val filterOnlyStarredChats by viewModel.filterOnlyStarredChats.collectAsState()

    val filteredSessions = remember(sessions, chatSearchQuery, filterOnlyStarredChats, starredSessionIds) {
        sessions.filter { session ->
            val matchQuery = session.title.contains(chatSearchQuery, ignoreCase = true)
            val matchStar = if (filterOnlyStarredChats) starredSessionIds.contains(session.sessionId) else true
            matchQuery && matchStar
        }.sortedByDescending { starredSessionIds.contains(it.sessionId) }
    }

    val sortedSessions = remember(sessions, starredSessionIds) {
        sessions.sortedByDescending { starredSessionIds.contains(it.sessionId) }
    }
    
    var inputText by remember { mutableStateOf("") }
    var showHistoryDialog by remember { mutableStateOf(false) }
    var showLiveAiMode by remember { mutableStateOf(false) }
    var showLearningMemoryDialog by remember { mutableStateOf(false) }
    var showCombinedMenu by remember { mutableStateOf(false) }
    var showTaskAnalysisDialog by remember { mutableStateOf(false) }
    var taskCategory by remember { mutableStateOf("Soal Matematika") }
    var requestedAction by remember { mutableStateOf("Memberikan langkah penyelesaian") }
    var taskAdditionalDetail by remember { mutableStateOf("") }
    var showSessionDeleteConfirm by remember { mutableStateOf<ChatSession?>(null) }
    var longPressedSession by remember { mutableStateOf<ChatSession?>(null) }
    var showLongPressMenu by remember { mutableStateOf(false) }
    var showRenameDialog by remember { mutableStateOf<ChatSession?>(null) }
    var renameInputText by remember { mutableStateOf("") }
    val selectedSessions = remember { mutableStateListOf<String>() }
    var showBulkDeleteConfirm by remember { mutableStateOf(false) }
    var menuExpanded by remember { mutableStateOf(false) }
    var isPlusMenuExpanded by remember { mutableStateOf(false) }
    var isModelMenuExpanded by remember { mutableStateOf(false) }
    var showThemeSelectionDialog by remember { mutableStateOf(false) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    // Audio recording state
    val recorder = remember { VoiceRecorder(context) }
    var isRecording by remember { mutableStateOf(false) }
    var recordingSeconds by remember { mutableStateOf(0) }
    var recorderFilePath by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(isRecording) {
        if (isRecording) {
            recordingSeconds = 0
            while (isRecording) {
                delay(1000)
                recordingSeconds += 1
            }
        }
    }
    
    val focusManager = LocalFocusManager.current
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var animatedMessageIds by remember(currentSessionId) { mutableStateOf(emptySet<Long>()) }

    // Activity Result Launchers for documents or pictures uploading
    val selectImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val base64 = getUriBase64(context, it)
            if (base64 != null) {
                val name = getFileName(context, it) ?: "gambar.png"
                val mimeType = context.contentResolver.getType(it) ?: "image/png"
                viewModel.attachFile(name, mimeType, base64)
            } else {
                Toast.makeText(context, "Gagal memproses gambar.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val selectFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val base64 = getUriBase64(context, it)
            if (base64 != null) {
                val name = getFileName(context, it) ?: "berkas.pdf"
                val mimeType = context.contentResolver.getType(it) ?: "application/pdf"
                viewModel.attachFile(name, mimeType, base64)
            } else {
                Toast.makeText(context, "Gagal memproses berkas.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Camera capture state and launchers
    var cameraTempUri by remember { mutableStateOf<Uri?>(null) }
    var cameraTempFile by remember { mutableStateOf<java.io.File?>(null) }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success) {
            cameraTempUri?.let { uri ->
                val base64 = getUriBase64(context, uri)
                if (base64 != null) {
                    val name = cameraTempFile?.name ?: "kamera_tugas.png"
                    val mimeType = "image/png"
                    viewModel.attachFile(name, mimeType, base64)
                } else {
                    Toast.makeText(context, "Gagal memproses gambar kamera.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            val file = java.io.File(context.cacheDir, "camera_capture_${System.currentTimeMillis()}.png")
            cameraTempFile = file
            try {
                val authority = "${context.packageName}.fileprovider"
                val uri = androidx.core.content.FileProvider.getUriForFile(context, authority, file)
                cameraTempUri = uri
                takePictureLauncher.launch(uri)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Gagal menginisialisasi kamera: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Izin kamera diperlukan untuk mengambil foto.", Toast.LENGTH_SHORT).show()
        }
    }

    val triggerCamera = {
        val hasPermission = androidx.core.content.ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.CAMERA
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        
        if (hasPermission) {
            val file = java.io.File(context.cacheDir, "camera_capture_${System.currentTimeMillis()}.png")
            cameraTempFile = file
            try {
                val authority = "${context.packageName}.fileprovider"
                val uri = androidx.core.content.FileProvider.getUriForFile(context, authority, file)
                cameraTempUri = uri
                takePictureLauncher.launch(uri)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Gagal menginisialisasi kamera: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        } else {
            cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
        }
    }

    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            val fileName = "VN_${System.currentTimeMillis()}"
            val file = recorder.startRecording(fileName)
            if (file != null) {
                recorderFilePath = file.absolutePath
                isRecording = true
            } else {
                Toast.makeText(context, "Gagal memulai perekaman audio.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Izin perekaman audio diperlukan untuk fitur Voice Note.", Toast.LENGTH_SHORT).show()
        }
    }

    val triggerRecording = {
        val hasPermission = androidx.core.content.ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.RECORD_AUDIO
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        
        if (hasPermission) {
            val fileName = "VN_${System.currentTimeMillis()}"
            val file = recorder.startRecording(fileName)
            if (file != null) {
                recorderFilePath = file.absolutePath
                isRecording = true
            } else {
                Toast.makeText(context, "Gagal memulai perekaman obrolan.", Toast.LENGTH_SHORT).show()
            }
        } else {
            micPermissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
        }
    }

    // Handle home screen widget shortcut trigger actions
    LaunchedEffect(initialWidgetAction) {
        initialWidgetAction?.let { action ->
            when (action) {
                "voice" -> {
                    Toast.makeText(context, "Membuka Obrolan Suara Zann AI...", Toast.LENGTH_SHORT).show()
                    triggerRecording()
                }
                "camera" -> {
                    Toast.makeText(context, "Membuka Kamera Zann AI...", Toast.LENGTH_SHORT).show()
                    triggerCamera()
                }
                "file" -> {
                    Toast.makeText(context, "Mengunggah Dokumen / Pelajaran...", Toast.LENGTH_SHORT).show()
                    selectFileLauncher.launch("*/*")
                }
                "gallery" -> {
                    Toast.makeText(context, "Membuka Galeri Foto...", Toast.LENGTH_SHORT).show()
                    selectImageLauncher.launch("image/*")
                }
                "live" -> {
                    Toast.makeText(context, "Memulai Kontrol Interaksi Live Zann AI...", Toast.LENGTH_SHORT).show()
                    triggerRecording()
                }
                else -> {}
            }
            onHandledWidgetAction()
        }
    }

    // Scroll to bottom indicator helper
    val showScrollToBottom by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 2
        }
    }

    // Auto-scroll to bottom of the chat dynamically
    LaunchedEffect(messages.size, isLoading) {
        if (messages.isNotEmpty()) {
            val targetIndex = if (isLoading) messages.size else messages.size - 1
            listState.animateScrollToItem(targetIndex)
        }
    }

    // Confirmation to delete a single complete discussion session
    showSessionDeleteConfirm?.let { session ->
        AlertDialog(
            onDismissRequest = { showSessionDeleteConfirm = null },
            title = { Text(text = "Hapus Obrolan", fontWeight = FontWeight.Bold) },
            text = { Text(text = "Apakah Anda yakin ingin menghapus topik obrolan \"${session.title}\"?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteSession(session.sessionId)
                        showSessionDeleteConfirm = null
                    }
                ) {
                    Text("Hapus", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSessionDeleteConfirm = null }) {
                    Text("Batal")
                }
            }
        )
    }

    // Long-press options menu bottom sheet
    if (showLongPressMenu && longPressedSession != null) {
        val session = longPressedSession!!
        val isSessionStarred = starredSessionIds.contains(session.sessionId)
        ModalBottomSheet(
            onDismissRequest = { showLongPressMenu = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .width(44.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f))
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp, start = 8.dp, end = 8.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                // Sematkan Option
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                            showLongPressMenu = false
                            viewModel.toggleStarSession(session.sessionId)
                        }
                        .padding(horizontal = 20.dp, vertical = 18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isSessionStarred) Icons.Default.StarBorder else Icons.Default.Star,
                        contentDescription = if (isSessionStarred) "Lepas Sematkan" else "Sematkan",
                        tint = if (isSessionStarred) Color(0xFFFFB300) else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(18.dp))
                    Text(
                        text = if (isSessionStarred) "Lepas Sematkan" else "Sematkan",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Ganti nama Option
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                            showLongPressMenu = false
                            renameInputText = session.title
                            showRenameDialog = session
                        }
                        .padding(horizontal = 20.dp, vertical = 18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Ganti nama",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(18.dp))
                    Text(
                        text = "Ganti nama",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Hapus Option
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                            showLongPressMenu = false
                            showSessionDeleteConfirm = session
                        }
                        .padding(horizontal = 20.dp, vertical = 18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Hapus",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(18.dp))
                    Text(
                        text = "Hapus",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }

    // Rename dialog
    showRenameDialog?.let { session ->
        AlertDialog(
            onDismissRequest = { showRenameDialog = null },
            title = { Text(text = "Ganti Nama Obrolan", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Masukkan judul baru untuk obrolan ini:", fontSize = 12.sp)
                    OutlinedTextField(
                        value = renameInputText,
                        onValueChange = { renameInputText = it },
                        placeholder = { Text("Judul Obrolan") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (renameInputText.isNotBlank()) {
                            viewModel.updateSessionTitle(session.sessionId, renameInputText.trim())
                        }
                        showRenameDialog = null
                    }
                ) {
                    Text("Simpan", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRenameDialog = null }) {
                    Text("Batal")
                }
            }
        )
    }

    // Bulk Delete Confirm Dialog
    if (showBulkDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showBulkDeleteConfirm = false },
            title = { Text(text = "Hapus Beberapa Obrolan", fontWeight = FontWeight.Bold) },
            text = {
                Text(text = "Apakah Anda yakin ingin menghapus ${selectedSessions.size} topik obrolan terpilih?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteSessions(selectedSessions.toList())
                        selectedSessions.clear()
                        showBulkDeleteConfirm = false
                    }
                ) {
                    Text("Hapus", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showBulkDeleteConfirm = false }) {
                    Text("Batal")
                }
            }
        )
    }

    // AI Homework Photo Analysis Dialog
    if (showTaskAnalysisDialog) {
        val attachedBitmap = remember(attachedFileBase64) {
            attachedFileBase64?.let { base64 ->
                try {
                    val bytes = android.util.Base64.decode(base64, android.util.Base64.DEFAULT)
                    android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.let {
                        it.asImageBitmap()
                    }
                } catch (e: Exception) {
                    null
                }
            }
        }
        
        AlertDialog(
            onDismissRequest = { showTaskAnalysisDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Assignment,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(text = "Tutor Foto Tugas AI")
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Foto atau pilih gambar tugas Anda (seperti Soal Matematika, Kode Program, atau Dokumen) untuk dianalisis secara instan oleh Zann AI.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Foto Preview / Upload options
                    if (attachedBitmap != null) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(130.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            ) {
                                Image(
                                    bitmap = attachedBitmap,
                                    contentDescription = "Preview Gambar Tugas",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Brush.verticalGradient(
                                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f))
                                        ))
                                )
                                Text(
                                    text = attachedFileName ?: "Gambar terpilih.png",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(8.dp)
                                )
                            }
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { triggerCamera() },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(vertical = 6.dp, horizontal = 12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PhotoCamera,
                                        contentDescription = "Ambil Foto Baru",
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Buka Kamera", fontSize = 11.sp)
                                }
                                
                                Button(
                                    onClick = { selectImageLauncher.launch("image/*") },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(vertical = 6.dp, horizontal = 12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AddAPhoto,
                                        contentDescription = "Pilih dari Galeri",
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Pilih Galeri", fontSize = 11.sp)
                                }
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Card(
                                onClick = { triggerCamera() },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(115.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                                ),
                                border = BorderStroke(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                                )
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize().padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PhotoCamera,
                                        contentDescription = "Gunakan Kamera",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(26.dp)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Buka Kamera",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Foto Tugas Langsung",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                        modifier = Modifier.padding(horizontal = 4.dp)
                                    )
                                }
                            }
                            
                            Card(
                                onClick = { selectImageLauncher.launch("image/*") },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(115.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                                ),
                                border = BorderStroke(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                                )
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize().padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AddAPhoto,
                                        contentDescription = "Pilih Galeri",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(26.dp)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Pilih Galeri",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Ambil dari album foto",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                        modifier = Modifier.padding(horizontal = 4.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Task Category selection card chips
                    Column {
                        Text(
                            text = "Kategori Tugas:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val categories = listOf("Soal Matematika", "Kode Program", "Dokumen")
                            
                            categories.forEach { cat ->
                                val isSelected = taskCategory == cat
                                Card(
                                    onClick = { taskCategory = cat },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerLow
                                    ),
                                    border = BorderStroke(
                                        width = if (isSelected) 1.5.dp else 1.dp,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                                    )
                                ) {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = cat,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Tindakan AI selector radios
                    Column {
                        Text(
                            text = "Tindakan AI yang Diinginkan:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        val actions = listOf(
                            "Menjelaskan isi soal",
                            "Memberikan langkah penyelesaian",
                            "Menunjukkan letak kesalahan"
                        )
                        actions.forEach { action ->
                            val isSelected = requestedAction == action
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { requestedAction = action }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { requestedAction = action }
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = action,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    // Additional text notes input field
                    OutlinedTextField(
                        value = taskAdditionalDetail,
                        onValueChange = { taskAdditionalDetail = it },
                        label = { Text("Pertanyaan Tambahan / Konteks (Opsional)") },
                        placeholder = { Text("Contoh: Tolong carikan nilai x, atau kenapa kode program ini error?") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp),
                        maxLines = 3,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val base64 = attachedFileBase64
                        if (base64 != null) {
                            val mime = attachedFileMimeType ?: "image/png"
                            val name = attachedFileName ?: "tugas.png"
                            viewModel.analyzeHomeworkPhoto(
                                base64 = base64,
                                mimeType = mime,
                                fileName = name,
                                taskCategory = taskCategory,
                                requestedAction = requestedAction,
                                additionalDetail = taskAdditionalDetail
                            )
                            showTaskAnalysisDialog = false
                            taskAdditionalDetail = ""
                        } else {
                            Toast.makeText(context, "Silakan ambil/pilih foto tugas Anda terlebih dahulu.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    enabled = attachedFileBase64 != null,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("Mulai Analisis")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTaskAnalysisDialog = false }) {
                    Text("Batal")
                }
            },
            shape = RoundedCornerShape(28.dp),
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    }

    // --- PILIH TEMA WARNA AKTIF MODAL DIALOG ---
    if (showThemeSelectionDialog) {
        val activeThemeId by viewModel.appThemeId.collectAsState()
        val themeOptions = listOf(
            Triple("cyber_fusion", "Cyber Fusion", "Cyberpunk, Biru, Cyan, Ungu"),
            Triple("sunset_horizon", "Sunset Horizon", "Hangat, Jingga Sinar Matahari"),
            Triple("forest_moss", "Forest Moss", "Hijau Daun, Teduh, Hutan"),
            Triple("cosmic_nebula", "Cosmic Nebula", "Lembayung, Misteri Angkasa"),
            Triple("mint_breeze", "Mint Breeze", "Teal Segar, Angin Kutub")
        )

        AlertDialog(
            onDismissRequest = { showThemeSelectionDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Pilih Palet Tema Warna",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Silakan pilih skema warna yang menarik untuk Zann AI & Flashcard:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    themeOptions.forEach { (id, name, desc) ->
                        val isSelected = id == activeThemeId
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.setAppThemeId(id) },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) {
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                }
                            ),
                            border = BorderStroke(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                                }
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = name,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            MaterialTheme.colorScheme.onSurface
                                        }
                                    )
                                    Text(
                                        text = desc,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                    )
                                }

                                // Theme Colors Preview Dots
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val (colorPrimary, colorSecondary, colorBg) = when(id) {
                                        "sunset_horizon" -> Triple(Color(0xFFFF5A36), Color(0xFFFFB300), Color(0xFF140D0B))
                                        "forest_moss" -> Triple(Color(0xFF4CAF50), Color(0xFF81C784), Color(0xFF0C100E))
                                        "cosmic_nebula" -> Triple(Color(0xFF9D4EDD), Color(0xFFE0A1FF), Color(0xFF0A0216))
                                        "mint_breeze" -> Triple(Color(0xFF00B4D8), Color(0xFF90E0EF), Color(0xFF050F14))
                                        else -> Triple(Color(0xFF0F62FE), Color(0xFF00F2FE), Color(0xFF0D0E15))
                                    }

                                    // Primary Dot
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clip(CircleShape)
                                            .background(colorPrimary)
                                    )
                                    // Secondary Dot
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clip(CircleShape)
                                            .background(colorSecondary)
                                    )
                                    // Dark Bg Dot
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clip(CircleShape)
                                            .background(colorBg)
                                            .border(1.dp, Color.White.copy(alpha = 0.15f), CircleShape)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showThemeSelectionDialog = false },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Selesai")
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    }

    // --- MEMORI PEMBELAJARAN BERKELANJUTAN MODAL DIALOG ---
    if (showLearningMemoryDialog) {
        val learnedList by viewModel.learnedKnowledge.collectAsState()
        var newManualContent by remember { mutableStateOf("") }
        var selectedType by remember { mutableStateOf("Pengetahuan") }
        val typesList = listOf("Pengetahuan", "Koreksi", "Preferensi", "Gaya Bahasa")
        var knowledgeSearchQuery by remember { mutableStateOf("") }
        var activeFilterType by remember { mutableStateOf("Semua") }
        
        val filteredKnowledgeList = remember(learnedList, knowledgeSearchQuery, activeFilterType) {
            learnedList.filter { item ->
                val matchesSearch = item.content.contains(knowledgeSearchQuery, ignoreCase = true)
                val matchesType = if (activeFilterType == "Semua") true else item.type == activeFilterType
                matchesSearch && matchesType
            }
        }
        
        Dialog(
            onDismissRequest = { showLearningMemoryDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .fillMaxHeight(0.85f)
                    .clip(RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.TipsAndUpdates,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Zann AI Brain Memory",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Pembelajaran Berkelanjutan Real-time",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                            }
                        }
                        
                        IconButton(onClick = { showLearningMemoryDialog = false }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Tutup",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Main Scrollable Area
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Form Input Manual Guideline/Pengetahuan
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Ajarkan Sesuatu ke Zann AI",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                
                                // Types Selector Chips
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    typesList.forEach { type ->
                                        val isSel = selectedType == type
                                        Card(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable { selectedType = type },
                                            shape = RoundedCornerShape(8.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                                            ),
                                            border = if (isSel) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                                        ) {
                                            Box(
                                                modifier = Modifier.padding(vertical = 6.dp, horizontal = 2.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = type,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isSel) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(10.dp))
                                
                                OutlinedTextField(
                                    value = newManualContent,
                                    onValueChange = { newManualContent = it },
                                    placeholder = { Text("Contoh: 'Pengguna bernama Fauzan' atau 'Gunakan bahasa santai saat menjawab'", fontSize = 12.sp) },
                                    modifier = Modifier.fillMaxWidth(),
                                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp),
                                    maxLines = 2,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                                    )
                                )
                                
                                Spacer(modifier = Modifier.height(12.dp))
                                
                                Button(
                                    onClick = {
                                        if (newManualContent.isNotBlank()) {
                                            viewModel.addNewManualKnowledge(selectedType, newManualContent)
                                            newManualContent = ""
                                            Toast.makeText(context, "Berhasil ditambahkan ke brain memory!", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    enabled = newManualContent.isNotBlank()
                                ) {
                                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Simpan ke Memori Zann AI", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        
                        // Stored Knowledge Header & Actions
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Basis Pengetahuan Terlampir (${filteredKnowledgeList.size} dari ${learnedList.size})",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                if (learnedList.isNotEmpty()) {
                                    TextButton(onClick = { viewModel.clearAllLearnedKnowledge() }) {
                                        Text("Bersihkan Semua", fontSize = 11.sp, color = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }

                            // Clipboard Export Button alongside Search field
                            if (learnedList.isNotEmpty()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedTextField(
                                        value = knowledgeSearchQuery,
                                        onValueChange = { knowledgeSearchQuery = it },
                                        placeholder = { Text("Filter teks memori...", fontSize = 11.sp) },
                                        modifier = Modifier.weight(1.2f),
                                        leadingIcon = { Icon(Icons.Default.FilterList, contentDescription = null, modifier = Modifier.size(14.dp)) },
                                        singleLine = true,
                                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                                        )
                                    )

                                    Button(
                                        onClick = {
                                            val textToCopy = filteredKnowledgeList.joinToString("\n\n") { 
                                                "[${it.type.uppercase()}] (${java.text.SimpleDateFormat("dd/MM HH:mm", java.util.Locale.getDefault()).format(it.timestamp)})\n${it.content}"
                                            }
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                            val clip = android.content.ClipData.newPlainText("Zann AI Brain Memory", textToCopy)
                                            clipboard.setPrimaryClip(clip)
                                            Toast.makeText(context, "Selesai mengekspor ${filteredKnowledgeList.size} catatan ke clipboard!", Toast.LENGTH_SHORT).show()
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.weight(1f),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                                        enabled = filteredKnowledgeList.isNotEmpty()
                                    ) {
                                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Salin Memori", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                // Category tabs / filter chips row
                                val chipCategories = listOf("Semua") + typesList
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    chipCategories.forEach { category ->
                                        val isSel = activeFilterType == category
                                        Card(
                                            modifier = Modifier
                                                .clickable { activeFilterType = category },
                                            shape = RoundedCornerShape(6.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = if (isSel) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                            ),
                                            border = BorderStroke(1.dp, if (isSel) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f) else Color.Transparent)
                                        ) {
                                            Text(
                                                text = category,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        
                        // List Items
                        if (filteredKnowledgeList.isEmpty()) {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                        modifier = Modifier.size(36.dp)
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "Memori tidak ditemukan.",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = "Zann AI mempelajari obrolan secara otomatis. Koreksi pesan atau ajarkan fakta baru maka memori ini akan bertambah secara berkelanjutan!",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }
                        } else {
                            filteredKnowledgeList.forEach { knowledge ->
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surface
                                    ),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Card(
                                                    shape = RoundedCornerShape(6.dp),
                                                    colors = CardDefaults.cardColors(
                                                        containerColor = when (knowledge.type) {
                                                            "Koreksi" -> Color(0xFFFDE8E8)
                                                            "Preferensi" -> Color(0xFFE1EFFE)
                                                            "Gaya Bahasa" -> Color(0xFFEDEBFE)
                                                            else -> Color(0xFFE5F6FD)
                                                        }
                                                    )
                                                ) {
                                                    Text(
                                                        text = knowledge.type.uppercase(),
                                                        fontSize = 8.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = when (knowledge.type) {
                                                            "Koreksi" -> Color(0xFF9B1C1C)
                                                            "Preferensi" -> Color(0xFF1E429F)
                                                            "Gaya Bahasa" -> Color(0xFF5850EC)
                                                            else -> Color(0xFF0D5A77)
                                                        },
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = java.text.SimpleDateFormat("dd/MM HH:mm", java.util.Locale.getDefault()).format(knowledge.timestamp),
                                                    fontSize = 9.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = knowledge.content,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                        
                                        IconButton(onClick = { viewModel.deleteLearnedKnowledge(knowledge.id) }) {
                                            Icon(
                                                imageVector = Icons.Default.DeleteOutline,
                                                contentDescription = "Hapus",
                                                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // --- ZANNAI LIVE INTERACTIVE VOICE CHAT WINDOW OVERLAY (INTEGRATED SPEECHRECOGNIZER) ---
    if (showLiveAiMode) {
        var liveStatus by remember { mutableStateOf("Menunggu obrolan...") }
        var lastSpeakerText by remember { mutableStateOf("") }
        var isListeningForVoice by remember { mutableStateOf(false) }
        var liveInputText by remember { mutableStateOf("") }
        
        // Audio and Text-to-Speech management
        var liveTts by remember { mutableStateOf<android.speech.tts.TextToSpeech?>(null) }
        var speechRecognizer by remember { mutableStateOf<android.speech.SpeechRecognizer?>(null) }
        val scope = rememberCoroutineScope()
        
        DisposableEffect(showLiveAiMode) {
            if (!showLiveAiMode) return@DisposableEffect onDispose {}

            var ttsInstance: android.speech.tts.TextToSpeech? = null
            ttsInstance = android.speech.tts.TextToSpeech(context) { status ->
                if (status == android.speech.tts.TextToSpeech.SUCCESS) {
                    val idLoc = java.util.Locale("id", "ID")
                    ttsInstance?.language = idLoc
                    ttsInstance?.setPitch(0.82f) // Male lower pitch voice setup
                    ttsInstance?.setSpeechRate(0.95f)
                }
            }
            liveTts = ttsInstance

            var recognizerInstance: android.speech.SpeechRecognizer? = null
            if (android.speech.SpeechRecognizer.isRecognitionAvailable(context)) {
                recognizerInstance = android.speech.SpeechRecognizer.createSpeechRecognizer(context)
                recognizerInstance.setRecognitionListener(object : android.speech.RecognitionListener {
                    override fun onReadyForSpeech(params: android.os.Bundle?) {
                        liveStatus = "Mendengarkan... Silakan bicara"
                    }
                    override fun onBeginningOfSpeech() {
                        liveStatus = "Mendengar ucapan..."
                    }
                    override fun onRmsChanged(rmsdB: Float) {}
                    override fun onBufferReceived(buffer: ByteArray?) {}
                    override fun onEndOfSpeech() {
                        liveStatus = "Memproses suara..."
                    }
                    override fun onError(error: Int) {
                        isListeningForVoice = false
                        val errorMsg = when (error) {
                            android.speech.SpeechRecognizer.ERROR_AUDIO -> "Error perekaman audio."
                            android.speech.SpeechRecognizer.ERROR_CLIENT -> "Klien terputus."
                            android.speech.SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Izin mikrofon diperlukan."
                            android.speech.SpeechRecognizer.ERROR_NETWORK -> "Kesalahan jaringan."
                            android.speech.SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Waktu jaringan habis."
                            android.speech.SpeechRecognizer.ERROR_NO_MATCH -> "Tidak terdengar suara. Silakan coba lagi."
                            android.speech.SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Mesin suara sibuk."
                            android.speech.SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Waktu bicara habis."
                            else -> "Gagal menangkap input (Error $error)."
                        }
                        liveStatus = errorMsg
                    }
                    override fun onResults(results: android.os.Bundle?) {
                        isListeningForVoice = false
                        val matches = results?.getStringArrayList(android.speech.SpeechRecognizer.RESULTS_RECOGNITION)
                        val spokenText = matches?.firstOrNull()?.trim()
                        if (!spokenText.isNullOrBlank()) {
                            liveStatus = "Zann AI sedang berpikir..."
                            lastSpeakerText = "Anda: $spokenText"
                            scope.launch {
                                val reply = viewModel.getLiveResponse(spokenText)
                                lastSpeakerText = "Anda: $spokenText\n\nZann AI: $reply"
                                liveStatus = "Zann AI berbicara..."
                                liveTts?.speak(reply, android.speech.tts.TextToSpeech.QUEUE_FLUSH, null, null)
                            }
                        } else {
                            liveStatus = "Perekaman selesai tanpa ucapan."
                        }
                    }
                    override fun onPartialResults(partialResults: android.os.Bundle?) {
                        val matches = partialResults?.getStringArrayList(android.speech.SpeechRecognizer.RESULTS_RECOGNITION)
                        val partialText = matches?.firstOrNull() ?: ""
                        if (partialText.isNotEmpty()) {
                            liveStatus = "Mendengar: $partialText..."
                        }
                    }
                    override fun onEvent(eventType: Int, params: android.os.Bundle?) {}
                })
            }
            speechRecognizer = recognizerInstance

            onDispose {
                ttsInstance?.stop()
                ttsInstance?.shutdown()
                recognizerInstance?.destroy()
            }
        }

        // Animated sound visualizer wave pulse value
        val transition = rememberInfiniteTransition(label = "pulse_trans")
        val pulseScale1 by transition.animateFloat(
            initialValue = 1f,
            targetValue = 1.6f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "p1"
        )
        val pulseScale2 by transition.animateFloat(
            initialValue = 1f,
            targetValue = 1.9f,
            animationSpec = infiniteRepeatable(
                animation = tween(1600, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "p2"
        )

        Dialog(
            onDismissRequest = {
                liveTts?.stop()
                showLiveAiMode = false
            },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF090A0F)), // Immersive deep cosmic space feel
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF090A0F)
                ),
                shape = RoundedCornerShape(0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Header Status
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 28.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(
                                        color = if (isListeningForVoice) Color.Green else MaterialTheme.colorScheme.primary,
                                        shape = CircleShape
                                    )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Zann AI Live",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                        
                        IconButton(
                            onClick = {
                                liveTts?.stop()
                                showLiveAiMode = false
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Tutup",
                                tint = Color.LightGray,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    // Sound Visualizer / Interaction Bubble
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = liveStatus,
                            color = if (isListeningForVoice) Color.Green else Color.LightGray,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
                        // Soundwave pulsing circle layout
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(240.dp)
                                .padding(vertical = 24.dp)
                        ) {
                            // Pulse Layer 2
                            if (isListeningForVoice || liveStatus.contains("Berbicara")) {
                                Box(
                                    modifier = Modifier
                                        .size(100.dp)
                                        .graphicsLayer {
                                            scaleX = pulseScale2
                                            scaleY = pulseScale2
                                            alpha = (1f - (pulseScale2 - 1f) / 0.9f).coerceIn(0f, 1f)
                                        }
                                        .background(
                                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                            shape = CircleShape
                                        )
                                )
                                // Pulse Layer 1
                                Box(
                                    modifier = Modifier
                                        .size(100.dp)
                                        .graphicsLayer {
                                            scaleX = pulseScale1
                                            scaleY = pulseScale1
                                            alpha = (1f - (pulseScale1 - 1f) / 0.6f).coerceIn(0f, 1f)
                                        }
                                        .background(
                                            color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.25f),
                                            shape = CircleShape
                                        )
                                )
                            }
                            
                            // Center Sound Bubble Icon
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(100.dp)
                                    .background(
                                        brush = Brush.linearGradient(
                                            listOf(
                                                MaterialTheme.colorScheme.primary,
                                                MaterialTheme.colorScheme.tertiary
                                            )
                                        ),
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        val hasPermission = androidx.core.content.ContextCompat.checkSelfPermission(
                                            context,
                                            android.Manifest.permission.RECORD_AUDIO
                                        ) == android.content.pm.PackageManager.PERMISSION_GRANTED

                                        if (!hasPermission) {
                                            micPermissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
                                            liveStatus = "Meminta izin mikrofon..."
                                            return@clickable
                                        }

                                        if (isListeningForVoice) {
                                            isListeningForVoice = false
                                            speechRecognizer?.stopListening()
                                            liveStatus = "Zann AI memproses..."
                                        } else {
                                            liveTts?.stop()
                                            isListeningForVoice = true
                                            
                                            val recognizerIntent = android.content.Intent(android.speech.RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                                putExtra(android.speech.RecognizerIntent.EXTRA_LANGUAGE_MODEL, android.speech.RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                                putExtra(android.speech.RecognizerIntent.EXTRA_LANGUAGE, "id-ID")
                                                putExtra(android.speech.RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                                            }
                                            
                                            if (speechRecognizer != null) {
                                                speechRecognizer?.startListening(recognizerIntent)
                                                liveStatus = "Silakan bicara..."
                                            } else {
                                                liveStatus = "Voice input tidak didukung di perangkat ini."
                                                isListeningForVoice = false
                                            }
                                        }
                                    }
                            ) {
                                Icon(
                                    imageVector = if (isListeningForVoice) Icons.Default.Mic else Icons.Default.RecordVoiceOver,
                                    contentDescription = "Voice Mode Toggle",
                                    tint = Color.White,
                                    modifier = Modifier.size(42.dp)
                                )
                            }
                        }

                        // Last Dialogue Transcript Box
                        if (lastSpeakerText.isNotEmpty()) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 140.dp)
                                    .padding(horizontal = 16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFF1E1F22)
                                ),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .verticalScroll(rememberScrollState())
                                        .padding(16.dp)
                                ) {
                                    Text(
                                        text = lastSpeakerText,
                                        color = Color(0xFFE3E2E6),
                                        fontSize = 12.sp,
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }
                    }

                    // Combined Quick typing area for full interaction versatility
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 32.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = liveInputText,
                            onValueChange = { liveInputText = it },
                            placeholder = { Text("Ketik pesan Anda ke Zann AI...", color = Color.Gray, fontSize = 13.sp) },
                            modifier = Modifier.weight(1f),
                            textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 13.sp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = Color.DarkGray
                            ),
                            maxLines = 1,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                            keyboardActions = KeyboardActions(
                                onSend = {
                                    if (liveInputText.isNotBlank()) {
                                        liveStatus = "Zann AI sedang berpikir..."
                                        isListeningForVoice = false
                                        val typed = liveInputText
                                        scope.launch {
                                            val reply = viewModel.getLiveResponse(typed)
                                            lastSpeakerText = "Anda: $typed\n\nZann AI: $reply"
                                            liveStatus = "Zann AI berbicara..."
                                            liveTts?.speak(reply, android.speech.tts.TextToSpeech.QUEUE_FLUSH, null, null)
                                            liveInputText = ""
                                        }
                                    }
                                }
                            )
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        IconButton(
                            onClick = {
                                if (liveInputText.isNotBlank()) {
                                    liveStatus = "Zann AI sedang berpikir..."
                                    isListeningForVoice = false
                                    val typed = liveInputText
                                    scope.launch {
                                        val reply = viewModel.getLiveResponse(typed)
                                        lastSpeakerText = "Anda: $typed\n\nZann AI: $reply"
                                        liveStatus = "Zann AI berbicara..."
                                        liveTts?.speak(reply, android.speech.tts.TextToSpeech.QUEUE_FLUSH, null, null)
                                        liveInputText = ""
                                    }
                                }
                            },
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.primary, CircleShape)
                                .size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Kirim",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // Custom design list dialog for conversation history index
    if (showHistoryDialog) {
        Dialog(
            onDismissRequest = { showHistoryDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .fillMaxHeight(0.75f)
                    .clip(RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {
                    // Dialog Header Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Riwayat Obrolan",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        IconButton(
                            onClick = { showHistoryDialog = false }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Tutup",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // "New Chat" Quick Action in Dialog
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.createNewChat()
                                showHistoryDialog = false
                            },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.95f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddComment,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Percakapan Baru",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Search Bar & Filter Row
                    OutlinedTextField(
                        value = chatSearchQuery,
                        onValueChange = { viewModel.setChatSearchQuery(it) },
                        placeholder = { Text("Cari obrolan...", fontSize = 11.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(15.dp)) },
                        trailingIcon = {
                            if (chatSearchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.setChatSearchQuery("") }) {
                                    Icon(Icons.Default.Close, contentDescription = "Bersihkan", modifier = Modifier.size(13.dp))
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true,
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Riwayat Percakapan",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        IconButton(
                            onClick = { viewModel.toggleFilterOnlyStarredChats() },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = if (filterOnlyStarredChats) Icons.Default.FilterAlt else Icons.Default.FilterAltOff,
                                contentDescription = "Saring Bintang",
                                tint = if (filterOnlyStarredChats) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Conversation Sessions List
                    if (filteredSessions.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChatBubbleOutline,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Belum Ada Riwayat Percakapan",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(filteredSessions) { session ->
                                val isSelected = session.sessionId == currentSessionId
                                val isChecked = selectedSessions.contains(session.sessionId)
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .combinedClickable(
                                            onClick = {
                                                viewModel.selectSession(session.sessionId)
                                                showHistoryDialog = false
                                            },
                                            onLongClick = {
                                                longPressedSession = session
                                                showLongPressMenu = true
                                            }
                                        ),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) {
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                                        } else {
                                            MaterialTheme.colorScheme.surfaceContainer
                                        }
                                    ),
                                    border = BorderStroke(
                                        width = 1.dp,
                                        color = if (isSelected) {
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                                        } else {
                                            MaterialTheme.colorScheme.outline.copy(alpha = 0.08f)
                                        }
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 12.dp, top = 8.dp, bottom = 8.dp, end = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Forum,
                                            contentDescription = null,
                                            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                            modifier = Modifier.size(16.dp)
                                        )

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column(
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(
                                                text = session.title,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = "Aktif",
                                                fontSize = 10.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                            )
                                        }

                                        val isSessionStarred = starredSessionIds.contains(session.sessionId)
                                        IconButton(
                                            onClick = { viewModel.toggleStarSession(session.sessionId) },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (isSessionStarred) Icons.Default.Star else Icons.Default.StarBorder,
                                                contentDescription = "Bintang",
                                                tint = if (isSessionStarred) Color(0xFFFFB300) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                                modifier = Modifier.size(17.dp)
                                            )
                                        }

                                        Checkbox(
                                            checked = isChecked,
                                            onCheckedChange = { checked ->
                                                if (checked == true) {
                                                    selectedSessions.add(session.sessionId)
                                                } else {
                                                    selectedSessions.remove(session.sessionId)
                                                }
                                            },
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

      ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                drawerShape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp),
                modifier = Modifier.width(310.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 24.dp)
                ) {
                    // Drawer Header with futuristic branding
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(46.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.zann_ai_z_logo_1781975518951),
                                contentDescription = "Zann AI App Logo",
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Zann AI",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                letterSpacing = (-0.5).sp
                            )
                            Text(
                                text = "Asisten Cerdas Cepat",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.60f),
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Unified Dropdown Menu for Learning Features (Flashcards & Custom Memory)
                        Box {
                            Card(
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                                ),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                                modifier = Modifier
                                    .clickable { showCombinedMenu = true }
                                    .testTag("unified_learning_menu_button")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = "Fitur Belajar",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Fitur",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            DropdownMenu(
                                expanded = showCombinedMenu,
                                onDismissRequest = { showCombinedMenu = false },
                                modifier = Modifier.width(200.dp)
                            ) {
                                DropdownMenuItem(
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Style,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.secondary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text(
                                                    text = "Flashcard Zann AI",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.secondary
                                                )
                                                Text(
                                                    text = "Kuis & Pembuat Otomatis",
                                                    fontSize = 9.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                                )
                                            }
                                        }
                                    },
                                    onClick = {
                                        showCombinedMenu = false
                                        onNavigateToFlashcards()
                                        coroutineScope.launch { drawerState.close() }
                                    }
                                )
                                
                                DropdownMenuItem(
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.TipsAndUpdates,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text(
                                                    text = "Memori Belajar Zann AI",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                                Text(
                                                    text = "Basis Pengetahuan Kustom",
                                                    fontSize = 9.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                                )
                                            }
                                        }
                                    },
                                    onClick = {
                                        showCombinedMenu = false
                                        showLearningMemoryDialog = true
                                        coroutineScope.launch { drawerState.close() }
                                    }
                                )

                                DropdownMenuItem(
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.MenuBook,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.tertiary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text(
                                                    text = "Rencana Belajar Zann AI",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.tertiary
                                                )
                                                Text(
                                                    text = "Pecah Topik Sesuai Target",
                                                    fontSize = 9.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                                )
                                            }
                                        }
                                    },
                                    onClick = {
                                        showCombinedMenu = false
                                        onNavigateToPlanner()
                                        coroutineScope.launch { drawerState.close() }
                                    }
                                )
                            }
                        }
                    }

                    // CTA: Percakapan Baru button
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.createNewChat()
                                coroutineScope.launch { drawerState.close() }
                            }
                            .testTag("drawer_new_chat_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddComment,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Percakapan Baru",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Text Section Header
                    Text(
                        text = "RIWAYAT OBROLAN",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
                    )

                    // Sessions / History content - Scrollable
                    if (sessions.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChatBubbleOutline,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Belum Ada Riwayat",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(sortedSessions) { session ->
                                val isSelected = session.sessionId == currentSessionId
                                val isChecked = selectedSessions.contains(session.sessionId)
                                val isSessionStarred = starredSessionIds.contains(session.sessionId)
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .combinedClickable(
                                            onClick = {
                                                viewModel.selectSession(session.sessionId)
                                                coroutineScope.launch { drawerState.close() }
                                            },
                                            onLongClick = {
                                                longPressedSession = session
                                                showLongPressMenu = true
                                            }
                                        ),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) {
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                                        } else {
                                            Color.Transparent
                                        }
                                    ),
                                    border = if (isSelected) {
                                        BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f))
                                    } else {
                                        null
                                    }
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 12.dp, top = 8.dp, bottom = 8.dp, end = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Forum,
                                            contentDescription = null,
                                            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                            modifier = Modifier.size(16.dp)
                                        )

                                        Spacer(modifier = Modifier.width(10.dp))

                                        Column(
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                if (isSessionStarred) {
                                                    Icon(
                                                        imageVector = Icons.Default.Star,
                                                        contentDescription = "Disematkan",
                                                        tint = Color(0xFFFFB300),
                                                        modifier = Modifier.size(14.dp).padding(end = 4.dp)
                                                    )
                                                }
                                                Text(
                                                    text = session.title,
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                    modifier = Modifier.weight(1f, fill = false)
                                                )
                                            }
                                            Text(
                                                text = if (isSessionStarred) "Disematkan" else "Aktif",
                                                fontSize = 10.sp,
                                                color = if (isSessionStarred) Color(0xFFFFB300) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                            )
                                        }

                                        Checkbox(
                                            checked = isChecked,
                                            onCheckedChange = { checked ->
                                                if (checked == true) {
                                                    selectedSessions.add(session.sessionId)
                                                } else {
                                                    selectedSessions.remove(session.sessionId)
                                                }
                                            },
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Divider segment
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Bottom settings / actions area
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    if (selectedSessions.isEmpty()) {
                                        Toast.makeText(context, "Pilih setidaknya satu obrolan untuk dihapus", Toast.LENGTH_SHORT).show()
                                    } else {
                                        showBulkDeleteConfirm = true
                                    }
                                }
                                .padding(vertical = 8.dp, horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = "Hapus Chat",
                                    tint = if (selectedSessions.isNotEmpty()) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Hapus Chat",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedSessions.isNotEmpty()) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                            }
                            if (selectedSessions.isNotEmpty()) {
                                Card(
                                    shape = RoundedCornerShape(8.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.errorContainer
                                    )
                                ) {
                                    Text(
                                        text = "${selectedSessions.size}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.onErrorContainer,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        // Color theme choice option row
                        val appThemeId by viewModel.appThemeId.collectAsState()
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { showThemeSelectionDialog = true }
                                .padding(vertical = 8.dp, horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Palette,
                                    contentDescription = "Palet Warna",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Palet Warna",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            // Small colored indicator badge
                            Card(
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text(
                                    text = when(appThemeId) {
                                        "sunset_horizon" -> "Sunset"
                                        "forest_moss" -> "Forest"
                                        "cosmic_nebula" -> "Cosmic"
                                        "mint_breeze" -> "Mint"
                                        else -> "Cyber"
                                    },
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    ) {
        // Abstract Cyber Ambient background with corner blurry light sources
        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                coroutineScope.launch { drawerState.open() }
                            },
                            modifier = Modifier.testTag("drawer_menu_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Buka Menu",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Pulsing avatar frame
                            val infiniteTransition = rememberInfiniteTransition(label = "avatar_pulse")
                            val pulseAlpha by infiniteTransition.animateFloat(
                                initialValue = 0.2f,
                                targetValue = 0.7f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(1500, easing = FastOutSlowInEasing),
                                    repeatMode = RepeatMode.Reverse
                                ),
                                label = "pulse_alpha"
                            )
                            val pulseScale by infiniteTransition.animateFloat(
                                initialValue = 0.9f,
                                targetValue = 1.15f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(1500, easing = FastOutSlowInEasing),
                                    repeatMode = RepeatMode.Reverse
                                ),
                                label = "pulse_scale"
                            )

                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.size(42.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .graphicsLayer(scaleX = pulseScale, scaleY = pulseScale)
                                        .background(
                                            Brush.radialGradient(
                                                listOf(
                                                    MaterialTheme.colorScheme.primary.copy(alpha = pulseAlpha),
                                                    Color.Transparent
                                                )
                                            ),
                                            CircleShape
                                        )
                                )

                                // Main circular avatar showing custom launcher icon
                                Image(
                                    painter = painterResource(id = R.drawable.zann_ai_z_logo_1781975518951),
                                    contentDescription = "Zann AI Avatar",
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Zann AI",
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                Brush.linearGradient(
                                                    listOf(
                                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                                        MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f)
                                                    )
                                                ),
                                                RoundedCornerShape(6.dp)
                                            )
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "v3",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(top = 1.dp)
                                ) {
                                    val greenPulseScale by infiniteTransition.animateFloat(
                                        initialValue = 0.7f,
                                        targetValue = 1.3f,
                                        animationSpec = infiniteRepeatable(
                                            animation = tween(1000, easing = LinearEasing),
                                            repeatMode = RepeatMode.Restart
                                        ),
                                        label = "green_pulse_scale"
                                    )

                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.size(10.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .graphicsLayer(scaleX = greenPulseScale, scaleY = greenPulseScale)
                                                .background(Color(0xFF4CAF50).copy(alpha = 0.5f), CircleShape)
                                        )
                                        Box(
                                            modifier = Modifier
                                                .size(5.dp)
                                                .background(Color(0xFF4CAF50), CircleShape)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Online & Real-time",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                    )
                                }
                            }
                        }
                    },
                    actions = {
                        val isDarkTheme by viewModel.isDarkTheme.collectAsState()
                        IconButton(
                            onClick = { showLiveAiMode = true },
                            modifier = Modifier.testTag("ai_live_mode_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.RecordVoiceOver,
                                contentDescription = "AI Live Mode",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        IconButton(
                            onClick = { viewModel.toggleTheme() },
                            modifier = Modifier.testTag("theme_toggle_button")
                        ) {
                            Icon(
                                imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                                contentDescription = "Ganti Tema",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.95f),
                        scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer
                    )
                )
            },
            modifier = modifier.fillMaxSize()
        ) { innerPadding ->
        val ambientColorPrimary = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
        val ambientColorSecondary = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.06f)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .drawBehind {
                    drawCircle(
                        color = ambientColorPrimary,
                        radius = size.width * 0.7f,
                        center = androidx.compose.ui.geometry.Offset(size.width, 0f)
                    )
                    drawCircle(
                        color = ambientColorSecondary,
                        radius = size.width * .8f,
                        center = androidx.compose.ui.geometry.Offset(0f, size.height)
                    )
                }
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {

                // Interactive Message Feed Area
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    if (messages.isEmpty()) {
                        WelcomeState(
                            onSelectPrompt = { prompt ->
                                viewModel.sendMessage(prompt)
                            }
                        )
                    } else {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 16.dp, bottom = 80.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(messages) { message ->
                                val isLastModelMessage = message == messages.lastOrNull { it.sender == "model" }
                                val isRecent = System.currentTimeMillis() - message.timestamp < 10000
                                val shouldAnimate = isLastModelMessage && isRecent && !animatedMessageIds.contains(message.id)

                                ChatBubble(
                                    message = message,
                                    onEditUserMessage = { id, text -> viewModel.editUserMessage(id, text) },
                                    onEditAiResponse = { id, text -> viewModel.editAiResponse(id, text) },
                                    onRegenerateResponse = { id -> viewModel.regenerateResponse(id) },
                                    animateTyping = shouldAnimate,
                                    onTypingFinished = {
                                        animatedMessageIds = animatedMessageIds + message.id
                                    }
                                )
                            }

                            if (isLoading) {
                                item {
                                    AIResponseLoadingBubble()
                                }
                            }
                        }
                    }

                    // Modern Floating "Scroll to Bottom" Button
                    androidx.compose.animation.AnimatedVisibility(
                        visible = showScrollToBottom,
                        enter = fadeIn() + scaleIn(),
                        exit = fadeOut() + scaleOut(),
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(bottom = 16.dp, end = 16.dp)
                    ) {
                        FloatingActionButton(
                            onClick = {
                                coroutineScope.launch {
                                    if (messages.isNotEmpty()) {
                                        val targetIndex = if (isLoading) messages.size else messages.size - 1
                                        listState.animateScrollToItem(targetIndex)
                                    }
                                }
                            },
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            shape = CircleShape,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowDownward,
                                contentDescription = "Scroll ke bawah",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Interactive Bottom Input Deck with Glassmorphic Feel and Voice Recording Support
                Surface(
                    tonalElevation = 12.dp,
                    shadowElevation = 12.dp,
                    color = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.95f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        // Visual Upload Queue/Deck
                        if (attachedFileName != null) {
                            val isImage = attachedFileMimeType?.startsWith("image/") == true
                            val imageBitmap = remember(attachedFileBase64) {
                                if (isImage && attachedFileBase64 != null) {
                                    try {
                                        val bytes = android.util.Base64.decode(attachedFileBase64, android.util.Base64.DEFAULT)
                                        val bmp = android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                                        bmp?.asImageBitmap()
                                    } catch (e: Exception) {
                                        null
                                    }
                                } else {
                                    null
                                }
                            }

                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.85f)
                                ),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(8.dp)
                                ) {
                                    if (isImage && imageBitmap != null) {
                                        Image(
                                            bitmap = imageBitmap,
                                            contentDescription = "Preview Lampiran Gambar",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .size(56.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .border(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .background(
                                                            color = MaterialTheme.colorScheme.secondary,
                                                            shape = RoundedCornerShape(4.dp)
                                                        )
                                                        .padding(horizontal = 5.dp, vertical = 2.dp)
                                                ) {
                                                    Text(
                                                        text = "GAMBAR",
                                                        fontSize = 8.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.onSecondary,
                                                        letterSpacing = 0.3.sp
                                                    )
                                                }
                                                Text(
                                                    text = attachedFileName ?: "Gambar terpilih",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = "Siap dikirimkan ke Zann AI",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                                            )
                                        }
                                    } else {
                                        Icon(
                                            imageVector = if (attachedFileMimeType?.startsWith("audio/") == true) {
                                                Icons.Default.AudioFile
                                            } else {
                                                Icons.Default.FileOpen
                                            },
                                            contentDescription = "Tipe Lampiran",
                                            tint = MaterialTheme.colorScheme.secondary,
                                            modifier = Modifier.size(22.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = attachedFileName ?: "Berkas terlampir",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                    IconButton(
                                        onClick = { viewModel.clearAttachedFile() },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Hapus lampiran",
                                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Voice recording active panel
                        if (isRecording) {
                            Card(
                                shape = RoundedCornerShape(24.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.85f)
                                ),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.2f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 10.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        // Pulsing Mic Icon
                                        val recordingTransition = rememberInfiniteTransition("recording_mic")
                                        val scalePulse by recordingTransition.animateFloat(
                                            initialValue = 0.9f,
                                            targetValue = 1.3f,
                                            animationSpec = infiniteRepeatable(
                                                animation = tween(600, easing = LinearEasing),
                                                repeatMode = RepeatMode.Reverse
                                            ),
                                            label = "rec_pulse"
                                        )

                                        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(24.dp)) {
                                            Box(
                                                modifier = Modifier
                                                    .size(16.dp)
                                                    .graphicsLayer(scaleX = scalePulse, scaleY = scalePulse)
                                                    .background(MaterialTheme.colorScheme.error.copy(alpha = 0.4f), CircleShape)
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .size(10.dp)
                                                    .background(MaterialTheme.colorScheme.error, CircleShape)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = "Merekam Pesan Suara...",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onErrorContainer
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = String.format("%02d:%02d", recordingSeconds / 60, recordingSeconds % 60),
                                            fontSize = 13.sp,
                                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                            color = MaterialTheme.colorScheme.onErrorContainer,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        // Cancel button
                                        IconButton(
                                            onClick = {
                                                recorder.stopRecording()
                                                isRecording = false
                                                recorderFilePath = null
                                            },
                                            modifier = Modifier.size(32.dp).background(MaterialTheme.colorScheme.surface, CircleShape)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Batalkan",
                                                tint = MaterialTheme.colorScheme.error,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }

                                        // Stop and attach button
                                        IconButton(
                                            onClick = {
                                                recorder.stopRecording()
                                                isRecording = false
                                                val path = recorderFilePath
                                                if (path != null) {
                                                    val file = File(path)
                                                    if (file.exists() && file.length() > 0) {
                                                        val base64 = getFileBase64(file)
                                                        if (base64 != null) {
                                                            viewModel.attachFile(
                                                                name = "Voice Note (${recordingSeconds}s).m4a",
                                                                mimeType = "audio/mp4",
                                                                base64 = base64,
                                                                path = path
                                                            )
                                                        }
                                                    }
                                                }
                                            },
                                            modifier = Modifier.size(32.dp).background(MaterialTheme.colorScheme.error, CircleShape)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Selesai",
                                                tint = Color.White,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Text input & auxiliary icons row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val plusRotationAngle by animateFloatAsState(
                                targetValue = if (isPlusMenuExpanded) 135f else 0f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                ),
                                label = "plusRotation"
                            )

                            // Unified + feature selection menu
                            Box {
                                IconButton(
                                    onClick = { isPlusMenuExpanded = !isPlusMenuExpanded },
                                    enabled = !isLoading && !isRecording,
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Menu Fitur",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier
                                            .size(24.dp)
                                            .rotate(plusRotationAngle)
                                    )
                                }

                                DropdownMenu(
                                    expanded = isPlusMenuExpanded,
                                    onDismissRequest = { isPlusMenuExpanded = false },
                                    modifier = Modifier
                                        .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                                        .width(260.dp)
                                ) {
                                    DropdownMenuItem(
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.Assignment,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        },
                                        text = {
                                            Column {
                                                Text(
                                                    text = "Tutor Foto Tugas AI",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp
                                                )
                                                Text(
                                                    text = "Analisis & bantu jawab tugas",
                                                    fontSize = 10.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                                )
                                            }
                                        },
                                        onClick = {
                                            isPlusMenuExpanded = false
                                            taskAdditionalDetail = ""
                                            showTaskAnalysisDialog = true
                                        }
                                    )

                                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                                    DropdownMenuItem(
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.AttachFile,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.secondary,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        },
                                        text = {
                                            Column {
                                                Text(
                                                    text = "Tambahkan File",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp
                                                )
                                                Text(
                                                    text = "Unggah berkas atau lembar materi",
                                                    fontSize = 10.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                                )
                                            }
                                        },
                                        onClick = {
                                            isPlusMenuExpanded = false
                                            selectFileLauncher.launch("*/*")
                                        }
                                    )

                                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                                    DropdownMenuItem(
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.Language,
                                                contentDescription = null,
                                                tint = if (isGoogleSearchEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        },
                                        text = {
                                            Column {
                                                Text(
                                                    text = "Web Grounding",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp
                                                )
                                                Text(
                                                    text = if (isGoogleSearchEnabled) "Menggunakan info web terkini" else "Tanpa pencarian internet tambahan",
                                                    fontSize = 10.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                                )
                                            }
                                        },
                                        trailingIcon = {
                                            Switch(
                                                checked = isGoogleSearchEnabled,
                                                onCheckedChange = { 
                                                    viewModel.toggleGoogleSearch()
                                                },
                                                modifier = Modifier.scale(0.7f)
                                            )
                                        },
                                        onClick = {
                                            viewModel.toggleGoogleSearch()
                                        }
                                    )
                                }
                            }

                            // Compact Model Selector Dropdown Pill
                            Box {
                                val currentModelLabel = if (selectedModel == "gemini-2.5-flash") "Pro" else "Lite"
                                
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                                        .clickable { isModelMenuExpanded = true }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = currentModelLabel,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "Pilih Model",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                DropdownMenu(
                                    expanded = isModelMenuExpanded,
                                    onDismissRequest = { isModelMenuExpanded = false },
                                    modifier = Modifier
                                        .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                                        .width(200.dp)
                                ) {
                                    DropdownMenuItem(
                                        text = {
                                            Column {
                                                Text(
                                                    text = "3.1 Flash-Lite",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp,
                                                    color = if (selectedModel == "gemini-3.1-flash-lite-preview") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                                )
                                                Text(
                                                    text = "Jawaban tercepat",
                                                    fontSize = 10.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                                )
                                            }
                                        },
                                        onClick = {
                                            viewModel.setModel("gemini-3.1-flash-lite-preview")
                                            isModelMenuExpanded = false
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = {
                                            Column {
                                                Text(
                                                    text = "3.5 Flash",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp,
                                                    color = if (selectedModel == "gemini-2.5-flash") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                                )
                                                Text(
                                                    text = "Bantuan serbaguna",
                                                    fontSize = 10.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                                )
                                            }
                                        },
                                        onClick = {
                                            viewModel.setModel("gemini-2.5-flash")
                                            isModelMenuExpanded = false
                                        }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(4.dp))

                            OutlinedTextField(
                                value = inputText,
                                onValueChange = { inputText = it },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("message_input_field"),
                                placeholder = {
                                    Text(
                                        text = "Tanya Zann AI...",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                        fontSize = 14.sp
                                    )
                                },
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Sentences,
                                    imeAction = ImeAction.Send
                                ),
                                keyboardActions = KeyboardActions(
                                    onSend = {
                                        if ((inputText.isNotBlank() || attachedFileBase64 != null) && !isLoading) {
                                            viewModel.sendMessage(inputText)
                                            inputText = ""
                                            focusManager.clearFocus()
                                        }
                                    }
                                ),
                                maxLines = 5,
                                shape = RoundedCornerShape(26.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                                )
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            // If text is blank and no attachment, display Microphne recording button
                            if (inputText.isBlank() && attachedFileBase64 == null) {
                                IconButton(
                                    onClick = { triggerRecording() },
                                    enabled = !isLoading && !isRecording,
                                    modifier = Modifier
                                        .size(44.dp)
                                        .background(
                                            Brush.linearGradient(
                                                listOf(
                                                    MaterialTheme.colorScheme.secondary,
                                                    MaterialTheme.colorScheme.primary
                                                )
                                            ),
                                            CircleShape
                                        )
                                        .testTag("voice_record_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.KeyboardVoice,
                                        contentDescription = "Merekam Pesan Suara",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            } else {
                                val buttonBrush = if (!isLoading) {
                                    Brush.linearGradient(
                                        listOf(
                                            MaterialTheme.colorScheme.primary,
                                            MaterialTheme.colorScheme.tertiary
                                        )
                                    )
                                } else {
                                    Brush.linearGradient(
                                        listOf(
                                            MaterialTheme.colorScheme.surfaceVariant,
                                            MaterialTheme.colorScheme.surfaceVariant
                                        )
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        if (!isLoading) {
                                            viewModel.sendMessage(inputText)
                                            inputText = ""
                                            focusManager.clearFocus()
                                        }
                                    },
                                    enabled = !isLoading,
                                    modifier = Modifier
                                        .size(44.dp)
                                        .background(
                                            brush = buttonBrush,
                                            shape = CircleShape
                                        )
                                        .testTag("send_message_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Send,
                                        contentDescription = "Kirim pesan ke Zann AI",
                                        tint = if (!isLoading) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
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
fun WelcomeState(
    onSelectPrompt: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val suggestedPrompts = remember {
        listOf(
            SuggestedPromptItem("Beri ide teknologi masa depan", "💡", Icons.Default.Lightbulb, "Inspirasi & ide kreatif"),
            SuggestedPromptItem("Review ringkas buku fiksi bagus", "📚", Icons.Default.MenuBook, "Rekomendasi literatur"),
            SuggestedPromptItem("Tips produktif di pagi hari", "⚡", Icons.Default.TipsAndUpdates, "Pengembangan diri")
        )
    }

    val scrollState = rememberScrollState()
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Redesigned Welcome orbiting star portal containing launcher icon
        val infiniteTransition = rememberInfiniteTransition(label = "banner")
        val rotateAngle by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(12000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "rotate"
        )
        val breathingScale by infiniteTransition.animateFloat(
            initialValue = 0.93f,
            targetValue = 1.07f,
            animationSpec = infiniteRepeatable(
                animation = tween(2200, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "breath"
        )

        Box(
            modifier = Modifier
                .size(96.dp)
                .graphicsLayer(
                    scaleX = breathingScale,
                    scaleY = breathingScale,
                    rotationZ = rotateAngle
                )
                .background(
                    Brush.sweepGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.tertiary,
                            MaterialTheme.colorScheme.secondary,
                            MaterialTheme.colorScheme.primary
                        )
                    ),
                    CircleShape
                )
                .padding(2.5.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(rotationZ = -rotateAngle) // Stabilize image
                    .background(MaterialTheme.colorScheme.background, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.zann_ai_z_logo_1781975518951),
                    contentDescription = null,
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                )
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        Text(
            text = "Zann AI",
            fontSize = 36.sp,
            fontWeight = FontWeight.Black,
            style = androidx.compose.ui.text.TextStyle(
                brush = Brush.linearGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.secondary,
                        MaterialTheme.colorScheme.tertiary
                    )
                )
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Asisten AI online cerdas yang dibekali aneka model (Flash-Lite / Flash) serta upload file dan perekaman voice note real-time.",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
            textAlign = TextAlign.Center,
            lineHeight = 22.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "PROMPT REKOMENDASI",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f),
            letterSpacing = 1.5.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            suggestedPrompts.forEach { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectPrompt(item.prompt) },
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                    ),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.4f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = item.categoryIcon,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = item.prompt,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = item.subtext,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.Forum,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

data class SuggestedPromptItem(
    val prompt: String,
    val emoji: String,
    val categoryIcon: ImageVector,
    val subtext: String
)

@Composable
fun VoicePlayer(audioPath: String) {
    var isPlaying by remember { mutableStateOf(false) }
    var currentPosition by remember { mutableStateOf(0f) }
    var totalDuration by remember { mutableStateOf(1f) }
    
    val mediaPlayer = remember { MediaPlayer() }
    
    LaunchedEffect(audioPath) {
        try {
            mediaPlayer.reset()
            mediaPlayer.setDataSource(audioPath)
            mediaPlayer.prepare()
            totalDuration = mediaPlayer.duration.coerceAtLeast(1).toFloat()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    DisposableEffect(Unit) {
        onDispose {
            try {
                mediaPlayer.release()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (isPlaying) {
                try {
                    if (mediaPlayer.isPlaying) {
                        currentPosition = mediaPlayer.currentPosition.toFloat()
                    } else {
                        isPlaying = false
                        currentPosition = 0f
                    }
                } catch (e: Exception) {
                    isPlaying = false
                }
                delay(150)
            }
        }
    }
    
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceContainerLowest.copy(alpha = 0.4f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(vertical = 4.dp, horizontal = 10.dp)
    ) {
        IconButton(
            onClick = {
                try {
                    if (isPlaying) {
                        mediaPlayer.pause()
                        isPlaying = false
                    } else {
                        if (currentPosition >= totalDuration || currentPosition < 0f) {
                            mediaPlayer.seekTo(0)
                        }
                        mediaPlayer.start()
                        isPlaying = true
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            },
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (isPlaying) "Pause" else "Play",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(4.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Slider(
                value = currentPosition,
                onValueChange = { newVal ->
                    currentPosition = newVal
                    try {
                        mediaPlayer.seekTo(newVal.toInt())
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                },
                valueRange = 0f..totalDuration,
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                ),
                modifier = Modifier.height(18.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val currentSeconds = (currentPosition / 1000).toInt().coerceAtLeast(0)
                val totalSeconds = (totalDuration / 1000).toInt().coerceAtLeast(0)
                Text(
                    text = String.format("%02d:%02d", currentSeconds / 60, currentSeconds % 60),
                    fontSize = 9.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
                Text(
                    text = String.format("%02d:%02d", totalSeconds / 60, totalSeconds % 60),
                    fontSize = 9.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun ChatBubble(
    message: ChatMessage,
    onEditUserMessage: (Long, String) -> Unit,
    onEditAiResponse: (Long, String) -> Unit,
    onRegenerateResponse: (Long) -> Unit,
    modifier: Modifier = Modifier,
    animateTyping: Boolean = false,
    onTypingFinished: () -> Unit = {}
) {
    val isUser = message.sender == "user"
    val text = message.text

    var showEditDialog by remember { mutableStateOf(false) }
    var editedTextState by remember(message.text) {
        val displayPrompt = if (message.text.startsWith("[")) {
            val idx = message.text.indexOf("\n\n")
            if (idx != -1) message.text.substring(idx + 2) else ""
        } else {
            message.text
        }
        mutableStateOf(displayPrompt)
    }

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = {
                Text(
                    text = if (isUser) "Edit Pertanyaan Anda" else "Edit Jawaban AI",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                OutlinedTextField(
                    value = editedTextState,
                    onValueChange = { editedTextState = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 120.dp, max = 240.dp),
                    placeholder = { Text("Tulis pesan baru Anda di sini...") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        cursorColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (isUser) {
                            onEditUserMessage(message.id, editedTextState)
                        } else {
                            onEditAiResponse(message.id, editedTextState)
                        }
                        showEditDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(if (isUser) "Simpan & Regenerasi" else "Simpan Perubahan", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showEditDialog = false }
                ) {
                    Text("Batal", color = MaterialTheme.colorScheme.primary)
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        )
    }

    val isVoiceNote = text.startsWith("[voice_note:")
    val voiceNotePath = if (isVoiceNote) {
        text.substringAfter("[voice_note:").substringBefore("]")
    } else null
    val voiceCapText = if (isVoiceNote) {
        text.substringAfter("]").trim()
    } else null

    val isImage = text.startsWith("[Gambar:")
    val imageName = if (isImage) {
        text.substringBefore("]").substringAfter("[Gambar:").trim()
    } else null
    val imageCapText = if (isImage) {
        text.substringAfter("]").trim()
    } else null

    val isGenericFile = text.startsWith("[Berkas:")
    val genericFileName = if (isGenericFile) {
        text.substringBefore("]").substringAfter("[Berkas:").trim()
    } else null
    val genericFileCapText = if (isGenericFile) {
        text.substringAfter("]").trim()
    } else null

    val isGeneratedImage = text.startsWith("[generated_image:")
    val generatedImagePath = if (isGeneratedImage) {
        text.substringAfter("[generated_image:").substringBefore("]")
    } else null
    val generatedImageCaption = if (isGeneratedImage) {
        text.substringAfter("]").trim()
    } else null

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        if (!isUser) {
            // Pulsing star portal or launcher avatar
            Image(
                painter = painterResource(id = R.drawable.zann_ai_z_logo_1781975518951),
                contentDescription = "Zann AI Head Avatar",
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(10.dp))
        }

        // Custom Bubble Styling
        Card(
            shape = RoundedCornerShape(
                topStart = 20.dp,
                topEnd = 20.dp,
                bottomStart = if (isUser) 20.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 20.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = if (isUser) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.surfaceContainerHigh
                }
            ),
            border = if (isUser) null else BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = if (isUser) 3.dp else 0.dp
            ),
            modifier = Modifier.widthIn(max = if (text.contains("```")) 500.dp else 295.dp)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                if (isVoiceNote && voiceNotePath != null) {
                    // Title badge indicating a processed voice recorder
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardVoice,
                            contentDescription = null,
                            tint = if (isUser) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f) else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Pesan Suara",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    
                    // Actual voice playback bar
                    VoicePlayer(audioPath = voiceNotePath)
                    
                    if (!voiceCapText.isNullOrBlank()) {
                        Text(
                            text = voiceCapText,
                            fontSize = 14.sp,
                            color = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                            lineHeight = 22.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                } else if (isImage && imageName != null) {
                    // Frame displaying image filename
                    val messageBitmap = remember(message.fileBase64) {
                        if (message.fileBase64 != null) {
                            try {
                                val bytes = android.util.Base64.decode(message.fileBase64, android.util.Base64.DEFAULT)
                                val bmp = android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                                bmp?.asImageBitmap()
                            } catch (e: Exception) {
                                null
                            }
                        } else {
                            null
                        }
                    }

                    if (messageBitmap != null) {
                        Image(
                            bitmap = messageBitmap,
                            contentDescription = "Gambar Terlampir",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .border(
                                    width = 1.dp,
                                    color = (if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface).copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(10.dp)
                                )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                    }

                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isUser) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainer
                        ),
                        modifier = Modifier.padding(bottom = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = null,
                                tint = if (isUser) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = imageName,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isUser) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    if (!imageCapText.isNullOrBlank()) {
                        Text(
                            text = imageCapText,
                            fontSize = 14.sp,
                            color = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                            lineHeight = 22.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                } else if (isGenericFile && genericFileName != null) {
                    // Frame displaying file filename 
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isUser) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainer
                        ),
                        modifier = Modifier.padding(bottom = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AttachFile,
                                contentDescription = null,
                                tint = if (isUser) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = genericFileName,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isUser) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    if (!genericFileCapText.isNullOrBlank()) {
                        Text(
                            text = genericFileCapText,
                            fontSize = 14.sp,
                            color = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                            lineHeight = 22.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                } else if (isGeneratedImage && generatedImagePath != null) {
                    Column(modifier = Modifier.padding(bottom = 6.dp)) {
                        val context = LocalContext.current
                        val imageRequest = remember(generatedImagePath) {
                            ImageRequest.Builder(context)
                                .data(generatedImagePath)
                                .setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                                .diskCachePolicy(coil.request.CachePolicy.DISABLED)
                                .memoryCachePolicy(coil.request.CachePolicy.DISABLED)
                                .crossfade(true)
                                .crossfade(200)
                                .build()
                        }
                        
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            )
                        ) {
                            SubcomposeAsyncImage(
                                model = imageRequest,
                                contentDescription = "Gambar Hasil Buatan Zann AI",
                                modifier = Modifier.fillMaxSize(),
                                loading = {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color(0xFF1E1E2E))
                                            .padding(16.dp),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        CircularProgressIndicator(
                                            color = Color(0xFF00F2FE), // CyberTeal
                                            modifier = Modifier.size(36.dp),
                                            strokeWidth = 3.dp
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text(
                                            text = "🎨 Zann Art sedang melukis...",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color.White,
                                            textAlign = TextAlign.Center
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Menghasilkan karya seni AI beresolusi tinggi (3-8 detik)...",
                                            fontSize = 11.sp,
                                            color = Color.LightGray,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                },
                                error = { state ->
                                    val th = state.result.throwable
                                    val errorMsg = th.message ?: th.localizedMessage ?: "Format gambar tidak didukung atau server sibuk"
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color(0xFF2A1F2D))
                                            .padding(12.dp),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Gagal memuat gambar",
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(28.dp)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "Gagal memuat Gambar",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.error,
                                            textAlign = TextAlign.Center
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "Detail: $errorMsg",
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                                            textAlign = TextAlign.Center,
                                            maxLines = 2
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Button(
                                            onClick = { onRegenerateResponse(message.id) },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                                            ),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                                            modifier = Modifier.height(30.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.Refresh,
                                                    contentDescription = "Muat ulang",
                                                    tint = Color.White,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Coba Lagi (Ganti Seed)", fontSize = 10.sp, color = Color.White)
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "Atau dapat mengetuk tombol reload kecil bulat di sudut kanan bawah chat bubble ini.",
                                            fontSize = 9.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                },
                                contentScale = ContentScale.Crop
                            )
                        }
                        if (!generatedImageCaption.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = generatedImageCaption,
                                fontSize = 14.sp,
                                color = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                lineHeight = 22.sp
                            )
                        }
                    }
                } else {
                    FormattedMessageText(
                        text = text,
                        isUser = isUser,
                        animateTyping = animateTyping,
                        onTypingFinished = onTypingFinished
                    )
                }

                if (isUser) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { showEditDialog = true },
                            modifier = Modifier
                                .size(28.dp)
                                .testTag("edit_user_question_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit pertanyaan Anda",
                                tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                }

                if (!isUser) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val clipboardManager = androidx.compose.ui.platform.LocalClipboardManager.current
                        val context = LocalContext.current

                        IconButton(
                            onClick = {
                                onRegenerateResponse(message.id)
                                Toast.makeText(context, "Memperbarui jawaban...", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("regenerate_ai_response_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Regenerasi respons AI",
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        
                        IconButton(
                            onClick = { showEditDialog = true },
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("edit_ai_response_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit respons AI",
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        IconButton(
                            onClick = {
                                val textToCopy = if (isVoiceNote && voiceCapText != null) {
                                    voiceCapText
                                } else if (isImage && imageCapText != null) {
                                    imageCapText
                                } else if (isGenericFile && genericFileCapText != null) {
                                    genericFileCapText
                                } else {
                                    text
                                }
                                clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(textToCopy))
                                Toast.makeText(context, "Teks berhasil disalin!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("copy_message_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Salin respons AI",
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        if (isUser) {
            Spacer(modifier = Modifier.width(10.dp))
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(
                        Brush.linearGradient(
                            listOf(
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f),
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            )
                        ),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "User Avatar Icon",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun AIResponseLoadingBubble(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        Image(
            painter = painterResource(id = R.drawable.zann_ai_z_logo_1781975518951),
            contentDescription = "Zann AI Loader Logo",
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(10.dp))

        Card(
            shape = RoundedCornerShape(
                topStart = 18.dp,
                topEnd = 18.dp,
                bottomStart = 4.dp,
                bottomEnd = 18.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
            ),
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
            ),
            modifier = Modifier.widthIn(max = 120.dp)
        ) {
            TypingIndicator()
        }
    }
}

@Composable
fun TypingIndicator(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val transition = rememberInfiniteTransition(label = "typing_dots")

        val dotsYOffset = listOf(
            transition.animateFloat(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = keyframes {
                        durationMillis = 800
                        0f at 0 with LinearEasing
                        1f at 150 with LinearEasing
                        0f at 300 with LinearEasing
                    },
                    repeatMode = RepeatMode.Restart
                ),
                label = "dotY1"
            ),
            transition.animateFloat(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = keyframes {
                        durationMillis = 800
                        0f at 150 with LinearEasing
                        1f at 300 with LinearEasing
                        0f at 450 with LinearEasing
                    },
                    repeatMode = RepeatMode.Restart
                ),
                label = "dotY2"
            ),
            transition.animateFloat(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = keyframes {
                        durationMillis = 800
                        0f at 300 with LinearEasing
                        1f at 450 with LinearEasing
                        0f at 600 with LinearEasing
                    },
                    repeatMode = RepeatMode.Restart
                ),
                label = "dotY3"
            )
        )

        dotsYOffset.forEach { floatState ->
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .graphicsLayer(translationY = -floatState.value * 7f)
                    .background(
                        brush = Brush.linearGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.tertiary
                            )
                        ),
                        shape = CircleShape
                    )
            )
        }
    }
}

@Composable
fun parseMarkdownToAnnotatedString(text: String, isUser: Boolean): AnnotatedString {
    return buildAnnotatedString {
        val boldParts = text.split("**")
        boldParts.forEachIndexed { boldIndex, boldPart ->
            val isBold = boldIndex % 2 == 1
            
            val codeParts = boldPart.split("`")
            codeParts.forEachIndexed { codeIndex, codePart ->
                val isInlineCode = codeIndex % 2 == 1
                
                val spanStyle = when {
                    isInlineCode -> SpanStyle(
                        fontFamily = FontFamily.Monospace,
                        background = if (isUser) Color.White.copy(alpha = 0.15f) else Color.Black.copy(alpha = 0.08f),
                        color = if (isUser) Color.White else MaterialTheme.colorScheme.primary,
                        fontSize = 13.sp
                    )
                    isBold -> SpanStyle(
                        fontWeight = FontWeight.Bold
                    )
                    else -> SpanStyle()
                }
                
                withStyle(style = spanStyle) {
                    append(codePart)
                }
            }
        }
    }
}

@Composable
fun FormattedMessageText(
    text: String, 
    isUser: Boolean,
    animateTyping: Boolean = false,
    onTypingFinished: () -> Unit = {}
) {
    val clipboardManager = androidx.compose.ui.platform.LocalClipboardManager.current
    val context = LocalContext.current
    
    val citationsMarker = "🌐 **Sitasi & Referensi:**"
    val mainText: String
    val hasCitations: Boolean
    val citationLinks = mutableListOf<Pair<String, String>>()
    
    if (!isUser && text.contains(citationsMarker)) {
        val parts = text.split("\n\n---\n🌐 **Sitasi & Referensi:**")
        mainText = parts[0].trim()
        hasCitations = true
        
        val citationsPart = if (parts.size > 1) parts[1] else ""
        val linkRegex = Regex("\\[(.*?)\\]\\((https?://.*?)\\)")
        linkRegex.findAll(citationsPart).forEach { match ->
            val title = match.groups[1]?.value ?: ""
            val url = match.groups[2]?.value ?: ""
            if (title.isNotBlank() && url.isNotBlank()) {
                citationLinks.add(Pair(title, url))
            }
        }
    } else {
        mainText = text
        hasCitations = false
    }

    val totalLength = mainText.length
    var currentIndex by remember(mainText) { mutableStateOf(if (animateTyping) 0 else totalLength) }
    val isTyping = animateTyping && currentIndex < totalLength

    var cursorVisible by remember { mutableStateOf(true) }
    LaunchedEffect(isTyping) {
        if (isTyping) {
            while (true) {
                delay(400)
                cursorVisible = !cursorVisible
            }
        }
    }

    LaunchedEffect(mainText, animateTyping) {
        if (animateTyping && currentIndex < totalLength) {
            val delayTime = if (totalLength > 1500) 1L else if (totalLength > 800) 2L else 4L
            val step = if (totalLength > 2500) 8 else if (totalLength > 1500) 5 else if (totalLength > 800) 3 else if (totalLength > 300) 2 else 1
            
            while (currentIndex < totalLength) {
                delay(delayTime)
                currentIndex = (currentIndex + step).coerceAtMost(totalLength)
            }
            onTypingFinished()
        }
    }

    val displayedMainText = if (isTyping) mainText.take(currentIndex) else mainText

    val parts = displayedMainText.split("```")
    Column(
        modifier = Modifier.clickable(
            enabled = isTyping,
            onClick = {
                currentIndex = totalLength
                onTypingFinished()
            },
            interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
            indication = null
        ),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (parts.size <= 1) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = parseMarkdownToAnnotatedString(displayedMainText, isUser),
                    fontSize = 14.sp,
                    color = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                    lineHeight = 22.sp,
                    fontWeight = if (isUser) FontWeight.Medium else FontWeight.Normal,
                    modifier = Modifier.weight(1f, fill = false)
                )
                if (isTyping && cursorVisible) {
                    Text(
                        text = "▎",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 2.dp)
                    )
                }
            }
        } else {
            parts.forEachIndexed { index, part ->
                if (index % 2 == 0) {
                    if (part.isNotBlank() || (index == parts.lastIndex && isTyping)) {
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = parseMarkdownToAnnotatedString(part.trim(), isUser),
                                fontSize = 14.sp,
                                color = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                lineHeight = 22.sp,
                                fontWeight = if (isUser) FontWeight.Medium else FontWeight.Normal,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                            if (isTyping && cursorVisible && index == parts.lastIndex) {
                                Text(
                                    text = "▎",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(start = 2.dp)
                                )
                            }
                        }
                    }
                } else {
                    val lines = part.split("\n")
                    val rawLang = lines.firstOrNull()?.trim() ?: ""
                    val isActualLang = rawLang.isNotEmpty() && 
                                       rawLang.length < 15 && 
                                       !rawLang.any { it == '=' || it == '(' || it == '{' || it == ' ' || it == '"' || it == '\'' || it == '<' }
                                       
                    val lang = if (isActualLang) {
                        when (rawLang.lowercase()) {
                            "vbnet", "vb.net", "vb", "visualbasic" -> "VB.Net"
                            "kotlin", "kt" -> "Kotlin"
                            "java" -> "Java"
                            "py", "python" -> "Python"
                            "cpp", "c++" -> "C++"
                            "c" -> "C"
                            "csharp", "cs", "c#" -> "C#"
                            "js", "javascript" -> "JavaScript"
                            "ts", "typescript" -> "TypeScript"
                            "html" -> "HTML"
                            "css" -> "CSS"
                            "sql" -> "SQL"
                            "json" -> "JSON"
                            "xml" -> "XML"
                            else -> rawLang.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                        }
                    } else {
                        "Kode Program"
                    }
                    
                    val codeContent = if (isActualLang && lines.size > 1) {
                        lines.drop(1).joinToString("\n").trim()
                    } else {
                        part.trim()
                    }
                    
                    if (codeContent.isNotEmpty()) {
                        CodeBlockView(
                            lang = lang,
                            code = codeContent,
                            onCopy = {
                                clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(codeContent))
                                Toast.makeText(context, "Kode berhasil disalin ke clipboard!", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }

        if (isTyping) {
            Text(
                text = "Mengetik... • Ketuk untuk melewati",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        if (!isUser && !isTyping && hasCitations && citationLinks.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                thickness = 1.dp
            )
            Spacer(modifier = Modifier.height(10.dp))
            
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Language,
                    contentDescription = "Pencarian Web",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(15.dp)
                )
                Text(
                    text = "Menghubungkan ke Google Search...",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 0.3.sp
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Timeline-styled layout
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(androidx.compose.foundation.layout.IntrinsicSize.Min)
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Vertical stem line on the left side
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(2.dp)
                        .background(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(1.dp)
                        )
                )
                
                // Vertical Column of Sources
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current
                    citationLinks.forEach { (title, url) ->
                        val domain = try {
                            val uri = java.net.URI(url)
                            val host = uri.host ?: ""
                            host.removePrefix("www.")
                        } catch (e: Exception) {
                            "sumber"
                        }
                        
                        val domainShort = if (domain.length > 15) {
                            domain.take(12) + "..."
                        } else {
                            domain
                        }
                        
                        val monogram = try {
                            val parts = domain.split(".")
                            val mainPart = parts.firstOrNull() ?: "web"
                            if (mainPart.length > 3) mainPart.take(3) else mainPart
                        } catch (e: Exception) {
                            "web"
                        }

                        // Interactive custom source row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.5f))
                                .clickable {
                                    try {
                                        uriHandler.openUri(url)
                                    } catch (e: Exception) {
                                        // Ignore
                                    }
                                }
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Rounded monogram badge representing favicon template
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f),
                                        shape = RoundedCornerShape(6.dp)
                                    )
                            ) {
                                Text(
                                    text = monogram.lowercase(),
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    maxLines = 1,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            
                            // Host domain
                            Text(
                                text = domainShort,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.widthIn(max = 90.dp)
                            )
                            
                            // Web page title
                            Text(
                                text = title,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Normal,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            
                            // External link navigation icon
                            Icon(
                                imageVector = Icons.Default.ArrowOutward,
                                contentDescription = "Buka Link",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                modifier = Modifier.size(10.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CodeBlockView(
    lang: String,
    code: String,
    onCopy: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var isCopied by remember { mutableStateOf(false) }
    val context = LocalContext.current
    
    val isHtml = remember(lang, code) {
        lang.equals("html", ignoreCase = true) || 
        lang.equals("htm", ignoreCase = true) || 
        lang.equals("xml", ignoreCase = true) || 
        code.contains("<!DOCTYPE html", ignoreCase = true) || 
        code.contains("<html", ignoreCase = true)
    }
    
    var showPreviewDialog by remember { mutableStateOf(false) }
    var isDesktopMode by remember { mutableStateOf(false) }
    
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF131314) // Luxurious editor dark gray
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1E1F22)) // Visual section header
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(Color(0xFF2ECC71), CircleShape) // Accent light indicator
                    )
                    Text(
                        text = "Terdeteksi: $lang",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color(0xFFE3E2E6)
                    )
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Preview Icon (for HTML/CSS website)
                    if (isHtml) {
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = "Pratinjau Situs Web",
                            tint = Color(0xFFC4C6D0),
                            modifier = Modifier
                                .size(18.dp)
                                .clickable { 
                                    showPreviewDialog = true
                                }
                        )
                    }

                    // Download icon
                    Icon(
                        imageVector = Icons.Default.ArrowDownward,
                        contentDescription = "Simpan Kode",
                        tint = Color(0xFFC4C6D0),
                        modifier = Modifier
                            .size(18.dp)
                            .clickable { 
                                Toast.makeText(context, "Unduh kode sebagai berkas berhasil!", Toast.LENGTH_SHORT).show()
                            }
                    )
                    
                    // Copy icon
                    Icon(
                        imageVector = if (isCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                        contentDescription = "Salin Kode",
                        tint = if (isCopied) Color(0xFF2ECC71) else Color(0xFFC4C6D0),
                        modifier = Modifier
                            .size(18.dp)
                            .clickable { 
                                onCopy()
                                isCopied = true
                                scope.launch {
                                    delay(2000)
                                    isCopied = false
                                }
                            }
                    )
                }
            }
            
            // Code Content Panel
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Text(
                    text = code,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                    color = Color(0xFFD1D1D6),
                    lineHeight = 18.sp
                )
            }
        }
    }

    // HTML/CSS Website Preview Dialog
    if (showPreviewDialog) {
        Dialog(
            onDismissRequest = { showPreviewDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .fillMaxHeight(0.9f)
                    .clip(RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Header Area
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(horizontal = 20.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Pratinjau Situs Web",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Hasil render HTML & CSS",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Responsive Toggle Button
                            Surface(
                                onClick = { isDesktopMode = !isDesktopMode },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isDesktopMode) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.height(32.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = if (isDesktopMode) "Mode Desktop" else "Mode Seluler",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDesktopMode) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            
                            IconButton(
                                onClick = { showPreviewDialog = false }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Tutup",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                    
                    // WebView Content Area
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .background(Color.White)
                    ) {
                        AndroidView(
                            factory = { ctx ->
                                WebView(ctx).apply {
                                    settings.javaScriptEnabled = true
                                    settings.domStorageEnabled = true
                                    settings.useWideViewPort = true
                                    settings.loadWithOverviewMode = true
                                    settings.userAgentString = if (isDesktopMode) {
                                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
                                    } else {
                                        null
                                    }
                                    webViewClient = WebViewClient()
                                    loadDataWithBaseURL(null, code, "text/html", "UTF-8", null)
                                }
                            },
                            update = { webView ->
                                webView.settings.userAgentString = if (isDesktopMode) {
                                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
                                } else {
                                    null
                                }
                                webView.loadDataWithBaseURL(null, code, "text/html", "UTF-8", null)
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}
