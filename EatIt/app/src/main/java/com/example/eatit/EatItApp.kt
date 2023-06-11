package com.example.eatit

import android.app.Application
import android.content.SharedPreferences
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LunchDining
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterVertically
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
import com.example.eatit.ui.components.ConnectivitySnackBarComposable
import com.example.eatit.ui.components.EatItIconButton
import com.example.eatit.ui.components.GPSAlertDialogComposable
import com.example.eatit.ui.components.PermissionSnackBarComposable
import com.example.eatit.viewModel.CartViewModel
import com.example.eatit.viewModel.RestaurantsViewModel
import com.example.eatit.viewModel.UsersViewModel
import com.example.eatit.viewModel.WarningViewModel
import dagger.hilt.android.HiltAndroidApp
import kotlin.reflect.KFunction3
import kotlin.reflect.KFunction9

sealed class AppScreen(var name: String) {
    object Home : AppScreen("Home")
    object Details : AppScreen("Details")
    object Settings : AppScreen("Settings")
    object Profile : AppScreen("Profile")
    object Map : AppScreen("Map")
    object Login : AppScreen("Login")
    object Register : AppScreen("Register")
    object Cart : AppScreen("Cart")
    object Filter : AppScreen("Filter")
}


@HiltAndroidApp
class EatItApp : Application()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarFunction(
    currentScreen: String,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    CenterAlignedTopAppBar(
        title = {
            Row {
                Icon(
                    Icons.Filled.LunchDining,
                    contentDescription = stringResource(id = R.string.back),
                    modifier = Modifier.align(CenterVertically)
                )
                Text(
                    text = "EatIt",
                    fontWeight = FontWeight.Medium,
                )
            }
        },
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack && currentScreen != AppScreen.Home.name
                && currentScreen != AppScreen.Profile.name
                && currentScreen != AppScreen.Map.name
                && currentScreen != AppScreen.Settings.name
                && currentScreen != AppScreen.Login.name
                && currentScreen != AppScreen.Register.name
            ) {
                EatItIconButton(icon = Icons.Filled.ArrowBack, function = navigateUp)
            }
        }
    )
}

@Composable
fun BottomAppBarFunction(
    modifier: Modifier = Modifier,
    currentScreen: String,
    onSettingsButtonClicked: () -> Unit,
    onUserProfileButtonClicked: () -> Unit,
    onMapButtonClicked: () -> Unit,
    onHomeButtonClicked: () -> Unit,
) {

    BottomAppBar(modifier = modifier,
        actions = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                if (currentScreen == AppScreen.Home.name) {
                    EatItIconButton(function = { onHomeButtonClicked() }, icon = Icons.Filled.Home)
                } else {
                    EatItIconButton(
                        function = { onHomeButtonClicked() },
                        icon = Icons.Outlined.Home
                    )
                }
                if (currentScreen == AppScreen.Profile.name) {
                    EatItIconButton(
                        function = { onUserProfileButtonClicked() },
                        icon = Icons.Filled.AccountCircle
                    )
                } else {
                    EatItIconButton(
                        function = { onUserProfileButtonClicked() },
                        icon = Icons.Outlined.AccountCircle
                    )
                }
                if (currentScreen == AppScreen.Map.name) {
                    EatItIconButton(function = { onMapButtonClicked() }, icon = Icons.Filled.Map)
                } else {
                    EatItIconButton(function = { onMapButtonClicked() }, icon = Icons.Outlined.Map)
                }
                if (currentScreen == AppScreen.Settings.name) {
                    EatItIconButton(
                        function = { onSettingsButtonClicked() },
                        icon = Icons.Filled.Settings
                    )
                } else {
                    EatItIconButton(
                        function = { onSettingsButtonClicked() },
                        icon = Icons.Outlined.Settings
                    )
                }
            }
        }
    )
}

