package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.model.ActiveOrder
import com.example.model.AppLanguage
import com.example.model.OrderStage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.pizzaDataStore: DataStore<Preferences> by preferencesDataStore(name = "forno_pizza_prefs")

class PizzaPreferencesRepository(private val context: Context) {

    private object Keys {
        val FAVORITES = stringSetPreferencesKey("favorite_pizza_ids")
        val LANGUAGE = stringPreferencesKey("app_language")
        val LAST_ORDER_NUMBER = stringPreferencesKey("last_order_number")
        val LAST_ORDER_TOTAL = stringPreferencesKey("last_order_total")
        val LAST_ORDER_COUNT = stringPreferencesKey("last_order_count")
        val LAST_ORDER_DELIVERY = stringPreferencesKey("last_order_delivery")
        val LAST_ORDER_NOTE = stringPreferencesKey("last_order_note")
        val LAST_ORDER_STAGE = stringPreferencesKey("last_order_stage")
        val LAST_ORDER_TIME = stringPreferencesKey("last_order_time")
    }

    val favoriteIdsFlow: Flow<Set<String>> = context.pizzaDataStore.data.map { prefs ->
        prefs[Keys.FAVORITES] ?: emptySet()
    }

    val languageFlow: Flow<AppLanguage> = context.pizzaDataStore.data.map { prefs ->
        when (prefs[Keys.LANGUAGE]) {
            AppLanguage.EN.name -> AppLanguage.EN
            else -> AppLanguage.AR
        }
    }

    val activeOrderFlow: Flow<ActiveOrder?> = context.pizzaDataStore.data.map { prefs ->
        val orderNum = prefs[Keys.LAST_ORDER_NUMBER] ?: return@map null
        val total = prefs[Keys.LAST_ORDER_TOTAL]?.toIntOrNull() ?: 0
        val count = prefs[Keys.LAST_ORDER_COUNT]?.toIntOrNull() ?: 1
        val isDelivery = prefs[Keys.LAST_ORDER_DELIVERY]?.toBooleanStrictOrNull() ?: true
        val note = prefs[Keys.LAST_ORDER_NOTE] ?: ""
        val stageName = prefs[Keys.LAST_ORDER_STAGE] ?: OrderStage.RECEIVED.name
        val stage = OrderStage.entries.firstOrNull { it.name == stageName } ?: OrderStage.RECEIVED
        val time = prefs[Keys.LAST_ORDER_TIME] ?: ""
        ActiveOrder(
            orderNumber = orderNum,
            totalEgp = total,
            itemCount = count,
            isDelivery = isDelivery,
            addressOrNote = note,
            stage = stage,
            placedTimeLabel = time,
        )
    }

    suspend fun toggleFavorite(pizzaId: String) {
        context.pizzaDataStore.edit { prefs ->
            val current = prefs[Keys.FAVORITES] ?: emptySet()
            prefs[Keys.FAVORITES] = if (pizzaId in current) current - pizzaId else current + pizzaId
        }
    }

    suspend fun setLanguage(language: AppLanguage) {
        context.pizzaDataStore.edit { prefs ->
            prefs[Keys.LANGUAGE] = language.name
        }
    }

    suspend fun saveActiveOrder(order: ActiveOrder) {
        context.pizzaDataStore.edit { prefs ->
            prefs[Keys.LAST_ORDER_NUMBER] = order.orderNumber
            prefs[Keys.LAST_ORDER_TOTAL] = order.totalEgp.toString()
            prefs[Keys.LAST_ORDER_COUNT] = order.itemCount.toString()
            prefs[Keys.LAST_ORDER_DELIVERY] = order.isDelivery.toString()
            prefs[Keys.LAST_ORDER_NOTE] = order.addressOrNote
            prefs[Keys.LAST_ORDER_STAGE] = order.stage.name
            prefs[Keys.LAST_ORDER_TIME] = order.placedTimeLabel
        }
    }

    suspend fun updateOrderStage(stage: OrderStage) {
        context.pizzaDataStore.edit { prefs ->
            prefs[Keys.LAST_ORDER_STAGE] = stage.name
        }
    }

    suspend fun clearActiveOrder() {
        context.pizzaDataStore.edit { prefs ->
            prefs.remove(Keys.LAST_ORDER_NUMBER)
            prefs.remove(Keys.LAST_ORDER_TOTAL)
            prefs.remove(Keys.LAST_ORDER_COUNT)
            prefs.remove(Keys.LAST_ORDER_DELIVERY)
            prefs.remove(Keys.LAST_ORDER_NOTE)
            prefs.remove(Keys.LAST_ORDER_STAGE)
            prefs.remove(Keys.LAST_ORDER_TIME)
        }
    }
}
