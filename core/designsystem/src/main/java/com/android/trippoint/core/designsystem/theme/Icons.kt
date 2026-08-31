package com.android.trippoint.core.designsystem.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.NoteAdd
import androidx.compose.material.icons.automirrored.filled.PlaylistAddCheck
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.automirrored.outlined.NoteAdd
import androidx.compose.material.icons.automirrored.outlined.PlaylistAddCheck
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalActivity
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.FileOpen
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Flight
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Hotel
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocalActivity
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material.icons.outlined.Timeline
import androidx.compose.material.icons.outlined.WorkOutline

/**
 * TripPoint Iconography System (Section 26.7).
 * Provides access to Outlined (Line) and Filled variants.
 */
object TripPointIcons {
    
    /** Line Style Icons (Default) */
    object Outlined {
        val Home = Icons.Outlined.Home
        val Trip = Icons.Outlined.WorkOutline
        val Timeline = Icons.Outlined.Timeline
        val Bookings = Icons.Outlined.FileOpen
        val Budget = Icons.Outlined.Payments
        val Checklist = Icons.AutoMirrored.Outlined.PlaylistAddCheck
        
        val Activity = Icons.Outlined.LocalActivity
        val Search = Icons.Outlined.Search
        val AIAssistant = Icons.Outlined.AutoAwesome
        val Docs = Icons.AutoMirrored.Outlined.Assignment
        val People = Icons.Outlined.Group
        val Notifications = Icons.Outlined.Notifications
        
        val Profile = Icons.Outlined.AccountCircle
        val Settings = Icons.Outlined.Settings
        val Location = Icons.Outlined.LocationOn
        val Flight = Icons.Outlined.Flight
        val Hotel = Icons.Outlined.Hotel
        val Car = Icons.Outlined.DirectionsCar
        
        val Calendar = Icons.Outlined.CalendarToday
        val Share = Icons.Outlined.Share
        val Download = Icons.Outlined.Download
        val Filter = Icons.Outlined.FilterList
        val Edit = Icons.Outlined.Edit
        val More = Icons.Outlined.MoreHoriz
        
        val Star = Icons.Outlined.StarBorder
        val Heart = Icons.Outlined.FavoriteBorder
        val Lock = Icons.Outlined.Lock
        val Bell = Icons.Outlined.Notifications
        val Info = Icons.Outlined.Info
        val Help = Icons.AutoMirrored.Outlined.HelpOutline
        
        val Back = Icons.AutoMirrored.Outlined.ArrowBack
        val Add = Icons.AutoMirrored.Outlined.NoteAdd
    }

    /** Filled Style Icons (Used for active states) */
    object Filled {
        val Home = Icons.Filled.Home
        val Trip = Icons.Filled.Work
        val Timeline = Icons.Filled.Timeline
        val Bookings = Icons.Filled.FileOpen
        val Budget = Icons.Filled.Payments
        val Checklist = Icons.AutoMirrored.Filled.PlaylistAddCheck
        
        val Activity = Icons.Filled.LocalActivity
        val Search = Icons.Filled.Search
        val AIAssistant = Icons.Filled.AutoAwesome
        val Docs = Icons.AutoMirrored.Filled.Assignment
        val People = Icons.Filled.Group
        val Notifications = Icons.Filled.Notifications
        
        val Profile = Icons.Filled.AccountCircle
        val Settings = Icons.Filled.Settings
        val Location = Icons.Filled.LocationOn
        val Flight = Icons.Filled.Flight
        val Hotel = Icons.Filled.Hotel
        val Car = Icons.Filled.DirectionsCar
        
        val Calendar = Icons.Filled.CalendarToday
        val Share = Icons.Filled.Share
        val Download = Icons.Filled.Download
        val Filter = Icons.Filled.FilterList
        val Edit = Icons.Filled.Edit
        val More = Icons.Filled.MoreHoriz
        
        val Star = Icons.Filled.Star
        val Heart = Icons.Filled.Favorite
        val Lock = Icons.Filled.Lock
        val Bell = Icons.Filled.Notifications
        val Info = Icons.Filled.Info
        val Help = Icons.AutoMirrored.Filled.HelpOutline
        
        val Back = Icons.AutoMirrored.Filled.ArrowBack
        val Add = Icons.AutoMirrored.Filled.NoteAdd
    }

    // Default exposure for legacy compatibility
    val Home = Outlined.Home
    val Trip = Outlined.Trip
    val Timeline = Outlined.Timeline
    val Bookings = Outlined.Bookings
    val Budget = Outlined.Budget
    val Checklist = Outlined.Checklist
    val Activity = Outlined.Activity
    val Search = Outlined.Search
    val AIAssistant = Outlined.AIAssistant
    val Docs = Outlined.Docs
    val People = Outlined.People
    val Notifications = Outlined.Notifications
    val Profile = Outlined.Profile
    val Settings = Outlined.Settings
    val Location = Outlined.Location
    val Flight = Outlined.Flight
    val Hotel = Outlined.Hotel
    val Car = Outlined.Car
    val Calendar = Outlined.Calendar
    val Share = Outlined.Share
    val Download = Outlined.Download
    val Filter = Outlined.Filter
    val Edit = Outlined.Edit
    val More = Outlined.More
    val Star = Outlined.Star
    val Heart = Outlined.Heart
    val Lock = Outlined.Lock
    val Bell = Outlined.Bell
    val Info = Outlined.Info
    val Help = Outlined.Help
    val Back = Outlined.Back
    val Add = Outlined.Add
}
