package com.example

import android.content.Context
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Afterma", appName)
  }

  @Test
  fun `launch main activity`() {
    ActivityScenario.launch(MainActivity::class.java).use { scenario ->
      assertNotNull(scenario)
    }
  }

  @Test
  fun `test database creation and insertion`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val database = com.example.data.database.AppDatabase.getDatabase(context, kotlinx.coroutines.GlobalScope)
    val dao = database.moodEntryDao()
    kotlinx.coroutines.runBlocking {
      dao.insertMoodEntry(
        com.example.data.database.entities.MoodEntry(
          moodScore = 5,
          energyLevel = 5,
          recoveryProgress = 50,
          emotionalNote = "Test note",
          symptoms = ""
        )
      )
      // Read back to confirm
      val list = dao.getAllMoodEntries()
      assertNotNull(list)
    }
  }
}
