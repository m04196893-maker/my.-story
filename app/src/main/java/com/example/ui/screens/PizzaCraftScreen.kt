package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BakeryDining
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.PizzaCatalog
import com.example.model.AppLanguage
import com.example.model.PizzaStyleGuide
import com.example.viewmodel.DoughCalculatorState

@Composable
fun PizzaCraftScreen(
    doughCalc: DoughCalculatorState,
    language: AppLanguage,
    onDoughCountChange: (Int) -> Unit,
    onBallWeightChange: (Int) -> Unit,
    onHydrationChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isAr = language == AppLanguage.AR

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Column {
                Text(
                    text = if (isAr) "عالم البيتزا وفن العجينة" else "Pizza Encyclopedia & Craft",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isAr) {
                        "اكتشف أسرار مدارس البيتزا العالمية واحسب مقادير العجينة الإيطالية بدقة"
                    } else {
                        "Explore iconic pizza traditions and calculate exact Neapolitan dough ratios"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        // Interactive Neapolitan Dough Calculator
        item {
            DoughCalculatorCard(
                state = doughCalc,
                isAr = isAr,
                onCountChange = onDoughCountChange,
                onBallWeightChange = onBallWeightChange,
                onHydrationChange = onHydrationChange,
            )
        }

        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Public,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = if (isAr) "مدارس البيتزا العالمية" else "World Pizza Styles",
                    style = MaterialTheme.typography.titleLarge,
                )
            }
        }

        items(PizzaCatalog.worldStyles, key = { it.id }) { style ->
            WorldPizzaStyleCard(style = style, isAr = isAr)
        }
    }
}

@Composable
private fun DoughCalculatorCard(
    state: DoughCalculatorState,
    isAr: Boolean,
    onCountChange: (Int) -> Unit,
    onBallWeightChange: (Int) -> Unit,
    onHydrationChange: (Int) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.BakeryDining,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = if (isAr) "حاسبة العجينة الإيطالية (نسب الخبّازين)" else "Neapolitan Dough Calculator",
                    style = MaterialTheme.typography.titleLarge,
                )
            }
            Spacer(modifier = Modifier.height(14.dp))

            // Pizza Count Stepper
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = if (isAr) "عدد البيتزات" else "Number of Dough Balls",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = if (isAr) "وزن الكرة الواحدة: ${state.ballWeightGrams} جم" else "Ball weight: ${state.ballWeightGrams}g",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    FilledTonalIconButton(
                        onClick = { onCountChange(state.pizzaCount - 1) },
                        modifier = Modifier.testTag("dough_count_minus"),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = if (isAr) "تقليل" else "Decrease",
                        )
                    }
                    Text(
                        text = "${state.pizzaCount}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    FilledTonalIconButton(
                        onClick = { onCountChange(state.pizzaCount + 1) },
                        modifier = Modifier.testTag("dough_count_plus"),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = if (isAr) "زيادة" else "Increase",
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = if (isAr) "وزن الكرة: ${state.ballWeightGrams} جم" else "Ball Weight: ${state.ballWeightGrams}g",
                style = MaterialTheme.typography.labelLarge,
            )
            Slider(
                value = state.ballWeightGrams.toFloat(),
                onValueChange = { onBallWeightChange(it.toInt()) },
                valueRange = 200f..340f,
                modifier = Modifier.testTag("dough_weight_slider"),
            )

            Text(
                text = if (isAr) "نسبة الرطوبة (الماء): ${state.hydrationPercent}%" else "Hydration: ${state.hydrationPercent}%",
                style = MaterialTheme.typography.labelLarge,
            )
            Slider(
                value = state.hydrationPercent.toFloat(),
                onValueChange = { onHydrationChange(it.toInt()) },
                valueRange = 58f..76f,
                modifier = Modifier.testTag("dough_hydration_slider"),
            )

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (isAr) "المقادير الدقيقة المطلوبة:" else "Exact Ingredients:",
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                IngredientMetricTile(
                    label = if (isAr) "دقيق 00" else "00 Flour",
                    value = if (isAr) "${state.flourGrams} جم" else "${state.flourGrams}g",
                    modifier = Modifier.weight(1f),
                )
                IngredientMetricTile(
                    label = if (isAr) "ماء بارد" else "Water",
                    value = if (isAr) "${state.waterMl} مل" else "${state.waterMl}ml",
                    modifier = Modifier.weight(1f),
                )
                IngredientMetricTile(
                    label = if (isAr) "ملح بحري" else "Sea Salt",
                    value = if (isAr) "${state.saltGrams} جم" else "${state.saltGrams}g",
                    modifier = Modifier.weight(1f),
                )
                IngredientMetricTile(
                    label = if (isAr) "خميرة" else "Yeast",
                    value = if (isAr) "${state.yeastGrams} جم" else "${state.yeastGrams}g",
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun IngredientMetricTile(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun WorldPizzaStyleCard(
    style: PizzaStyleGuide,
    isAr: Boolean,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column {
            Image(
                painter = painterResource(id = style.imageRes),
                contentDescription = if (isAr) style.titleAr else style.titleEn,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(155.dp)
                    .clip(RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp)),
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (isAr) style.originAr else style.originEn,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (isAr) style.titleAr else style.titleEn,
                    style = MaterialTheme.typography.titleLarge,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
                                text = if (isAr) style.tempAr else style.tempEn,
                                style = MaterialTheme.typography.labelSmall,
                            )
                        }
                    }
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Default.WaterDrop,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                            )
                            Text(
                                text = if (isAr) style.hydrationAr else style.hydrationEn,
                                style = MaterialTheme.typography.labelSmall,
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = if (isAr) style.descriptionAr else style.descriptionEn,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
