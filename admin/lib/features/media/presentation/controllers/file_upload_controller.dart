import 'package:file_picker/file_picker.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../data/repositories/file_repository_impl.dart';
import '../../domain/models/file_model.dart';
import '../../domain/repositories/file_repository.dart';

/// Immutable State for File Uploading
class FileUploadState {
  final bool isUploading;
  final double progress; // 0.0 to 1.0
  final FileModel? uploadedFile;
  final String? errorMessage;
  final String? successMessage;

  const FileUploadState({
    this.isUploading = false,
    this.progress = 0.0,
    this.uploadedFile,
    this.errorMessage,
    this.successMessage,
  });

  factory FileUploadState.initial() => const FileUploadState();

  FileUploadState copyWith({
    bool? isUploading,
    double? progress,
    FileModel? uploadedFile,
    String? errorMessage,
    String? successMessage,
    bool clearUploadedFile = false,
    bool clearError = false,
    bool clearSuccess = false,
  }) {
    return FileUploadState(
      isUploading: isUploading ?? this.isUploading,
      progress: progress ?? this.progress,
      uploadedFile: clearUploadedFile ? null : (uploadedFile ?? this.uploadedFile),
      errorMessage: clearError ? null : (errorMessage ?? this.errorMessage),
      successMessage: clearSuccess ? null : (successMessage ?? this.successMessage),
    );
  }
}

/// StateNotifier managing the upload lifecycle
class FileUploadController extends StateNotifier<FileUploadState> {
  final FileRepository _repository;

  FileUploadController(this._repository) : super(FileUploadState.initial());

  /// Handles picking and uploading an image file from the device
  Future<FileModel?> uploadPickedFile(PlatformFile pickedFile) async {
    final bytes = pickedFile.bytes;
    if (bytes == null || bytes.isEmpty) {
      state = state.copyWith(
        errorMessage: 'Unable to read file contents from picker',
        clearSuccess: true,
      );
      return null;
    }

    state = state.copyWith(
      isUploading: true,
      progress: 0.0,
      clearError: true,
      clearSuccess: true,
    );

    try {
      final file = await _repository.uploadFile(
        filename: pickedFile.name,
        bytes: bytes,
        onProgress: (sent, total) {
          if (total > 0) {
            state = state.copyWith(progress: sent / total);
          }
        },
      );

      state = state.copyWith(
        isUploading: false,
        progress: 1.0,
        uploadedFile: file,
        successMessage: 'File uploaded successfully!',
      );

      return file;
    } catch (e) {
      state = state.copyWith(
        isUploading: false,
        progress: 0.0,
        errorMessage: e.toString().replaceAll('Exception: ', ''),
      );
      return null;
    }
  }

  /// Removes currently uploaded file
  void clear() {
    state = FileUploadState.initial();
  }
}

/// Provider for FileUploadController
final fileUploadControllerProvider =
    StateNotifierProvider.autoDispose<FileUploadController, FileUploadState>((ref) {
  final repository = ref.watch(fileRepositoryProvider);
  return FileUploadController(repository);
});
