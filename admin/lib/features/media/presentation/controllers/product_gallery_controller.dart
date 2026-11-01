import 'package:file_picker/file_picker.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../data/repositories/file_repository_impl.dart';
import '../../domain/models/file_model.dart';
import '../../domain/models/gallery_image_item.dart';
import '../../domain/repositories/file_repository.dart';

/// State of the Product Media Gallery
class ProductGalleryState {
  final List<GalleryImageItem> items;
  final bool isUploading;
  final double uploadProgress;
  final String? errorMessage;
  final String? successMessage;

  const ProductGalleryState({
    this.items = const [],
    this.isUploading = false,
    this.uploadProgress = 0.0,
    this.errorMessage,
    this.successMessage,
  });

  ProductGalleryState copyWith({
    List<GalleryImageItem>? items,
    bool? isUploading,
    double? uploadProgress,
    String? errorMessage,
    String? successMessage,
    bool clearError = false,
    bool clearSuccess = false,
  }) {
    return ProductGalleryState(
      items: items ?? this.items,
      isUploading: isUploading ?? this.isUploading,
      uploadProgress: uploadProgress ?? this.uploadProgress,
      errorMessage: clearError ? null : (errorMessage ?? this.errorMessage),
      successMessage: clearSuccess ? null : (successMessage ?? this.successMessage),
    );
  }
}

/// StateNotifier managing product gallery batch uploads, reordering, and primary selection
class ProductGalleryController extends StateNotifier<ProductGalleryState> {
  final FileRepository _repository;

  ProductGalleryController(this._repository) : super(const ProductGalleryState());

  /// Sets initial gallery items from existing product records
  void setInitialImages(List<FileModel> files, {String? primaryFileId}) {
    final items = files.asMap().entries.map((entry) {
      final index = entry.key;
      final file = entry.value;
      final isPrimary = primaryFileId != null ? file.id == primaryFileId : index == 0;
      return GalleryImageItem(
        file: file,
        isPrimary: isPrimary,
        sortOrder: index,
      );
    }).toList();

    state = state.copyWith(items: items);
  }

  /// Uploads a batch of picked files and appends them to the gallery
  Future<void> uploadFiles(List<PlatformFile> pickedFiles, {String? productId}) async {
    final validFiles = pickedFiles.where((f) => f.bytes != null && f.bytes!.isNotEmpty).toList();
    if (validFiles.isEmpty) {
      state = state.copyWith(errorMessage: 'No valid files selected for upload');
      return;
    }

    state = state.copyWith(
      isUploading: true,
      uploadProgress: 0.0,
      clearError: true,
      clearSuccess: true,
    );

    try {
      final payload = validFiles.map((f) => (filename: f.name, bytes: f.bytes!)).toList();
      final uploadedFiles = await _repository.uploadMultipleFiles(
        files: payload,
        productId: productId,
        onProgress: (sent, total) {
          if (total > 0) {
            state = state.copyWith(uploadProgress: sent / total);
          }
        },
      );

      final currentItems = List<GalleryImageItem>.from(state.items);
      final hasPrimary = currentItems.any((item) => item.isPrimary);

      for (int i = 0; i < uploadedFiles.length; i++) {
        final file = uploadedFiles[i];
        final isPrimary = !hasPrimary && i == 0;
        currentItems.add(
          GalleryImageItem(
            file: file,
            isPrimary: isPrimary,
            sortOrder: currentItems.length,
          ),
        );
      }

      state = state.copyWith(
        items: currentItems,
        isUploading: false,
        uploadProgress: 1.0,
        successMessage: '${uploadedFiles.length} image(s) uploaded successfully!',
      );
    } catch (e) {
      state = state.copyWith(
        isUploading: false,
        uploadProgress: 0.0,
        errorMessage: e.toString().replaceAll('Exception: ', ''),
      );
    }
  }

  /// Sets an image as the primary cover photo
  void setPrimary(String fileId) {
    final updated = state.items.map((item) {
      return item.copyWith(isPrimary: item.file.id == fileId);
    }).toList();

    state = state.copyWith(items: updated);
  }

  /// Reorders items in the gallery after drag-and-drop
  void reorder(int oldIndex, int newIndex) {
    if (oldIndex < 0 || oldIndex >= state.items.length) return;

    final updated = List<GalleryImageItem>.from(state.items);
    if (oldIndex < newIndex) {
      newIndex -= 1;
    }
    final item = updated.removeAt(oldIndex);
    updated.insert(newIndex, item);

    final reordered = updated.asMap().entries.map((entry) {
      return entry.value.copyWith(sortOrder: entry.key);
    }).toList();

    state = state.copyWith(items: reordered);
  }

  /// Removes an image from the gallery
  void removeImage(String fileId) {
    final updated = state.items.where((item) => item.file.id != fileId).toList();
    if (updated.isNotEmpty && !updated.any((item) => item.isPrimary)) {
      updated[0] = updated[0].copyWith(isPrimary: true);
    }

    final reordered = updated.asMap().entries.map((entry) {
      return entry.value.copyWith(sortOrder: entry.key);
    }).toList();

    state = state.copyWith(items: reordered);
  }
}

/// Provider for ProductGalleryController
final productGalleryProvider = StateNotifierProvider<ProductGalleryController, ProductGalleryState>((ref) {
  final repository = ref.watch(fileRepositoryProvider);
  return ProductGalleryController(repository);
});
