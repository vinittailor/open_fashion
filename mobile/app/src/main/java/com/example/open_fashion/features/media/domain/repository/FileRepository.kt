package com.example.open_fashion.features.media.domain.repository

import android.net.Uri
import com.example.open_fashion.features.media.domain.model.FileItem

/**
 * Domain repository interface for media and file storage operations.
 */
interface FileRepository {

    /**
     * Uploads an image file selected from the device storage.
     *
     * @param uri The Android content Uri returned by Photo Picker
     * @return [Result] containing the uploaded [FileItem] or an exception on error
     */
    suspend fun uploadFile(uri: Uri): Result<FileItem>

    /**
     * Retrieves file metadata by its UUID.
     *
     * @param fileId UUID of the file
     * @return [Result] containing [FileItem] or an exception
     */
    suspend fun getFileById(fileId: String): Result<FileItem>
}
