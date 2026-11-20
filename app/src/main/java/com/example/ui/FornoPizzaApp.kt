package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.RestaurantMenu
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.model.AppLanguage
import com.example.ui.components.PizzaDetailSheet
import com.example.ui.screens.CartAndOrdersScreen
import com.example.ui.screens.MenuScreen
import com.example.ui.screens.PizzaBuilderScreen
import com.example.ui.screens.PizzaCraftScreen
import com.example.viewmodel.PizzaViewModel

const val ROUTE_MENU = "menu"
const val ROUTE_BUILDER = "builder"
const val ROUTE_CRAFT = "craft"
const val ROUTE_CART = "cart"

private data class NavTab(
    val route: String,
    val labelAr: String,
    val labelEn: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
)

private val navTabs = listOf(
    NavTab(
        route = ROUTE_MENU,
        labelAr = "القائمة",
        labelEn = "Menu",
        selectedIcon = Icons.Filled.RestaurantMenu,
        unselectedIcon = Icons.Outlined.RestaurantMenu,
    ),
    NavTab(
        route = ROUTE_BUILDER,
        labelAr = "صمّم بيتزتك",
        labelEn = "Studio",
        selectedIcon = Icons.Filled.AutoAwesome,
        unselectedIcon = Icons.Outlined.AutoAwesome,
    ),
    NavTab(
        route = ROUTE_CRAFT,
        labelAr = "عالم البيتزا",
        labelEn = "Craft",
        selectedIcon = Icons.Filled.MenuBook,
        unselectedIcon = Icons.Outlined.MenuBook,
    ),
    NavTab(
        route = ROUTE_CART,
        labelAr = "السلة",
        labelEn = "Cart",
        selectedIcon = Icons.Filled.ShoppingBag,
        unselectedIcon = Icons.Outlined.ShoppingBag,
    ),
)

