package app.utils;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class SvgConverter {
    private static final String CLOUD_NAME = "dnfvlvylb";
    private static final String API_KEY = "713935467898345";
    private static final String API_SECRET = "8X8_iOSibQsTMKJa2eydBBvQsnM";

    private static final Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
            "cloud_name", CLOUD_NAME,
            "api_key", API_KEY,
            "api_secret", API_SECRET
    ));

    public static String convertSvgToPng(String svgContent) {
        File tempFile = null;
        try {
            // Før vi kan sende til vores api bliver vi nødt til at gemme vores svg midlertidigt
            tempFile = File.createTempFile("temp-svg-", ".svg");
            try (FileWriter writer = new FileWriter(tempFile)) {
                writer.write(svgContent);
            }

            // Upload til Cloudinary som står for at konveteret til png
            Map<?, ?> uploadResult = cloudinary.uploader().upload(tempFile, ObjectUtils.asMap(
                    "resource_type", "image",
                    "format", "png"
            ));

            // Henter vores url til billedet og retuner den.
            return (String) uploadResult.get("secure_url");
        } catch (Exception e) {
            throw new RuntimeException("Fejl ved konvertering af SVG til PNG: " + e.getMessage());
        } finally {
            // Seltter nu vores midlertidig fil.
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }
}
