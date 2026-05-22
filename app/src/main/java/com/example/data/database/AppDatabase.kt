package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.database.daos.*
import com.example.data.database.entities.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        MoodEntry::class,
        TherapySession::class,
        RecipeEntity::class,
        LearningArticle::class,
        MindfulnessSession::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun moodEntryDao(): MoodEntryDao
    abstract fun therapySessionDao(): TherapySessionDao
    abstract fun recipeDao(): RecipeDao
    abstract fun learningArticleDao(): LearningArticleDao
    abstract fun mindfulnessSessionDao(): MindfulnessSessionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "afterma_secure.db"
                )
                .addCallback(DatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database)
                }
            }
        }

        private suspend fun populateInitialData(db: AppDatabase) {
            // 1. Prepopulate Maternal Care Connect Providers (Therapy doctors)
            val sessionDao = db.therapySessionDao()
            val providers = listOf(
                TherapySession(
                    providerName = "Dr. Clara Fontaine, MD",
                    providerTitle = "Obstetrician & Postpartum Recovery Expert",
                    specialty = "Physical Restoration & Hormonal Balance",
                    avatarUrl = "",
                    appointmentDate = "2026-06-02",
                    appointmentTime = "10:00 AM",
                    sessionNotes = "Initial dynamic physical baseline assessment.",
                    status = "Available",
                    isBooked = false,
                    durationMinutes = 45
                ),
                TherapySession(
                    providerName = "Dr. Eliana Sterling, PhD",
                    providerTitle = "Perinatal Psychotherapist",
                    specialty = "Postpartum Depression & Emotional Transition",
                    avatarUrl = "",
                    appointmentDate = "2026-06-04",
                    appointmentTime = "02:30 PM",
                    sessionNotes = "Cognitive-behavioral transition guidance.",
                    status = "Available",
                    isBooked = false,
                    durationMinutes = 50
                ),
                TherapySession(
                    providerName = "Sarah Jenkins, IBCLC",
                    providerTitle = "Certified Lactation & Nutrition Coach",
                    specialty = "Ayurvedic Nutrition & Breastfeeding Guidance",
                    avatarUrl = "",
                    appointmentDate = "2026-06-06",
                    appointmentTime = "11:15 AM",
                    sessionNotes = "Breastfeeding pain analysis & meal balancing.",
                    status = "Available",
                    isBooked = false,
                    durationMinutes = 60
                ),
                TherapySession(
                    providerName = "Amara Kincaid, PsyD",
                    providerTitle = "Maternal Mindfulness & Somatic Coach",
                    specialty = "Vagus Nerve Regulation & Breathwork",
                    avatarUrl = "",
                    appointmentDate = "2026-06-10",
                    appointmentTime = "04:00 PM",
                    sessionNotes = "Deep somatic regulation session.",
                    status = "Available",
                    isBooked = false,
                    durationMinutes = 45
                )
            )
            for (p in providers) {
                sessionDao.insertSession(p)
            }

            // 2. Prepopulate Ayurvedic & Postpartum Healing Recipes
            val recipeDao = db.recipeDao()
            val recipes = listOf(
                RecipeEntity(
                    title = "Golden Turmeric Milk (Haldi Doodh)",
                    category = "Sleep & Calm",
                    prepTime = "10 mins",
                    calories = "180 kcal",
                    benefits = "Antiseptic, warm, improves lactation, calming.",
                    ingredients = "2 cups almond or organic whole milk, 1 tsp ground turmeric, 1/2 tsp ground cinnamon, 1/4 tsp ground ginger, 1 tbsp organic honey, small pinch black pepper.",
                    instructions = "Combine milk, turmeric, cinnamon, ginger, and black pepper in a small saucepan. Bring to a light simmer over medium-low heat. Whisk continuously for 5 minutes. Remove from heat, let cool slightly, stir in honey, and enjoy while cozy.",
                    isFavorite = false
                ),
                RecipeEntity(
                    title = "Postpartum Recovery Oats with Ghee",
                    category = "Postpartum Recovery",
                    prepTime = "15 mins",
                    calories = "340 kcal",
                    benefits = "Restores physical energy, rich underbelly digestive support.",
                    ingredients = "1 cup steel-cut oats, 2.5 cups water or almond milk, 1 tbsp organic grass-fed ghee, 1 tbsp soaked raisins, 1 tsp ground cardamom, pinch Himalayan salt, toasted pumpkin seeds.",
                    instructions = "Simmer oats in water/milk for 12 minutes. Stir in ghee, raisins, cardamom, and salt. Cook for an additional 2 minutes. Serve in a warm ceramic bowl. Garnish with pumpkin seeds.",
                    isFavorite = true
                ),
                RecipeEntity(
                    title = "Lactation Boost Fenugreek Soup",
                    category = "Lactation Support",
                    prepTime = "25 mins",
                    calories = "220 kcal",
                    benefits = "Galactagogue-rich, boosts milk supply, anti-inflammatory.",
                    ingredients = "1/2 cup fenugreek seeds (soaked overnight), 1 cup chopped spinach, 1 diced sweet potato, 3 cups organic vegetable broth, 1 tbsp coconut oil, cumin seeds.",
                    instructions = "Heat coconut oil, toast cumin seeds. Add sweet potato and soaked fenugreek. Sauté for 5 mins. Pour broth, simmer for 15 mins. Add spinach, cook 2 mins. Puree lightly if desired.",
                    isFavorite = false
                ),
                RecipeEntity(
                    title = "Almond & Fennel Lactation Energy Bites",
                    category = "Lactation Support",
                    prepTime = "15 mins",
                    calories = "120 kcal each",
                    benefits = "Quick healthy calorie boost on-the-go for active milk production.",
                    ingredients = "1.5 cups rolled oats, 1/2 cup almond butter, 1/4 cup ground flaxseed, 2 tbsp toasted fennel seeds, 1/3 cup organic maple syrup, 1 tsp vanilla extract, pinch sea salt.",
                    instructions = "Pulse oats lightly in food processor. Stir all ingredients in a spacious bowl until thoroughly bound. Roll into 1-inch spherical bites. Store in the refrigerator for ready grazing.",
                    isFavorite = false
                ),
                RecipeEntity(
                    title = "Warm Ginger-Bone Restoration Broth",
                    category = "Anti-inflammatory",
                    prepTime = "4 hours (brew time)",
                    calories = "90 kcal",
                    benefits = "Replenishes depleted collagen, restores uterine muscles.",
                    ingredients = "1 kg pasture-raised organic bones, 3 liters structural spring water, 2 tbsp grated organic ginger, 3 cloves garlic, 1 cup chopped healing celery, 1 tbsp apple cider vinegar.",
                    instructions = "Combine bones, water, and apple cider vinegar. Let sit 30 mins. Bring to boil, skim impurities. Add ginger, garlic, celery. Simmer on low heat for 4 hours. Season to taste.",
                    isFavorite = false
                )
            )
            for (r in recipes) {
                recipeDao.insertRecipe(r)
            }

            // 3. Prepopulate Maternal Education and Pediatric Healing Articles
            val articleDao = db.learningArticleDao()
            val articles = listOf(
                LearningArticle(
                    title = "The Golden Month: Navigating Your First 40 Days Postpartum",
                    category = "Physical Recovery",
                    author = "Dr. Clara Fontaine, MD",
                    readTime = "6 min read",
                    snippet = "In traditional cultures, the first 40 days is sacred. Learn why physical rest is your master key to biological longevity.",
                    content = "The First 40 Days (often termed the Golden Month) is a critical period where a mother’s endocrine, cardiovascular, and physical systems undergo massive transitions back to baseline. During this phase, maternal cells are uniquely sponge-like and susceptible to healing. Resting fully, avoiding dramatic temperature drops, consuming warmed liquid-based nourishment, and binding the abdominal area help safely contract the womb and preserve maternal vitality. Focus entirely on light-touch bonding and allow your caregivers to cushion you."
                ),
                LearningArticle(
                    title = "Safe Sleeping & Dream Routines for Postpartum Peace",
                    category = "Pediatric Guide",
                    author = "Sarah Jenkins, IBCLC",
                    readTime = "5 min read",
                    snippet = "Shed sleep anxiety. Creating a safe, calming, and biological co-sleeping or bedside sleep hygiene.",
                    content = "Infant circadian rhythms do not mature until week 12. Prior to this, your touch, heartbeat, and body heat act as outer-womb regulators. Setting up safe bedside bassinet hygiene: keeping a clear and firm flat mattress, keeping ambient thermal conditions at 68-72°F, utilizing red light at night, and establishing consistent soothing olfactory signals like lavender will assist both your recovery and baby's biological regulation."
                ),
                LearningArticle(
                    title = "Understanding the 'Baby Blues' vs. PPD",
                    category = "Mental Wellness",
                    author = "Dr. Eliana Sterling, PhD",
                    readTime = "8 min read",
                    snippet = "Hormones fluctuate dramatically inside the first fortnight. Learn the safe markers and therapeutic channels.",
                    content = "Over 80% of mothers experience some touch of the 'Baby Blues' within 3 to 14 days of birth. This is an organic, physiological response to the rapid drop in estrogen and progesterone. If feelings of absolute isolation, numbness toward bonding, intense sleep-inhibiting hypervigilance, or heavy tear cycles extend past 3 weeks, it transitions to clinical Postpartum Depression (PPD). This is not an individual failing; it is a treatable neurological and biological health state. Therapy, support anchors, and non-judgmental spaces are your path forward."
                ),
                LearningArticle(
                    title = "The Vital Importance of Pelvic Floor Re-education",
                    category = "Physical Recovery",
                    author = "Dr. Clara Fontaine, MD",
                    readTime = "5 min read",
                    snippet = "Reconnecting with your deep pelvic alignment safely and slowly without strenuous exercises.",
                    content = "Your pelvic floor muscles have accommodated significant load. Rushing into standard abdominal crunches increases pressure and can cause diastasis recti or prolapse. True recovery begins with structural alignment, lateral rib cage diaphragmatic breathing, and gentle transverse abdominal activations. Treat your core like an injured sports ligament: start slow, be intentional, and let dynamic stability compound naturally."
                )
            )
            for (a in articles) {
                articleDao.insertArticle(a)
            }
        }
    }
}
