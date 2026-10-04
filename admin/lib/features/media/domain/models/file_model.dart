/// Domain model representing a registered file/media asset in Open Fashion Admin
class FileModel {
  final String id;
  final String filename;
  final String key;
  final String url;
  final String mimeType;
  final int sizeBytes;
  final String provider;
  final bool isPublic;
  final DateTime createdAt;
  final DateTime updatedAt;

  const FileModel({
    required this.id,
    required this.filename,
    required this.key,
    required this.url,
    required this.mimeType,
    required this.sizeBytes,
    this.provider = 'LOCAL',
    this.isPublic = true,
    required this.createdAt,
    required this.updatedAt,
  });

  /// Formatted human-readable file size (e.g., "1.24 MB", "340 KB")
  String get formattedSize {
    if (sizeBytes < 1024) return '$sizeBytes B';
    if (sizeBytes < 1024 * 1024) {
      return '${(sizeBytes / 1024).toStringAsFixed(1)} KB';
    }
    return '${(sizeBytes / (1024 * 1024)).toStringAsFixed(2)} MB';
  }

  /// Whether the file is an image
  bool get isImage => mimeType.startsWith('image/');

  factory FileModel.fromJson(Map<String, dynamic> json) {
    return FileModel(
      id: json['id'] as String? ?? '',
      filename: json['filename'] as String? ?? '',
      key: json['key'] as String? ?? '',
      url: json['url'] as String? ?? '',
      mimeType: json['mimeType'] as String? ?? 'application/octet-stream',
      sizeBytes: json['sizeBytes'] as int? ?? 0,
      provider: json['provider'] as String? ?? 'LOCAL',
      isPublic: json['isPublic'] as bool? ?? true,
      createdAt: json['createdAt'] != null
          ? DateTime.tryParse(json['createdAt'] as String) ?? DateTime.now()
          : DateTime.now(),
      updatedAt: json['updatedAt'] != null
          ? DateTime.tryParse(json['updatedAt'] as String) ?? DateTime.now()
          : DateTime.now(),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'filename': filename,
      'key': key,
      'url': url,
      'mimeType': mimeType,
      'sizeBytes': sizeBytes,
      'provider': provider,
      'isPublic': isPublic,
      'createdAt': createdAt.toIso8601String(),
      'updatedAt': updatedAt.toIso8601String(),
    };
  }
}
