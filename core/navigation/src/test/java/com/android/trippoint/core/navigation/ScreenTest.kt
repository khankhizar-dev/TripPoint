package com.android.trippoint.core.navigation

import org.junit.Assert.assertEquals
import org.junit.Test

class ScreenTest {

    @Test
    fun `verify screen routes`() {
        assertEquals("splash", Screen.Splash.route)
        assertEquals("login", Screen.Login.route)
        assertEquals("register", Screen.Register.route)
        assertEquals("home", Screen.Home.route)
    }
}