@Composable
fun NavigationApp(
    warningViewModel: WarningViewModel,
    navController: NavHostController = rememberNavController(),
    signIn: KFunction3<String, String, () -> Unit, Unit>,
    createAccount: KFunction9<String, String, String, String, String, Boolean, String, String, () -> Unit, Unit>,
    startLocationUpdates: () -> Unit,
    theme: String?,
    sharedPref: SharedPreferences,

    onOptionSelected: (String?) -> Unit
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = backStackEntry?.destination?.route ?: AppScreen.Home.name
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBarFunction(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() }
            )
        },
        bottomBar = {
            if (currentScreen != AppScreen.Register.name && currentScreen != AppScreen.Login.name) {
                BottomAppBarFunction(
                    onSettingsButtonClicked = { navController.navigate(AppScreen.Settings.name) },
                    currentScreen = currentScreen,
                    onUserProfileButtonClicked = { navController.navigate(AppScreen.Profile.name) },
                    onMapButtonClicked = { navController.navigate(AppScreen.Map.name) },
                    onHomeButtonClicked = { navController.navigate(AppScreen.Home.name) },
                )
            }
        }
    ) { innerPadding ->
        NavigationGraph(
            navController,
            innerPadding,
            Modifier,
            signIn,
            createAccount,
            startLocationUpdates,
            sharedPref,
            theme,
            onOptionSelected
        )
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun NavigationGraph(
    navController: NavHostController,
    innerPadding: PaddingValues,
    modifier: Modifier = Modifier,
    signIn: KFunction3<String, String, () -> Unit, Unit>,
    createAccount: KFunction9<String, String, String, String, String, Boolean, String, String, () -> Unit, Unit>,
    startLocationUpdates: () -> Unit,
    sharedPref: SharedPreferences,
    theme: String?,
    onThemeChanged: (String?) -> Unit
) {
    val restaurantsViewModel = hiltViewModel<RestaurantsViewModel>()
    val usersViewModel = hiltViewModel<UsersViewModel>()
    val cartViewModel = hiltViewModel<CartViewModel>()
    NavHost(
        navController = navController,
        startDestination = AppScreen.Home.name,
        modifier = modifier.padding(innerPadding)
    ) {
        composable(route = AppScreen.Home.name) {
            HomeScreen(
                onItemClicked = {
                    navController.navigate(AppScreen.Details.name)
                },
                restaurantsViewModel = restaurantsViewModel,
                onLoginClicked = {
                    navController.navigate(AppScreen.Login.name)
                },
                usersViewModel = usersViewModel,
                cartViewModel = cartViewModel,
                onFilterClicked = {
                    navController.navigate(AppScreen.Filter.name)
                },
                startLocationUpdates = startLocationUpdates
            )
        }
        composable(route = AppScreen.Details.name) {
            DetailsRestaurantScreen(
                restaurantsViewModel = restaurantsViewModel,
                /*onAddButtonClicked = {
                    navController.navigate(AppScreen.AddProduct.name)
                },*/
                cartViewModel = cartViewModel,
                usersViewModel = usersViewModel,
                onNextButtonClicked = {
                    navController.navigate(AppScreen.Cart.name)
                }
            )
        }
        composable(route = AppScreen.Settings.name) {
            SettingsScreen(
                onNextButtonClicked = {
                    navController.navigate(AppScreen.Login.name)
                }, usersViewModel = usersViewModel,
                startLocationUpdates = startLocationUpdates,
                sharedPref = sharedPref,
                theme = theme,
                onThemeChanged = onThemeChanged
            )
        }
        composable(route = AppScreen.Map.name) {
            MapScreen(
                restaurantsViewModel = restaurantsViewModel,
                usersViewModel = usersViewModel,
            )
        }
        composable(route = AppScreen.Profile.name) {
            ProfileScreen(
                usersViewModel = usersViewModel,
                restaurantsViewModel = restaurantsViewModel,
                cartViewModel = cartViewModel
            )
        }
        composable(route = AppScreen.Register.name) {
            RegisterScreen(
                modifier,
                createAccount,
                onNextButtonClicked = {
                    navController.navigate(AppScreen.Home.name)
                },
                onLoginButtonClicked = {
                    navController.navigate(AppScreen.Login.name)
                },
                startLocationUpdates = startLocationUpdates,
                usersViewModel = usersViewModel,
            )
        }
        composable(route = AppScreen.Login.name) {
            LoginScreen(modifier, signIn, onRegisterClicked = {
                navController.navigate(AppScreen.Register.name)
            }, onNextButtonClicked = {
                navController.navigate(AppScreen.Home.name)
            })
        }
        composable(route = AppScreen.Cart.name) {
            CartScreen(
                cartViewModel = cartViewModel,
                onNextButtonClicked = {
                    navController.navigate(AppScreen.Home.name)
                }
            )
        }
        composable(route = AppScreen.Filter.name) {
            FilterScreen(
                usersViewModel = usersViewModel,
                restaurantsViewModel = restaurantsViewModel
            )
        }
    }
}