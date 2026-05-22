package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.database.entities.*
import com.example.data.repository.AftermaRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application, viewModelScope)
    private val repository = AftermaRepository(
        database.moodEntryDao(),
        database.therapySessionDao(),
        database.recipeDao(),
        database.learningArticleDao(),
        database.mindfulnessSessionDao()
    )

    // Auth State (Phase 3)
    private val _userLoggedIn = MutableStateFlow(false)
    val userLoggedIn: StateFlow<Boolean> = _userLoggedIn.asStateFlow()

    private val _onboardingCompleted = MutableStateFlow(false)
    val onboardingCompleted: StateFlow<Boolean> = _onboardingCompleted.asStateFlow()

    private val _currentUserEmail = MutableStateFlow("")
    val currentUserEmail: StateFlow<String> = _currentUserEmail.asStateFlow()

    private val _currentUserName = MutableStateFlow("Mama")
    val currentUserName: StateFlow<String> = _currentUserName.asStateFlow()

    // Community Feed State (Phase 10)
    private val _communityPosts = MutableStateFlow<List<CommunityPost>>(emptyList())
    val communityPosts: StateFlow<List<CommunityPost>> = _communityPosts.asStateFlow()

    // Phase 7: Cycle Tracker & Menstrual Wellness State
    private val _cycleEntries = MutableStateFlow<List<CycleEntry>>(listOf(
        CycleEntry(
            date = "19-May-2026",
            mood = "Balanced",
            flow = "Spotting",
            sleep = "7-9 HRS",
            ovulation = false,
            medication = true,
            painIntensity = 2,
            energyVitality = 3,
            crampsSeverity = 1,
            waterIntake = "2L",
            symptoms = listOf("Nausea", "Fatigue"),
            notes = "Mild postpartum spotting starting today. Drinking warm infusion water."
        ),
        CycleEntry(
            date = "20-May-2026",
            mood = "Low",
            flow = "Light",
            sleep = "5-7 HRS",
            ovulation = false,
            medication = true,
            painIntensity = 4,
            energyVitality = 2,
            crampsSeverity = 3,
            waterIntake = "1.5L",
            symptoms = listOf("Fatigue", "Cramps", "Headache"),
            notes = "Slight cramping around pelvic floor. Drank warm chamomile tea."
        ),
        CycleEntry(
            date = "21-May-2026",
            mood = "Good",
            flow = "Medium",
            sleep = "7-9 HRS",
            ovulation = false,
            medication = false,
            painIntensity = 3,
            energyVitality = 4,
            crampsSeverity = 2,
            waterIntake = "2L",
            symptoms = listOf("Bloating", "Tender Breasts"),
            notes = "Flow increased. Feeling generally stable and rested."
        )
    ))
    val cycleEntries: StateFlow<List<CycleEntry>> = _cycleEntries.asStateFlow()

    // Phase 11: Lactation Log Tracking State
    private val _lactationLogs = MutableStateFlow<List<LactationLog>>(listOf(
        LactationLog(
            id = 1,
            timestamp = "22-May-2026, 09:20 am",
            type = "Breast",
            side = "Left",
            quantityMl = 80,
            durationMin = 15,
            response = "Happy"
        ),
        LactationLog(
            id = 2,
            timestamp = "21-May-2026, 06:12 pm",
            type = "Breast",
            side = "Right",
            quantityMl = 60,
            durationMin = 12,
            response = "Fussy"
        ),
        LactationLog(
            id = 3,
            timestamp = "20-May-2026, 02:40 pm",
            type = "Pump",
            side = "Both",
            quantityMl = 140,
            durationMin = 25,
            response = "Sleepy"
        ),
        LactationLog(
            id = 4,
            timestamp = "19-May-2026, 08:15 am",
            type = "Breast",
            side = "Left",
            quantityMl = 90,
            durationMin = 20,
            response = "Refused"
        )
    ))
    val lactationLogs: StateFlow<List<LactationLog>> = _lactationLogs.asStateFlow()

    // Room Database State Streams
    val moodEntries: StateFlow<List<MoodEntry>> = repository.allMoodEntries
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val therapySessions: StateFlow<List<TherapySession>> = repository.allSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recipes: StateFlow<List<RecipeEntity>> = repository.allRecipes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteRecipes: StateFlow<List<RecipeEntity>> = repository.favoriteRecipes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val articles: StateFlow<List<LearningArticle>> = repository.allArticles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val mindfulnessHistory: StateFlow<List<MindfulnessSession>> = repository.allMindfulnessSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI Derived metrics (e.g. Postpartum streak or progress factor)
    val healingProgressIndex: StateFlow<Int> = moodEntries
        .map { entries ->
            if (entries.isEmpty()) 42 // Default positive baseline progress
            else {
                // Calculate based on average recovery progress in recent logs
                val sum = entries.sumOf { it.recoveryProgress }
                (sum / entries.size).coerceIn(10, 100)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 42)

    init {
        // Initialize with default comforting mother community dialogue
        _communityPosts.value = listOf(
            CommunityPost(
                id = 1,
                authorName = "Elena V.",
                weeksPostpartum = "3 Weeks Postpartum",
                content = "Does anyone else find themselves holding their breath at 3 AM? Spent 10 minutes doing the Afterma breathing exercise tonight, and it honestly slowed my heart rate right down. You are all doing so well.",
                likesCount = 28,
                commentsCount = 7,
                tag = "Inspiration"
            ),
            CommunityPost(
                id = 2,
                authorName = "Jordan K.",
                weeksPostpartum = "6 Weeks Postpartum",
                content = "Tried the Golden Turmeric Milk recipe tonight. It of course calmed my gut, but my little one actually nested and slept for a contiguous 4-hour stretch! Celebrating this win.",
                likesCount = 42,
                commentsCount = 12,
                tag = "Recipes & Feeding"
            ),
            CommunityPost(
                id = 3,
                authorName = "Maya S.",
                weeksPostpartum = "10 Weeks Postpartum",
                content = "Just had my initial postpartum check up. My pelvic floor feels so neglected but Dr. Clara and the articles on Afterma guided me to not panic. Feeling emotionally restored today.",
                likesCount = 14,
                commentsCount = 3,
                tag = "Physical Healing"
            )
        )
    }

    // --- Actions ---

    // Phase 3 Authentication Flow
    fun completeOnboarding() {
        _onboardingCompleted.value = true
    }

    fun loginUser(email: String, name: String) {
        _currentUserEmail.value = email
        _currentUserName.value = if (name.isNotBlank()) name else "Mama"
        _userLoggedIn.value = true
    }

    fun registerUser(email: String, name: String) {
        loginUser(email, name)
    }

    fun logout() {
        _userLoggedIn.value = false
    }

    // Phase 5: Care Journey - Add a daily log entry
    fun logDailyStatus(
        moodScore: Int,
        energyLevel: Int,
        recoveryProgress: Int,
        note: String,
        symptoms: List<String>
    ) {
        viewModelScope.launch {
            val symptomString = symptoms.joinToString(", ")
            val entry = MoodEntry(
                moodScore = moodScore,
                energyLevel = energyLevel,
                recoveryProgress = recoveryProgress,
                emotionalNote = note,
                symptoms = symptomString
            )
            repository.insertMoodEntry(entry)
        }
    }

    // Phase 6: Mental Wellness - Complete a mindfulness session
    fun logMindfulnessSession(type: String, durationSeconds: Int, feedback: String) {
        viewModelScope.launch {
            val session = MindfulnessSession(
                type = type,
                durationSeconds = durationSeconds,
                feedback = feedback
            )
            repository.insertMindfulnessSession(session)
        }
    }

    // Phase 7: Safe Recipes - Toggle Saved favorite
    fun toggleRecipeFavorite(recipeId: Int, isFav: Boolean) {
        viewModelScope.launch {
            repository.toggleFavoriteRecipe(recipeId, isFav)
        }
    }

    // Phase 8: Learning Center - Toggle saved bookmark
    fun toggleArticleSaved(articleId: Int, isSaved: Boolean) {
        viewModelScope.launch {
            repository.toggleSavedArticle(articleId, isSaved)
        }
    }

    // Phase 9: Care Connect - Book a therapist appointment
    fun bookAppointment(providerId: Int) {
        viewModelScope.launch {
            repository.setBookingStatus(providerId, true)
        }
    }

    fun cancelAppointment(providerId: Int) {
        viewModelScope.launch {
            repository.setBookingStatus(providerId, false)
        }
    }

    // Phase 10: Community Feed - Write support post
    fun shareCommunityPost(content: String, tag: String) {
        val newPost = CommunityPost(
            id = _communityPosts.value.size + 1,
            authorName = "${_currentUserName.value} (You)",
            weeksPostpartum = "Just Now",
            content = content,
            likesCount = 0,
            commentsCount = 0,
            tag = tag
        )
        _communityPosts.value = listOf(newPost) + _communityPosts.value
    }

    fun likeCommunityPost(postId: Int) {
        _communityPosts.value = _communityPosts.value.map {
            if (it.id == postId) it.copy(likesCount = it.likesCount + 1) else it
        }
    }

    fun logCycleEntry(entry: CycleEntry) {
        _cycleEntries.value = _cycleEntries.value.filter { it.date != entry.date } + entry
    }

    fun logLactationLog(entry: LactationLog) {
        val nextId = (_lactationLogs.value.maxOfOrNull { it.id } ?: 0) + 1
        val newEntry = entry.copy(id = nextId)
        _lactationLogs.value = listOf(newEntry) + _lactationLogs.value
    }
}

data class CycleEntry(
    val date: String,
    val mood: String,
    val flow: String,
    val sleep: String,
    val ovulation: Boolean,
    val medication: Boolean,
    val painIntensity: Int,
    val energyVitality: Int,
    val crampsSeverity: Int,
    val waterIntake: String,
    val symptoms: List<String>,
    val notes: String
)

data class LactationLog(
    val id: Int = 0,
    val timestamp: String,
    val type: String,
    val side: String,
    val quantityMl: Int,
    val durationMin: Int,
    val response: String
)

// Support Struct for Community feed (stored in memory to allow free posting interaction)
data class CommunityPost(
    val id: Int,
    val authorName: String,
    val weeksPostpartum: String,
    val content: String,
    val likesCount: Int,
    val commentsCount: Int,
    val tag: String
)
