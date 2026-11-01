import 'dart:typed_data';
import 'package:file_picker/file_picker.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:open_fashion_admin/features/media/domain/models/file_model.dart';
import 'package:open_fashion_admin/features/media/domain/repositories/file_repository.dart';
import 'package:open_fashion_admin/features/media/presentation/controllers/product_gallery_controller.dart';

class MockFileRepository implements FileRepository {
  bool shouldFail = false;

  @override
  Future<FileModel> uploadFile({
    required String filename,
    required List<int> bytes,
    void Function(int sentBytes, int totalBytes)? onProgress,
  }) async {
    throw UnimplementedError();
  }

  @override
  Future<List<FileModel>> uploadMultipleFiles({
    required List<({String filename, List<int> bytes})> files,
    String? productId,
    void Function(int sentBytes, int totalBytes)? onProgress,
  }) async {
    if (shouldFail) {
      throw Exception('Server 500 error during batch upload');
    }

    if (onProgress != null) {
      onProgress(50, 100);
      onProgress(100, 100);
    }

    return files.asMap().entries.map((entry) {
      final index = entry.key;
      final file = entry.value;
      return FileModel(
        id: 'file-$index',
        filename: file.filename,
        key: 'products/${file.filename}',
        url: 'http://127.0.0.1:5000/uploads/products/${file.filename}',
        mimeType: 'image/jpeg',
        sizeBytes: file.bytes.length,
        createdAt: DateTime.now(),
        updatedAt: DateTime.now(),
      );
    }).toList();
  }

  @override
  Future<FileModel> fetchFileById(String fileId) async {
    throw UnimplementedError();
  }

  @override
  Future<void> deleteFile(String fileId) async {}
}

void main() {
  group('ProductGalleryController Unit Tests (Micro-Slice 2.2.2)', () {
    late MockFileRepository mockRepo;
    late ProductGalleryController controller;

    setUp(() {
      mockRepo = MockFileRepository();
      controller = ProductGalleryController(mockRepo);
    });

    test('initial state has empty gallery list and default progress', () {
      expect(controller.state.items, isEmpty);
      expect(controller.state.isUploading, false);
      expect(controller.state.uploadProgress, 0.0);
    });

    test('uploadFiles successfully uploads batch and designates first image as primary', () async {
      final files = [
        PlatformFile(name: 'front.jpg', size: 100, bytes: Uint8List.fromList([1, 2, 3])),
        PlatformFile(name: 'back.jpg', size: 200, bytes: Uint8List.fromList([4, 5, 6])),
      ];

      await controller.uploadFiles(files);

      expect(controller.state.items.length, 2);
      expect(controller.state.items[0].isPrimary, true);
      expect(controller.state.items[1].isPrimary, false);
      expect(controller.state.isUploading, false);
      expect(controller.state.successMessage, contains('2 image(s) uploaded'));
    });

    test('setPrimary promotes selected image and demotes previous primary', () async {
      final files = [
        PlatformFile(name: 'front.jpg', size: 100, bytes: Uint8List.fromList([1, 2])),
        PlatformFile(name: 'back.jpg', size: 200, bytes: Uint8List.fromList([3, 4])),
      ];

      await controller.uploadFiles(files);
      expect(controller.state.items[0].isPrimary, true);
      expect(controller.state.items[1].isPrimary, false);

      // Make second image primary
      controller.setPrimary('file-1');

      expect(controller.state.items[0].isPrimary, false);
      expect(controller.state.items[1].isPrimary, true);
    });

    test('reorder swaps images and updates visual sort order', () async {
      final files = [
        PlatformFile(name: 'img0.jpg', size: 10, bytes: Uint8List.fromList([1])),
        PlatformFile(name: 'img1.jpg', size: 20, bytes: Uint8List.fromList([2])),
        PlatformFile(name: 'img2.jpg', size: 30, bytes: Uint8List.fromList([3])),
      ];

      await controller.uploadFiles(files);

      // Reorder index 0 to index 2
      controller.reorder(0, 2);

      expect(controller.state.items[0].file.filename, 'img1.jpg');
      expect(controller.state.items[0].sortOrder, 0);
      expect(controller.state.items[1].file.filename, 'img0.jpg');
      expect(controller.state.items[1].sortOrder, 1);
    });

    test('removeImage removes targeted image and reassigns primary if needed', () async {
      final files = [
        PlatformFile(name: 'primary.jpg', size: 10, bytes: Uint8List.fromList([1])),
        PlatformFile(name: 'secondary.jpg', size: 20, bytes: Uint8List.fromList([2])),
      ];

      await controller.uploadFiles(files);
      expect(controller.state.items[0].isPrimary, true);

      // Remove the primary image
      controller.removeImage('file-0');

      expect(controller.state.items.length, 1);
      expect(controller.state.items[0].file.filename, 'secondary.jpg');
      expect(controller.state.items[0].isPrimary, true); // Promoted to primary
    });

    test('uploadFiles sets error message on failure', () async {
      mockRepo.shouldFail = true;

      final files = [
        PlatformFile(name: 'test.jpg', size: 10, bytes: Uint8List.fromList([1])),
      ];

      await controller.uploadFiles(files);

      expect(controller.state.isUploading, false);
      expect(controller.state.errorMessage, contains('Server 500 error'));
    });
  });
}
