package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.BrandPresets
import com.example.ui.components.parseHexColor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
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
    assertEquals("Brand Bible", appName)
  }

  @Test
  fun `verify brand presets and 5-color palette structure`() {
    val bible = BrandPresets.createFallbackBrandBible()
    assertEquals("AuraTerra", bible.brandName)
    assertEquals(5, bible.palette.size)
    assertNotNull(bible.primaryLogo)
    assertTrue(bible.secondaryMarks.isNotEmpty())
    assertNotNull(bible.typography.headerFont)
    assertNotNull(bible.typography.bodyFont)
  }

  @Test
  fun `verify hex color parser`() {
    val color = parseHexColor("#6366F1")
    assertNotNull(color)
    val color3 = parseHexColor("#FFF")
    assertNotNull(color3)
  }
}
