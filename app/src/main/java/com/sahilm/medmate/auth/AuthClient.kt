package com.sahilm.medmate.auth

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.sahilm.medmate.BuildConfig
import com.sahilm.medmate.model.LoginState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

val Context.dataStore: DataStore<Preferences> by preferencesDataStore("user_pref")

class AuthClient(
    private val context: Context
) {

    private var credentialManager: CredentialManager? = CredentialManager.create(context)
    private var firebaseAuth = FirebaseAuth.getInstance()
    private val googleClientId = BuildConfig.GOOGLE_CLIENT_ID

    companion object {
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val USER_TOKEN_ID = stringPreferencesKey("user_token_id")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_PFP_URI = stringPreferencesKey("user_pfp_uri")
    }

    suspend fun saveUserDetails(
        isLoggedIn: Boolean,
        userEmail: String = "",
        tokenId: String = "",
        userName: String = "",
        userPhotoUri: String =""
    ) {
        context.dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = isLoggedIn
            preferences[USER_EMAIL] = userEmail
            preferences[USER_TOKEN_ID] = tokenId
            preferences[USER_NAME] = userName
            preferences[USER_PFP_URI] = userPhotoUri
        }
    }

    val loginState: Flow<LoginState> = context.dataStore.data.map { preferences ->
        LoginState(
            isLoggedIn = preferences[IS_LOGGED_IN] ?: false,
            userEmail = preferences[USER_EMAIL] ?: "",
            userName = preferences[USER_NAME] ?: "",
            tokenId = preferences[USER_TOKEN_ID] ?: "",
            userPhotoUri = preferences[USER_PFP_URI] ?: ""
        )
    }

    suspend fun signIn () : Boolean {
        if (isSignedIn()) {
            return true
        }
        try {
            val result = getCredentialRequest()
            return handleSignIn(result)
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e

            return false
        }
    }

    private suspend fun getCredentialRequest(): GetCredentialResponse {
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(
                GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(googleClientId)
                    .setAutoSelectEnabled(false)
                    .build()
            ).build()

        return credentialManager!!.getCredential(context, request)
    }

    private fun isSignedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    suspend fun signOut() {
        credentialManager!!.clearCredentialState(
            ClearCredentialStateRequest()
        )
        firebaseAuth.signOut()
    }

    fun clearCredentialManager() {
        credentialManager = null
    }

    private suspend fun handleSignIn(result: GetCredentialResponse): Boolean {
        val credential = result.credential

        if (
            credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            try {
                val tokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

                val authCredential = GoogleAuthProvider.getCredential(
                    tokenCredential.idToken, null
                )
                val authResult = firebaseAuth.signInWithCredential(authCredential).await()

                authResult.user?.let {
                    saveUserDetails(
                        isLoggedIn = true,
                        userName = tokenCredential.displayName ?: "",
                        userEmail = it.email ?: "",
                        userPhotoUri = tokenCredential.profilePictureUri.toString(),
                        tokenId = tokenCredential.idToken
                    )
                }
                println("AuthClient, DataStore: ${loginState.first()}")

                return authResult.user != null
            } catch (e: GoogleIdTokenParsingException) {
                return false
            }
        } else {
            return false
        }
    }
}