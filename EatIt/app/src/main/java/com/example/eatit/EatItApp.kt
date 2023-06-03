package com.example.eatit

import android.app.Application
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LunchDining
import androidx.compose.material.icons.filled.Map
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
import com.example.eatit.ui.*
import com.example.eatit.viewModel.RestaurantsViewModel
import com.example.eatit.viewModel.SettingsViewModel
import com.example.eatit.viewModel.WarningViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.HiltAndroidApp

sealed class AppScreen(val name: String) {
    object Home : AppScreen("EatIt")
    object AddRestaurant : AppScreen("Add Restaurant")
    object AddProduct : AppScreen("Add Product")
    object Details : AppScreen("Details Screen")
    object Settings : AppScreen("Settings Screen")
    object UserProfile : AppScreen("User Profile Screen")
    object Map : AppScreen("Map Screen")
    object Login : AppScreen("Login Screen")
    object Register : AppScreen("Register Screen")
}


@HiltAndroidApp
class EatItApp : Application()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarFunction(
    currentScreen: String,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    onSettingsButtonClicked: () -> Unit,
    onUserProfileButtonClicked: () -> Unit,
    onMapButtonClicked: () -> Unit,
) {
    CenterAlignedTopAppBar(
        title = {
            Row {
                Icon(
                    Icons.Filled.LunchDining,
                    contentDescription = stringResource(id = R.string.back)
                )
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
            if (currentScreen == AppScreen.UserProfile.name) {
                IconButton(onClick = onSettingsButtonClicked) {
                    Icon(
                        Icons.Filled.Settings,
                        contentDescription = stringResource(id = R.string.settings)
                    )
                }
            }
            if (currentScreen == AppScreen.Home.name) {
                IconButton(onClick = onUserProfileButtonClicked) {
                    Icon(
                        Icons.Filled.AccountCircle,
                        contentDescription = stringResource(id = R.string.settings)
                    )
                }
            }
            if (currentScreen != AppScreen.Map.name) {
                IconButton(onClick = onMapButtonClicked) {
                    Icon(
                        Icons.Filled.Map,
                        contentDescription = stringResource(id = R.string.settings),
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
    navController: NavHostController = rememberNavController(),
    singIn: (String, String) -> Unit,
    createAccount: (String, String) -> Unit,
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
                onUserProfileButtonClicked = { navController.navigate(AppScreen.UserProfile.name) },
                onMapButtonClicked = { navController.navigate(AppScreen.Map.name) }
            )
        }
    ) { innerPadding ->
        NavigationGraph(
            navController,
            innerPadding,
            startLocationUpdates,
            Modifier,
            singIn,
            createAccount
        )
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
    modifier: Modifier = Modifier,
    singIn: (String, String) -> Unit,
    createAccount: (String, String) -> Unit
) {
    val restaurantsViewModel = hiltViewModel<RestaurantsViewModel>()
    NavHost(
        navController = navController,
        startDestination = AppScreen.Home.name,
        modifier = modifier.padding(innerPadding)
    ) {
        composable(route = AppScreen.Home.name) {
            if (Firebase.auth.currentUser == null) {
                LoginScreen(modifier, singIn, onItemClicked = {
                    navController.navigate(AppScreen.Register.name)
                }, createAccount, onAddButtonClicked = {
                    navController.navigate(AppScreen.Home.name)
                })
            } else {
                HomeScreen(
                    onAddButtonClicked = {
                        navController.navigate(AppScreen.AddRestaurant.name)
                    },
                    onItemClicked = {
                        navController.navigate(AppScreen.Details.name)
                    },
                    restaurantsViewModel = restaurantsViewModel
                )
            }
            //TODO: Refactor
            //RegisterScreen(restaurantsViewModel = restaurantsViewModel, startLocationUpdates = startLocationUpdates)
            //LoginScreen()
            //RestaurantMenuScreen()
            //MapScreen(startLocationUpdates = startLocationUpdates)
            //UserOrderingMenuScreen()
            //OrderSummaryScreen()

        }
        composable(route = AppScreen.AddRestaurant.name) {
            AddRestaurantScreen(
                onNextButtonClicked = {
                    navController.popBackStack(AppScreen.Home.name, inclusive = false)
                },
                restaurantsViewModel = restaurantsViewModel,
                startLocationUpdates = startLocationUpdates
            )
        }
        composable(route = AppScreen.AddProduct.name) {
            AddProductScreen(
                onNextButtonClicked = {
                    navController.popBackStack(AppScreen.Home.name, inclusive = false)
                },
                restaurantsViewModel = restaurantsViewModel
            )
        }
        composable(route = AppScreen.Details.name) {
            DetailsRestaurantScreen(
                restaurantsViewModel = restaurantsViewModel,
                onAddButtonClicked = {
                    navController.navigate(AppScreen.AddProduct.name)
                })
        }
        composable(route = AppScreen.Settings.name) {
            val settingsViewModel = hiltViewModel<SettingsViewModel>()
            SettingsScreen(settingsViewModel)
        }
        composable(route = AppScreen.Map.name) {
            MapScreen(
                startLocationUpdates = startLocationUpdates,
                restaurantsViewModel = restaurantsViewModel
            )
        }
        composable(route = AppScreen.UserProfile.name) {
            UserProfileScreen()
        }
        composable(route = AppScreen.Register.name) {
            RegisterScreen(modifier, startLocationUpdates, createAccount) {
                navController.navigate(AppScreen.Home.name)
            }
        }
        composable(route = AppScreen.Login.name) {
            LoginScreen(modifier, singIn, onItemClicked = {
                navController.navigate(AppScreen.Register.name)
            }, createAccount, onAddButtonClicked = {
                navController.navigate(AppScreen.Home.name)
            })
        }
    }
}