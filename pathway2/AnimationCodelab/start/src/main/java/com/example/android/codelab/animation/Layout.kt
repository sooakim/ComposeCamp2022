package com.example.android.codelab.animation

import androidx.annotation.FloatRange
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.tooling.preview.Preview
import com.example.android.codelab.animation.ui.AnimationCodelabTheme

@Composable
fun BottomNavItem(
    icon: @Composable BoxScope.() -> Unit,
    text: @Composable BoxScope.() -> Unit,
    isSelected: Boolean = false,
) {
    val animationProgress by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0f
    )

    BaseNavItem(
        icon,
        text,
        animationProgress
    )
}

@Composable
fun BaseNavItem(
    icon: @Composable BoxScope.() -> Unit,
    text: @Composable BoxScope.() -> Unit,
    @FloatRange(from = 0.0, to = 1.0) animationProgress: Float
) {
    Layout(
        content = {
            Box(
                modifier = Modifier.layoutId("icon"),
                content = icon
            )
            Box(
                modifier = Modifier.layoutId("text"),
                content = text
            )
        }
    ) { measureables, constraints ->
        val iconPlaceable = measureables.first { it.layoutId == "icon" }.measure(constraints)
        val textPlaceable = measureables.first { it.layoutId == "text" }.measure(constraints)

        measure(
            textPlaceable,
            iconPlaceable,
            constraints.maxWidth,
            constraints.maxHeight,
            animationProgress
        )
    }
}

fun MeasureScope.measure(
    textPlaceable: Placeable,
    iconPlaceable: Placeable,
    width: Int,
    height: Int,
    @FloatRange(from = 0.0, to = 1.0) animationProgress: Float
): MeasureResult {
    val iconY = (height - iconPlaceable.height) / 2
    val textY = (height - textPlaceable.height) / 2
    val textWidth = textPlaceable.width * animationProgress
    val iconX = (width - textWidth - iconPlaceable.width) / 2
    val textX = iconX + iconPlaceable.width

    return layout(width, height) {
        iconPlaceable.placeRelative(iconX.toInt(), iconY)
        if (animationProgress != 0f) {
            textPlaceable.placeRelative(textX.toInt(), textY)
        }
    }
}

@Composable
fun VerticalGrid(
    modifier: Modifier = Modifier,
    columns: Int = 2,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measureables, constraints ->
        val itemWidth = constraints.maxWidth / columns
        val itemConstraints = constraints.copy(
            minWidth = itemWidth,
            maxWidth = itemWidth
        )
        val placeables = measureables.map {
            it.measure(itemConstraints)
        }
        val width = constraints.maxWidth
        val heights = placeables.windowed(size = columns, step = columns, partialWindows = true) {
            it.maxOf { it.height }
        }
        val height = heights.sumOf { it }
        layout(width, height) {
            var currentColumn = 0
            var y = 0
            placeables.forEachIndexed { index, placeable ->
                val row = index % columns
                val column = index / columns
                val x = row * placeable.width
                if (column != currentColumn) {
                    currentColumn = column
                    y += heights[column - 1]
                }
                placeable.placeRelative(x = x, y = y)
            }
        }
    }
}

@Composable
fun CustomColumn(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measureables, constraints ->
        val placeables = measureables.map { it.measure(constraints) }
        val width = placeables.maxOf { it.width }
        val height = placeables.sumOf { it.height }
        layout(width, height) {
            var y = 0
            placeables.forEach { placeable ->
                placeable.placeRelative(x = 0, y = y)
                y += placeable.height
            }
        }
    }
}

@Preview
@Composable
fun BottomNavItemPreview() {
    AnimationCodelabTheme {
        Surface {
            var isSelected by remember { mutableStateOf(false) }
            Button(onClick = {
                isSelected = !isSelected
            }) {
                Text("Toggle it!")
            }
            BottomNavItem(
                icon = {
                    Icon(
                        Icons.Default.List,
                        contentDescription = null
                    )
                },
                text = {
                    Text("List")
                },
                isSelected = isSelected
            )
        }
    }
}

@Preview
@Composable
fun VerticalGridPreview() {
    AnimationCodelabTheme {
        VerticalGrid {
            Text("123412d3123")
            Text("123123123")
            Text("123123123")
            Text("123123123123")
            Text("123412d3123")
            Text("123123123")
            Text("123412d3123")
        }
    }
}

@Preview
@Composable
fun CustomColumnPreview() {
    AnimationCodelabTheme {
        CustomColumn {
            Text("1234123123")
            Text("123123123")
            Text("123123123")
            Text("123123123123")
        }
    }
}