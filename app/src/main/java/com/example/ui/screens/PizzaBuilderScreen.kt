package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.PizzaCatalog
import com.example.model.AppLanguage
import com.example.model.CrustType
import com.example.model.PizzaSize
import com.example.model.SauceType
import com.example.ui.components.PizzaCanvasPreview
import com.example.viewmodel.CustomPizzaState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PizzaBuilderScreen(
    customPizza: CustomPizzaState,
    language: AppLanguage,
    onSizeSelected: (PizzaSize) -> Unit,
    onCrustSelected: (CrustType) -> Unit,
    onSauceSelected: (SauceType) -> Unit,
    onToggleExtraMozzarella: () -> Unit,
    onToggleTopping: (String) -> Unit,
    onReset: () -> Unit,
    onAddCustomPizzaToCart: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isAr = language == AppLanguage.AR

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isWide = maxWidth >= 720.dp

        if (isWide) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                Column(
                    modifier = Modifier.weight(0.45f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    BuilderHeader(isAr = isAr, onReset = onReset)
                    Spacer(modifier = Modifier.height(12.dp))
                    LivePreviewCard(customPizza = customPizza, isAr = isAr)
                    Spacer(modifier = Modifier.height(16.dp))
                    BuilderCheckoutBar(
                        customPizza = customPizza,
                        isAr = isAr,
                        onAdd = onAddCustomPizzaToCart,
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(0.55f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(18.dp),
                ) {
                    BuilderControls(
                        customPizza = customPizza,
                        isAr = isAr,
                        onSizeSelected = onSizeSelected,
                        onCrustSelected = onCrustSelected,
                        onSauceSelected = onSauceSelected,
                        onToggleExtraMozzarella = onToggleExtraMozzarella,
                        onToggleTopping = onToggleTopping,
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    BuilderHeader(isAr = isAr, onReset = onReset)
                    LivePreviewCard(customPizza = customPizza, isAr = isAr)
                    BuilderControls(
                        customPizza = customPizza,
                        isAr = isAr,
                        onSizeSelected = onSizeSelected,
                        onCrustSelected = onCrustSelected,
                        onSauceSelected = onSauceSelected,
                        onToggleExtraMozzarella = onToggleExtraMozzarella,
                        onToggleTopping = onToggleTopping,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                Surface(
                    tonalElevation = 6.dp,
                    shadowElevation = 8.dp,
                    color = MaterialTheme.colorScheme.surface,
                ) {
                    Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                        BuilderCheckoutBar(
                            customPizza = customPizza,
                            isAr = isAr,
                            onAdd = onAddCustomPizzaToCart,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BuilderHeader(isAr: Boolean, onReset: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                text = if (isAr) "استوديو تصميم البيتزا" else "Artisan Pizza Studio",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = if (isAr) "اختر العجينة والصلصة وشاهد بيتزتك تتشكل مباشرة" else "Customize dough, sauce & toppings with live preview",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        IconButton(
            onClick = onReset,
            modifier = Modifier.testTag("builder_reset_button"),
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = if (isAr) "إعادة ضبط" else "Reset",
            )
        }
    }
}

@Composable
private fun LivePreviewCard(
    customPizza: CustomPizzaState,
    isAr: Boolean,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            PizzaCanvasPreview(
                size = customPizza.size,
                crust = customPizza.crust,
                sauce = customPizza.sauce,
                extraMozzarella = customPizza.extraMozzarella,
                toppings = customPizza.selectedToppings,
                modifier = Modifier.size(235.dp),
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                ) {
                    Text(
                        text = if (isAr) {
                            "${customPizza.size.labelAr} (${customPizza.size.diameterCm} سم)"
                        } else {
                            "${customPizza.size.labelEn} (${customPizza.size.diameterCm} cm)"
                        },
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    )
                }
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer,
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                        )
                        Text(
                            text = "${customPizza.totalCalories} kcal",
                            style = MaterialTheme.typography.labelMedium,
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun BuilderControls(
    customPizza: CustomPizzaState,
    isAr: Boolean,
    onSizeSelected: (PizzaSize) -> Unit,
    onCrustSelected: (CrustType) -> Unit,
    onSauceSelected: (SauceType) -> Unit,
    onToggleExtraMozzarella: () -> Unit,
    onToggleTopping: (String) -> Unit,
) {
    // Size
    Text(
        text = if (isAr) "١. الحجم" else "1. Size",
        style = MaterialTheme.typography.titleMedium,
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        PizzaSize.entries.forEach { sizeOption ->
            val selected = customPizza.size == sizeOption
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .border(
                        width = if (selected) 2.dp else 1.dp,
                        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(14.dp),
                    )
                    .clickable { onSizeSelected(sizeOption) }
                    .testTag("builder_size_${sizeOption.name.lowercase()}"),
                color = if (selected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface,
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = if (isAr) sizeOption.labelAr else sizeOption.labelEn,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "${sizeOption.diameterCm} cm",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }

    // Crust
    Text(
        text = if (isAr) "٢. نوع العجينة" else "2. Dough & Crust",
        style = MaterialTheme.typography.titleMedium,
    )
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        CrustType.entries.forEach { crustOption ->
            val selected = customPizza.crust == crustOption
            FilterChip(
                selected = selected,
                onClick = { onCrustSelected(crustOption) },
                label = {
                    val priceTxt = if (crustOption.extraPriceEgp > 0) " (+${crustOption.extraPriceEgp})" else ""
                    Text(
                        text = (if (isAr) crustOption.labelAr else crustOption.labelEn) + priceTxt
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                ),
                modifier = Modifier.testTag("builder_crust_${crustOption.name.lowercase()}"),
            )
        }
    }

    // Sauce
    Text(
        text = if (isAr) "٣. الصلصة" else "3. Artisan Sauce",
        style = MaterialTheme.typography.titleMedium,
    )
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        SauceType.entries.forEach { sauceOption ->
            val selected = customPizza.sauce == sauceOption
            FilterChip(
                selected = selected,
                onClick = { onSauceSelected(sauceOption) },
                leadingIcon = {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(sauceOption.color)
                    )
                },
                label = {
                    val priceTxt = if (sauceOption.extraPriceEgp > 0) " (+${sauceOption.extraPriceEgp})" else ""
                    Text(
                        text = (if (isAr) sauceOption.labelAr else sauceOption.labelEn) + priceTxt
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
                modifier = Modifier.testTag("builder_sauce_${sauceOption.name.lowercase()}"),
            )
        }
    }

    // Extra Mozzarella
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = if (isAr) "موتزاريلا بافلو مضاعفة" else "Double Buffalo Mozzarella",
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = if (isAr) "+25 ج.م • 130 سعرة حرارية" else "+25 EGP • 130 kcal",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Switch(
                checked = customPizza.extraMozzarella,
                onCheckedChange = { onToggleExtraMozzarella() },
                modifier = Modifier.testTag("builder_extra_cheese_switch"),
            )
        }
    }

    // Toppings
    Text(
        text = if (isAr) "٤. الإضافات الطازجة" else "4. Fresh Toppings",
        style = MaterialTheme.typography.titleMedium,
    )
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        PizzaCatalog.toppings.forEach { topping ->
            val selected = topping.id in customPizza.selectedToppingIds
            FilterChip(
                selected = selected,
                onClick = { onToggleTopping(topping.id) },
                leadingIcon = if (selected) {
                    {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                        )
                    }
                } else null,
                label = {
                    Text(
                        text = if (isAr) {
                            "${topping.nameAr} (+${topping.priceEgp})"
                        } else {
                            "${topping.nameEn} (+${topping.priceEgp})"
                        }
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onTertiaryContainer,
                ),
                modifier = Modifier.testTag("builder_topping_${topping.id}"),
            )
        }
    }
}

@Composable
private fun BuilderCheckoutBar(
    customPizza: CustomPizzaState,
    isAr: Boolean,
    onAdd: () -> Unit,
) {
    Button(
        onClick = onAdd,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .testTag("builder_add_to_cart_button"),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
        contentPadding = PaddingValues(horizontal = 20.dp),
    ) {
        Icon(imageVector = Icons.Default.AddShoppingCart, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = if (isAr) {
                "أضف بيتزتك للسلة • ${customPizza.totalPriceEgp} ج.م"
            } else {
                "Add Custom Pizza • ${customPizza.totalPriceEgp} EGP"
            },
            style = MaterialTheme.typography.titleMedium,
        )
    }
}
