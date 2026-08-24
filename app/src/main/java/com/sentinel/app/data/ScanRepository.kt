package com.sentinel.app.data

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.sentinel.app.domain.ScanResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ScanRepository(context: Context) {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    
    // Fallback ID generator for local mock usage
    private fun generateId() = System.currentTimeMillis()

    fun getAllScans(): Flow<List<ScanResult>> = callbackFlow {
        val user = auth.currentUser
        if (user == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        
        val subscription = firestore.collection("users")
            .document(user.uid)
            .collection("scans")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("ScanRepository", "Listen failed.", e)
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val scans = snapshot.documents.mapNotNull { it.toObject(ScanResult::class.java)?.copy(id = it.id.hashCode().toLong()) }
                    trySend(scans)
                }
            }
            
        awaitClose { subscription.remove() }
    }

    suspend fun getScanById(id: Long): ScanResult? {
        // Simple hack: We just fetch all and find it since we map string ID to Long hashCode in memory
        val user = auth.currentUser ?: return null
        return try {
            val snapshot = firestore.collection("users")
                .document(user.uid)
                .collection("scans")
                .get()
                .await()
            val doc = snapshot.documents.find { it.id.hashCode().toLong() == id }
            doc?.toObject(ScanResult::class.java)?.copy(id = id)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun saveScan(result: ScanResult): Long {
        val user = auth.currentUser ?: return generateId()
        return try {
            val docRef = firestore.collection("users")
                .document(user.uid)
                .collection("scans")
                .document()
                
            // Avoid saving the dummy id = 0 in firestore
            val toSave = result.copy(id = docRef.id.hashCode().toLong())
            docRef.set(toSave).await()
            toSave.id
        } catch (e: Exception) {
            Log.e("ScanRepository", "Error saving scan", e)
            generateId()
        }
    }

    suspend fun deleteScan(id: Long) {
        val user = auth.currentUser ?: return
        try {
            val snapshot = firestore.collection("users")
                .document(user.uid)
                .collection("scans")
                .get()
                .await()
            val doc = snapshot.documents.find { it.id.hashCode().toLong() == id }
            doc?.reference?.delete()?.await()
        } catch (e: Exception) {
            Log.e("ScanRepository", "Error deleting scan", e)
        }
    }
}
