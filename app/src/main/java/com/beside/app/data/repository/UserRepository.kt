package com.beside.app.data.repository

import com.beside.app.data.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class UserRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val usersCol = db.collection("users")
    private val eventsCol = db.collection("events")
    private val pairingCol = db.collection("pairing")

    val currentUid: String? get() = auth.currentUser?.uid

    // ========== 用户资料 ==========

    suspend fun getProfile(uid: String): UserProfile? {
        return usersCol.document(uid).get().await()
            .toObject(UserProfile::class.java)
    }

    suspend fun updateProfile(profile: UserProfile) {
        usersCol.document(profile.uid).set(profile).await()
    }

    fun observePartner(partnerId: String): Flow<UserProfile?> = callbackFlow {
        val listener = usersCol.document(partnerId)
            .addSnapshotListener { snapshot, _ ->
                trySend(snapshot?.toObject(UserProfile::class.java))
            }
        awaitClose { listener.remove() }
    }

    // ========== 配对 ==========

    suspend fun generatePairingCode(): String {
        val uid = currentUid ?: throw IllegalStateException("未登录呢~")
        val code = (100000..999999).random().toString()
        val profile = getProfile(uid) ?: throw IllegalStateException("找不到你的资料哦~")
        updateProfile(profile.copy(pairingCode = code))
        return code
    }

    suspend fun pairWithCode(code: String): Boolean {
        val uid = currentUid ?: return false
        val result = usersCol.whereEqualTo("pairingCode", code).get().await()
        val partner = result.documents.firstOrNull() ?: return false
        val partnerId = partner.id
        if (partnerId == uid) return false

        // 双向绑定
        usersCol.document(uid).update("partnerId", partnerId).await()
        usersCol.document(partnerId).update("partnerId", uid).await()
        // 清除配对码
        usersCol.document(partnerId).update("pairingCode", "").await()
        return true
    }

    suspend fun pairWithUserId(targetUid: String): Boolean {
        val uid = currentUid ?: return false
        if (targetUid == uid) return false
        val partner = getProfile(targetUid) ?: return false

        val request = PairingRequest(fromUid = uid, toUid = targetUid)
        pairingCol.add(request).await()
        return true
    }

    // ========== 活动事件 ==========

    suspend fun postEvent(event: ActivityEvent) {
        eventsCol.add(event).await()
    }

    fun observePartnerEvents(partnerId: String): Flow<List<ActivityEvent>> = callbackFlow {
        val today = com.google.firebase.Timestamp.now().let {
            val cal = java.util.Calendar.getInstance()
            cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
            cal.set(java.util.Calendar.MINUTE, 0)
            cal.set(java.util.Calendar.SECOND, 0)
            com.google.firebase.Timestamp(cal.time)
        }

        val listener = eventsCol
            .whereEqualTo("userId", partnerId)
            .whereGreaterThan("timestamp", today)
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, _ ->
                val events = snapshot?.toObjects(ActivityEvent::class.java) ?: emptyList()
                trySend(events)
            }
        awaitClose { listener.remove() }
    }

    // ========== 位置更新 ==========

    suspend fun updateLocation(geoPoint: GeoPoint) {
        val uid = currentUid ?: return
        usersCol.document(uid).update(
            mapOf(
                "lastLocation" to geoPoint,
                "lastOnline" to com.google.firebase.Timestamp.now()
            )
        ).await()
    }
}
