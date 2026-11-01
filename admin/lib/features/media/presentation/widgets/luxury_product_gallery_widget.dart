import 'package:file_picker/file_picker.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../../core/theme/app_colors.dart';
import '../../../../core/widgets/luxury_badge.dart';
import '../../../../core/widgets/luxury_button.dart';
import '../../../../core/widgets/luxury_card.dart';
import '../controllers/product_gallery_controller.dart';

/// Reusable Luxury Product Media Gallery Widget with Drag-and-Drop Reordering
/// and Primary Image Selection. Adheres to ADR-007 design system standards.
class LuxuryProductGalleryWidget extends ConsumerWidget {
  final String? productId;

  const LuxuryProductGalleryWidget({
    super.key,
    this.productId,
  });

  Future<void> _pickAndUpload(BuildContext context, WidgetRef ref) async {
    try {
      final result = await FilePicker.platform.pickFiles(
        type: FileType.custom,
        allowedExtensions: ['jpg', 'jpeg', 'png', 'webp', 'gif', 'avif', 'heic'],
        allowMultiple: true,
        withData: true,
      );

      if (result != null && result.files.isNotEmpty) {
        await ref.read(productGalleryProvider.notifier).uploadFiles(
              result.files,
              productId: productId,
            );
      }
    } catch (e) {
      if (context.mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text('Failed to pick images: $e'),
            backgroundColor: AppColors.error,
          ),
        );
      }
    }
  }

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final state = ref.watch(productGalleryProvider);
    final controller = ref.read(productGalleryProvider.notifier);

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        // 1. Header Bar: Title + Upload Button
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  'PRODUCT MEDIA GALLERY',
                  style: Theme.of(context).textTheme.titleSmall?.copyWith(
                        letterSpacing: 2.0,
                        fontWeight: FontWeight.w600,
                        color: AppColors.textPrimaryLight,
                      ),
                ),
                const SizedBox(height: 4),
                Text(
                  'Drag items to reorder. First image is default cover photo.',
                  style: Theme.of(context).textTheme.bodySmall?.copyWith(
                        color: AppColors.textSecondaryLight,
                      ),
                ),
              ],
            ),
            LuxuryButton(
              text: 'Upload Images',
              icon: Icons.add_photo_alternate_outlined,
              variant: LuxuryButtonVariant.secondary,
              height: 40,
              isLoading: state.isUploading,
              onPressed: state.isUploading ? null : () => _pickAndUpload(context, ref),
            ),
          ],
        ),

        const SizedBox(height: 16),

        // 2. Upload Progress Bar (when uploading)
        if (state.isUploading) ...[
          ClipRRect(
            borderRadius: BorderRadius.circular(2),
            child: LinearProgressIndicator(
              value: state.uploadProgress > 0 ? state.uploadProgress : null,
              backgroundColor: AppColors.borderLight,
              valueColor: const AlwaysStoppedAnimation<Color>(AppColors.accent),
              minHeight: 4,
            ),
          ),
          const SizedBox(height: 16),
        ],

        // 3. Status Alerts (Error / Success)
        if (state.errorMessage != null) ...[
          Container(
            padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
            decoration: BoxDecoration(
              color: AppColors.error.withValues(alpha: 0.08),
              borderRadius: BorderRadius.circular(4),
              border: Border.all(color: AppColors.error.withValues(alpha: 0.3)),
            ),
            child: Row(
              children: [
                const Icon(Icons.error_outline, color: AppColors.error, size: 20),
                const SizedBox(width: 12),
                Expanded(
                  child: Text(
                    state.errorMessage!,
                    style: const TextStyle(color: AppColors.error, fontSize: 13),
                  ),
                ),
              ],
            ),
          ),
          const SizedBox(height: 16),
        ],

        // 4. Gallery Content: Empty State vs Reorderable Grid
        if (state.items.isEmpty && !state.isUploading)
          LuxuryCard(
            padding: const EdgeInsets.symmetric(vertical: 48, horizontal: 24),
            child: Center(
              child: Column(
                mainAxisSize: MainAxisSize.min,
                children: [
                  Container(
                    padding: const EdgeInsets.all(16),
                    decoration: BoxDecoration(
                      color: AppColors.accentLight.withValues(alpha: 0.3),
                      shape: BoxShape.circle,
                    ),
                    child: const Icon(
                      Icons.cloud_upload_outlined,
                      size: 36,
                      color: AppColors.accent,
                    ),
                  ),
                  const SizedBox(height: 16),
                  const Text(
                    'No Product Images Uploaded',
                    style: TextStyle(
                      fontSize: 16,
                      fontWeight: FontWeight.w600,
                      color: AppColors.textPrimaryLight,
                    ),
                  ),
                  const SizedBox(height: 6),
                  const Text(
                    'Upload high-resolution WEBP, PNG, or JPEG files for the store catalog.',
                    style: TextStyle(
                      fontSize: 13,
                      color: AppColors.textSecondaryLight,
                    ),
                    textAlign: TextAlign.center,
                  ),
                  const SizedBox(height: 20),
                  LuxuryButton(
                    text: 'Select Images',
                    icon: Icons.folder_open,
                    variant: LuxuryButtonVariant.outline,
                    height: 42,
                    onPressed: () => _pickAndUpload(context, ref),
                  ),
                ],
              ),
            ),
          )
        else
          // Reorderable wrap/list of luxury image cards
          ReorderableListView.builder(
            shrinkWrap: true,
            physics: const NeverScrollableScrollPhysics(),
            itemCount: state.items.length,
            onReorder: controller.reorder,
            itemBuilder: (context, index) {
              final item = state.items[index];
              return Padding(
                key: ValueKey(item.file.id),
                padding: const EdgeInsets.only(bottom: 12.0),
                child: LuxuryCard(
                  padding: const EdgeInsets.all(12.0),
                  child: Row(
                    children: [
                      // Drag Handle
                      const Padding(
                        padding: EdgeInsets.symmetric(horizontal: 8.0),
                        child: Icon(
                          Icons.drag_indicator,
                          color: AppColors.textSecondaryLight,
                          size: 20,
                        ),
                      ),

                      // Image Thumbnail Container
                      ClipRRect(
                        borderRadius: BorderRadius.circular(4),
                        child: Container(
                          width: 64,
                          height: 64,
                          color: const Color(0xFFF0EFEA),
                          child: Image.network(
                            item.file.url,
                            fit: BoxFit.cover,
                            errorBuilder: (_, __, ___) => const Icon(
                              Icons.broken_image_outlined,
                              color: AppColors.textSecondaryLight,
                            ),
                          ),
                        ),
                      ),

                      const SizedBox(width: 16),

                      // Image Details (Filename + Dimensions/Size)
                      Expanded(
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Row(
                              children: [
                                Flexible(
                                  child: Text(
                                    item.file.filename,
                                    style: const TextStyle(
                                      fontWeight: FontWeight.w600,
                                      fontSize: 14,
                                      color: AppColors.textPrimaryLight,
                                    ),
                                    maxLines: 1,
                                    overflow: TextOverflow.ellipsis,
                                  ),
                                ),
                                if (item.isPrimary) ...[
                                  const SizedBox(width: 8),
                                  const LuxuryBadge(
                                    text: 'PRIMARY',
                                    variant: LuxuryBadgeVariant.gold,
                                  ),
                                ],
                              ],
                            ),
                            const SizedBox(height: 4),
                            Text(
                              '${item.file.formattedSize} • ${item.file.mimeType.toUpperCase().replaceAll('IMAGE/', '')}',
                              style: const TextStyle(
                                fontSize: 12,
                                color: AppColors.textSecondaryLight,
                              ),
                            ),
                          ],
                        ),
                      ),

                      // Action Buttons
                      if (!item.isPrimary)
                        LuxuryButton(
                          text: 'Make Primary',
                          variant: LuxuryButtonVariant.ghost,
                          height: 36,
                          icon: Icons.star_border,
                          onPressed: () => controller.setPrimary(item.file.id),
                        ),

                      IconButton(
                        tooltip: 'Remove image',
                        icon: const Icon(
                          Icons.delete_outline,
                          color: AppColors.error,
                          size: 20,
                        ),
                        onPressed: () => controller.removeImage(item.file.id),
                      ),
                    ],
                  ),
                ),
              );
            },
          ),
      ],
    );
  }
}