@Composable
fun FornoPizzaApp(
    viewModel: PizzaViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: ROUTE_MENU
    val snackbarHostState = remember { SnackbarHostState() }

    val isAr = uiState.language == AppLanguage.AR
    val layoutDirection = if (isAr) LayoutDirection.Rtl else LayoutDirection.Ltr

    LaunchedEffect(Unit) {
        viewModel.snackbarEvents.collect { msg ->
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(msg)
        }
    }

    val navigateToTab: (String) -> Unit = { route ->
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val useNavigationRail = maxWidth >= 680.dp

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = MaterialTheme.colorScheme.background,
                contentWindowInsets = WindowInsets.safeDrawing,
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                bottomBar = {
                    if (!useNavigationRail) {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface,
                        ) {
                            navTabs.forEach { tab ->
                                val selected = currentRoute == tab.route
                                NavigationBarItem(
                                    selected = selected,
                                    onClick = { navigateToTab(tab.route) },
                                    modifier = Modifier.testTag("nav_tab_${tab.route}"),
                                    icon = {
                                        NavIconWithBadge(
                                            tab = tab,
                                            selected = selected,
                                            cartCount = uiState.cartBadgeCount,
                                            hasActiveOrder = uiState.activeOrder != null,
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = if (isAr) tab.labelAr else tab.labelEn,
                                            style = MaterialTheme.typography.labelMedium,
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = MaterialTheme.colorScheme.primary,
                                        selectedTextColor = MaterialTheme.colorScheme.primary,
                                        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                    ),
                                )
                            }
                        }
                    }
                },
            ) { innerPadding ->
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                ) {
                    if (useNavigationRail) {
                        NavigationRail(
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(vertical = 12.dp),
                            containerColor = MaterialTheme.colorScheme.surface,
                        ) {
                            navTabs.forEach { tab ->
                                val selected = currentRoute == tab.route
                                NavigationRailItem(
                                    selected = selected,
                                    onClick = { navigateToTab(tab.route) },
                                    modifier = Modifier.testTag("nav_rail_${tab.route}"),
                                    icon = {
                                        NavIconWithBadge(
                                            tab = tab,
                                            selected = selected,
                                            cartCount = uiState.cartBadgeCount,
                                            hasActiveOrder = uiState.activeOrder != null,
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = if (isAr) tab.labelAr else tab.labelEn,
                                            style = MaterialTheme.typography.labelMedium,
                                        )
                                    },
                                )
                            }
                        }
                    }

                    Box(modifier = Modifier.weight(1f).fillMaxSize()) {
                        NavHost(
                            navController = navController,
                            startDestination = ROUTE_MENU,
                        ) {
                            composable(ROUTE_MENU) {
                                MenuScreen(
                                    uiState = uiState,
                                    onSearchChange = viewModel::onSearchQueryChange,
                                    onSelectCategory = viewModel::onCategorySelected,
                                    onToggleFavoritesOnly = viewModel::toggleFavoritesFilter,
                                    onToggleFavorite = viewModel::toggleFavorite,
                                    onOpenPizzaDetail = viewModel::openPizzaDetail,
                                    onQuickAdd = { pizza -> viewModel.addPizzaToCart(pizza) },
                                    onNavigateToBuilder = { navigateToTab(ROUTE_BUILDER) },
                                    onToggleLanguage = viewModel::toggleLanguage,
                                )
                            }
                            composable(ROUTE_BUILDER) {
                                PizzaBuilderScreen(
                                    customPizza = uiState.customPizza,
                                    language = uiState.language,
                                    onSizeSelected = viewModel::updateCustomSize,
                                    onCrustSelected = viewModel::updateCustomCrust,
                                    onSauceSelected = viewModel::updateCustomSauce,
                                    onToggleExtraMozzarella = viewModel::toggleCustomExtraMozzarella,
                                    onToggleTopping = viewModel::toggleCustomTopping,
                                    onReset = viewModel::resetCustomPizza,
                                    onAddCustomPizzaToCart = viewModel::addCustomPizzaToCart,
                                )
                            }
                            composable(ROUTE_CRAFT) {
                                PizzaCraftScreen(
                                    doughCalc = uiState.doughCalc,
                                    language = uiState.language,
                                    onDoughCountChange = viewModel::updateDoughCount,
                                    onBallWeightChange = viewModel::updateDoughBallWeight,
                                    onHydrationChange = viewModel::updateDoughHydration,
                                )
                            }
                            composable(ROUTE_CART) {
                                CartAndOrdersScreen(
                                    uiState = uiState,
                                    onUpdateQuantity = viewModel::updateCartQuantity,
                                    onPromoInputChange = viewModel::onPromoCodeChange,
                                    onApplyPromo = viewModel::applyPromoCode,
                                    onClearPromo = viewModel::clearPromoCode,
                                    onSetDeliveryMode = viewModel::setDeliveryMode,
                                    onAddressChange = viewModel::onAddressOrNoteChange,
                                    onPlaceOrder = viewModel::placeOrder,
                                    onAdvanceOrderStage = viewModel::advanceOrderStage,
                                    onDismissOrder = viewModel::dismissActiveOrder,
                                    onNavigateToMenu = { navigateToTab(ROUTE_MENU) },
                                    onNavigateToBuilder = { navigateToTab(ROUTE_BUILDER) },
                                )
                            }
                        }
                    }
                }
            }

            val detailPizza = uiState.selectedPizzaForDetail
            if (detailPizza != null) {
                BackHandler {
                    viewModel.closePizzaDetail()
                }
                PizzaDetailSheet(
                    pizza = detailPizza,
                    language = uiState.language,
                    isFavorite = detailPizza.id in uiState.favoriteIds,
                    onToggleFavorite = { viewModel.toggleFavorite(detailPizza.id) },
                    onDismiss = viewModel::closePizzaDetail,
                    onAddToCart = { size, crust, toppings ->
                        viewModel.addPizzaToCart(detailPizza, size, crust, toppings)
                    },
                )
            }
        }
    }
}

@Composable
private fun NavIconWithBadge(
    tab: NavTab,
    selected: Boolean,
    cartCount: Int,
    hasActiveOrder: Boolean,
) {
    val icon = if (selected) tab.selectedIcon else tab.unselectedIcon
    if (tab.route == ROUTE_CART && (cartCount > 0 || hasActiveOrder)) {
        BadgedBox(
            badge = {
                Badge(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ) {
                    Text(text = if (cartCount > 0) "$cartCount" else "•")
                }
            }
        ) {
            Icon(imageVector = icon, contentDescription = null)
        }
    } else {
        Icon(imageVector = icon, contentDescription = null)
    }
}
