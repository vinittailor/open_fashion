import 'file_model.dart';

/// Represents an image within a product media gallery.
/// Tracks primary cover status and visual display sort order.
class GalleryImageItem {
  final FileModel file;
  final bool isPrimary;
  final int sortOrder;

  const GalleryImageItem({
    required this.file,
    this.isPrimary = false,
    this.sortOrder = 0,
  });

  GalleryImageItem copyWith({
    FileModel? file,
    bool? isPrimary,
    int? sortOrder,
  }) {
    return GalleryImageItem(
      file: file ?? this.file,
      isPrimary: isPrimary ?? this.isPrimary,
      sortOrder: sortOrder ?? this.sortOrder,
    );
  }
}
