package com.example.open_fashion.features.media.data.remote.dto

import com.example.open_fashion.features.media.domain.model.FileItem
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data Transfer Object representing a single file entity from the backend.
 */
@Serializable
data class FileItemDto(
    @SerialName("id") val id: String,
    @SerialName("filename") val filename: String,
    @SerialName("key") val key: String? = null,
    @SerialName("url") val url: String,
    @SerialName("mimeType") val mimeType: String? = "image/jpeg",
    @SerialName("sizeBytes") val sizeBytes: Long? = 0L
) {
    /**
     * Maps the network DTO into a pure domain [FileItem].
     */
    fun toDomain(): FileItem {
        return FileItem(
            id = id,
            filename = filename,
            url = url,
            mimeType = mimeType ?: "image/jpeg",
            sizeBytes = sizeBytes ?: 0L
        )
    }
}

/**
 * Data wrapper envelope for file upload response.
 */
@Serializable
data class FileUploadResponseDataDto(
    @SerialName("file") val file: FileItemDto
)

/**
 * Root API response envelope for POST /api/v1/files/upload.
 */
@Serializable
data class FileUploadResponseDto(
    @SerialName("success") val success: Boolean,
    @SerialName("message") val message: String? = null,
    @SerialName("data") val data: FileUploadResponseDataDto
)
