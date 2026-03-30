package com.aaditya.smartfit.data.repository.impl

import com.aaditya.smartfit.data.local.dao.UserDao
import com.aaditya.smartfit.data.local.entity.UserEntity
import com.aaditya.smartfit.data.preferences.UserPreferencesDataStore
import com.aaditya.smartfit.data.repository.AuthRepository
import com.aaditya.smartfit.data.repository.model.AuthResult
import com.aaditya.smartfit.data.repository.model.RegisterResult
import java.security.MessageDigest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class LocalAuthRepository(
    private val userDao: UserDao,
    private val preferencesDataStore: UserPreferencesDataStore
) : AuthRepository {

    override suspend fun login(email: String, password: String): AuthResult {
        val normalizedEmail = email.trim().lowercase()
        val user = userDao.findByEmail(normalizedEmail) ?: return AuthResult.InvalidCredentials

        val hash = hashPassword(password)
        if (user.passwordHash != hash) {
            return AuthResult.InvalidCredentials
        }

        preferencesDataStore.setActiveUser(user.id)
        return AuthResult.Success(user.id)
    }

    override suspend fun register(name: String, email: String, password: String): RegisterResult {
        val normalizedEmail = email.trim().lowercase()
        val existing = userDao.findByEmail(normalizedEmail)
        if (existing != null) {
            return RegisterResult.EmailAlreadyExists
        }

        val now = System.currentTimeMillis()
        val userId = userDao.insert(
            UserEntity(
                fullName = name.trim(),
                email = normalizedEmail,
                passwordHash = hashPassword(password),
                createdAtMillis = now,
                updatedAtMillis = now
            )
        )

        preferencesDataStore.setActiveUser(userId)
        return RegisterResult.Success(userId)
    }

    override suspend fun logout() {
        preferencesDataStore.clearActiveUser()
    }

    override suspend fun isLoggedIn(): Boolean {
        return currentUserId() != null
    }

    override suspend fun currentUserId(): Long? {
        return preferencesDataStore.preferencesFlow
            .map { it.activeUserId }
            .first()
    }

    private fun hashPassword(value: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(value.toByteArray())
        return buildString(hashBytes.size * 2) {
            hashBytes.forEach { byte ->
                append("%02x".format(byte))
            }
        }
    }
}

