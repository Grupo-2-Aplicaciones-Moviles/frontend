package weTech.weRide.utils

import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLException

/**
 * Error handling utility for consistent error messages across the app
 */
object ErrorHandler {

    /**
     * Get user-friendly error message from exception
     */
    fun getErrorMessage(exception: Throwable): String {
        return when (exception) {
            is UnknownHostException -> "No hay conexión a internet. Verifica tu red."
            is SocketTimeoutException -> "Tiempo de espera agotado. Inténtalo nuevamente."
            is ConnectException -> "No se pudo conectar al servidor. Verifica tu conexión."
            is SSLException -> "Error de conexión segura. Inténtalo más tarde."
            is IOException -> "Error de red. Verifica tu conexión a internet."
            else -> getGenericErrorMessage(exception)
        }
    }

    /**
     * Get generic error message based on exception type
     */
    private fun getGenericErrorMessage(exception: Throwable): String {
        return when {
            exception.message?.contains("HTTP 401") == true -> "Sesión expirada. Inicia sesión nuevamente."
            exception.message?.contains("HTTP 403") == true -> "No tienes permiso para realizar esta acción."
            exception.message?.contains("HTTP 404") == true -> "Recurso no encontrado."
            exception.message?.contains("HTTP 500") == true -> "Error del servidor. Inténtalo más tarde."
            exception.message?.contains("HTTP 503") == true -> "Servicio no disponible. Inténtalo más tarde."
            exception.message?.isNotEmpty() == true -> exception.message ?: "Error desconocido"
            else -> "Ocurrió un error inesperado. Inténtalo nuevamente."
        }
    }

    /**
     * Check if error is recoverable (should show retry button)
     */
    fun isRecoverable(exception: Throwable): Boolean {
        return when (exception) {
            is UnknownHostException,
            is SocketTimeoutException,
            is ConnectException,
            is SSLException,
            is IOException -> true
            else -> {
                val message = exception.message ?: ""
                !message.contains("HTTP 401") &&
                !message.contains("HTTP 403") &&
                !message.contains("HTTP 404")
            }
        }
    }

    /**
     * Get error icon type
     */
    fun getErrorIconType(exception: Throwable): ErrorIconType {
        return when (exception) {
            is UnknownHostException,
            is SocketTimeoutException,
            is ConnectException,
            is IOException -> ErrorIconType.NETWORK
            is SSLException -> ErrorIconType.SECURITY
            else -> {
                val message = exception.message ?: ""
                when {
                    message.contains("HTTP 401") -> ErrorIconType.AUTH
                    message.contains("HTTP 403") -> ErrorIconType.PERMISSION
                    message.contains("HTTP 404") -> ErrorIconType.NOT_FOUND
                    message.contains("HTTP 500") -> ErrorIconType.SERVER
                    else -> ErrorIconType.GENERIC
                }
            }
        }
    }

    /**
     * Get error title
     */
    fun getErrorTitle(exception: Throwable): String {
        return when (val type = getErrorIconType(exception)) {
            ErrorIconType.NETWORK -> "Error de conexión"
            ErrorIconType.SECURITY -> "Error de seguridad"
            ErrorIconType.AUTH -> "Sesión expirada"
            ErrorIconType.PERMISSION -> "Acceso denegado"
            ErrorIconType.NOT_FOUND -> "No encontrado"
            ErrorIconType.SERVER -> "Error del servidor"
            ErrorIconType.GENERIC -> "Error"
        }
    }

    /**
     * Parse HTTP error code from exception
     */
    fun parseHttpCode(exception: Throwable): Int? {
        val message = exception.message ?: return null
        val regex = "HTTP (\\d{3})".toRegex()
        val match = regex.find(message)
        return match?.groupValues?.get(1)?.toIntOrNull()
    }

    /**
     * Check if error is authentication related
     */
    fun isAuthError(exception: Throwable): Boolean {
        return getErrorIconType(exception) == ErrorIconType.AUTH
    }

    /**
     * Check if error is server related (5xx)
     */
    fun isServerError(exception: Throwable): Boolean {
        val code = parseHttpCode(exception)
        return code != null && code >= 500 && code < 600
    }

    /**
     * Check if error is client related (4xx, except 401)
     */
    fun isClientError(exception: Throwable): Boolean {
        val code = parseHttpCode(exception)
        return code != null && code >= 400 && code < 500 && code != 401
    }
}

/**
 * Error icon types for UI
 */
enum class ErrorIconType {
    NETWORK,
    SECURITY,
    AUTH,
    PERMISSION,
    NOT_FOUND,
    SERVER,
    GENERIC
}

/**
 * Extension function to get user-friendly message for Resource.Error
 */
fun <T> Resource<T>.getUserMessage(): String? {
    return if (this is Resource.Error) {
        ErrorHandler.getErrorMessage(Exception(this.message))
    } else null
}

/**
 * Extension function to check if Resource.Error is recoverable
 */
fun <T> Resource<T>.isRecoverable(): Boolean {
    return if (this is Resource.Error) {
        ErrorHandler.isRecoverable(Exception(this.message))
    } else false
}

/**
 * Extension function to get error icon type from Resource.Error
 */
fun <T> Resource<T>.getErrorIconType(): ErrorIconType? {
    return if (this is Resource.Error) {
        ErrorHandler.getErrorIconType(Exception(this.message))
    } else null
}

/**
 * Extension function to get error title from Resource.Error
 */
fun <T> Resource<T>.getErrorTitle(): String? {
    return if (this is Resource.Error) {
        ErrorHandler.getErrorTitle(Exception(this.message))
    } else null
}
