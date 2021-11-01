package com.example.myapplication.presentation.components


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

@Composable
fun IconToggleButtonGroup(
    selectedIndex: Int,
    itemCount: Int,
    content: @Composable (Int) -> Unit
) {
    val toggleCache = remember { ToggleDrawingCache() }
    Row(
        modifier = Modifier
            .drawToggleButtonFrame(
                shape = MaterialTheme.shapes.small,
                // @color/mtrl_btn_stroke_color_selector
                borderColor = MaterialTheme.colors.onSurface.copy(alpha = 0.12f),
                checkedBorderColor = MaterialTheme.colors.primary.copy(alpha = 1f),
                // @color/mtrl_btn_text_btn_bg_color_selector
                backgroundColor = MaterialTheme.colors.primary.copy(alpha = 0.08f),
                selectedIndex = selectedIndex,
                itemCount = itemCount,
                drawingCache = toggleCache
            )
            .clip(MaterialTheme.shapes.small)
    ) {
        for (i in 0 until itemCount) {
            content(i)
        }
    }
}

private class ToggleDrawingCache(
    val path: Path = Path(),
)

private fun Modifier.drawToggleButtonFrame(
    shape: CornerBasedShape,
    borderColor: Color,
    checkedBorderColor: Color,
    backgroundColor: Color,
    selectedIndex: Int,
    itemCount: Int,
    drawingCache: ToggleDrawingCache,
): Modifier = this.drawWithContent {

    val strokeWidth = 1.dp.toPx()
    val halfStrokeWidth = strokeWidth / 2

    val buttonWidth = size.width / itemCount

    val density = this
    fun CornerSize.toCornerRadius(): CornerRadius {
        return CornerRadius(toPx(Size(buttonWidth, size.height), density))
    }

    val topStartRadius = shape.topStart.toCornerRadius()
    val topEndRadius = shape.topEnd.toCornerRadius()
    val bottomStartRadius = shape.bottomStart.toCornerRadius()
    val bottomEndRadius = shape.bottomEnd.toCornerRadius()

    val topLeftCornerRadius = when (layoutDirection) {
        LayoutDirection.Ltr -> topStartRadius
        LayoutDirection.Rtl -> topEndRadius
    }
    val topRightCornerRadius = when (layoutDirection) {
        LayoutDirection.Ltr -> topEndRadius
        LayoutDirection.Rtl -> topStartRadius
    }
    val bottomLeftCornerRadius = when (layoutDirection) {
        LayoutDirection.Ltr -> bottomStartRadius
        LayoutDirection.Rtl -> bottomEndRadius
    }
    val bottomRightCornerRadius = when (layoutDirection) {
        LayoutDirection.Ltr -> bottomEndRadius
        LayoutDirection.Rtl -> bottomStartRadius
    }

    val path = drawingCache.path

    // draw unchecked border
    drawPath(
        path = path.apply {
            reset()
            addRoundRect(
                RoundRect(
                    left = halfStrokeWidth,
                    top = halfStrokeWidth,
                    right = size.width - halfStrokeWidth,
                    bottom = size.height - halfStrokeWidth,
                    topLeftCornerRadius = topLeftCornerRadius,
                    bottomLeftCornerRadius = bottomLeftCornerRadius,
                    topRightCornerRadius = topRightCornerRadius,
                    bottomRightCornerRadius = bottomRightCornerRadius,
                )
            )
            for (i in 1 until itemCount) {
                val x = i * buttonWidth
                moveTo(x, strokeWidth)
                lineTo(x, size.height - strokeWidth)
            }
        },
        color = borderColor,
        style = Stroke(strokeWidth),
    )

    val isLeftSide = when (layoutDirection) {
        LayoutDirection.Ltr -> selectedIndex == 0
        LayoutDirection.Rtl -> selectedIndex == itemCount - 1
    }
    val isRightSide = when (layoutDirection) {
        LayoutDirection.Ltr -> selectedIndex == itemCount - 1
        LayoutDirection.Rtl -> selectedIndex == 0
    }

    val checkedLeft = when (layoutDirection) {
        LayoutDirection.Ltr -> selectedIndex * buttonWidth
        LayoutDirection.Rtl -> (itemCount - 1 - selectedIndex) * buttonWidth
    }
    val checkedRight = checkedLeft + buttonWidth
    val checkedTop = 0f
    val checkedBottom = size.height

    val checkedTopLeftCornerRadius = if (isLeftSide) topLeftCornerRadius else CornerRadius.Zero
    val checkedBottomLeftCornerRadius =
        if (isLeftSide) bottomLeftCornerRadius else CornerRadius.Zero
    val checkedTopRightCornerRadius = if (isRightSide) topRightCornerRadius else CornerRadius.Zero
    val checkedBottomRightCornerRadius =
        if (isRightSide) bottomRightCornerRadius else CornerRadius.Zero

    // draw checked background
    drawPath(
        path = path.apply {
            reset()
            addRoundRect(
                RoundRect(
                    left = checkedLeft,
                    top = checkedTop,
                    right = checkedRight,
                    bottom = checkedBottom,
                    topLeftCornerRadius = checkedTopLeftCornerRadius,
                    bottomLeftCornerRadius = checkedBottomLeftCornerRadius,
                    topRightCornerRadius = checkedTopRightCornerRadius,
                    bottomRightCornerRadius = checkedBottomRightCornerRadius,
                )
            )
        },
        color = backgroundColor,
        style = Fill,
    )

    // draw checked border
    drawPath(
        path = path.apply {
            reset()
            addRoundRect(
                RoundRect(
                    left = checkedLeft + if (isLeftSide) halfStrokeWidth else 0f,
                    top = checkedTop + halfStrokeWidth,
                    right = checkedRight + if (isRightSide) -halfStrokeWidth else 0f,
                    bottom = checkedBottom - halfStrokeWidth,
                    topLeftCornerRadius = checkedTopLeftCornerRadius,
                    bottomLeftCornerRadius = checkedBottomLeftCornerRadius,
                    topRightCornerRadius = checkedTopRightCornerRadius,
                    bottomRightCornerRadius = checkedBottomRightCornerRadius,
                )
            )
        },
        color = checkedBorderColor,
        style = Stroke(strokeWidth),
    )

    drawContent()
}

@Composable
fun contentColor(enabled: Boolean, checked: Boolean): Color {
    // @color/mtrl_text_btn_text_color_selector
    return if (enabled) {
        if (checked) {
            MaterialTheme.colors.primary.copy(alpha = 1f)
        } else {
            MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
        }
    } else {
        MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.disabled)
    }
}

@Composable
fun IconToggleButton(
    imageVector: ImageVector,
    contentDescription: String?,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true
) {
    CompositionLocalProvider(
        LocalContentColor provides contentColor(enabled = enabled, checked = checked),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .toggleable(
                    value = checked,
                    onValueChange = onCheckedChange,
                    role = Role.RadioButton,
                )
                .size(48.dp)
        ) {
            Icon(
                imageVector = imageVector,
                contentDescription = contentDescription,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

