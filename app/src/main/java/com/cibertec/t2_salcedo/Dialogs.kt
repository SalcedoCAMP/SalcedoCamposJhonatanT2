package com.cibertec.t2_salcedo

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.window.DialogProperties
import com.cibertec.t2_salcedo.Persona
@Composable
fun SimpleAlertDialog(title: String, message: String, onDismissRequest: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = title) },
        text = { Text(text = message) },
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text("OK")
            }
        },
        properties = DialogProperties(dismissOnClickOutside = true, dismissOnBackPress = true)
    )
}


@Composable
fun ConfirmAlertDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title) },
        text = { Text(text = message) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Sí")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("No")
            }
        },
        properties = DialogProperties(dismissOnClickOutside = false, dismissOnBackPress = false)
    )
}


@Composable
fun PersonDetailsDialog(person: Persona, onDismissRequest: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = "Detalles de la Persona") },
        text = {
            Text(
                text = """
                    Nombre: ${person.fullName}
                    DNI: ${person.documentId}
                    Teléfono: ${person.celular}
                    Dirección: ${person.direccion}
                    Distrito: ${person.districto}
                    Estado Civil: ${person.civilStatus ?: "No especificado"}
                """.trimIndent()
            )
        },
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text("OK")
            }
        },
        properties = DialogProperties(dismissOnClickOutside = true, dismissOnBackPress = true)
    )
}