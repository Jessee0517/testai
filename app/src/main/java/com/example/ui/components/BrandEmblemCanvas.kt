package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

/**
 * Procedural vector emblem renderer for primary logos and secondary marks.
 */
@Composable
fun BrandEmblemCanvas(
    modifier: Modifier = Modifier,
    style: String = "Biomorphic",
    letter: String = "B",
    primaryColor: Color = Color(0xFF6366F1),
    accentColor: Color = Color(0xFFF43F5E),
    size: Dp = 80.dp
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasW = this.size.width
            val canvasH = this.size.height
            val center = Offset(canvasW / 2f, canvasH / 2f)
            val radius = (canvasW.coerceAtMost(canvasH) / 2f) * 0.85f

            when {
                style.contains("Seal", ignoreCase = true) || style.contains("Circular", ignoreCase = true) -> {
                    // Outer decorative dashed or solid circle
                    drawCircle(
                        brush = Brush.linearGradient(listOf(primaryColor, accentColor)),
                        radius = radius,
                        center = center,
                        style = Stroke(width = canvasW * 0.04f)
                    )
                    // Inner ring
                    drawCircle(
                        color = primaryColor.copy(alpha = 0.35f),
                        radius = radius * 0.8f,
                        center = center,
                        style = Stroke(width = canvasW * 0.02f)
                    )
                    // Inner star rays / emblem
                    val rayCount = 8
                    for (i in 0 until rayCount) {
                        val angle = (i * 360f / rayCount) * (Math.PI / 180f)
                        val startX = center.x + (radius * 0.45f) * cos(angle).toFloat()
                        val startY = center.y + (radius * 0.45f) * sin(angle).toFloat()
                        val endX = center.x + (radius * 0.72f) * cos(angle).toFloat()
                        val endY = center.y + (radius * 0.72f) * sin(angle).toFloat()
                        drawLine(
                            brush = Brush.linearGradient(listOf(accentColor, primaryColor)),
                            start = Offset(startX, startY),
                            end = Offset(endX, endY),
                            strokeWidth = canvasW * 0.035f,
                            cap = StrokeCap.Round
                        )
                    }
                    // Center core
                    drawCircle(
                        brush = Brush.radialGradient(listOf(accentColor, primaryColor)),
                        radius = radius * 0.32f,
                        center = center
                    )
                }

                style.contains("Monogram", ignoreCase = true) || style.contains("Favicon", ignoreCase = true) -> {
                    // Modern squircle background container
                    val cornerR = canvasW * 0.22f
                    drawRoundRect(
                        brush = Brush.linearGradient(listOf(primaryColor, accentColor)),
                        topLeft = Offset(canvasW * 0.08f, canvasH * 0.08f),
                        size = Size(canvasW * 0.84f, canvasH * 0.84f),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerR, cornerR)
                    )
                    // Diamond geometric intersection inside
                    val path = Path().apply {
                        moveTo(center.x, canvasH * 0.22f)
                        lineTo(canvasW * 0.78f, center.y)
                        lineTo(center.x, canvasH * 0.78f)
                        lineTo(canvasW * 0.22f, center.y)
                        close()
                    }
                    drawPath(
                        path = path,
                        color = Color.White.copy(alpha = 0.28f),
                        style = Stroke(width = canvasW * 0.04f, cap = StrokeCap.Round)
                    )
                    // Inner bright core
                    drawCircle(
                        color = Color.White,
                        radius = canvasW * 0.12f,
                        center = center
                    )
                }

                style.contains("Badge", ignoreCase = true) || style.contains("Horizontal", ignoreCase = true) -> {
                    // Architectural Hexagonal mark
                    val hexPath = Path()
                    val sides = 6
                    for (i in 0 until sides) {
                        val angle = (i * 60 - 30) * (Math.PI / 180.0)
                        val x = center.x + (radius * cos(angle)).toFloat()
                        val y = center.y + (radius * sin(angle)).toFloat()
                        if (i == 0) hexPath.moveTo(x, y) else hexPath.lineTo(x, y)
                    }
                    hexPath.close()

                    drawPath(
                        path = hexPath,
                        brush = Brush.linearGradient(listOf(primaryColor, accentColor)),
                        style = Stroke(width = canvasW * 0.06f, cap = StrokeCap.Round)
                    )

                    // Diagonal cross struts
                    drawLine(
                        brush = Brush.linearGradient(listOf(accentColor, primaryColor)),
                        start = Offset(center.x - radius * 0.55f, center.y),
                        end = Offset(center.x + radius * 0.55f, center.y),
                        strokeWidth = canvasW * 0.045f,
                        cap = StrokeCap.Round
                    )
                    drawCircle(
                        brush = Brush.radialGradient(listOf(accentColor, primaryColor)),
                        radius = radius * 0.22f,
                        center = center
                    )
                }

                else -> {
                    // Default Biomorphic / Modern Interlocking Crest
                    // Outer glowing curved petals
                    rotate(45f, pivot = center) {
                        drawRoundRect(
                            brush = Brush.linearGradient(listOf(primaryColor, accentColor)),
                            topLeft = Offset(canvasW * 0.18f, canvasH * 0.18f),
                            size = Size(canvasW * 0.64f, canvasH * 0.64f),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(canvasW * 0.18f, canvasW * 0.18f),
                            style = Stroke(width = canvasW * 0.05f)
                        )
                    }
                    rotate(15f, pivot = center) {
                        val innerLeaf = Path().apply {
                            moveTo(center.x, canvasH * 0.2f)
                            cubicTo(
                                canvasW * 0.8f, canvasH * 0.35f,
                                canvasW * 0.8f, canvasH * 0.65f,
                                center.x, canvasH * 0.8f
                            )
                            cubicTo(
                                canvasW * 0.2f, canvasH * 0.65f,
                                canvasW * 0.2f, canvasH * 0.35f,
                                center.x, canvasH * 0.2f
                            )
                            close()
                        }
                        drawPath(
                            path = innerLeaf,
                            brush = Brush.linearGradient(listOf(accentColor, primaryColor))
                        )
                    }
                    // Center focal star
                    drawCircle(
                        color = Color.White,
                        radius = canvasW * 0.09f,
                        center = center
                    )
                }
            }
        }
    }
}
