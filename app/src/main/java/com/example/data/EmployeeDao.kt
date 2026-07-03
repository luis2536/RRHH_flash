package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface EmployeeDao {
    @Query("SELECT * FROM employees ORDER BY id ASC")
    fun getAllEmployees(): Flow<List<Employee>>

    @Query("SELECT * FROM employees WHERE cid = :cid LIMIT 1")
    suspend fun getEmployeeByCid(cid: String): Employee?

    @Query("SELECT * FROM novelties WHERE employeeCid = :cid ORDER BY timestamp DESC")
    fun getNoveltiesForEmployee(cid: String): Flow<List<Novelty>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmployee(employee: Employee): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmployees(employees: List<Employee>)

    @Update
    suspend fun updateEmployee(employee: Employee)

    @Delete
    suspend fun deleteEmployee(employee: Employee)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNovelty(novelty: Novelty)

    @Query("SELECT * FROM system_configs WHERE `key` = :key LIMIT 1")
    suspend fun getConfig(key: String): SystemConfig?

    @Query("SELECT * FROM system_configs WHERE `key` = :key LIMIT 1")
    fun getConfigFlow(key: String): Flow<SystemConfig?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConfig(config: SystemConfig)
}
