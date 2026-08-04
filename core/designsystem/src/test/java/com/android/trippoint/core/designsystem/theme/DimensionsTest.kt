package com.android.trippoint.core.designsystem.theme

import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Test

class DimensionsTest {

    @Test
    fun `verify default dimensions`() {
        val dimensions = TripPointDimensions()
        assertEquals(4.dp, dimensions.spacingExtraSmall)
        assertEquals(8.dp, dimensions.spacingSmall)
        assertEquals(12.dp, dimensions.spacingMedium)
        assertEquals(16.dp, dimensions.spacingLarge)
        assertEquals(24.dp, dimensions.spacingExtraLarge)
        assertEquals(32.dp, dimensions.spacingXXLarge)
        assertEquals(40.dp, dimensions.spacingXXXLarge)
        assertEquals(48.dp, dimensions.spacingHuge)
        assertEquals(64.dp, dimensions.spacingExHuge)
        assertEquals(80.dp, dimensions.spacingGigantic)
        assertEquals(96.dp, dimensions.spacingEnormous)
        
        assertEquals(96.dp, dimensions.logoSize)
        assertEquals(320.dp, dimensions.illustrationHeight)
        assertEquals(32.dp, dimensions.loaderSize)
        assertEquals(100.dp, dimensions.iconSizeLarge)
    }
}
