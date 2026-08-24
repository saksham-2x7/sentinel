package com.sentinel.app.data
import androidx.room.*
import kotlinx.coroutines.flow.Flow
@Dao
interface ScanDao {
    @Query("SELECT * FROM scan_records ORDER BY timestamp DESC")
    fun getAllScans(): Flow<List<ScanRecord>>
    @Query("SELECT * FROM scan_records WHERE id = :id")
    suspend fun getScanById(id: Long): ScanRecord?
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScan(record: ScanRecord): Long
    @Query("DELETE FROM scan_records WHERE id = :id")
    suspend fun deleteScan(id: Long)
    @Query("DELETE FROM scan_records")
    suspend fun deleteAllScans()
    @Query("SELECT COUNT(*) FROM scan_records")
    suspend fun getScanCount(): Int
}
