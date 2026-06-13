# EstoQ — Autenticação com Firebase Auth + Credential Manager

Guia completo de implementação de login com **Email/Senha** e **Google Sign-In** utilizando **Firebase Authentication**, **Credential Manager**, **Koin** e **Jetpack Compose**.

---

## Índice

- [1. Projeto Firebase](#1-projeto-firebase)
- [2. Dependências](#2-dependências)
- [3. strings.xml](#3-stringsxml)
- [4. AuthRepository](#4-authrepository)
- [5. ViewModel](#5-viewmodel)
- [6. UI (Jetpack Compose)](#6-ui-jetpack-compose)
- [7. Koin DI](#7-koin-di)
- [8. Checklist de Verificação](#8-checklist-de-verificação)
- [9. Troubleshooting](#9-troubleshooting)
- [10. Fluxo Legado (Diagnóstico)](#10-fluxo-legado-diagnóstico)
- [11. Erros Comuns](#11-erros-comuns)

---

## 1. Projeto Firebase

### 1.1 Criar / Configurar Projeto

1. Acesse [Firebase Console](https://console.firebase.google.com) e crie um projeto (ou use existente).
2. **Adicionar app Android**:
   - Package name: `com.exemplo.app`
   - SHA-1: gere com:
     ```bash
     keytool -list -v -alias androiddebugkey -keystore ~/.android/debug.keystore -storepass android -keypass android
     ```
   - Baixe o arquivo `google-services.json` e coloque em `app/`.

### 1.2 OAuth Clients (CRÍTICO)

O `google-services.json` deve conter **dois** OAuth clients na seção `oauth_client`:

```json
"oauth_client": [
  {
    "client_id": "123456789-androidclientid.apps.googleusercontent.com",
    "client_type": 1,
    "android_info": {
      "package_name": "com.exemplo.app",
      "certificate_hash": "C4581EB5AFA35D5BC7B7673C45D01299ED1910BB"
    }
  },
  {
    "client_id": "123456789-webclientid.apps.googleusercontent.com",
    "client_type": 3
  }
]
```

| Tipo | O que é | Finalidade |
|------|---------|------------|
| **Type 1 (Android)** | Gerado automaticamente ao adicionar o SHA-1 no Firebase | Verificar identidade do app via Google Play Services |
| **Type 3 (Web)** | Cliente OAuth Web | Usado como `serverClientId` no `GetGoogleIdOption` |

> ⚠️ Se faltar o **Android (type 1)**, o Credential Manager lançará `GetCredentialCancellationException: activity is cancelled by the user`.

### 1.3 Tela de Consentimento OAuth

Acesse [Google Cloud Console](https://console.cloud.google.com/apis/credentials/consent) → APIs e Serviços → Tela de consentimento OAuth:

- Escolha **"Externa"** (ou "Interna" se tiver G Workspace)
- Preencha: nome do app, e-mail de suporte, e-mail do desenvolvedor
- Escopos obrigatórios: `openid`, `email`, `profile`
- Se estiver em estado **"Testing"**, adicione seu e-mail como **test user**
- Para produção, publique o app

### 1.4 Ativar Provedor Google no Firebase

Firebase Console → Authentication → Sign-in method → **Google** → Habilitar

- **Web client ID**: deve ser o mesmo **type 3** do `google-services.json` e do `strings.xml`

---

## 2. Dependências

### app/build.gradle.kts

```kotlin
plugins {
    id("com.google.gms.google-services")
}

dependencies {
    // Firebase BOM
    implementation(platform("com.google.firebase:firebase-bom:33.3.0"))
    implementation("com.google.firebase:firebase-auth")

    // Google Play Services Auth (opcional — fluxo legado)
    implementation("com.google.android.gms:play-services-auth:21.2.0")

    // Credential Manager
    val credentialsVersion = "1.3.0"
    implementation("androidx.credentials:credentials:$credentialsVersion")
    implementation("androidx.credentials:credentials-play-services-auth:$credentialsVersion")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")
}
```

### build.gradle.kts (raiz)

```kotlin
plugins {
    id("com.google.gms.google-services") version "4.4.2" apply false
}
```

---

## 3. strings.xml

```xml
<resources>
    <string name="app_name">MeuApp</string>
    <!-- Web OAuth Client ID (type 3) -->
    <string name="web_client_id">123456789-webclientid.apps.googleusercontent.com</string>
</resources>
```

> ⚠️ **Nunca** use o Android client ID (type 1) como `web_client_id`. Eles são diferentes.

---

## 4. AuthRepository

```kotlin
package com.exemplo.app.data.repository

import android.app.Activity
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.security.MessageDigest
import java.util.UUID

sealed interface AuthResponse {
    data object Success : AuthResponse
    data class Error(val errorMessage: String) : AuthResponse
}

class AuthRepository(
    private val auth: FirebaseAuth,
    private val credentialManager: CredentialManager
) {

    // ──────────────────────────────────────────────
    //  LOGIN EMAIL / SENHA
    // ──────────────────────────────────────────────

    fun loginWithEmail(email: String, password: String): Flow<AuthResponse> =
        callbackFlow {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    trySend(
                        if (task.isSuccessful) AuthResponse.Success
                        else AuthResponse.Error(task.exception?.message ?: "Erro no login")
                    )
                }
            awaitClose()
        }

    fun createAccountWithEmailAndPassword(email: String, password: String): Flow<AuthResponse> =
        callbackFlow {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    trySend(
                        if (task.isSuccessful) AuthResponse.Success
                        else AuthResponse.Error(task.exception?.message ?: "Erro no cadastro")
                    )
                }
            awaitClose()
        }

    // ──────────────────────────────────────────────
    //  LOGIN GOOGLE (CREDENTIAL MANAGER)
    // ──────────────────────────────────────────────

    fun signInWithGoogle(activity: Activity, webClientId: String): Flow<AuthResponse> =
        callbackFlow {

            // 1. Gerar o nonce
            val rawNonce = UUID.randomUUID().toString()
            val hashedNonce = rawNonce.toByteArray()
                .let { MessageDigest.getInstance("SHA-256").digest(it) }
                .fold("") { str, byte -> str + "%02x".format(byte) }

            // 2. Criar GoogleIdOption
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(webClientId)
                .setAutoSelectEnabled(false)
                .setNonce(hashedNonce)
                .build()

            // 3. Montar requisição
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            try {
                // 4. Chamar Credential Manager → exibe seletor de contas
                val result = credentialManager.getCredential(
                    context = activity,
                    request = request
                )

                val credential = result.credential

                if (credential is CustomCredential &&
                    credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                ) {
                    val googleIdTokenCredential =
                        GoogleIdTokenCredential.createFrom(credential.data)

                    // 5. Autenticar no Firebase com ID token + raw nonce
                    val firebaseCredential = GoogleAuthProvider.getCredential(
                        googleIdTokenCredential.idToken,
                        rawNonce  // ← raw nonce, NÃO o hash
                    )

                    auth.signInWithCredential(firebaseCredential)
                        .addOnCompleteListener { task ->
                            trySend(
                                if (task.isSuccessful) AuthResponse.Success
                                else AuthResponse.Error(
                                    task.exception?.message ?: "Erro ao autenticar no Firebase"
                                )
                            )
                        }
                }
            } catch (e: Exception) {
                trySend(AuthResponse.Error(e.message ?: "Erro no Google Sign-In"))
            }

            awaitClose()
        }

    // ──────────────────────────────────────────────
    //  UTILIDADES
    // ──────────────────────────────────────────────

    fun logout() {
        auth.signOut()
    }

    fun getCurrentUser() = auth.currentUser
}
```

### ⚠️ Nonce: o detalhe que derruba o fluxo

| Etapa | O que usar | Exemplo |
|-------|-----------|---------|
| `GetGoogleIdOption.Builder().setNonce()` | SHA-256 **hash** do raw nonce | `hashedNonce` |
| `GoogleAuthProvider.getCredential(idToken, ...)` | **Raw nonce** (sem hash) | `rawNonce` |

Se você passar o hash nos dois lugares, o Firebase Auth rejeitará o token silenciosamente.

---

## 5. ViewModel

```kotlin
package com.exemplo.app.data.viewmodel

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exemplo.app.data.repository.AuthRepository
import com.exemplo.app.data.repository.AuthResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val authError: String? = null,
    val isAuthenticated: Boolean = false
)

class LoginViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) {
        _uiState.value = _uiState.value.copy(email = value, authError = null)
    }

    fun onPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(password = value, authError = null)
    }

    fun loginWithEmail() {
        val email = _uiState.value.email.trim()
        val password = _uiState.value.password

        if (email.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(authError = "Preencha todos os campos")
            return
        }

        viewModelScope.launch {
            repository.loginWithEmail(email, password).collectLatest { response ->
                when (response) {
                    is AuthResponse.Success -> _uiState.value = _uiState.value.copy(isAuthenticated = true)
                    is AuthResponse.Error   -> _uiState.value = _uiState.value.copy(authError = response.errorMessage)
                }
            }
        }
    }

    fun signInWithGoogle(activity: Activity, webClientId: String) {
        viewModelScope.launch {
            repository.signInWithGoogle(activity, webClientId).collectLatest { response ->
                when (response) {
                    is AuthResponse.Success -> _uiState.value = _uiState.value.copy(isAuthenticated = true)
                    is AuthResponse.Error   -> _uiState.value = _uiState.value.copy(authError = response.errorMessage)
                }
            }
        }
    }

    fun logout() {
        repository.logout()
        _uiState.value = LoginUiState()
    }
}
```

---

## 6. UI (Jetpack Compose)

```kotlin
@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel,
    onNavigateToHome: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as Activity
    val state by loginViewModel.uiState.collectAsState()

    LaunchedEffect(state.isAuthenticated) {
        if (state.isAuthenticated) onNavigateToHome()
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center
        ) {

            // ── TÍTULO ──
            Text(
                text = "Login",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(8.dp))
            Text("Preencha os campos abaixo para acessar sua conta")
            Spacer(Modifier.height(24.dp))

            // ── EMAIL ──
            OutlinedTextField(
                value = state.email,
                onValueChange = { loginViewModel.onEmailChange(it) },
                label = { Text("E-mail") },
                leadingIcon = { Icon(Icons.Rounded.Email, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            // ── SENHA ──
            OutlinedTextField(
                value = state.password,
                onValueChange = { loginViewModel.onPasswordChange(it) },
                label = { Text("Senha") },
                leadingIcon = { Icon(Icons.Rounded.Lock, contentDescription = null) },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            // ── BOTÃO LOGIN ──
            Button(
                onClick = { loginViewModel.loginWithEmail() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Entrar", modifier = Modifier.padding(vertical = 4.dp))
            }

            Spacer(Modifier.height(16.dp))
            Text(
                "Ou continue com",
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(Modifier.height(16.dp))

            // ── BOTÃO GOOGLE ──
            OutlinedButton(
                onClick = {
                    loginViewModel.signInWithGoogle(
                        activity,
                        context.getString(R.string.web_client_id)
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.google),
                    contentDescription = "Google",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text("Login com Google")
            }

            // ── ERRO ──
            state.authError?.let { error ->
                Spacer(Modifier.height(12.dp))
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
```

---

## 7. Koin DI

```kotlin
package com.exemplo.app.di

import androidx.credentials.CredentialManager
import com.exemplo.app.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val authModule = module {
    single { FirebaseAuth.getInstance() }
    single { CredentialManager.create(androidContext()) }
    single { AuthRepository(auth = get(), credentialManager = get()) }
}
```

No `Application`:

```kotlin
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MainApplication)
            modules(authModule)
        }
    }
}
```

---

## 8. Checklist de Verificação

### Antes de compilar e testar

- [ ] `google-services.json` contém **Android OAuth Client (type 1)** com SHA-1 e package name
- [ ] `google-services.json` contém **Web OAuth Client (type 3)**
- [ ] `strings.xml` → `web_client_id` aponta para o **Web client** (type 3), **não** para o Android
- [ ] Firebase Console → Authentication → Sign-in method → Google → **Web client ID** corresponde ao de `strings.xml`
- [ ] Tela de consentimento OAuth no GCP está configurada (escopos `openid`/`email`/`profile`, test user adicionado)
- [ ] Nonce: `setNonce()` recebe o **hash**; `getCredential()` recebe o **raw nonce**

### Ao testar

1. `./gradlew clean build`
2. Desinstale o app do dispositivo manualmente
3. Execute novamente
4. Verifique os logs com a tag `GOOGLE_LOGIN` (ou a que você definiu)

---

## 9. Troubleshooting

| Sintoma | Causa mais provável |
|---------|-------------------|
| `GetCredentialCancellationException: activity is cancelled by the user` | Android OAuth Client (type 1) **ausente** ou SHA-1 incorreto |
| Seletor de contas aparece, mas FirebaseAuth falha | **Nonce** incorreto (passou o hash em vez do raw) |
| Login falha com `10:` no log | Web client ID errado em `strings.xml` |
| Login email/senha funciona, Google não | Problema de **configuração OAuth**, Firebase está ok |
| `GoogleSignInAccount` retorna null no fluxo legado | SHA-1 não cadastrado ou OAuth consent screen em draft |

---

## 10. Fluxo Legado (Diagnóstico)

Se o Credential Manager continuar falhando mesmo com a configuração correta, implemente o fluxo tradicional com `GoogleSignInClient` para isolar o problema:

```kotlin
fun signInWithGoogleLegacy(activity: Activity, webClientId: String): Flow<AuthResponse> =
    callbackFlow {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(activity, gso)
        val signInIntent = googleSignInClient.signInIntent
        activity.startActivityForResult(signInIntent, GOOGLE_SIGN_IN_CODE)

        awaitClose { googleSignInClient.signOut() }
    }

// No onActivityResult:
// val account = GoogleSignIn.getSignedInAccountFromIntent(data)
// val credential = GoogleAuthProvider.getCredential(account.idToken, null)
// auth.signInWithCredential(credential)
```

| Resultado | Conclusão |
|-----------|-----------|
| Fluxo legado **funciona**, Credential Manager **não** | Problema específico na implementação do Credential Manager |
| Fluxo legado **também falha** | Problema na camada OAuth / Firebase (SHA-1, client ID, consent screen) |

---

## 11. Erros Comuns

### "activity is cancelled by the user" sem o seletor aparecer

- **Causa #1 (99% dos casos):** Android OAuth Client (type 1) ausente no Google Cloud Console
- **Solução:** Re-adicionar o app Android no Firebase Console com o SHA-1 correto e baixar novo `google-services.json`

### SHA-1 não corresponde

- O SHA-1 do **keystore usado ao executar o app** precisa estar cadastrado no Firebase
- Debug keystore padrão (`~/.android/debug.keystore`) é diferente de um keystore de release
- É possível cadastrar **múltiplos SHA-1** no mesmo app no Firebase Console

### Tela de Consentimento OAuth incompleta

- Se estiver em "Draft" ou "Testing" sem test users, o Google rejeita a requisição
- Acesse GCP → APIs e Serviços → Tela de consentimento OAuth e finalize a configuração

### google-services.json desatualizado

- Após alterar OAuth clients, sempre **re-baixe** o `google-services.json` do Firebase Console
- O arquivo é estático e não sincroniza automaticamente com o GCP

---

## Arquitetura do Fluxo

```
Usuário clica "Login com Google"
         │
         ▼
CredentialManager.getCredential()
         │
         ▼
Google Play Services verifica app
via Android OAuth Client (type 1)
   ── SHA-1 confere? ──→ Não → GetCredentialCancellationException ❌
   ── Sim              ──→
         │
         ▼
Seletor de contas Google
         │
         ▼
Usuário seleciona conta
         │
         ▼
Retorna GoogleIdTokenCredential
(ID token + raw nonce)
         │
         ▼
GoogleAuthProvider.getCredential(idToken, rawNonce)
         │
         ▼
FirebaseAuth.signInWithCredential()
         │
         ▼
Usuário autenticado ✅
```

---

> Este guia foi gerado a partir da experiência com o projeto **EstoQ** — um aplicativo Android com Kotlin, Jetpack Compose, Firebase Auth, Credential Manager, Koin e Room.
