package com.example.eatit

import android.app.Application
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LunchDining
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.eatit.model.User
import com.example.eatit.ui.*
import com.example.eatit.viewModel.CartViewModel
import com.example.eatit.viewModel.RestaurantsViewModel
import com.example.eatit.viewModel.SettingsViewModel
import com.example.eatit.viewModel.UsersViewModel
import com.example.eatit.viewModel.WarningViewModel
import dagger.hilt.android.HiltAndroidApp
import kotlin.reflect.KFunction3
import kotlin.reflect.KFunction4

sealed class AppScreen(var name: String) {
    object Home : AppScreen("Home")
    object AddRestaurant : AppScreen("AddRestaurant")
    object AddProduct : AppScreen("AddProduct")
    object Details : AppScreen("Details")
    object Settings : AppScreen("Settings")
    object UserProfile : AppScreen("UserProfile")
    object Map : AppScreen("Map")
    object Login : AppScreen("Login")
    object Register : AppScreen("Register")
    object Cart : AppScreen("Cart")
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
                && currentScreen != AppScreen.UserProfile.name
                && currentScreen != AppScreen.Cart.name
                && currentScreen != AppScreen.Map.name
                && currentScreen != AppScreen.Settings.name
            ) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back button"
                    )
                }
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
    onCartButtonClicked: () -> Unit,
) {

    BottomAppBar(modifier = modifier,
        actions = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                IconButton(onClick = onHomeButtonClicked) {
                    Icon(
                        Icons.Filled.Home,
                        contentDescription = stringResource(id = R.string.settings),
                    )

                }
                if (currentScreen == AppScreen.Home.name) {
                    Text(
                        text = "Home",
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.align(CenterVertically)
                    )
                }
                IconButton(onClick = onUserProfileButtonClicked) {
                    Icon(
                        Icons.Filled.AccountCircle,
                        contentDescription = stringResource(id = R.string.settings)
                    )

                }
                if (currentScreen == AppScreen.UserProfile.name) {
                    Text(
                        text = "Profile",
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.align(CenterVertically)
                    )
                }
                IconButton(onClick = onCartButtonClicked) {
                    Icon(
                        Icons.Filled.ShoppingCart,
                        contentDescription = stringResource(id = R.string.settings),
                    )
                }
                if (currentScreen == AppScreen.Cart.name) {
                    Text(
                        text = "Cart",
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.align(CenterVertically)
                    )
                }
                IconButton(onClick = onMapButtonClicked) {
                    Icon(
                        Icons.Filled.Map,
                        contentDescription = stringResource(id = R.string.settings),
                    )
                }
                if (currentScreen == AppScreen.Map.name) {
                    Text(
                        text = "Map",
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.align(CenterVertically)
                    )
                }
                IconButton(onClick = onSettingsButtonClicked) {
                    Icon(
                        Icons.Filled.Settings,
                        contentDescription = stringResource(id = R.string.settings)
                    )
                }
                if (currentScreen == AppScreen.Settings.name) {
                    Text(
                        text = "Settings",
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.align(CenterVertically)
                    )
                }

            }
        }
    )
}

@Composable
fun NavigationApp(
    warningViewModel: WarningViewModel,
    startLocationUpdates: () -> Unit,
    navController: NavHostController = rememberNavController(),
    signIn: KFunction3<String, String, () -> Unit, Unit>,
    createAccount: KFunction4<String, String, User, () -> Unit, Unit>,
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
                    onUserProfileButtonClicked = { navController.navigate(AppScreen.UserProfile.name) },
                    onMapButtonClicked = { navController.navigate(AppScreen.Map.name) },
                    onHomeButtonClicked = { navController.navigate(AppScreen.Home.name) },
                    onCartButtonClicked = { navController.navigate(AppScreen.Cart.name) },
                )
            }
        }
    ) { innerPadding ->
        NavigationGraph(
            navController,
            innerPadding,
            startLocationUpdates,
            Modifier,
            signIn,
            createAccount
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

@Composable
private fun NavigationGraph(
    navController: NavHostController,
    innerPadding: PaddingValues,
    startLocationUpdates: () -> Unit,
    modifier: Modifier = Modifier,
    signIn: KFunction3<String, String, () -> Unit, Unit>,
    createAccount: KFunction4<String, String, User, () -> Unit, Unit>,
) {
    val restaurantsViewModel = hiltViewModel<RestaurantsViewModel>()
    val usersViewModel = hiltViewModel<UsersViewModel>()
    val settingsViewModel = hiltViewModel<SettingsViewModel>()
    val cartViewModel = hiltViewModel<CartViewModel>()
    NavHost(
        navController = navController,
        startDestination = AppScreen.Home.name,
        modifier = modifier.padding(innerPadding)
    ) {
        composable(route = AppScreen.Home.name) {
            HomeScreen(
                onAddButtonClicked = {
                    navController.navigate(AppScreen.AddRestaurant.name)
                },
                onItemClicked = {
                    navController.navigate(AppScreen.Details.name)
                },
                restaurantsViewModel = restaurantsViewModel,
                onLoginClicked = {
                    navController.navigate(AppScreen.Login.name)
                }
            )
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
                },
                cartViewModel = cartViewModel
            )
        }
        composable(route = AppScreen.Settings.name) {
            SettingsScreen(settingsViewModel, onNextButtonClicked = {
                navController.navigate(AppScreen.Login.name)
            })
        }
        composable(route = AppScreen.Map.name) {
            MapScreen(
                startLocationUpdates = startLocationUpdates,
                restaurantsViewModel = restaurantsViewModel
            )
        }
        composable(route = AppScreen.UserProfile.name) {
            UserProfileScreen(
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
                restaurantsViewModel,
                startLocationUpdates
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
            CartScreen()
        }
    }
}