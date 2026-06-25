package com.example.estoq.data.Viewmodel.Storage

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.estoq.data.Exceptions.Storage.StorageException
import com.example.estoq.data.Model.Storage.Storage
import com.example.estoq.data.Repository.Storage.StorageRepository
import com.example.estoq.data.Ui_State.Storage.StorageUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StorageViewModel(
    private val repository: StorageRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StorageUiState())

    val uiState: StateFlow<StorageUiState> = _uiState

    val allStorages = repository.getAll()

    fun onTitleChange(value: String) {
        _uiState.value = _uiState.value.copy(title = value, titleError = null)
    }

    private fun hsvToColorLong(hue: Float, saturation: Float, brightness: Float): Long {
        val color = Color.hsv(hue, saturation.coerceIn(0f, 1f), brightness.coerceIn(0f, 1f))
        return ((color.value shr 32) and 0xFFFFFFFFUL).toLong()
    }

    private fun colorLongToHsv(colorLong: Long): FloatArray {
        val argb = (colorLong and 0xFFFFFFFFL).toInt()
        val hsv = FloatArray(3)
        android.graphics.Color.colorToHSV(argb, hsv)
        return hsv
    }

    fun onColorChange(color: Long) {
        val hsv = colorLongToHsv(color)
        _uiState.value = _uiState.value.copy(
            mainColor = color,
            hue = hsv[0],
            saturation = hsv[1],
            brightness = hsv[2],
            mainColorError = null
        )
    }

    fun onHueChange(value: Float) {
        val h = value.coerceIn(0f, 360f)
        val colorLong = hsvToColorLong(h, _uiState.value.saturation, _uiState.value.brightness)
        _uiState.value = _uiState.value.copy(
            mainColor = colorLong,
            hue = h,
            mainColorError = null
        )
    }

    fun onSaturationBrightnessChange(saturation: Float, brightness: Float) {
        val s = saturation.coerceIn(0f, 1f)
        val b = brightness.coerceIn(0f, 1f)
        val colorLong = hsvToColorLong(_uiState.value.hue, s, b)
        _uiState.value = _uiState.value.copy(
            mainColor = colorLong,
            saturation = s,
            brightness = b,
            mainColorError = null
        )
    }

    fun resetState() {
        _uiState.value = StorageUiState()
    }

    fun clearIsUpdated() {
        _uiState.value = _uiState.value.copy(isUpdated = false)
    }

    fun clearIsDeleted() {
        _uiState.value = _uiState.value.copy(isDeleted = false)
    }


    fun getById(id: Long) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                val storage = repository.getById(id)
                if (storage != null) {
                    val hsv = colorLongToHsv(storage.mainColor)
                    _uiState.value = _uiState.value.copy(
                        id = storage.id,
                        title = storage.title,
                        mainColor = storage.mainColor,
                        hue = hsv[0],
                        saturation = hsv[1],
                        brightness = hsv[2],
                        createdAt = storage.createdAt,
                        isLoading = false
                    )
                } else {
                    throw StorageException.NotFoundException()
                }
            } catch (e: StorageException) {
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

    fun createStorage() {
        val state = _uiState.value

        viewModelScope.launch {
            try {
                if (state.title.isBlank()) {
                    throw StorageException.EmptyTitleException()
                }

                _uiState.value = state.copy(isLoading = true)

                repository.insert(
                    Storage(
                        title = state.title.trim(),
                        mainColor = state.mainColor
                    )
                )

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isCreated = true
                )

            } catch (e: StorageException) {
                _uiState.value = _uiState.value.copy(isLoading = false)
                _uiState.value = when (e) {
                    is StorageException.EmptyTitleException ->
                        _uiState.value.copy(titleError = e.message)

                    is StorageException.ItemUnknownException ->
                        _uiState.value.copy(error = e.message)

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

    fun updateStorage() {
        val state = _uiState.value

        viewModelScope.launch {
            try {
                if (state.title.isBlank()) {
                    throw StorageException.EmptyTitleException()
                }

                if (state.id == 0L) {
                    throw StorageException.InvalidIdException()
                }

                _uiState.value = state.copy(isLoading = true)

                repository.update(
                    Storage(
                        id = state.id,
                        title = state.title.trim(),
                        mainColor = state.mainColor,
                        createdAt = state.createdAt
                    )
                )

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isUpdated = true
                )
            } catch (e: StorageException) {
                _uiState.value = _uiState.value.copy(isLoading = false)
                _uiState.value = when (e) {
                    is StorageException.EmptyTitleException ->
                        _uiState.value.copy(titleError = e.message)

                    is StorageException.InvalidIdException ->
                        _uiState.value.copy(error = e.message)

                    is StorageException.ItemUnknownException ->
                        _uiState.value.copy(error = e.message)

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

    fun deleteStorage(storage: Storage) {
        viewModelScope.launch {
            try {
                if (storage.id == 0L) throw StorageException.InvalidIdException()

                _uiState.value = _uiState.value.copy(isLoading = true)
                repository.delete(storage)
                _uiState.value = _uiState.value.copy(isLoading = false, isDeleted = true)
            } catch (e: StorageException) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = StorageException.NotFoundException().message
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
