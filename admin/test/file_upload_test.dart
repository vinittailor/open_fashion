import 'dart:typed_data';
import 'package:file_picker/file_picker.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:open_fashion_admin/features/media/domain/models/file_model.dart';
import 'package:open_fashion_admin/features/media/domain/repositories/file_repository.dart';
import 'package:open_fashion_admin/features/media/presentation/controllers/file_upload_controller.dart';

class MockFileRepository implements FileRepository {
  bool shouldFail = false;

  @override
  Future<FileModel> uploadFile({
    required String filename,
    required List<int> bytes,
    void Function(int sentBytes, int totalBytes)? onProgress,
  }) async {
    if (shouldFail) {
      throw Exception('Network upload timeout');
    }

    // Simulate progress callback
    if (onProgress != null) {
      onProgress(50, 100);
      onProgress(100, 100);
    }

    return FileModel(
      id: 'mock-file-uuid',
      filename: filename,
      key: 'general/$filename',
      url: 'http://127.0.0.1:5000/uploads/general/$filename',
      mimeType: 'image/jpeg',
      sizeBytes: bytes.length,
      provider: 'LOCAL',
      isPublic: true,
      createdAt: DateTime.now(),
      updatedAt: DateTime.now(),
    );
  }

  @override
  Future<FileModel> fetchFileById(String fileId) async {
    return FileModel(
      id: fileId,
      filename: 'sample.jpg',
      key: 'general/sample.jpg',
      url: 'http://127.0.0.1:5000/uploads/general/sample.jpg',
      mimeType: 'image/jpeg',
      sizeBytes: 2048,
      createdAt: DateTime.now(),
      updatedAt: DateTime.now(),
    );
  }

  @override
  Future<void> deleteFile(String fileId) async {}
}

void main() {
  group('FileUploadController Unit Tests', () {
    late MockFileRepository mockRepo;
    late FileUploadController controller;

    setUp(() {
      mockRepo = MockFileRepository();
      controller = FileUploadController(mockRepo);
    });

    test('uploadPickedFile successfully streams bytes and updates state', () async {
      final sampleBytes = Uint8List.fromList([1, 2, 3, 4, 5]);
      final pickedFile = PlatformFile(
        name: 'avatar.jpg',
        size: sampleBytes.length,
        bytes: sampleBytes,
      );

      final result = await controller.uploadPickedFile(pickedFile);

      expect(result, isNotNull);
      expect(controller.state.isUploading, false);
      expect(controller.state.progress, 1.0);
      expect(controller.state.uploadedFile?.filename, 'avatar.jpg');
      expect(controller.state.successMessage, isNotNull);
    });

    test('uploadPickedFile sets errorMessage when upload throws', () async {
      mockRepo.shouldFail = true;

      final sampleBytes = Uint8List.fromList([1, 2, 3]);
      final pickedFile = PlatformFile(
        name: 'error.jpg',
        size: sampleBytes.length,
        bytes: sampleBytes,
      );

      final result = await controller.uploadPickedFile(pickedFile);

      expect(result, isNull);
      expect(controller.state.isUploading, false);
      expect(controller.state.errorMessage, contains('Network upload timeout'));
    });

    test('clear resets the upload state to initial', () async {
      final sampleBytes = Uint8List.fromList([1, 2, 3]);
      final pickedFile = PlatformFile(
        name: 'test.jpg',
        size: sampleBytes.length,
        bytes: sampleBytes,
      );

      await controller.uploadPickedFile(pickedFile);
      expect(controller.state.uploadedFile, isNotNull);

      controller.clear();
      expect(controller.state.uploadedFile, isNull);
      expect(controller.state.progress, 0.0);
    });
  });
}
