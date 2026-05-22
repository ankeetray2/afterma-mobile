package com.example.data.repository

import com.example.data.database.daos.*
import com.example.data.database.entities.*
import kotlinx.coroutines.flow.Flow

class AftermaRepository(
    private val moodDao: MoodEntryDao,
    private val sessionDao: TherapySessionDao,
    private val recipeDao: RecipeDao,
    private val articleDao: LearningArticleDao,
    private val mindfulnessDao: MindfulnessSessionDao
) {
    // Mood operations
    val allMoodEntries: Flow<List<MoodEntry>> = moodDao.getAllMoodEntries()
    suspend fun insertMoodEntry(entry: MoodEntry) = moodDao.insertMoodEntry(entry)
    suspend fun deleteMoodEntry(entry: MoodEntry) = moodDao.deleteMoodEntry(entry)

    // Session / Care Connect operations
    val allSessions: Flow<List<TherapySession>> = sessionDao.getAllSessions()
    fun getSessionsByStatus(status: String): Flow<List<TherapySession>> = sessionDao.getSessionsByStatus(status)
    suspend fun insertSession(session: TherapySession) = sessionDao.insertSession(session)
    suspend fun updateSession(session: TherapySession) = sessionDao.updateSession(session)
    suspend fun setBookingStatus(id: Int, isBooked: Boolean) = sessionDao.setBookingStatus(id, isBooked)

    // Recipe operations
    val allRecipes: Flow<List<RecipeEntity>> = recipeDao.getAllRecipes()
    val favoriteRecipes: Flow<List<RecipeEntity>> = recipeDao.getFavoriteRecipes()
    fun getRecipesByCategory(category: String): Flow<List<RecipeEntity>> = recipeDao.getRecipesByCategory(category)
    suspend fun insertRecipe(recipe: RecipeEntity) = recipeDao.insertRecipe(recipe)
    suspend fun toggleFavoriteRecipe(id: Int, isFav: Boolean) = recipeDao.toggleFavorite(id, isFav)

    // Learning Article operations
    val allArticles: Flow<List<LearningArticle>> = articleDao.getAllArticles()
    fun getArticlesByCategory(category: String): Flow<List<LearningArticle>> = articleDao.getArticlesByCategory(category)
    suspend fun insertArticle(article: LearningArticle) = articleDao.insertArticle(article)
    suspend fun toggleSavedArticle(id: Int, isSaved: Boolean) = articleDao.toggleSaved(id, isSaved)

    // Mindfulness operations
    val allMindfulnessSessions: Flow<List<MindfulnessSession>> = mindfulnessDao.getAllMindfulnessSessions()
    suspend fun insertMindfulnessSession(session: MindfulnessSession) = mindfulnessDao.insertMindfulnessSession(session)
}
