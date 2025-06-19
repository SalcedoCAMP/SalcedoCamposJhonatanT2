package com.cibertec.t2_salcedo


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.cibertec.t2_salcedo.PersonaDao
import com.cibertec.t2_salcedo.Persona
import com.cibertec.t2_salcedo.AppDatabase

class PersonViewModel(application: Application, private val personDao: PersonaDao) : AndroidViewModel(application) {

    private val _allPersons = MutableStateFlow<List<Persona>>(emptyList())
    val allPersons: StateFlow<List<Persona>> = _allPersons.asStateFlow()

    private val _filteredPersons = MutableStateFlow<List<Persona>>(emptyList())
    val filteredPersons: StateFlow<List<Persona>> = _filteredPersons.asStateFlow()

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText.asStateFlow()

    init {
        viewModelScope.launch {
            personDao.getAllPersons().collectLatest { persons ->
                _allPersons.value = persons

                applyFilter(_searchText.value)
            }
        }
    }

    fun onSearchTextChanged(text: String) {
        _searchText.value = text
        applyFilter(text)
    }

    private fun applyFilter(query: String) {
        viewModelScope.launch {
            if (query.isBlank()) {
                _filteredPersons.value = _allPersons.value
            } else {
                personDao.buscarPersonas(query).collectLatest {
                    _filteredPersons.value = it
                }
            }
        }
    }

    suspend fun insertPerson(person: Persona) {
        personDao.insertPerson(person)
    }

    suspend fun updatePerson(person: Persona) {
        personDao.updatePersona(person)
    }

    suspend fun deletePerson(person: Persona) {
        personDao.deletePersona(person)
    }

    suspend fun getPersonById(id: Int): Persona? {
        return personDao.getPersonById(id)
    }

    suspend fun isDocumentIdUnique(documentId: String, currentPersonId: Int? = null): Boolean {
        return if (currentPersonId == null) {
            personDao.countPersonsWithDocumentId(documentId) == 0
        } else {
            personDao.countPersonsWithSameDocumentId(documentId, currentPersonId) == 0
        }
    }

    // Factory para instanciar el ViewModel con el DAO
    class PersonViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PersonViewModel::class.java)) {
                val database = AppDatabase.getDatabase(application)
                @Suppress("UNCHECKED_CAST")
                return PersonViewModel(application, database.personDao()) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}