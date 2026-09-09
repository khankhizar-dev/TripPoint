package com.android.trippoint

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.android.trippoint.core.designsystem.theme.TripPointTheme
import com.android.trippoint.core.network.NetworkModule
import com.android.trippoint.core.network.TripRemoteDataSource
import com.android.trippoint.core.network.BookingRemoteDataSource
import com.android.trippoint.core.network.BudgetRemoteDataSource
import com.android.trippoint.core.network.ItineraryRemoteDataSource
import com.android.trippoint.core.database.preferences.PreferencesManager
import com.android.trippoint.trip.data.repository.TripRepositoryImpl
import com.android.trippoint.itinerary.data.repository.ItineraryRepositoryImpl
import com.android.trippoint.booking.data.repository.BookingRepositoryImpl
import com.android.trippoint.budget.data.repository.BudgetRepositoryImpl
import com.android.trippoint.checklist.data.repository.ChecklistRepositoryImpl
import com.android.trippoint.core.network.DocumentRemoteDataSource
import com.android.trippoint.documents.data.repository.DocumentRepositoryImpl
import com.android.trippoint.navigation.AppNavGraph

class MainActivity : ComponentActivity() {
    @Suppress("LongMethod")
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        
        enableEdgeToEdge()
        setContent {
            TripPointTheme {
                val navController = rememberNavController()
                val context = androidx.compose.ui.platform.LocalContext.current
                val preferencesManager = remember { PreferencesManager(context) }
                
                val tripRepository = remember {
                    val api = NetworkModule.provideTripPointApi(
                        authTokenProvider = { preferencesManager.getAuthToken() },
                        refreshTokenProvider = { preferencesManager.getRefreshToken() },
                        onTokenRefreshed = { token, refresh ->
                            preferencesManager.setAuthToken(token)
                            preferencesManager.setRefreshToken(refresh)
                        }
                    )
                    TripRepositoryImpl(TripRemoteDataSource(api))
                }
                
                val itineraryRepository = remember {
                    val api = NetworkModule.provideTripPointApi(
                        authTokenProvider = { preferencesManager.getAuthToken() },
                        refreshTokenProvider = { preferencesManager.getRefreshToken() },
                        onTokenRefreshed = { token, refresh ->
                            preferencesManager.setAuthToken(token)
                            preferencesManager.setRefreshToken(refresh)
                        }
                    )
                    ItineraryRepositoryImpl(ItineraryRemoteDataSource(api))
                }

                val bookingRepository = remember {
                    val api = NetworkModule.provideTripPointApi(
                        authTokenProvider = { preferencesManager.getAuthToken() },
                        refreshTokenProvider = { preferencesManager.getRefreshToken() },
                        onTokenRefreshed = { token, refresh ->
                            preferencesManager.setAuthToken(token)
                            preferencesManager.setRefreshToken(refresh)
                        }
                    )
                    BookingRepositoryImpl(
                        BookingRemoteDataSource(api),
                        TripRemoteDataSource(api)
                    )
                }

                val budgetRepository = remember {
                    val api = NetworkModule.provideTripPointApi(
                        authTokenProvider = { preferencesManager.getAuthToken() },
                        refreshTokenProvider = { preferencesManager.getRefreshToken() },
                        onTokenRefreshed = { token, refresh ->
                            preferencesManager.setAuthToken(token)
                            preferencesManager.setRefreshToken(refresh)
                        }
                    )
                    BudgetRepositoryImpl(
                        BudgetRemoteDataSource(api),
                        TripRemoteDataSource(api),
                        preferencesManager
                    )
                }

                val budgetStatsProvider = remember {
                    com.android.trippoint.budget.data.provider.BudgetStatsProviderImpl(budgetRepository)
                }
                
                val itineraryStatsProvider = remember {
                    com.android.trippoint.itinerary.data.provider.ItineraryStatsProviderImpl(itineraryRepository)
                }
                
                val getTripOverviewUseCase = remember {
                    com.android.trippoint.trip.domain.usecase.GetTripOverviewUseCase(
                        tripRepository,
                        budgetStatsProvider,
                        itineraryStatsProvider
                    )
                }

                val documentRepository = remember {
                    val api = NetworkModule.provideTripPointApi(
                        authTokenProvider = { preferencesManager.getAuthToken() },
                        refreshTokenProvider = { preferencesManager.getRefreshToken() },
                        onTokenRefreshed = { token, refresh ->
                            preferencesManager.setAuthToken(token)
                            preferencesManager.setRefreshToken(refresh)
                        }
                    )
                    DocumentRepositoryImpl(DocumentRemoteDataSource(api))
                }

                val checklistRepository = remember {
                    val api = NetworkModule.provideTripPointApi(
                        authTokenProvider = { preferencesManager.getAuthToken() },
                        refreshTokenProvider = { preferencesManager.getRefreshToken() },
                        onTokenRefreshed = { token, refresh ->
                            preferencesManager.setAuthToken(token)
                            preferencesManager.setRefreshToken(refresh)
                        }
                    )
                    ChecklistRepositoryImpl(com.android.trippoint.core.network.ChecklistRemoteDataSource(api))
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavGraph(
                        navController = navController,
                        tripRepository = tripRepository,
                        itineraryRepository = itineraryRepository,
                        bookingRepository = bookingRepository,
                        budgetRepository = budgetRepository,
                        documentRepository = documentRepository,
                        checklistRepository = checklistRepository,
                        getTripOverviewUseCase = getTripOverviewUseCase,
                        preferencesManager = preferencesManager,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
