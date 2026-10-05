package com.example.open_fashion.features.media.domain.model

/**
 * Pure domain model representing an uploaded file/media asset in Open Fashion.
 * Free from Retrofit, Serialization, or Android framework dependencies.
 *
 * @property id Unique UUID of the file record
 * @property filename Unique filename stored on the backend
 * @property url Fully accessible public URL (e.g. "http://10.0.2.2:5000/uploads/avatars/...")
 * @property mimeType Media MIME descriptor (e.g. "image/jpeg", "image/webp")
 * @property sizeBytes Size of the file in bytes
 */
data class FileItem(
    val id: String,
    val filename: String,
    val url: String,
    val mimeType: String,
    val sizeBytes: Long
)
