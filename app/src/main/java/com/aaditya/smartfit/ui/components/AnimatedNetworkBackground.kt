package com.aaditya.smartfit.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntSize
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

private const val MOTION_SHADOW_ALPHA = 0.30f

private data class NetworkNode(
    val xFraction: Float,
    val yFraction: Float,
    val wanderRadius: Float,
    val speed: Float,
    val phase: Float,
    val isAccent: Boolean,
    val depth: Float
)

@Composable
fun AnimatedNetworkBackground(
    modifier: Modifier = Modifier,
    nodeCount: Int = 52
) {
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }
    val nodes = remember(canvasSize, nodeCount) {
        val seed = canvasSize.width * 31 + canvasSize.height
        val random = Random(seed)
        List(nodeCount) {
            NetworkNode(
                xFraction = random.nextFloat(),
                yFraction = random.nextFloat(),
                wanderRadius = random.nextFloat() * 44f + 18f,
                speed = random.nextFloat() * 1.2f + 0.8f,
                phase = random.nextFloat() * (2f * PI.toFloat()),
                isAccent = random.nextFloat() < 0.12f,
                depth = random.nextFloat() * 0.75f + 0.4f
            )
        }
    }

    val phase = rememberContinuousPhase(speed = 1.55f)
    val pulse = ((sin(phase * 2.4f) + 1f) * 0.5f)

    Canvas(modifier = modifier.fillMaxSize()) {
        canvasSize = IntSize(size.width.toInt(), size.height.toInt())

        drawRect(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFF010A0F),
                    Color(0xFF02151F),
                    Color(0xFF032534)
                ),
                start = Offset.Zero,
                end = Offset(size.width, size.height)
            )
        )

        // Subtle vignette to keep focus around center and match the dark edge style.
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0x1400E7FF),
                    Color(0x22000D14)
                ),
                center = Offset(size.width * 0.52f, size.height * 0.46f),
                radius = size.maxDimension * 0.82f
            ),
            radius = size.maxDimension
        )

        if (nodes.isEmpty()) return@Canvas

        val time = phase
        val driftX = cos(time * 0.7f) * (size.width * 0.015f)
        val driftY = sin(time * 0.55f) * (size.height * 0.012f)
        val points = nodes.map { node ->
            val baseX = node.xFraction * size.width
            val baseY = node.yFraction * size.height
            val offsetX = cos(time * node.speed + node.phase) * node.wanderRadius * node.depth
            val offsetY = sin(time * node.speed + node.phase) * node.wanderRadius * node.depth
            Offset(baseX + offsetX + driftX, baseY + offsetY + driftY)
        }

        val trailTime = time - 0.22f
        val trailDriftX = cos(trailTime * 0.7f) * (size.width * 0.015f)
        val trailDriftY = sin(trailTime * 0.55f) * (size.height * 0.012f)
        val trailPoints = nodes.map { node ->
            val baseX = node.xFraction * size.width
            val baseY = node.yFraction * size.height
            val offsetX = cos(trailTime * node.speed + node.phase) * node.wanderRadius * node.depth
            val offsetY = sin(trailTime * node.speed + node.phase) * node.wanderRadius * node.depth
            Offset(baseX + offsetX + trailDriftX, baseY + offsetY + trailDriftY)
        }

        val maxDistance = size.minDimension * 0.52f

        for (i in trailPoints.indices) {
            val from = trailPoints[i]
            val fromNode = nodes[i]
            for (j in i + 1 until trailPoints.size) {
                val to = trailPoints[j]
                val toNode = nodes[j]
                val dx = from.x - to.x
                val dy = from.y - to.y
                val distance = sqrt(dx * dx + dy * dy)
                if (distance < maxDistance) {
                    val baseAlpha = (1f - (distance / maxDistance)) * MOTION_SHADOW_ALPHA
                    val isRedPair = fromNode.isAccent || toNode.isAccent
                    drawLine(
                        color = if (isRedPair) {
                            Color(0xFFFF3B61).copy(alpha = baseAlpha * 0.65f)
                        } else {
                            Color(0xFF21E8FF).copy(alpha = baseAlpha)
                        },
                        start = from,
                        end = to,
                        strokeWidth = if (isRedPair) 1.2f else 1.35f
                    )
                }
            }
        }

        for (i in points.indices) {
            val from = points[i]
            val fromNode = nodes[i]
            for (j in i + 1 until points.size) {
                val to = points[j]
                val toNode = nodes[j]
                val dx = from.x - to.x
                val dy = from.y - to.y
                val distance = sqrt(dx * dx + dy * dy)
                if (distance < maxDistance) {
                    val baseAlpha = (1f - (distance / maxDistance)) * (0.42f + pulse * 0.2f)
                    val isRedPair = fromNode.isAccent || toNode.isAccent
                    val lineColor = if (isRedPair) {
                        Color(0xFFFF3B61).copy(alpha = baseAlpha * 0.58f)
                    } else {
                        Color(0xFF21E8FF).copy(alpha = baseAlpha)
                    }
                    drawLine(
                        color = lineColor,
                        start = from,
                        end = to,
                        strokeWidth = if (isRedPair) 1.6f else 1.9f
                    )
                }
            }
        }

        points.forEachIndexed { index, point ->
            val node = nodes[index]
            val coreColor = if (node.isAccent) Color(0xFFFF4569) else Color(0xFF53F2FF)
            val glowColor = if (node.isAccent) Color(0x66FF204A) else Color(0x4D53F2FF)
            val pulseScale = 0.95f + pulse * 0.22f
            val trailPoint = trailPoints[index]
            drawCircle(
                color = glowColor.copy(alpha = MOTION_SHADOW_ALPHA),
                radius = (11.8f + node.depth * 8f) * pulseScale,
                center = trailPoint
            )
            drawCircle(
                color = coreColor.copy(alpha = 0.9f),
                radius = (3.8f + node.depth * 1.2f) * pulseScale,
                center = point
            )
            drawCircle(
                color = glowColor,
                radius = (10f + node.depth * 7.5f) * pulseScale,
                center = point
            )
        }

        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(Color.Transparent, Color(0xAA00070B)),
                center = Offset(size.width * 0.5f, size.height * 0.48f),
                radius = size.maxDimension * 0.72f
            )
        )
    }
}

@Composable
private fun rememberContinuousPhase(speed: Float): Float {
    var phase by remember { mutableStateOf(0f) }
    LaunchedEffect(speed) {
        var lastFrameNanos = 0L
        while (true) {
            withFrameNanos { frameNanos ->
                if (lastFrameNanos != 0L) {
                    val deltaSeconds = (frameNanos - lastFrameNanos) / 1_000_000_000f
                    phase += deltaSeconds * speed
                    if (phase > (2f * PI.toFloat())) {
                        phase -= (2f * PI.toFloat())
                    }
                }
                lastFrameNanos = frameNanos
            }
        }
    }
    return phase
}



