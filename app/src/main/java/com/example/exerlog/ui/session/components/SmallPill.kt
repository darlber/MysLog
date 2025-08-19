package com.example.exerlog.ui.session.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalAbsoluteTonalElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.exerlog.ui.theme.Shapes

@Composable
fun SmallPill(
    text: String,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = backgroundColor,
        contentColor = contentColor,
        modifier = modifier
    ) {
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(4.dp)
        )
    }
}
@Preview
@Composable
fun SmallPillPreview() {
    SmallPill(
        text = "Small Pill",
        modifier = Modifier.padding(8.dp)
    )
}