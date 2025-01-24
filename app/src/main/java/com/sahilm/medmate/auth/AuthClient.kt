package com.sahilm.medmate.auth

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.sahilm.medmate.BuildConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await

class AuthClient(
    private val context: Context
) {

    private var credentialManager: CredentialManager? = CredentialManager.create(context)
    private var firebaseAuth = FirebaseAuth.getInstance()
    private val googleClientId = BuildConfig.GOOGLE_CLIENT_ID

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

    fun isSignedIn(): Boolean {
        if (firebaseAuth.currentUser != null) {
            return true
        } else {
            return false
        }
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

                return authResult.user != null
            } catch (e: GoogleIdTokenParsingException) {
                return false
            }
        } else {
            return false
        }
    }
}