import '../models/file_model.dart';

/// Abstract domain repository for media & file operations
abstract class FileRepository {
  /// Uploads a single file stream with real-time upload progress reporting
  Future<FileModel> uploadFile({
    required String filename,
    required List<int> bytes,
    void Function(int sentBytes, int totalBytes)? onProgress,
  });

  /// Retrieves file metadata by ID
  Future<FileModel> fetchFileById(String fileId);

  /// Deletes a file by ID
  Future<void> deleteFile(String fileId);
}
