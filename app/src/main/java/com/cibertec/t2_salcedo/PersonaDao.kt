package com.cibertec.t2_salcedo

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonaDao {
    @Query("SELECT * FROM persons ORDER BY fullName ASC")
    fun getAllPersons(): Flow<List<Persona>>

    @Query("SELECT * FROM persons WHERE id = :id")
    suspend fun getPersonById(id: Int): Persona?

    @Query("SELECT * FROM persons WHERE documentId = :documentId LIMIT 1")
    suspend fun getPersonaByDocumentId(documentId: String): Persona?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPerson(persona: Persona)

    @Update
    suspend fun updatePersona(persona: Persona)

    @Delete
    suspend fun deletePersona(persona: Persona)

    @Query("SELECT COUNT(*) FROM persons WHERE documentId = :documentId AND id != :currentId")
    suspend fun countPersonsWithSameDocumentId(documentId: String, currentId: Int): Int

    @Query("SELECT COUNT(*) FROM persons WHERE documentId = :documentId")
    suspend fun countPersonsWithDocumentId(documentId: String): Int

    @Query("SELECT * FROM persons WHERE fullName LIKE '%' || :query || '%' OR documentId LIKE '%' || :query || '%' ORDER BY fullName ASC")
    fun buscarPersonas(query: String): Flow<List<Persona>>
}