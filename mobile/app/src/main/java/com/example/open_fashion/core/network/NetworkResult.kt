package com.example.open_fashion.core.network

/**
 * Standard Clean Architecture generic result wrapper for network & data operations.
 */
sealed interface NetworkResult<out T> {
    data class Success<out T>(val data: T) : NetworkResult<T>
    data class Error(val code: String? = null, val message: String, val throwable: Throwable? = null) : NetworkResult<Nothing>
    data object Loading : NetworkResult<Nothing>

    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error
    val isLoading: Boolean get() = this is Loading

    fun getOrNull(): T? = (this as? Success)?.data
}
