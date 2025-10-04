package com.cmpmodular.socialSignIn

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
internal fun GoogleButtonUI(
    backgroundColor: Color,
    drawableResource: DrawableResource,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Image(
        modifier = Modifier.then(modifier)
            .size(60.dp)
            .clip(CircleShape)
            .clickable {
                onClick.invoke()
            }
            .background(backgroundColor)
            .padding(16.dp),
        painter = painterResource(drawableResource),
        contentDescription = null
    )
}

@Composable
internal fun AppleButtonUI(
    backgroundColor: Color,
    drawableResource: DrawableResource,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Image(
        modifier = Modifier.then(modifier)
            .size(60.dp)
            .clip(CircleShape)
            .clickable {
                onClick.invoke()
            }
            .background(backgroundColor)
            .padding(16.dp),
        painter = painterResource(drawableResource),
        contentDescription = null
    )
}