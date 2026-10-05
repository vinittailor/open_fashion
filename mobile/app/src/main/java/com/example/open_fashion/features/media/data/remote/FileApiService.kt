package com.example.open_fashion.features.media.data.remote

import com.example.open_fashion.core.constants.ApiEndpoints
import com.example.open_fashion.features.media.data.remote.dto.FileUploadResponseDto
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

/**
 * Retrofit service defining media & file upload HTTP endpoints.
 */
interface FileApiService {

    /**
     * Uploads a single image file via multipart/form-data.
     * Protected endpoint: Automatically authenticated by AuthInterceptor.
     *
     * @param file The multipart body part containing the image bytes and filename
     * @return Retrofit [Response] wrapping [FileUploadResponseDto]
     */
    @Multipart
    @POST(ApiEndpoints.FILES_UPLOAD)
    suspend fun uploadSingleFile(
        @Part file: MultipartBody.Part
    ): Response<FileUploadResponseDto>

    /**
     * Retrieves file metadata record by ID.
     */
    @GET("files/{id}")
    suspend fun getFileById(
        @Path("id") id: String
    ): Response<FileUploadResponseDto>
}
