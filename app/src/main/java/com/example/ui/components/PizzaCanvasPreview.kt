package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import com.example.model.CrustType
import com.example.model.PizzaSize
import com.example.model.SauceType
import com.example.model.ToppingItem
import com.example.model.ToppingVisualType
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun PizzaCanvasPreview(
    size: PizzaSize,
    crust: CrustType,
    sauce: SauceType,
    extraMozzarella: Boolean,
    toppings: List<ToppingItem>,
    modifier: Modifier = Modifier,
) {
    val targetScale = when (size) {
        PizzaSize.SMALL -> 0.78f
        PizzaSize.MEDIUM -> 0.88f
        PizzaSize.FAMILY -> 0.98f
    }
    val animatedScale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = spring(dampingRatio = 0.72f, stiffness = 320f),
        label = "pizza_size_scale",
    )

    Box(
        modifier = modifier.aspectRatio(1f),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val minDim = this.size.minDimension
            val center = Offset(this.size.width / 2f, this.size.height / 2f)

            // Warm wooden pizza peel shadow + board
            val boardRadius = minDim * 0.46f
            drawCircle(
                color = Color(0x33000000),
                radius = boardRadius * 1.03f,
                center = center + Offset(0f, minDim * 0.02f),
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFC58C58), Color(0xFFA06739), Color(0xFF7D4B25)),
                    center = center,
                    radius = boardRadius,
                ),
                radius = boardRadius,
                center = center,
            )
            // Subtle wood grain rings
            drawCircle(
                color = Color(0x284D2B10),
                radius = boardRadius * 0.92f,
                center = center,
                style = Stroke(width = 2.5f),
            )

            // Pizza outer radius scaled by chosen size
            val pizzaRadius = minDim * 0.39f * animatedScale
            val crustThickness = when (crust) {
                CrustType.NEAPOLITAN -> pizzaRadius * 0.16f
                CrustType.THIN_ROMAN -> pizzaRadius * 0.07f
                CrustType.CHEESE_STUFFED -> pizzaRadius * 0.19f
            }

            // Pizza drop shadow
            drawCircle(
                color = Color(0x44000000),
                radius = pizzaRadius * 1.01f,
                center = center + Offset(0f, minDim * 0.015f),
            )

            // Golden crust base
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFF3C685),
                        Color(0xFFE0A458),
                        Color(0xFFC67E32),
                    ),
                    center = center,
                    radius = pizzaRadius,
                ),
                radius = pizzaRadius,
                center = center,
            )

            // Cheese-stuffed crust ring highlight
            if (crust == CrustType.CHEESE_STUFFED) {
                drawCircle(
                    color = Color(0xFFFFE59D),
                    radius = pizzaRadius - crustThickness * 0.45f,
                    center = center,
                    style = Stroke(width = crustThickness * 0.32f),
                )
            }

            // Wood-fired leopard char spots around the cornicione
            val charSpots = 14
            for (i in 0 until charSpots) {
                val angle = (i * (360f / charSpots) + 12f) * (PI / 180.0)
                val dist = pizzaRadius - crustThickness * 0.42f
                val spotCenter = Offset(
                    x = center.x + (cos(angle) * dist).toFloat(),
                    y = center.y + (sin(angle) * dist).toFloat(),
                )
                drawCircle(
                    color = Color(0x994A2511),
                    radius = (if (i % 2 == 0) 0.032f else 0.02f) * pizzaRadius,
                    center = spotCenter,
                )
            }

            // Sauce layer
            val sauceRadius = pizzaRadius - crustThickness
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        sauce.color.copy(alpha = 0.96f),
                        sauce.color,
                        sauce.color.copy(alpha = 0.88f),
                    ),
                    center = center,
                    radius = sauceRadius,
                ),
                radius = sauceRadius,
                center = center,
            )

            // Melted mozzarella base pools
            val cheeseCount = if (extraMozzarella) 18 else 11
            val cheeseBaseRadius = if (extraMozzarella) sauceRadius * 0.93f else sauceRadius * 0.86f
            drawCircle(
                color = Color(0x66FFF4D6),
                radius = cheeseBaseRadius,
                center = center,
            )
            for (i in 0 until cheeseCount) {
                val angle = (i * 137.5f) * (PI / 180.0)
                val radial = sauceRadius * (0.18f + 0.65f * ((i * 7 % 10) / 10f))
                val pos = Offset(
                    x = center.x + (cos(angle) * radial).toFloat(),
                    y = center.y + (sin(angle) * radial).toFloat(),
                )
                drawCircle(
                    color = Color(0xEEFFF7E2),
                    radius = sauceRadius * (if (extraMozzarella) 0.16f else 0.12f),
                    center = pos,
                )
            }

            // Toppings rendered in deterministic golden-angle spiral so nothing overlaps awkwardly
            toppings.forEachIndexed { toppingIdx, topping ->
                drawToppingLayer(
                    type = topping.visualType,
                    center = center,
                    sauceRadius = sauceRadius,
                    seedOffset = toppingIdx * 23f,
                )
            }
        }
    }
}

