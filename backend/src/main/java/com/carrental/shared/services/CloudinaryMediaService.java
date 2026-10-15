package com.carrental.shared.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.carrental.shared.exceptions.MediaUploadException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryMediaService {
    private final Cloudinary cloudinary;

    @Value("${cloudinary.cloud-name:}")
    private String cloudName;

    @Value("${cloudinary.api-key:}")
    private String apiKey;

    @Value("${cloudinary.api-secret:}")
    private String apiSecret;

    public UploadedMedia uploadImage(MultipartFile file, String folder) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("An image file is required");
        }
        if (file.getContentType() == null || !java.util.Set.of("image/jpeg", "image/png", "image/webp").contains(file.getContentType().toLowerCase())) {
            throw new IllegalArgumentException("Only image files are allowed");
        }

        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("Images must be 10 MB or smaller");
        }
        if (cloudName.isBlank() || apiKey.isBlank() || apiSecret.isBlank()) {
            throw new IllegalStateException("Cloudinary is not configured");
        }

        try {
            Map<?, ?> result = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap("folder", folder, "resource_type", "image", "quality", "auto", "fetch_format", "auto")
            );
            return new UploadedMedia(
                    String.valueOf(result.get("secure_url")),
                    String.valueOf(result.get("public_id"))
            );
        } catch (IOException | RuntimeException exception) {
            throw new MediaUploadException(
                    "Cloudinary rejected the image upload. Check that the API key has upload/create permission.",
                    exception
            );
        }
    }

    public UploadedMedia uploadPrivateDocument(MultipartFile file, String folder) {
        if (cloudName.isBlank() || apiKey.isBlank() || apiSecret.isBlank()) {
            throw new IllegalStateException("Cloudinary is not configured");
        }

        try {
            Map<?, ?> result = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", folder,
                            "resource_type", "auto",
                            "type", "authenticated",
                            "access_mode", "authenticated"
                    )
            );
            return new UploadedMedia(null, String.valueOf(result.get("public_id")));
        } catch (IOException | RuntimeException exception) {
            throw new MediaUploadException("The identity document could not be stored securely.", exception);
        }
    }

    public String createPrivateDocumentUrl(String publicId, String contentType) {
        if (cloudName.isBlank() || apiKey.isBlank() || apiSecret.isBlank()) {
            throw new IllegalStateException("Cloudinary is not configured");
        }
        String resourceType = contentType != null && contentType.equalsIgnoreCase("application/pdf")
                ? "raw" : "image";
        return cloudinary.url()
                .secure(true)
                .signed(true)
                .type("authenticated")
                .resourceType(resourceType)
                .generate(publicId);
    }

    public record UploadedMedia(String url, String publicId) {}
}
