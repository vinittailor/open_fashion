import 'package:file_picker/file_picker.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../../core/theme/app_colors.dart';
import '../../../../core/theme/app_typography.dart';
import '../../domain/models/file_model.dart';
import '../controllers/file_upload_controller.dart';

/// Reusable Luxury File & Image Upload Widget for Open Fashion Admin
class LuxuryFileUploadWidget extends ConsumerWidget {
  final String label;
  final String hintText;
  final double height;
  final FileModel? initialFile;
  final ValueChanged<FileModel?>? onFileChanged;

  const LuxuryFileUploadWidget({
    super.key,
    this.label = 'UPLOAD IMAGE',
    this.hintText = 'Click to select JPG, PNG, WEBP, or HEIC (Max 5MB)',
    this.height = 180,
    this.initialFile,
    this.onFileChanged,
  });

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final isDark = Theme.of(context).brightness == Brightness.dark;
    final state = ref.watch(fileUploadControllerProvider);
    final controller = ref.read(fileUploadControllerProvider.notifier);

    // Active file is either freshly uploaded file or initial file passed in
    final activeFile = state.uploadedFile ?? initialFile;

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        if (label.isNotEmpty) ...[
          Text(label, style: AppTypography.labelLarge(isDark)),
          const SizedBox(height: 8),
        ],

        Container(
          width: double.infinity,
          height: height,
          decoration: BoxDecoration(
            color: isDark ? AppColors.surfaceDark : AppColors.surfaceLight,
            borderRadius: BorderRadius.circular(12),
            border: Border.all(
              color: state.isUploading
                  ? AppColors.accent
                  : (isDark ? AppColors.borderDark : AppColors.borderLight),
              width: state.isUploading ? 2 : 1,
            ),
          ),
          child: ClipRRect(
            borderRadius: BorderRadius.circular(12),
            child: activeFile != null
                ? _buildUploadedPreview(context, isDark, activeFile, controller)
                : state.isUploading
                    ? _buildUploadingState(isDark, state.progress)
                    : _buildDropzonePicker(context, isDark, controller),
          ),
        ),

