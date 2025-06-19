package com.cibertec.t2_salcedo

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

import kotlinx.coroutines.launch

@Composable
fun PersonListScreen(navController: NavController, viewModel: PersonViewModel = viewModel(factory = PersonViewModel.PersonViewModelFactory(LocalContext.current.applicationContext as Application))) {
    val allPersons by viewModel.allPersons.collectAsState()
    val filteredPersons by viewModel.filteredPersons.collectAsState()
    val searchText by viewModel.searchText.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Estados de dialogs
    var showConfirmDeleteDialog by remember { mutableStateOf<Persona?>(null) }
    var showDetailsForPerson by remember { mutableStateOf<Persona?>(null) }

    LaunchedEffect(filteredPersons.size, allPersons.size) {

        if (filteredPersons.isNotEmpty() || allPersons.isNotEmpty() || searchText.isNotBlank()) {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = "Mostrando ${filteredPersons.size} de ${allPersons.size} registros",
                    actionLabel = "Cerrar",
                    duration = SnackbarDuration.Indefinite // Duración indefinida según rúbrica
                )
            }
        }
    }


    showConfirmDeleteDialog?.let { personToDelete ->
        ConfirmAlertDialog(
            title = "Confirmar Eliminación",
            message = "¿Está seguro de eliminar a ${personToDelete.fullName}?",
            onConfirm = {

                showConfirmDeleteDialog = null
                scope.launch {
                    viewModel.deletePerson(personToDelete)
                    snackbarHostState.showSnackbar("Persona eliminada: ${personToDelete.fullName}")
                }
            },
            onDismiss = {
                showConfirmDeleteDialog = null
            }
        )
    }

    showDetailsForPerson?.let { person ->
        PersonDetailsDialog(
            person = person,
            onDismissRequest = { showDetailsForPerson = null }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },

        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Padrón de Personas",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Gestione el registro de personas en el sistema",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Button(
                        onClick = { navController.navigate("person_entry") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(Icons.Filled.Add, "Registrar Persona")
                        Spacer(Modifier.width(8.dp))
                        Text("Registrar Persona")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = searchText,
                    onValueChange = { viewModel.onSearchTextChanged(it) },
                    label = { Text("Buscar por nombre o documento...") },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Buscar") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                Text(
                    text = "Mostrando ${filteredPersons.size} de ${allPersons.size} registros",
                    modifier = Modifier.padding(bottom = 8.dp),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )


                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Spacer(modifier = Modifier.width(24.dp + 8.dp))

                    Text(
                        text = "Nombre",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(0.4f)
                    )
                    Text(
                        text = "Documento",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(0.3f)
                    )
                    Spacer(modifier = Modifier.weight(0.3f))
                }
                Divider()

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(filteredPersons) { person ->
                        PersonListItem(
                            person = person,
                            onEditClick = {
                                navController.navigate("person_entry?personId=${person.id}")
                            },
                            onDeleteClick = {
                                showConfirmDeleteDialog = person
                            },
                            onDetailsClick = {
                                showDetailsForPerson = person
                            }
                        )
                    }
                }
            }
        }
    )
}


@Composable
fun PersonListItem(
    person: Persona,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onDetailsClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = "Icono de persona",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = person.fullName,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(0.4f)
            )
            Text(
                text = person.documentId,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(0.3f)
            )

            Row(
                modifier = Modifier.weight(0.3f),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = onDetailsClick,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(Icons.Filled.Info, contentDescription = "Ver Detalles")
                }
                Spacer(modifier = Modifier.width(9.dp))
                IconButton(
                    onClick = onEditClick,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(Icons.Filled.Edit, contentDescription = "Editar")
                }

                Spacer(modifier = Modifier.width(9.dp))

                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(Icons.Filled.Delete, contentDescription = "Eliminar")
                }
            }
        }
    }
}