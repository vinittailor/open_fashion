import 'package:dio/dio.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../../core/network/api_client.dart';
import '../../domain/models/file_model.dart';
import '../../domain/repositories/file_repository.dart';

/// Provider for FileRepository
final fileRepositoryProvider = Provider<FileRepository>((ref) {
  final dio = ref.watch(dioProvider);
  return FileRepositoryImpl(dio);
});

/// Concrete implementation of FileRepository using Dio
class FileRepositoryImpl implements FileRepository {
  final Dio _dio;

  FileRepositoryImpl(this._dio);

  @override
  Future<FileModel> uploadFile({
    required String filename,
    required List<int> bytes,
    void Function(int sentBytes, int totalBytes)? onProgress,
  }) async {
    try {
      final formData = FormData.fromMap({
        'file': MultipartFile.fromBytes(
          bytes,
          filename: filename,
        ),
      });

      final response = await _dio.post(
        '/files/upload',
        data: formData,
        onSendProgress: (sent, total) {
          if (onProgress != null && total > 0) {
            onProgress(sent, total);
          }
        },
      );

      final fileData = response.data['data']['file'] as Map<String, dynamic>;
      return FileModel.fromJson(fileData);
    } on DioException catch (e) {
      final message = e.response?.data?['error']?['message'] ??
          e.response?.data?['message'] ??
          'File upload failed';
      throw Exception(message);
    }
  }

  @override
  Future<FileModel> fetchFileById(String fileId) async {
    try {
      final response = await _dio.get('/files/$fileId');
      final fileData = response.data['data']['file'] as Map<String, dynamic>;
      return FileModel.fromJson(fileData);
    } on DioException catch (e) {
      final message = e.response?.data?['error']?['message'] ??
          e.response?.data?['message'] ??
          'Failed to fetch file metadata';
      throw Exception(message);
    }
  }

  @override
  Future<void> deleteFile(String fileId) async {
    try {
      await _dio.delete('/files/$fileId');
    } on DioException catch (e) {
      final message = e.response?.data?['error']?['message'] ??
          e.response?.data?['message'] ??
          'Failed to delete file';
      throw Exception(message);
    }
  }
}