        // Error message notification
        if (state.errorMessage != null) ...[
          const SizedBox(height: 6),
          Row(
            children: [
              const Icon(Icons.error_outline, color: AppColors.error, size: 14),
              const SizedBox(width: 4),
              Expanded(
                child: Text(
                  state.errorMessage!,
                  style: const TextStyle(color: AppColors.error, fontSize: 12),
                ),
              ),
            ],
          ),
        ],
      ],
    );
  }

  Widget _buildDropzonePicker(
    BuildContext context,
    bool isDark,
    FileUploadController controller,
  ) {
    return InkWell(
      onTap: () => _pickAndUpload(controller),
      borderRadius: BorderRadius.circular(12),
      child: Center(
        child: Padding(
          padding: const EdgeInsets.all(16.0),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Container(
                padding: const EdgeInsets.all(12),
                decoration: BoxDecoration(
                  color: AppColors.accent.withValues(alpha: 0.12),
                  shape: BoxShape.circle,
                ),
                child: const Icon(
                  Icons.cloud_upload_outlined,
                  color: AppColors.accent,
                  size: 28,
                ),
              ),
              const SizedBox(height: 12),
              Text(
                'CHOOSE AN IMAGE',
                style: TextStyle(
                  color: isDark ? AppColors.textPrimaryDark : AppColors.textPrimaryLight,
                  fontWeight: FontWeight.bold,
                  fontSize: 13,
                  letterSpacing: 0.5,
                ),
              ),
              const SizedBox(height: 4),
              Text(
                hintText,
                textAlign: TextAlign.center,
                style: AppTypography.bodySmall(isDark),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildUploadingState(bool isDark, double progress) {
    final percentage = (progress * 100).toInt();
    return Center(
      child: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 32.0),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text(
              'UPLOADING ASSET · $percentage%',
              style: TextStyle(
                color: AppColors.accent,
                fontWeight: FontWeight.bold,
                fontSize: 12,
                letterSpacing: 0.5,
              ),
            ),
            const SizedBox(height: 12),
            ClipRRect(
              borderRadius: BorderRadius.circular(4),
              child: LinearProgressIndicator(
                value: progress > 0 ? progress : null,
                minHeight: 6,
                backgroundColor: isDark ? AppColors.cardDark : AppColors.backgroundLight,
                valueColor: const AlwaysStoppedAnimation<Color>(AppColors.accent),
              ),
            ),
            const SizedBox(height: 8),
            Text(
              'Streaming binary bytes to media storage...',
              style: AppTypography.bodySmall(isDark),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildUploadedPreview(
    BuildContext context,
    bool isDark,
    FileModel file,
    FileUploadController controller,
  ) {
    return Stack(
      children: [
        // File Content Layout
        Positioned.fill(
          child: Padding(
            padding: const EdgeInsets.all(16.0),
            child: Row(
              children: [
                // Image Thumbnail
                ClipRRect(
                  borderRadius: BorderRadius.circular(8),
                  child: Container(
                    width: 120,
                    height: double.infinity,
                    color: isDark ? AppColors.cardDark : AppColors.backgroundLight,
                    child: Image.network(
                      file.url,
                      fit: BoxFit.cover,
                      errorBuilder: (_, __, ___) => const Center(
                        child: Icon(Icons.broken_image_outlined, color: AppColors.textSecondaryLight),
                      ),
                      loadingBuilder: (context, child, progress) {
                        if (progress == null) return child;
                        return const Center(
                          child: CircularProgressIndicator(
                            strokeWidth: 2,
                            color: AppColors.accent,
                          ),
                        );
                      },
                    ),
                  ),
                ),
                const SizedBox(width: 16),

                // File Metadata Info
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Row(
                        children: [
                          const Icon(Icons.check_circle, color: AppColors.success, size: 16),
                          const SizedBox(width: 6),
                          Text(
                            'UPLOAD COMPLETE',
                            style: TextStyle(
                              color: AppColors.success,
                              fontWeight: FontWeight.bold,
                              fontSize: 11,
                              letterSpacing: 0.5,
                            ),
                          ),
                        ],
                      ),
                      const SizedBox(height: 6),
                      Text(
                        file.filename,
                        maxLines: 1,
                        overflow: TextOverflow.ellipsis,
                        style: AppTypography.labelLarge(isDark),
                      ),
                      const SizedBox(height: 4),
                      Row(
                        children: [
                          Container(
                            padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                            decoration: BoxDecoration(
                              color: AppColors.accent.withValues(alpha: 0.15),
                              borderRadius: BorderRadius.circular(4),
                            ),
                            child: Text(
                              file.formattedSize,
                              style: const TextStyle(
                                color: AppColors.accent,
                                fontWeight: FontWeight.bold,
                                fontSize: 10,
                              ),
                            ),
                          ),
                          const SizedBox(width: 8),
                          Text(
                            file.mimeType,
                            style: AppTypography.bodySmall(isDark),
                          ),
                        ],
                      ),
                    ],
                  ),
                ),
              ],
            ),
          ),
        ),

        // Remove / Replace Button in top-right
        Positioned(
          top: 8,
          right: 8,
          child: IconButton(
            icon: const Icon(Icons.close, size: 18),
            tooltip: 'Remove Image',
            style: IconButton.styleFrom(
              backgroundColor: isDark ? AppColors.cardDark : AppColors.backgroundLight,
              foregroundColor: AppColors.error,
            ),
            onPressed: () {
              controller.clear();
              if (onFileChanged != null) {
                onFileChanged!(null);
              }
            },
          ),
        ),
      ],
    );
  }

  Future<void> _pickAndUpload(FileUploadController controller) async {
    try {
      final result = await FilePicker.platform.pickFiles(
        type: FileType.custom,
        allowedExtensions: ['jpg', 'jpeg', 'png', 'webp', 'gif', 'avif', 'heic'],
        withData: true,
      );

      if (result != null && result.files.isNotEmpty) {
        final picked = result.files.first;
        final uploaded = await controller.uploadPickedFile(picked);
        if (uploaded != null && onFileChanged != null) {
          onFileChanged!(uploaded);
        }
      }
    } catch (e) {
      debugPrint('File picker error: $e');
    }
  }
}
