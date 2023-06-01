package com.example.eatit

import android.app.Application
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LunchDining
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.eatit.data.RestaurantsDatabase
import com.example.eatit.ui.*
import com.example.eatit.viewModel.RestaurantsViewModel
import com.example.eatit.viewModel.SettingsViewModel
import com.example.eatit.viewModel.WarningViewModel
import dagger.hilt.android.HiltAndroidApp

sealed class AppScreen(val name: String) {
    object Home : AppScreen("EatIt")
    object AddRestaurant : AppScreen("Add Restaurant")
    object AddProduct : AppScreen("Add Product")
    object Details : AppScreen("Details Screen")
    object Settings : AppScreen("Settings Screen")
    object UserProfile : AppScreen("User Profile Screen")
}


@HiltAndroidApp
class EatItApp : Application() {
    val database by lazy { RestaurantsDatabase.getDatabase(this) }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarFunction(
    currentScreen: String,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    onSettingsButtonClicked: () -> Unit,
    onUserProfileButtonClicked: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Row(){
                Icon(Icons.Filled.LunchDining, contentDescription = stringResource(id = R.string.back))
                Text(
                    text = currentScreen,
                    fontWeight = FontWeight.Medium,
                )
            }
        },
        modifier = modifier,
        navigationIcon = {
            //se si può navigare indietro (non home screen) allora appare la freccetta
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back button"
                    )
                }
            }
        },
        actions = {
            if (currentScreen != AppScreen.UserProfile.name) {
                IconButton(onClick = onUserProfileButtonClicked) {
                    Icon(
                        Icons.Filled.AccountCircle,
                        contentDescription = stringResource(id = R.string.settings)
                    )
                }
            }
            if (currentScreen != AppScreen.Settings.name) {
                IconButton(onClick = onSettingsButtonClicked) {
                    Icon(
                        Icons.Filled.Settings,
                        contentDescription = stringResource(id = R.string.settings)
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationApp(
    warningViewModel: WarningViewModel,
    startLocationUpdates: () -> Unit,
    navController: NavHostController = rememberNavController()
) {

    // Get current back stack entry
    val backStackEntry by navController.currentBackStackEntryAsState()
    // Get the name of the current screen
    val currentScreen = backStackEntry?.destination?.route ?: AppScreen.Home.name

    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBarFunction(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() },
                onSettingsButtonClicked = { navController.navigate(AppScreen.Settings.name) },
                onUserProfileButtonClicked = { navController.navigate(AppScreen.UserProfile.name) }
            )
        }
    ) { innerPadding ->
        NavigationGraph(navController, innerPadding, startLocationUpdates)
        val context = LocalContext.current
        if (warningViewModel.showPermissionSnackBar.value) {
            PermissionSnackBarComposable(snackbarHostState, context, warningViewModel)
        }
        if (warningViewModel.showGPSAlertDialog.value) {
            GPSAlertDialogComposable(context, warningViewModel)
        }
        if (warningViewModel.showConnectivitySnackBar.value) {
            ConnectivitySnackBarComposable(
                snackbarHostState,
                context,
                warningViewModel
            )
        }
    }
}

@Composable
private fun NavigationGraph(
    navController: NavHostController,
    innerPadding: PaddingValues,
    startLocationUpdates: () -> Unit,
    modifier: Modifier = Modifier
) {
    val restaurantsViewModel = hiltViewModel<RestaurantsViewModel>()
    NavHost(
        navController = navController,
        startDestination = AppScreen.Home.name,
        modifier = modifier.padding(innerPadding)
    ) {
        composable(route = AppScreen.Home.name) {
            /*HomeScreen(
                onAddButtonClicked = {
                    navController.navigate(AppScreen.AddRestaurant.name)
                },
                onItemClicked = {
                    navController.navigate(AppScreen.Details.name)
                },
                placesViewModel = placesViewModel,
                restaurantsViewModel = restaurantsViewModel
            )*/
            //TODO: Refactor
            //RegisterScreen(restaurantsViewModel = restaurantsViewModel, startLocationUpdates = startLocationUpdates)
            //LoginScreen()
            RestaurantMenuScreen()
        }
        composable(route = AppScreen.AddRestaurant.name) {
            AddRestaurantScreen(
                onNextButtonClicked = {
                    navController.popBackStack(AppScreen.Home.name, inclusive = false)
                },
                restaurantsViewModel = restaurantsViewModel,
            startLocationUpdates
            )
        }
        composable(route = AppScreen.AddProduct.name) {
            AddProductScreen(onNextButtonClicked = {
                navController.popBackStack(AppScreen.Home.name, inclusive = false)
            },
                restaurantsViewModel = restaurantsViewModel)
        }
        composable(route = AppScreen.Details.name) {
            DetailsRestaurantScreen(restaurantsViewModel = restaurantsViewModel,onAddButtonClicked = {
                navController.navigate(AppScreen.AddProduct.name)
            } )
        }
        composable(route = AppScreen.Settings.name) {
            val settingsViewModel = hiltViewModel<SettingsViewModel>()
            SettingsScreen(settingsViewModel)
        }
        composable(route = AppScreen.UserProfile.name) {
            UserProfileScreen()
        }
    }
}