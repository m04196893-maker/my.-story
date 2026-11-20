package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.R
import com.example.data.PizzaCatalog
import com.example.data.PizzaPreferencesRepository
import com.example.model.ActiveOrder
import com.example.model.AppLanguage
import com.example.model.CartItem
import com.example.model.CrustType
import com.example.model.OrderStage
import com.example.model.PizzaCategory
import com.example.model.PizzaItem
import com.example.model.PizzaSize
import com.example.model.SauceType
import com.example.model.ToppingItem
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

data class DoughCalculatorState(
    val pizzaCount: Int = 4,
    val ballWeightGrams: Int = 260,
    val hydrationPercent: Int = 65,
    val saltPercent: Double = 2.8,
    val freshYeastPercent: Double = 0.2,
) {
    private val totalDoughGrams: Double
        get() = (pizzaCount * ballWeightGrams).toDouble()

    private val totalPercent: Double
        get() = 100.0 + hydrationPercent + saltPercent + freshYeastPercent

    val flourGrams: Int
        get() = ((totalDoughGrams * 100.0) / totalPercent).roundToInt()

    val waterMl: Int
        get() = ((flourGrams * hydrationPercent) / 100.0).roundToInt()

    val saltGrams: Int
        get() = ((flourGrams * saltPercent) / 100.0).roundToInt()

    val yeastGrams: Double
        get() = (((flourGrams * freshYeastPercent) / 100.0) * 10.0).roundToInt() / 10.0
}

data class CustomPizzaState(
    val size: PizzaSize = PizzaSize.MEDIUM,
    val crust: CrustType = CrustType.NEAPOLITAN,
    val sauce: SauceType = SauceType.SAN_MARZANO,
    val selectedToppingIds: Set<String> = setOf("basil", "mushroom"),
    val extraMozzarella: Boolean = true,
) {
    val selectedToppings: List<ToppingItem>
        get() = PizzaCatalog.toppings.filter { it.id in selectedToppingIds }

    val totalPriceEgp: Int
        get() {
            val base = (155 * size.priceMultiplier).roundToInt()
            val extraCheeseCost = if (extraMozzarella) 25 else 0
            val toppingsCost = selectedToppings.sumOf { it.priceEgp }
            return base + crust.extraPriceEgp + sauce.extraPriceEgp + extraCheeseCost + toppingsCost
        }

    val totalCalories: Int
        get() {
            val base = (580 * size.calorieFactor).roundToInt()
            val cheeseCals = if (extraMozzarella) 130 else 0
            return base + cheeseCals + selectedToppings.sumOf { it.calories }
        }
}

data class PizzaUiState(
    val language: AppLanguage = AppLanguage.AR,
    val searchQuery: String = "",
    val selectedCategory: PizzaCategory = PizzaCategory.ALL,
    val favoritesOnly: Boolean = false,
    val favoriteIds: Set<String> = emptySet(),
    val menuItems: List<PizzaItem> = PizzaCatalog.menuItems,
    val selectedPizzaForDetail: PizzaItem? = null,
    val customPizza: CustomPizzaState = CustomPizzaState(),
    val doughCalc: DoughCalculatorState = DoughCalculatorState(),
    val cartItems: List<CartItem> = emptyList(),
    val promoCodeInput: String = "",
    val appliedPromoCode: String? = null,
    val isDelivery: Boolean = true,
    val customerAddressOrNote: String = "",
    val activeOrder: ActiveOrder? = null,
) {
    val filteredMenu: List<PizzaItem>
        get() = menuItems.filter { item ->
            val matchesCategory =
                selectedCategory == PizzaCategory.ALL || item.category == selectedCategory
            val matchesFavorite = !favoritesOnly || item.id in favoriteIds
            val query = searchQuery.trim()
            val matchesQuery = query.isEmpty() ||
                item.nameAr.contains(query, ignoreCase = true) ||
                item.nameEn.contains(query, ignoreCase = true) ||
                item.shortDescAr.contains(query, ignoreCase = true) ||
                item.shortDescEn.contains(query, ignoreCase = true)
            matchesCategory && matchesFavorite && matchesQuery
        }

    val popularPizzas: List<PizzaItem>
        get() = menuItems.filter { it.isPopular }

    val cartBadgeCount: Int
        get() = cartItems.sumOf { it.quantity }

    val subtotalEgp: Int
        get() = cartItems.sumOf { it.unitPriceEgp * it.quantity }

    val discountEgp: Int
        get() = when (appliedPromoCode?.uppercase()) {
            "PIZZA20" -> (subtotalEgp * 0.20).roundToInt()
            "FORNO50" -> if (subtotalEgp >= 200) 50 else 0
            else -> 0
        }

    val deliveryFeeEgp: Int
        get() = if (cartItems.isEmpty() || !isDelivery) 0 else 25

    val totalEgp: Int
        get() = (subtotalEgp - discountEgp + deliveryFeeEgp).coerceAtLeast(0)
}

class PizzaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PizzaPreferencesRepository(application.applicationContext)

    private val _uiState = MutableStateFlow(PizzaUiState())
    val uiState: StateFlow<PizzaUiState> = _uiState.asStateFlow()

    private val _snackbarEvents = MutableSharedFlow<String>(extraBufferCapacity = 4)
    val snackbarEvents: SharedFlow<String> = _snackbarEvents.asSharedFlow()

    private var orderTickerJob: Job? = null

    init {
        viewModelScope.launch {
            combine(
                repository.favoriteIdsFlow,
                repository.languageFlow,
                repository.activeOrderFlow,
            ) { favs, lang, order ->
                Triple(favs, lang, order)
            }.collect { (favs, lang, order) ->
                _uiState.update { state ->
                    state.copy(
                        favoriteIds = favs,
                        language = lang,
                        activeOrder = order,
                    )
                }
            }
        }
    }

    fun toggleLanguage() {
        val next = if (_uiState.value.language == AppLanguage.AR) AppLanguage.EN else AppLanguage.AR
        viewModelScope.launch {
            repository.setLanguage(next)
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onCategorySelected(category: PizzaCategory) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun toggleFavoritesFilter() {
        _uiState.update { it.copy(favoritesOnly = !it.favoritesOnly) }
    }

    fun toggleFavorite(pizzaId: String) {
        viewModelScope.launch {
            repository.toggleFavorite(pizzaId)
        }
    }

    fun openPizzaDetail(pizza: PizzaItem) {
        _uiState.update { it.copy(selectedPizzaForDetail = pizza) }
    }

    fun closePizzaDetail() {
        _uiState.update { it.copy(selectedPizzaForDetail = null) }
    }

    fun addPizzaToCart(
        pizza: PizzaItem,
        size: PizzaSize = PizzaSize.MEDIUM,
        crust: CrustType = CrustType.NEAPOLITAN,
        extraToppings: List<ToppingItem> = emptyList(),
    ) {
        val isAr = _uiState.value.language == AppLanguage.AR
        val unitPrice = (pizza.basePriceEgp * size.priceMultiplier).roundToInt() +
            crust.extraPriceEgp +
            extraToppings.sumOf { it.priceEgp }

        val toppingsKey = extraToppings.map { it.id }.sorted().joinToString(",")
        val cartKey = "${pizza.id}_${size.name}_${crust.name}_$toppingsKey"

        val subtitleAr = buildString {
            append("${size.labelAr} • ${crust.labelAr}")
            if (extraToppings.isNotEmpty()) {
                append(" + ${extraToppings.joinToString("، ") { it.nameAr }}")
            }
        }
        val subtitleEn = buildString {
            append("${size.labelEn} • ${crust.labelEn}")
            if (extraToppings.isNotEmpty()) {
                append(" + ${extraToppings.joinToString(", ") { it.nameEn }}")
            }
        }

        upsertCartItem(
            CartItem(
                cartKey = cartKey,
                titleAr = pizza.nameAr,
                titleEn = pizza.nameEn,
                subtitleAr = subtitleAr,
                subtitleEn = subtitleEn,
                unitPriceEgp = unitPrice,
                quantity = 1,
                imageRes = pizza.imageRes,
                isCustom = false,
            )
        )
        _uiState.update { it.copy(selectedPizzaForDetail = null) }
        val msg = if (isAr) "تمت إضافة ${pizza.nameAr} للسلة" else "Added ${pizza.nameEn} to cart"
        _snackbarEvents.tryEmit(msg)
    }

    fun updateCustomSize(size: PizzaSize) {
        _uiState.update { it.copy(customPizza = it.customPizza.copy(size = size)) }
    }

    fun updateCustomCrust(crust: CrustType) {
        _uiState.update { it.copy(customPizza = it.customPizza.copy(crust = crust)) }
    }

    fun updateCustomSauce(sauce: SauceType) {
        _uiState.update { it.copy(customPizza = it.customPizza.copy(sauce = sauce)) }
    }

    fun toggleCustomExtraMozzarella() {
        _uiState.update {
            it.copy(customPizza = it.customPizza.copy(extraMozzarella = !it.customPizza.extraMozzarella))
        }
    }

    fun toggleCustomTopping(toppingId: String) {
        _uiState.update { state ->
            val current = state.customPizza.selectedToppingIds
            val next = if (toppingId in current) current - toppingId else current + toppingId
            state.copy(customPizza = state.customPizza.copy(selectedToppingIds = next))
        }
    }

    fun resetCustomPizza() {
        _uiState.update { it.copy(customPizza = CustomPizzaState()) }
    }

    fun addCustomPizzaToCart() {
        val state = _uiState.value
        val custom = state.customPizza
        val isAr = state.language == AppLanguage.AR
        val toppingsAr = custom.selectedToppings.joinToString("، ") { it.nameAr }
            .ifEmpty { "جبنة وصلصة فقط" }
        val toppingsEn = custom.selectedToppings.joinToString(", ") { it.nameEn }
            .ifEmpty { "Cheese & sauce only" }

        val cartKey = "custom_${custom.size.name}_${custom.crust.name}_${custom.sauce.name}_${custom.extraMozzarella}_${
            custom.selectedToppingIds.sorted().joinToString(",")
        }"

        upsertCartItem(
            CartItem(
                cartKey = cartKey,
                titleAr = "بيتزا من تصميمك",
                titleEn = "Custom Artisan Pizza",
                subtitleAr = "${custom.size.labelAr} • ${custom.crust.labelAr} • ${custom.sauce.labelAr} • $toppingsAr",
                subtitleEn = "${custom.size.labelEn} • ${custom.crust.labelEn} • ${custom.sauce.labelEn} • $toppingsEn",
                unitPriceEgp = custom.totalPriceEgp,
                quantity = 1,
                imageRes = R.drawable.img_hero_oven,
                isCustom = true,
            )
        )
        val msg = if (isAr) "تمت إضافة بيتزتك المخصصة للسلة!" else "Your custom pizza was added!"
        _snackbarEvents.tryEmit(msg)
    }

    private fun upsertCartItem(newItem: CartItem) {
        _uiState.update { state ->
            val existing = state.cartItems.firstOrNull { it.cartKey == newItem.cartKey }
            val updated = if (existing != null) {
                state.cartItems.map {
                    if (it.cartKey == newItem.cartKey) it.copy(quantity = it.quantity + 1) else it
                }
            } else {
                state.cartItems + newItem
            }
            state.copy(cartItems = updated)
        }
    }

    fun updateCartQuantity(cartKey: String, delta: Int) {
        _uiState.update { state ->
            val updated = state.cartItems.mapNotNull { item ->
                if (item.cartKey == cartKey) {
                    val nextQty = item.quantity + delta
                    if (nextQty <= 0) null else item.copy(quantity = nextQty)
                } else {
                    item
                }
            }
            state.copy(cartItems = updated)
        }
    }

    fun onPromoCodeChange(code: String) {
        _uiState.update { it.copy(promoCodeInput = code) }
    }

    fun applyPromoCode() {
        val state = _uiState.value
        val code = state.promoCodeInput.trim().uppercase()
        val isAr = state.language == AppLanguage.AR
        when (code) {
            "PIZZA20", "FORNO50" -> {
                _uiState.update { it.copy(appliedPromoCode = code) }
                _snackbarEvents.tryEmit(
                    if (isAr) "تم تفعيل كود الخصم $code" else "Promo code $code applied!"
                )
            }
            else -> {
                _snackbarEvents.tryEmit(
                    if (isAr) "الكود غير صالح (جرّب PIZZA20)" else "Invalid code (try PIZZA20)"
                )
            }
        }
    }

    fun clearPromoCode() {
        _uiState.update { it.copy(appliedPromoCode = null, promoCodeInput = "") }
    }

    fun setDeliveryMode(isDelivery: Boolean) {
        _uiState.update { it.copy(isDelivery = isDelivery) }
    }

    fun onAddressOrNoteChange(text: String) {
        _uiState.update { it.copy(customerAddressOrNote = text) }
    }

    fun placeOrder() {
        val state = _uiState.value
        if (state.cartItems.isEmpty()) return
        val isAr = state.language == AppLanguage.AR
        val orderNum = "FP-${(1040..9980).random()}"
        val timeLabel = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
        val note = state.customerAddressOrNote.trim().ifEmpty {
            if (state.isDelivery) {
                if (isAr) "التوصيل للعنوان الافتراضي" else "Standard local delivery"
            } else {
                if (isAr) "استلام من فرع الزمالك" else "Pickup from Zamalek Oven"
            }
        }
        val order = ActiveOrder(
            orderNumber = orderNum,
            totalEgp = state.totalEgp,
            itemCount = state.cartBadgeCount,
            isDelivery = state.isDelivery,
            addressOrNote = note,
            stage = OrderStage.RECEIVED,
            placedTimeLabel = timeLabel,
        )

        _uiState.update {
            it.copy(
                cartItems = emptyList(),
                appliedPromoCode = null,
                promoCodeInput = "",
                activeOrder = order,
            )
        }

        viewModelScope.launch {
            repository.saveActiveOrder(order)
            startOrderProgression()
        }
        _snackbarEvents.tryEmit(
            if (isAr) "تم إرسال طلبك #$orderNum لفرن الحطب!" else "Order #$orderNum sent to the oven!"
        )
    }

    fun advanceOrderStage() {
        val current = _uiState.value.activeOrder ?: return
        val nextIndex = (current.stage.stepIndex + 1).coerceAtMost(OrderStage.DELIVERED.stepIndex)
        val nextStage = OrderStage.entries.first { it.stepIndex == nextIndex }
        viewModelScope.launch {
            repository.updateOrderStage(nextStage)
        }
    }

    fun dismissActiveOrder() {
        orderTickerJob?.cancel()
        viewModelScope.launch {
            repository.clearActiveOrder()
        }
    }

    private fun startOrderProgression() {
        orderTickerJob?.cancel()
        orderTickerJob = viewModelScope.launch {
            val stages = listOf(
                OrderStage.DOUGH_STRETCHING,
                OrderStage.WOOD_OVEN,
                OrderStage.OUT_FOR_DELIVERY,
                OrderStage.DELIVERED,
            )
            for (stage in stages) {
                delay(5500)
                if (_uiState.value.activeOrder == null) break
                repository.updateOrderStage(stage)
            }
        }
    }

    fun updateDoughCount(count: Int) {
        _uiState.update {
            it.copy(doughCalc = it.doughCalc.copy(pizzaCount = count.coerceIn(1, 12)))
        }
    }

    fun updateDoughBallWeight(weight: Int) {
        _uiState.update {
            it.copy(doughCalc = it.doughCalc.copy(ballWeightGrams = weight.coerceIn(200, 340)))
        }
    }

    fun updateDoughHydration(hydration: Int) {
        _uiState.update {
            it.copy(doughCalc = it.doughCalc.copy(hydrationPercent = hydration.coerceIn(58, 76)))
        }
    }
}
