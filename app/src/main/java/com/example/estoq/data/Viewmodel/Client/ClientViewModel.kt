package com.example.estoq.data.Viewmodel.Client

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.estoq.data.Exceptions.Client.ClientException
import com.example.estoq.data.Model.Client.Client
import com.example.estoq.data.Repository.Client.ClientRepository
import com.example.estoq.data.Repository.Network.AddressRepository
import com.example.estoq.data.Ui_State.Client.ClientUiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

data class ClientSection(
    val letter: Char,
    val clients: List<Client>
)

@OptIn(ExperimentalCoroutinesApi::class)
class ClientViewModel(
    private val clientRepository: ClientRepository,
    private val addressRepository: AddressRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClientUiState())
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val uiState: StateFlow<ClientUiState> = _uiState

    val filteredClients: Flow<List<Client>> = _searchQuery.flatMapLatest { query ->
        if (query.isBlank()) clientRepository.getAll()
        else clientRepository.searchByName(query)
    }

    val sections: Flow<List<ClientSection>> = filteredClients.map { list ->
        list.groupBy {
            val fullName = "${it.firstName} ${it.lastName}".trim()
            fullName.first().uppercaseChar()
        }
            .map { (letter, clients) -> ClientSection(letter, clients) }
            .sortedBy { it.letter }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onFirstNameChange(value: String) {
        _uiState.value = _uiState.value.copy(firstName = value, firstNameError = null)
    }

    fun onLastNameChange(value: String) {
        _uiState.value = _uiState.value.copy(lastName = value, lastNameError = null)
    }

    fun onTelephoneChange(value: String) {
        _uiState.value = _uiState.value.copy(telephone = value, telephoneError = null)
    }

    fun onCepChange(value: String) {
        val digits = value.filter { it.isDigit() }.take(8)
        _uiState.value = _uiState.value.copy(cep = digits, cepError = null)
        if (digits.length == 8) {
            fetchAddress(digits)
        }
    }

    fun onNumeroChange(value: String) {
        _uiState.value = _uiState.value.copy(numero = value, numeroError = null)
    }

    private fun fetchAddress(cep: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isAddressLoading = true)
                val address = addressRepository.findAddress(cep)
                _uiState.value = _uiState.value.copy(
                    logradouro = address.logradouro,
                    bairro = address.bairro,
                    cidade = address.cidade,
                    estado = address.estado,
                    isAddressLoading = false
                )
            } catch (e: ClientException) {
                _uiState.value = _uiState.value.copy(
                    isAddressLoading = false,
                    cepError = e.message
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isAddressLoading = false,
                    cepError = "Erro ao buscar CEP"
                )
            }
        }
    }

    fun resetState() {
        _uiState.value = ClientUiState()
    }

    fun clearIsCreated() {
        _uiState.value = _uiState.value.copy(isCreated = false)
    }

    fun clearIsUpdated() {
        _uiState.value = _uiState.value.copy(isUpdated = false)
    }

    fun clearIsDeleted() {
        _uiState.value = _uiState.value.copy(isDeleted = false)
    }

    val allClients = clientRepository.getAll()

    fun getById(id: Long) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                val client = clientRepository.getById(id)
                if (client != null) {
                    _uiState.value = _uiState.value.copy(
                        id = client.id,
                        firstName = client.firstName,
                        lastName = client.lastName,
                        telephone = client.telephone,
                        cep = client.cep,
                        logradouro = client.logradouro,
                        bairro = client.bairro,
                        cidade = client.cidade,
                        estado = client.estado,
                        numero = client.numero,
                        isLoading = false
                    )
                } else {
                    throw ClientException.ClientUnknownException()
                }
            } catch (e: ClientException) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun createClient() {
        val state = _uiState.value

        viewModelScope.launch {
            try {
                if (state.firstName.isBlank()) {
                    throw ClientException.EmptyFirstName()
                }
                if (state.lastName.isBlank()){
                    throw ClientException.EmptyLastName()
                }
                if (state.telephone.isBlank()) {
                    throw ClientException.EmptyTelephone()
                }
                if (state.cep.isBlank()) {
                    throw ClientException.EmptyCep()
                }
                if (state.numero.isBlank()) {
                    throw ClientException.EmptyNumero()
                }

                _uiState.value = state.copy(isLoading = true)

                clientRepository.insert(
                    Client(
                        firstName = state.firstName.trim(),
                        lastName = state.lastName.trim(),
                        telephone = state.telephone.trim(),
                        cep = state.cep.trim(),
                        logradouro = state.logradouro.trim(),
                        bairro = state.bairro.trim(),
                        cidade = state.cidade.trim(),
                        estado = state.estado.trim(),
                        numero = state.numero.trim()
                    )
                )

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isCreated = true
                )

            } catch (e: ClientException) {
                _uiState.value = _uiState.value.copy(isLoading = false)
                _uiState.value = when (e) {
                    is ClientException.EmptyFirstName ->
                        _uiState.value.copy(firstNameError = e.message)
                    is ClientException.EmptyLastName ->
                        _uiState.value.copy(lastNameError = e.message)
                    is ClientException.EmptyTelephone ->
                        _uiState.value.copy(telephoneError = e.message)
                    is ClientException.EmptyCep ->
                        _uiState.value.copy(cepError = e.message)
                    is ClientException.InvalidCep ->
                        _uiState.value.copy(cepError = e.message)
                    is ClientException.EmptyNumero ->
                        _uiState.value.copy(numeroError = e.message)
                    else -> _uiState.value.copy(error = e.message)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun updateClient() {
        val state = _uiState.value

        viewModelScope.launch {
            try {
                if (state.id == 0L) {
                    throw ClientException.InvalidIdException()
                }
                if (state.firstName.isBlank()) {
                    throw ClientException.EmptyFirstName()
                }
                if (state.lastName.isBlank()){
                    throw ClientException.EmptyLastName()
                }
                if (state.telephone.isBlank()) {
                    throw ClientException.EmptyTelephone()
                }
                if (state.cep.isBlank()) {
                    throw ClientException.EmptyCep()
                }
                if (state.numero.isBlank()) {
                    throw ClientException.EmptyNumero()
                }

                _uiState.value = state.copy(isLoading = true)

                clientRepository.update(
                    Client(
                        id = state.id,
                        firstName = state.firstName.trim(),
                        lastName = state.lastName.trim(),
                        telephone = state.telephone.trim(),
                        cep = state.cep.trim(),
                        logradouro = state.logradouro.trim(),
                        bairro = state.bairro.trim(),
                        cidade = state.cidade.trim(),
                        estado = state.estado.trim(),
                        numero = state.numero.trim()
                    )
                )

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isUpdated = true
                )

            } catch (e: ClientException) {
                _uiState.value = _uiState.value.copy(isLoading = false)
                _uiState.value = when (e) {
                    is ClientException.EmptyFirstName ->
                        _uiState.value.copy(firstNameError = e.message)
                    is ClientException.EmptyLastName ->
                        _uiState.value.copy(lastNameError = e.message)
                    is ClientException.EmptyTelephone ->
                        _uiState.value.copy(telephoneError = e.message)
                    is ClientException.EmptyCep ->
                        _uiState.value.copy(cepError = e.message)
                    is ClientException.InvalidCep ->
                        _uiState.value.copy(cepError = e.message)
                    is ClientException.EmptyNumero ->
                        _uiState.value.copy(numeroError = e.message)
                    else -> _uiState.value.copy(error = e.message)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun deleteClient(id: Long) {
        viewModelScope.launch {
            try {
                if (id == 0L) throw ClientException.InvalidIdException()

                _uiState.value = _uiState.value.copy(isLoading = true)
                clientRepository.delete(id)
                _uiState.value = _uiState.value.copy(isLoading = false, isDeleted = true)
            } catch (e: ClientException) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = ClientException.InvalidIdException().message
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
}
