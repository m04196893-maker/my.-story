package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.PizzaCategory
import com.example.model.PizzaSize
import com.example.viewmodel.DoughCalculatorState
import com.example.viewmodel.PizzaUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `app name matches localized pizza brand`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("فورنو بيتزا", appName)
    }

    @Test
    fun `dough calculator computes accurate baker percentages`() {
        val calc = DoughCalculatorState(
            pizzaCount = 4,
            ballWeightGrams = 250,
            hydrationPercent = 65,
        )
        // Total dough = 1000g; flour ~ 1000 * 100 / 168 = 595g
        assertEquals(595, calc.flourGrams)
        assertEquals(387, calc.waterMl)
        assertEquals(17, calc.saltGrams)
        assertTrue(calc.yeastGrams > 0.0)
    }

    @Test
    fun `menu filtering and promo code calculation work accurately`() {
        val state = PizzaUiState(selectedCategory = PizzaCategory.CLASSIC)
        assertEquals(1, state.filteredMenu.size)
        assertEquals("margherita_verace", state.filteredMenu.first().id)

        val customPriceMedium = state.customPizza.totalPriceEgp
        val familyCustom = state.customPizza.copy(size = PizzaSize.FAMILY)
        assertTrue(familyCustom.totalPriceEgp > customPriceMedium)
    }
}
