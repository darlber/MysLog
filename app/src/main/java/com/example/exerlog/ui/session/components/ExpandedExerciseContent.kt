package com.example.exerlog.ui.session.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.exerlog.core.TipoSet
import com.example.exerlog.db.entities.GymSet
import com.example.exerlog.ui.session.SessionEvent
import com.example.exerlog.utils.Event


@Composable
fun ExpandedExerciseContent(
    sets: List<GymSet>,
    onEvent: (Event) -> Unit,
    onSetDeleted: (GymSet) -> Unit,
    onSetCreated: () -> Unit
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        sets.forEach { set ->
            val localFocusManager = LocalFocusManager.current
            val reps = set.reps ?: ""
            val weight = set.weight ?: ""
            key(set.setId) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .padding(bottom = 4.dp, start = 6.dp, end = 8.dp)
                        .fillMaxWidth()
                        .clickable { }
                ) {
                    IconButton(
                        onClick = { onSetDeleted(set) },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Delete Set",
                            tint = LocalContentColor.current.copy(alpha = 0.75f)
                        )
                    }
                    InputField(
                        label = "reps",
                        initialValue = reps.toString(),
                        onValueChange = {
                            val tfv = it.text.trim().toIntOrNull()
                            if (tfv != null) {
                                onEvent(SessionEvent.SetChanged(set.copy(reps = tfv)))
                                true
                            } else {
                                false
                            }
                        },
                        keyboardActions = KeyboardActions(
                            onNext = { localFocusManager.moveFocus(FocusDirection.Next) }
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        autoRequestFocus = true
                    )
                    InputField(
                        label = "kg",
                        initialValue = weight.toString(),
                        onValueChange = {
                            val tfv = it.text.trim().toFloatOrNull()
                            if (tfv != null) {
                                onEvent(SessionEvent.SetChanged(set.copy(weight = tfv)))
                                true
                            } else {
                                false
                            }
                        },
                        keyboardActions = KeyboardActions(
                            onDone = {
                                localFocusManager.moveFocus(FocusDirection.Next)
                                localFocusManager.clearFocus()
                            }
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        )
                    )
                    Surface(
                        onClick = {
                            onEvent(SessionEvent.SetChanged(set.copy(tipoSet = TipoSet.next(set.tipoSet))))
                        },
                        color = setTypeColor(set.tipoSet, MaterialTheme.colorScheme),
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier
                            .defaultMinSize(minWidth = 100.dp)
                            .padding(start = 12.dp)
                    ) {
                        Text(
                            text = context.getString(set.tipoSet),
                            modifier = Modifier.padding(6.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
            IconButton(
                onClick = { onSetCreated() }
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add new set")
            }
    }
}

fun setTypeColor(tipoSet: Int, colorScheme: ColorScheme): Color {
    return when (tipoSet) {
        TipoSet.WARMUP -> Color(0xFF7A7272)
        TipoSet.EASY -> Color(0xFF6A9E44)
        TipoSet.NORMAL -> colorScheme.primary
        TipoSet.HARD -> Color(0xFFB84733)
        TipoSet.DROP -> Color(0x990A2CD0)
        else -> Color.White
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewExpandedExerciseContent() {
    val sampleSets = listOf(
        GymSet(
            setId = 1,
            parentSessionExerciseId = 1L,
            reps = 8,
            weight = 50f,
            tipoSet = TipoSet.WARMUP
        ),
        GymSet(
            setId = 2,
            parentSessionExerciseId = 1L,
            reps = 10,
            weight = 70f,
            tipoSet = TipoSet.NORMAL
        ),
        GymSet(
            setId = 3,
            parentSessionExerciseId = 1L,
            reps = 6,
            weight = 90f,
            tipoSet = TipoSet.HARD
        )
    )

    ExpandedExerciseContent(
        sets = sampleSets,
        onEvent = {},
        onSetDeleted = {},
        onSetCreated = {}
    )
}