private fun DrawScope.drawToppingLayer(
    type: ToppingVisualType,
    center: Offset,
    sauceRadius: Float,
    seedOffset: Float,
) {
    val itemCount = when (type) {
        ToppingVisualType.PEPPERONI -> 11
        ToppingVisualType.MUSHROOM -> 9
        ToppingVisualType.OLIVES -> 12
        ToppingVisualType.BASIL -> 7
        ToppingVisualType.BELL_PEPPER -> 10
        ToppingVisualType.RED_ONION -> 9
        ToppingVisualType.JALAPENO -> 10
        ToppingVisualType.EXTRA_CHEDDAR -> 16
        ToppingVisualType.CHERRY_TOMATO -> 8
    }

    for (i in 0 until itemCount) {
        val angleDeg = i * 137.5f + seedOffset
        val angleRad = angleDeg * (PI / 180.0)
        val normDist = 0.18f + 0.66f * ((i + 0.5f) / itemCount)
        val pos = Offset(
            x = center.x + (cos(angleRad) * sauceRadius * normDist).toFloat(),
            y = center.y + (sin(angleRad) * sauceRadius * normDist).toFloat(),
        )

        when (type) {
            ToppingVisualType.PEPPERONI -> {
                val r = sauceRadius * 0.135f
                drawCircle(color = Color(0xFF9C1B15), radius = r, center = pos)
                drawCircle(color = Color(0xFFC42D23), radius = r * 0.85f, center = pos)
                drawCircle(color = Color(0x88F7B2A8), radius = r * 0.18f, center = pos + Offset(-r * 0.25f, -r * 0.2f))
                drawCircle(color = Color(0x88F7B2A8), radius = r * 0.14f, center = pos + Offset(r * 0.28f, r * 0.22f))
            }

            ToppingVisualType.MUSHROOM -> {
                val r = sauceRadius * 0.11f
                rotate(degrees = angleDeg % 360f, pivot = pos) {
                    // Mushroom cap
                    drawArc(
                        color = Color(0xFF7D6252),
                        startAngle = 180f,
                        sweepAngle = 180f,
                        useCenter = true,
                        topLeft = Offset(pos.x - r, pos.y - r),
                        size = Size(r * 2f, r * 1.6f),
                    )
                    // Stem
                    drawRect(
                        color = Color(0xFFD8C6B8),
                        topLeft = Offset(pos.x - r * 0.35f, pos.y - r * 0.2f),
                        size = Size(r * 0.7f, r * 0.85f),
                    )
                }
            }

            ToppingVisualType.OLIVES -> {
                val r = sauceRadius * 0.068f
                drawCircle(
                    color = Color(0xFF211C1E),
                    radius = r,
                    center = pos,
                    style = Stroke(width = r * 0.72f),
                )
            }

            ToppingVisualType.BASIL -> {
                val r = sauceRadius * 0.115f
                rotate(degrees = (angleDeg * 1.4f) % 360f, pivot = pos) {
                    val leafPath = Path().apply {
                        moveTo(pos.x, pos.y - r)
                        quadraticTo(pos.x + r * 0.9f, pos.y - r * 0.2f, pos.x, pos.y + r)
                        quadraticTo(pos.x - r * 0.9f, pos.y - r * 0.2f, pos.x, pos.y - r)
                        close()
                    }
                    drawPath(path = leafPath, color = Color(0xFF2B7A38))
                    drawLine(
                        color = Color(0xFF6BC075),
                        start = Offset(pos.x, pos.y - r * 0.7f),
                        end = Offset(pos.x, pos.y + r * 0.7f),
                        strokeWidth = 2f,
                    )
                }
            }

            ToppingVisualType.BELL_PEPPER -> {
                val len = sauceRadius * 0.16f
                val color = if (i % 2 == 0) Color(0xFF398C36) else Color(0xFFEBA824)
                rotate(degrees = angleDeg % 180f, pivot = pos) {
                    drawLine(
                        color = color,
                        start = Offset(pos.x - len * 0.5f, pos.y),
                        end = Offset(pos.x + len * 0.5f, pos.y),
                        strokeWidth = sauceRadius * 0.048f,
                        cap = StrokeCap.Round,
                    )
                }
            }

            ToppingVisualType.RED_ONION -> {
                val r = sauceRadius * 0.105f
                drawArc(
                    color = Color(0xFF9E3B75),
                    startAngle = (i * 40f) % 360f,
                    sweepAngle = 210f,
                    useCenter = false,
                    topLeft = Offset(pos.x - r, pos.y - r),
                    size = Size(r * 2f, r * 2f),
                    style = Stroke(width = sauceRadius * 0.025f, cap = StrokeCap.Round),
                )
            }

            ToppingVisualType.JALAPENO -> {
                val r = sauceRadius * 0.072f
                drawCircle(
                    color = Color(0xFF3A8E2B),
                    radius = r,
                    center = pos,
                    style = Stroke(width = r * 0.55f),
                )
                drawCircle(
                    color = Color(0xFFDDE5B6),
                    radius = r * 0.25f,
                    center = pos,
                )
            }

            ToppingVisualType.EXTRA_CHEDDAR -> {
                val w = sauceRadius * 0.12f
                rotate(degrees = angleDeg % 360f, pivot = pos) {
                    drawRect(
                        color = Color(0xFFFFB703),
                        topLeft = Offset(pos.x - w * 0.5f, pos.y - w * 0.22f),
                        size = Size(w, w * 0.35f),
                    )
                }
            }

            ToppingVisualType.CHERRY_TOMATO -> {
                val r = sauceRadius * 0.088f
                drawCircle(color = Color(0xFFD62828), radius = r, center = pos)
                drawCircle(color = Color(0xFFFF8FA3), radius = r * 0.45f, center = pos)
            }
        }
    }
}
