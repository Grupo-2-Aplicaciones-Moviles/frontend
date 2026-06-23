package weTech.weRide.utils

/**
 * A wrapper class for handling different states of API responses
 */
sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error(val message: String, val code: Int? = null) : Resource<Nothing>()
    object Loading : Resource<Nothing>()

    fun isSuccess(): Boolean = this is Success
    fun isError(): Boolean = this is Error
    fun isLoading(): Boolean = this is Loading

    fun getErrorMessage(): String? = if (this is Error) message else null
}

/**
 * Extension to safely extract data without conflicting with Success.data's getter.
 */
fun <T> Resource<T>.getData(): T? = if (this is Resource.Success) data else null

/**
 * Extension function to wrap API calls in Resource
 */
suspend fun <T> wrapResource(apiCall: suspend () -> T): Resource<T> {
    return try {
        Resource.Success(apiCall())
    } catch (e: Exception) {
        Resource.Error(e.message ?: "Unknown error occurred")
    }
}
