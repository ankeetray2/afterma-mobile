package com.example.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "mood_entries")
data class MoodEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val moodScore: Int, // 1 to 5 (Awful, Hard, Okay, Good, Radiant)
    val energyLevel: Int, // 1 to 5
    val recoveryProgress: Int, // 1 to 100
    val emotionalNote: String,
    val symptoms: String, // Comma separated symptoms: "Fatigue, Soreness, Anxiety"
    val timestamp: Long = System.currentTimeMillis()
) : Serializable

@Entity(tableName = "therapy_sessions")
data class TherapySession(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val providerName: String,
    val providerTitle: String,
    val specialty: String,
    val avatarUrl: String,
    val appointmentDate: String,
    val appointmentTime: String,
    val sessionNotes: String,
    val status: String, // "Upcoming", "Completed", "Canceled"
    val isBooked: Boolean = false,
    val durationMinutes: Int = 50
) : Serializable

@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val category: String, // "Postpartum Recovery", "Lactation Support", "Anti-inflammatory", "Sleep & Calm"
    val prepTime: String,
    val calories: String,
    val benefits: String,
    val ingredients: String, // Comma-separated or bullet list
    val instructions: String,
    val isFavorite: Boolean = false,
    val imageResName: String = "ic_launcher_foreground"
) : Serializable

@Entity(tableName = "learning_articles")
data class LearningArticle(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val category: String, // "Physical Recovery", "Mental Wellness", "Pediatric Guide", "Safe Motherhood"
    val author: String,
    val readTime: String,
    val snippet: String,
    val content: String,
    val isSaved: Boolean = false,
    val imageResName: String = "ic_launcher_foreground"
) : Serializable

@Entity(tableName = "mindfulness_sessions")
data class MindfulnessSession(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String, // "Mindful Breathing", "Guided Therapy Reflection", "Anxiety Calm"
    val durationSeconds: Int,
    val feedback: String, // how the user felt after
    val timestamp: Long = System.currentTimeMillis()
) : Serializable
