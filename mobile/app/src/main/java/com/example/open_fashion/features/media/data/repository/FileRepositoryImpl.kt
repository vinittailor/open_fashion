package com.example.open_fashion.features.media.data.repository

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.example.open_fashion.features.media.data.remote.FileApiService
import com.example.open_fashion.features.media.domain.model.FileItem
import com.example.open_fashion.features.media.domain.repository.FileRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

/**
 * Concrete implementation of [FileRepository] bridging Android ContentResolver with Retrofit.
 */
class FileRepositoryImpl(
    private val context: Context,
    private val fileApiService: FileApiService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : FileRepository {

    override suspend fun uploadFile(uri: Uri): Result<FileItem> = withContext(ioDispatcher) {
        try {
            val contentResolver = context.contentResolver

            // 1. Resolve original filename from Android ContentResolver
            val filename = resolveFileName(uri) ?: "upload_${System.currentTimeMillis()}.jpg"

            // 2. Resolve MIME type (e.g. image/jpeg, image/png)
            val mimeType = contentResolver.getType(uri) ?: "image/jpeg"

            // 3. Read binary bytes safely using use {} for automatic stream closing
            val bytes = contentResolver.openInputStream(uri)?.use { stream ->
                stream.readBytes()
            } ?: return@withContext Result.failure(Exception("Failed to read image stream from device"))

            // 4. Build OkHttp RequestBody & MultipartBody.Part
            val requestBody = bytes.toRequestBody(mimeType.toMediaTypeOrNull())
            val multipartPart = MultipartBody.Part.createFormData("file", filename, requestBody)

            // 5. Execute HTTP request via Retrofit
            val response = fileApiService.uploadSingleFile(multipartPart)

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    Result.success(body.data.file.toDomain())
                } else {
                    Result.failure(Exception(body?.message ?: "Upload response contained empty data"))
                }
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Server returned HTTP ${response.code()}"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getFileById(fileId: String): Result<FileItem> = withContext(ioDispatcher) {
        try {
            val response = fileApiService.getFileById(fileId)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    Result.success(body.data.file.toDomain())
                } else {
                    Result.failure(Exception(body?.message ?: "File not found"))
                }
            } else {
                Result.failure(Exception("Failed to fetch file metadata (HTTP ${response.code()})"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Helper to query the user's original display filename from a content:// URI.
     */
    private fun resolveFileName(uri: Uri): String? {
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1) {
                        return it.getString(nameIndex)
                    }
                }
            }
        }
        return uri.path?.substringAfterLast('/')
    }
}
