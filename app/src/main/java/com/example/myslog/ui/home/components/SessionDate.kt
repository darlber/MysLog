package com.example.myslog.ui.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import com.example.myslog.db.entities.Session
import java.time.format.TextStyle

@Composable
fun SessionDate(
    session: Session,
    modifier: Modifier = Modifier
) {
    // Obtener la configuración local para el locale
    // Esto es necesario para que el mes se muestre en el idioma correcto
    // Por ejemplo, si el usuario tiene configurado el idioma español, el mes se mostrará en español
    // Si no se especifica, se usa el locale por defecto del sistema
    // en caso d cambiar en ejecucion, se actualiza automaticamente
    val configuration = LocalConfiguration.current
    val locale = configuration.locales[0]

    val month by remember {
        //derivedStateOf sirve para recalcular el mes solo cuando la sesión cambia
        // Esto mejora el rendimiento al evitar cálculos innecesarios
        derivedStateOf {
            session.start.month.getDisplayName(TextStyle.SHORT, locale)
        }
    }
    val day by remember { derivedStateOf { session.start.dayOfMonth.toString() } }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        DateText(text = month)
        DateText(text = day)
    }
}

@Composable
fun DateText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.SemiBold
    )
}