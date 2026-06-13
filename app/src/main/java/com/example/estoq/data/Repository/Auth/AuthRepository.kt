package com.example.estoq.data.Repository.Auth

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.example.estoq.MainActivity
import com.example.estoq.R
import com.example.estoq.data.Ui_State.AuthResponse
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.security.MessageDigest
import java.util.UUID

class AuthRepository (
    private val auth: FirebaseAuth,
    private val credentialManager: CredentialManager
) {

    fun createAccountWithEmailAndPassword(email: String, password: String): Flow<AuthResponse> =
        callbackFlow {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        trySend(AuthResponse.Success)
                    } else {
                        val errorCode = (task.exception as? FirebaseAuthException)?.errorCode
                        trySend(AuthResponse.Error(
                            errorMessage = task.exception?.message ?: "Erro no login",
                            errorCode = errorCode
                        ))
                    }
                }

            awaitClose()
        }

    fun loginWithEmail(email: String, password: String): Flow<AuthResponse> =
        callbackFlow {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        trySend(AuthResponse.Success)
                    } else {
                        val errorCode = (task.exception as? FirebaseAuthException)?.errorCode
                        trySend(AuthResponse.Error(
                            errorMessage = task.exception?.message ?: "Erro no login",
                            errorCode = errorCode
                        ))
                    }
                }

            awaitClose()
        }

    fun sendPasswordResetEmail(email: String): Flow<AuthResponse> =
        callbackFlow {
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        trySend(AuthResponse.Success)
                    } else {
                        val errorCode = (task.exception as? FirebaseAuthException)?.errorCode
                        trySend(AuthResponse.Error(
                            errorMessage = task.exception?.message ?: "Erro ao enviar email",
                            errorCode = errorCode
                        ))
                    }
                }
            awaitClose()
        }

    fun logout() {

        Log.e("GOOGLE_LOGIN", "LOGOUT CHAMADO")

        auth.signOut()
    }

    fun getCurrentUser() = auth.currentUser

    private fun createNonce(): String {
        val rawNonce = UUID.randomUUID().toString()
        val bytes = rawNonce.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)

        return digest.fold("") { str, it ->
            str + "%02x".format(it)
        }
    }

    fun singInWithGoogle(
        activity: Activity,
        webClientId: String
    ): Flow<AuthResponse> = callbackFlow {

        Log.d("GOOGLE_LOGIN", "1 - Iniciando login Google")

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(webClientId)
            .setAutoSelectEnabled(false)
            .setNonce(createNonce())
            .build()

        Log.d("GOOGLE_LOGIN", "2 - GoogleIdOption criado")

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        Log.d("GOOGLE_LOGIN", "3 - Request criado")

        try {

            Log.d("GOOGLE_LOGIN", "4 - Antes getCredential")

            val result = credentialManager.getCredential(
                context = activity,
                request = request
            )

            Log.d("GOOGLE_LOGIN", "5 - Depois getCredential")

            val credential = result.credential

            Log.d("GOOGLE_LOGIN", "6 - Credential recebido")
            Log.d("GOOGLE_LOGIN", "Tipo credential: ${credential.type}")

            if (credential is CustomCredential) {

                Log.d("GOOGLE_LOGIN", "7 - É CustomCredential")

                if (credential.type ==
                    GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                ) {

                    Log.d("GOOGLE_LOGIN", "8 - É Google ID Token")

                    try {

                        val googleIdTokenCredential =
                            GoogleIdTokenCredential.createFrom(
                                credential.data
                            )

                        Log.d(
                            "GOOGLE_LOGIN",
                            "9 - Email: ${googleIdTokenCredential.id}"
                        )

                        Log.d(
                            "GOOGLE_LOGIN",
                            "10 - Token recebido: ${
                                googleIdTokenCredential.idToken.take(20)
                            }..."
                        )

                        val firebaseCredential =
                            GoogleAuthProvider.getCredential(
                                googleIdTokenCredential.idToken,
                                null
                            )

                        Log.d(
                            "GOOGLE_LOGIN",
                            "11 - FirebaseCredential criado"
                        )

                        auth.signInWithCredential(firebaseCredential)
                            .addOnCompleteListener { task ->

                                Log.d(
                                    "GOOGLE_LOGIN",
                                    "12 - Callback Firebase executado"
                                )

                                if (task.isSuccessful) {

                                    Log.d(
                                        "GOOGLE_LOGIN",
                                        "13 - Login Firebase OK"
                                    )

                                    Log.d(
                                        "GOOGLE_LOGIN",
                                        "UID: ${auth.currentUser?.uid}"
                                    )

                                    Log.d(
                                        "GOOGLE_LOGIN",
                                        "EMAIL: ${auth.currentUser?.email}"
                                    )

                                    trySend(AuthResponse.Success)

                                } else {

                                    Log.e(
                                        "GOOGLE_LOGIN",
                                        "13 - Erro Firebase"
                                    )

                                    Log.e(
                                        "GOOGLE_LOGIN",
                                        "Mensagem: ${task.exception?.message}"
                                    )

                                    Log.e(
                                        "GOOGLE_LOGIN",
                                        "Exception:",
                                        task.exception
                                    )

                                    trySend(
                                        AuthResponse.Error(
                                            errorMessage =
                                                task.exception?.message
                                                    ?: "Erro no login"
                                        )
                                    )
                                }
                            }

                    } catch (e: GoogleIdTokenParsingException) {

                        Log.e(
                            "GOOGLE_LOGIN",
                            "Erro ao converter token",
                            e
                        )

                        trySend(
                            AuthResponse.Error(
                                errorMessage = e.message
                                    ?: "Erro ao converter token"
                            )
                        )
                    }

                } else {

                    Log.e(
                        "GOOGLE_LOGIN",
                        "Tipo de credential inesperado: ${credential.type}"
                    )
                }

            } else {

                Log.e(
                    "GOOGLE_LOGIN",
                    "Credential não é CustomCredential"
                )
            }

        } catch (e: Exception) {

            Log.e(
                "GOOGLE_LOGIN",
                "ERRO GERAL: ${e.javaClass.simpleName}"
            )

            Log.e(
                "GOOGLE_LOGIN",
                "Mensagem: ${e.message}"
            )

            Log.e(
                "GOOGLE_LOGIN",
                "StackTrace:",
                e
            )

            trySend(
                AuthResponse.Error(
                    errorMessage = e.message ?: "Erro no login"
                )
            )
        }

        awaitClose()
    }

}