package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.LocalPizza
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.model.ActiveOrder
import com.example.model.AppLanguage
import com.example.model.CartItem
import com.example.model.OrderStage
import com.example.viewmodel.PizzaUiState

@Composable
fun CartAndOrdersScreen(
    uiState: PizzaUiState,
    onUpdateQuantity: (String, Int) -> Unit,
    onPromoInputChange: (String) -> Unit,
    onApplyPromo: () -> Unit,
    onClearPromo: () -> Unit,
    onSetDeliveryMode: (Boolean) -> Unit,
    onAddressChange: (String) -> Unit,
    onPlaceOrder: () -> Unit,
    onAdvanceOrderStage: () -> Unit,
    onDismissOrder: () -> Unit,
    onNavigateToMenu: () -> Unit,
    onNavigateToBuilder: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isAr = uiState.language == AppLanguage.AR

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Text(
                text = if (isAr) "سلة الطلبات والتتبع" else "Your Cart & Live Oven Tracker",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
        }

        // Active Order Live Tracker
        if (uiState.activeOrder != null) {
            item {
                ActiveOrderTrackerCard(
                    order = uiState.activeOrder,
                    isAr = isAr,
                    onAdvanceStage = onAdvanceOrderStage,
                    onDismiss = onDismissOrder,
                )
            }
        }

        if (uiState.cartItems.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(68.dp),
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.LocalPizza,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(34.dp),
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = if (isAr) "سلتك فارغة حالياً" else "Your Cart is Empty",
                            style = MaterialTheme.typography.titleLarge,
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (isAr) {
                                "تصفح قائمة البيتزا المخمرة ٤٨ ساعة أو صمّم بيتزتك المفضلة"
                            } else {
                                "Browse our wood-fired menu or craft a custom pizza"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Button(
                                onClick = onNavigateToMenu,
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.testTag("empty_cart_menu_btn"),
                            ) {
                                Text(text = if (isAr) "تصفح القائمة" else "Browse Menu")
                            }
                            OutlinedButton(
                                onClick = onNavigateToBuilder,
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.testTag("empty_cart_builder_btn"),
                            ) {
                                Text(text = if (isAr) "صمّم بيتزا" else "Build Pizza")
                            }
                        }
                    }
                }
            }
        } else {
            items(uiState.cartItems, key = { it.cartKey }) { item ->
                CartItemRowCard(
                    item = item,
                    isAr = isAr,
                    onIncrement = { onUpdateQuantity(item.cartKey, 1) },
                    onDecrement = { onUpdateQuantity(item.cartKey, -1) },
                )
            }

            // Delivery / Pickup & Address
            item {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text(
                            text = if (isAr) "طريقة الاستلام" else "Order Method",
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            ModeToggleButton(
                                selected = uiState.isDelivery,
                                icon = Icons.Default.DeliveryDining,
                                label = if (isAr) "توصيل للمنزل" else "Delivery",
                                onClick = { onSetDeliveryMode(true) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("mode_delivery_btn"),
                            )
                            ModeToggleButton(
                                selected = !uiState.isDelivery,
                                icon = Icons.Default.Storefront,
                                label = if (isAr) "استلام من الفرع" else "Pickup",
                                onClick = { onSetDeliveryMode(false) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("mode_pickup_btn"),
                            )
                        }
                        OutlinedTextField(
                            value = uiState.customerAddressOrNote,
                            onValueChange = onAddressChange,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("checkout_address_input"),
                            label = {
                                Text(
                                    text = if (uiState.isDelivery) {
                                        if (isAr) "عنوان التوصيل أو رقم الهاتف" else "Delivery address & phone"
                                    } else {
                                        if (isAr) "ملاحظات للفرع (اختياري)" else "Kitchen notes (optional)"
                                    }
                                )
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                        )
                    }
                }
            }

            // Promo Code
            item {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            OutlinedTextField(
                                value = uiState.promoCodeInput,
                                onValueChange = onPromoInputChange,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("promo_code_input"),
                                placeholder = {
                                    Text(text = if (isAr) "كود الخصم (مثال: PIZZA20)" else "Promo code (e.g. PIZZA20)")
                                },
                                leadingIcon = {
                                    Icon(imageVector = Icons.Default.LocalOffer, contentDescription = null)
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                            )
                            Button(
                                onClick = onApplyPromo,
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .height(54.dp)
                                    .testTag("apply_promo_btn"),
                            ) {
                                Text(text = if (isAr) "تطبيق" else "Apply")
                            }
                        }
                        if (uiState.appliedPromoCode == null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            AssistChip(
                                onClick = {
                                    onPromoInputChange("PIZZA20")
                                    onApplyPromo()
                                },
                                label = {
                                    Text(
                                        text = if (isAr) "اضغط لتجربة كود PIZZA20 (خصم 20%)" else "Tap to apply PIZZA20 (20% off)"
                                    )
                                },
                            )
                        } else {
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = if (isAr) "تم تفعيل ${uiState.appliedPromoCode}" else "Active: ${uiState.appliedPromoCode}",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.tertiary,
                                )
                                TextButton(onClick = onClearPromo) {
                                    Text(text = if (isAr) "إزالة" else "Remove")
                                }
                            }
                        }
                    }
                }
            }

            // Receipt & Place Order CTA
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        SummaryRow(
                            label = if (isAr) "المجموع الفرعي" else "Subtotal",
                            value = if (isAr) "${uiState.subtotalEgp} ج.م" else "${uiState.subtotalEgp} EGP",
                        )
                        if (uiState.discountEgp > 0) {
                            SummaryRow(
                                label = if (isAr) "الخصم" else "Discount",
                                value = if (isAr) "-${uiState.discountEgp} ج.م" else "-${uiState.discountEgp} EGP",
                                highlight = true,
                            )
                        }
                        SummaryRow(
                            label = if (isAr) "رسوم التوصيل" else "Delivery",
                            value = if (uiState.deliveryFeeEgp == 0) {
                                if (isAr) "مجاناً" else "Free"
                            } else {
                                if (isAr) "${uiState.deliveryFeeEgp} ج.م" else "${uiState.deliveryFeeEgp} EGP"
                            },
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 4.dp),
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                text = if (isAr) "الإجمالي" else "Total",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                text = if (isAr) "${uiState.totalEgp} ج.م" else "${uiState.totalEgp} EGP",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = onPlaceOrder,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .testTag("place_order_button"),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        ) {
                            Icon(imageVector = Icons.Default.LocalFireDepartment, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isAr) "أرسل الطلب لفرن الحطب" else "Send Order to Wood Oven",
                                style = MaterialTheme.typography.titleMedium,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ActiveOrderTrackerCard(
    order: ActiveOrder,
    isAr: Boolean,
    onAdvanceStage: () -> Unit,
    onDismiss: () -> Unit,
) {
    val progress = (order.stage.stepIndex + 1) / 5f
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)),
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = if (isAr) "طلب نشط #${order.orderNumber}" else "Active Order #${order.orderNumber}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "${order.placedTimeLabel} • ${order.addressOrNote}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primary,
                ) {
                    Text(
                        text = if (isAr) "${order.totalEgp} ج.م" else "${order.totalEgp} EGP",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape),
            )
            Spacer(modifier = Modifier.height(12.dp))

            OrderStage.entries.forEach { stage ->
                val done = stage.stepIndex <= order.stage.stepIndex
                val current = stage == order.stage
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = if (done) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(18.dp),
                    )
                    Column {
                        Text(
                            text = if (isAr) stage.titleAr else stage.titleEn,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = if (current) FontWeight.Bold else FontWeight.Normal,
                            color = if (done) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        AnimatedVisibility(visible = current) {
                            Text(
                                text = if (isAr) stage.descAr else stage.descEn,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                if (order.stage != OrderStage.DELIVERED) {
                    OutlinedButton(
                        onClick = onAdvanceStage,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("advance_order_stage_btn"),
                    ) {
                        Text(text = if (isAr) "المرحلة التالية" else "Next Stage")
                    }
                } else {
                    Spacer(modifier = Modifier.width(4.dp))
                }
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("dismiss_order_btn"),
                ) {
                    Text(text = if (isAr) "إخفاء التتبع" else "Clear Tracker")
                }
            }
        }
    }
}

@Composable
private fun CartItemRowCard(
    item: CartItem,
    isAr: Boolean,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Image(
                painter = painterResource(id = item.imageRes),
                contentDescription = if (isAr) item.titleAr else item.titleEn,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(14.dp)),
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isAr) item.titleAr else item.titleEn,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = if (isAr) item.subtitleAr else item.subtitleEn,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isAr) "${item.unitPriceEgp * item.quantity} ج.م" else "${item.unitPriceEgp * item.quantity} EGP",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
            ) {
                IconButton(
                    onClick = onDecrement,
                    modifier = Modifier.size(38.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = if (isAr) "إنقاص" else "Decrease",
                        modifier = Modifier.size(18.dp),
                    )
                }
                Text(
                    text = "${item.quantity}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 4.dp),
                )
                IconButton(
                    onClick = onIncrement,
                    modifier = Modifier.size(38.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = if (isAr) "زيادة" else "Increase",
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun ModeToggleButton(
    selected: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Row(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp),
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    highlight: Boolean = false,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = if (highlight) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onBackground,
            fontWeight = if (highlight) FontWeight.Bold else FontWeight.Normal,
        )
    }
}
