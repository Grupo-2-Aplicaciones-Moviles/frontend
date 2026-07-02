package weTech.weRide.data.models.common

import com.google.gson.annotations.SerializedName

/**
 * Wrapper for Spring Data Page responses
 */
data class PageResponse<T>(
    @SerializedName("content")
    val content: List<T>,

    @SerializedName("pageable")
    val pageable: Pageable?,

    @SerializedName("totalElements")
    val totalElements: Int,

    @SerializedName("totalPages")
    val totalPages: Int,

    @SerializedName("last")
    val last: Boolean,

    @SerializedName("first")
    val first: Boolean,

    @SerializedName("size")
    val size: Int,

    @SerializedName("number")
    val number: Int,

    @SerializedName("numberOfElements")
    val numberOfElements: Int,

    @SerializedName("empty")
    val empty: Boolean
)

data class Pageable(
    @SerializedName("pageNumber")
    val pageNumber: Int,

    @SerializedName("pageSize")
    val pageSize: Int,

    @SerializedName("sort")
    val sort: Sort?,

    @SerializedName("offset")
    val offset: Int,

    @SerializedName("paged")
    val paged: Boolean,

    @SerializedName("unpaged")
    val unpaged: Boolean
)

data class Sort(
    @SerializedName("sorted")
    val sorted: Boolean,

    @SerializedName("empty")
    val empty: Boolean,

    @SerializedName("unsorted")
    val unsorted: Boolean
)
