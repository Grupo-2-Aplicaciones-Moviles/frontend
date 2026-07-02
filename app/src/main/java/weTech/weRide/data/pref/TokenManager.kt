package weTech.weRide.data.pref

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import weTech.weRide.ui.screens.wallet.StoredTransaction
import weTech.weRide.utils.Constants

/**
 * Token Manager for handling authentication tokens using DataStore
 */
class TokenManager(private val context: Context) {

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = Constants.PREFS_NAME)
        private val TOKEN_KEY = stringPreferencesKey(Constants.KEY_AUTH_TOKEN)
        private val USER_ID_KEY = stringPreferencesKey(Constants.KEY_USER_ID)
        private val USER_EMAIL_KEY = stringPreferencesKey(Constants.KEY_USER_EMAIL)
        private val USER_NAME_KEY = stringPreferencesKey(Constants.KEY_USER_NAME)
        private val USER_PHOTO_KEY = stringPreferencesKey(Constants.KEY_USER_PHOTO)
        private val IS_LOGGED_IN_KEY = stringPreferencesKey(Constants.KEY_IS_LOGGED_IN)
        private val WALLET_BALANCE_KEY = stringPreferencesKey(Constants.KEY_WALLET_BALANCE)
        private val WALLET_TRANSACTIONS_KEY = stringPreferencesKey(Constants.KEY_WALLET_TRANSACTIONS)
        private val WALLET_LAST_UPDATED_KEY = stringPreferencesKey(Constants.KEY_WALLET_LAST_UPDATED)

        private val gson = Gson()
    }

    /**
     * Save authentication token
     */
    suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
            preferences[IS_LOGGED_IN_KEY] = "true"
        }
    }

    /**
     * Get authentication token
     */
    fun getToken(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[TOKEN_KEY]
        }
    }

    /**
     * Get token synchronously (use with caution)
     */
    suspend fun getTokenSync(): String? {
        return context.dataStore.data.map { preferences ->
            preferences[TOKEN_KEY]
        }.first()
    }

    /**
     * Save user information
     */
    suspend fun saveUserInfo(userId: String, email: String, name: String, photoUrl: String? = null) {
        context.dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = userId
            preferences[USER_EMAIL_KEY] = email
            preferences[USER_NAME_KEY] = name
            if (photoUrl != null) {
                preferences[USER_PHOTO_KEY] = photoUrl
            }
            preferences[IS_LOGGED_IN_KEY] = "true"
        }
    }

    /**
     * Save user photo URL
     */
    suspend fun saveUserPhoto(photoUrl: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_PHOTO_KEY] = photoUrl
        }
    }

    /**
     * Get user ID
     */
    fun getUserId(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[USER_ID_KEY]
        }
    }

    /**
     * Get user email
     */
    fun getUserEmail(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[USER_EMAIL_KEY]
        }
    }

    /**
     * Get user name
     */
    fun getUserName(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[USER_NAME_KEY]
        }
    }

    /**
     * Get user photo URL
     */
    fun getUserPhoto(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[USER_PHOTO_KEY]
        }
    }

    /**
     * Check if user is logged in
     */
    fun isLoggedIn(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[IS_LOGGED_IN_KEY] == "true"
        }
    }

    /**
     * Clear all stored data (logout)
     */
    suspend fun clearAll() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    // Wallet methods

    /**
     * Get wallet balance
     */
    fun getWalletBalance(): Flow<Double?> {
        return context.dataStore.data.map { preferences ->
            preferences[WALLET_BALANCE_KEY]?.toDoubleOrNull()
        }
    }

    /**
     * Get wallet transactions
     */
    fun getWalletTransactions(): Flow<List<StoredTransaction>> {
        return context.dataStore.data.map { preferences ->
            val json = preferences[WALLET_TRANSACTIONS_KEY]
            if (json != null) {
                try {
                    val type = object : TypeToken<List<StoredTransaction>>() {}.type
                    gson.fromJson(json, type) ?: emptyList()
                } catch (e: Exception) {
                    emptyList()
                }
            } else {
                emptyList()
            }
        }
    }

    /**
     * Get wallet last updated timestamp
     */
    fun getWalletLastUpdated(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[WALLET_LAST_UPDATED_KEY]
        }
    }

    /**
     * Save wallet data (balance, transactions, timestamp)
     */
    suspend fun saveWalletData(
        balance: Double,
        transactions: List<StoredTransaction>
    ) {
        val transactionsJson = gson.toJson(transactions)
        val timestamp = System.currentTimeMillis().toString()

        context.dataStore.edit { preferences ->
            preferences[WALLET_BALANCE_KEY] = balance.toString()
            preferences[WALLET_TRANSACTIONS_KEY] = transactionsJson
            preferences[WALLET_LAST_UPDATED_KEY] = timestamp
        }
    }

    /**
     * Clear wallet data
     */
    suspend fun clearWalletData() {
        context.dataStore.edit { preferences ->
            preferences.remove(WALLET_BALANCE_KEY)
            preferences.remove(WALLET_TRANSACTIONS_KEY)
            preferences.remove(WALLET_LAST_UPDATED_KEY)
        }
    }
}
