package com.example.data.database.daos

import androidx.room.*
import com.example.data.database.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MoodEntryDao {
    @Query("SELECT * FROM mood_entries ORDER BY timestamp DESC")
    fun getAllMoodEntries(): Flow<List<MoodEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMoodEntry(entry: MoodEntry)

    @Delete
    suspend fun deleteMoodEntry(entry: MoodEntry)
}

@Dao
interface TherapySessionDao {
    @Query("SELECT * FROM therapy_sessions ORDER BY appointmentDate ASC")
    fun getAllSessions(): Flow<List<TherapySession>>

    @Query("SELECT * FROM therapy_sessions WHERE status = :status ORDER BY appointmentDate ASC")
    fun getSessionsByStatus(status: String): Flow<List<TherapySession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: TherapySession)

    @Update
    suspend fun updateSession(session: TherapySession)

    @Query("UPDATE therapy_sessions SET isBooked = :isBooked, status = 'Upcoming' WHERE id = :id")
    suspend fun setBookingStatus(id: Int, isBooked: Boolean)

    @Delete
    suspend fun deleteSession(session: TherapySession)
}

@Dao
interface RecipeDao {
    @Query("SELECT * FROM recipes")
    fun getAllRecipes(): Flow<List<RecipeEntity>>

    @Query("SELECT * FROM recipes WHERE category = :category")
    fun getRecipesByCategory(category: String): Flow<List<RecipeEntity>>

    @Query("SELECT * FROM recipes WHERE isFavorite = 1")
    fun getFavoriteRecipes(): Flow<List<RecipeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: RecipeEntity)

    @Update
    suspend fun updateRecipe(recipe: RecipeEntity)

    @Query("UPDATE recipes SET isFavorite = :isFav WHERE id = :id")
    suspend fun toggleFavorite(id: Int, isFav: Boolean)
}

@Dao
interface LearningArticleDao {
    @Query("SELECT * FROM learning_articles")
    fun getAllArticles(): Flow<List<LearningArticle>>

    @Query("SELECT * FROM learning_articles WHERE category = :category")
    fun getArticlesByCategory(category: String): Flow<List<LearningArticle>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticle(article: LearningArticle)

    @Update
    suspend fun updateArticle(article: LearningArticle)

    @Query("UPDATE learning_articles SET isSaved = :isSaved WHERE id = :id")
    suspend fun toggleSaved(id: Int, isSaved: Boolean)
}

@Dao
interface MindfulnessSessionDao {
    @Query("SELECT * FROM mindfulness_sessions ORDER BY timestamp DESC")
    fun getAllMindfulnessSessions(): Flow<List<MindfulnessSession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMindfulnessSession(session: MindfulnessSession)
}
