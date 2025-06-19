package com.cibertec.t2_salcedo

import android.app.Application
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.cibertec.t2_salcedo.Persona
import com.cibertec.t2_salcedo.Districts
import com.cibertec.t2_salcedo.ConfirmAlertDialog
import com.cibertec.t2_salcedo.PersonViewModel
import com.cibertec.t2_salcedo.SimpleAlertDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonEntryScreen(navController: NavController, personId: Int? = null, viewModel: PersonViewModel = viewModel(factory = PersonViewModel.PersonViewModelFactory(LocalContext.current.applicationContext as Application))) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var fullName by remember { mutableStateOf("") }
    var documentId by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var selectedDistrict by remember { mutableStateOf(Districts[0]) }
    val civilStatusOptions = listOf("Soltero(a)", "Casado(a)")
    var selectedCivilStatus by remember { mutableStateOf<String?>(null) }

    var showProgressBar by remember { mutableStateOf(false) }
    var isEditMode by remember { mutableStateOf(false) }

    // Estados para controlar la visibilidad de los diálogos de Compose
    var showDocumentIdErrorDialog by remember { mutableStateOf(false) }
    var showConfirmSaveDialog by remember { mutableStateOf(false) }

    val districts = remember { Districts }

    LaunchedEffect(personId) {
        if (personId != null && personId != 0) {
            isEditMode = true
            val person = viewModel.getPersonById(personId)
            person?.let {
                fullName = it.fullName
                documentId = it.documentId
                phone = it.celular
                address = it.direccion
                selectedDistrict = it.districto
                selectedCivilStatus = it.civilStatus
            }
        }
    }

    // Region de los Diálogos de Compose
    if (showDocumentIdErrorDialog) {
        SimpleAlertDialog(
            title = "Error de Validación",
            message = "El DNI ingresado ya existe.",
            onDismissRequest = { showDocumentIdErrorDialog = false }
        )
    }

    if (showConfirmSaveDialog) {
        ConfirmAlertDialog(
            title = "Confirmar Grabación",
            message = "¿Está seguro de grabar los datos?",
            onConfirm = {
                showConfirmSaveDialog = false
                scope.launch {
                    showProgressBar = true
                    delay(5000)

                    val person = Persona(
                        id = personId ?: 0,
                        fullName = fullName,
                        documentId = documentId,
                        celular = phone,
                        direccion = address,
                        districto = selectedDistrict,
                        civilStatus = selectedCivilStatus
                    )

                    if (isEditMode) {
                        viewModel.updatePerson(person)
                        Toast.makeText(context, "Persona actualizada exitosamente", Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.insertPerson(person)
                        Toast.makeText(context, "Persona registrada exitosamente", Toast.LENGTH_SHORT).show()
                    }

                    showProgressBar = false
                    navController.popBackStack()
                }
            },
            onDismiss = { showConfirmSaveDialog = false }
        )
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "Editar Persona" else "Registrar Nueva Persona") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Nombre Completo") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = fullName.isBlank() && showProgressBar
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = documentId,
                    onValueChange = { if (!isEditMode) documentId = it }, // DNI no editable
                    label = { Text("Documento de Identidad (DNI)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = isEditMode, // DNI no editable en modo edición
                    isError = documentId.isBlank() && showProgressBar
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Teléfono") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                    isError = phone.isBlank() && showProgressBar
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Dirección") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = address.isBlank() && showProgressBar
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Spinner para Distritos
                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedDistrict,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Distrito") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        districts.forEach { district ->
                            DropdownMenuItem(
                                text = { Text(district) },
                                onClick = {
                                    selectedDistrict = district
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))


                Text(
                    text = "Estado Civil (Opcional):",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectableGroup(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    civilStatusOptions.forEach { text ->
                        Row(
                            Modifier
                                .selectable(
                                    selected = (text == selectedCivilStatus),
                                    onClick = { selectedCivilStatus = text },
                                    role = Role.RadioButton
                                )
                                .padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (text == selectedCivilStatus),
                                onClick = null
                            )
                            Text(text = text)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                AnimatedVisibility(visible = showProgressBar) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Button(
                        onClick = { navController.popBackStack() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("Cancelar")
                    }
                    Button(
                        onClick = {

                            val missingFields = mutableListOf<String>()
                            if (fullName.isBlank()) missingFields.add("Nombre Completo")
                            if (documentId.isBlank()) missingFields.add("Documento de Identidad")
                            if (phone.isBlank()) missingFields.add("Teléfono")
                            if (address.isBlank()) missingFields.add("Dirección")

                            if (missingFields.isNotEmpty()) {
                                Toast.makeText(context, "Faltan campos: ${missingFields.joinToString(", ")}", Toast.LENGTH_LONG).show()
                                return@Button
                            }

                            scope.launch {
                                // Validar DNI único
                                val isUnique = viewModel.isDocumentIdUnique(documentId, personId)
                                if (!isUnique) {
                                    showDocumentIdErrorDialog = true
                                    return@launch
                                }

                                // Mensaje de confirmación
                                showConfirmSaveDialog = true

                            }
                        }
                    ) {
                        Text(if (isEditMode) "Actualizar" else "Grabar")
                    }
                }
            }
        }
    )
}