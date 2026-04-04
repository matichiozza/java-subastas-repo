package com.example.demo.servicio;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.StringUtils;
import java.io.IOException;
import java.util.UUID;

@Service
public class SupabaseStorageService {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    @Value("${supabase.bucket}")
    private String bucketName;

    private final RestTemplate restTemplate = new RestTemplate();

    public String uploadFile(MultipartFile file) throws IOException {
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename() != null ? file.getOriginalFilename() : "archivo");
        String extension = originalFilename.contains(".") ? originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
        String uniqueFilename = UUID.randomUUID().toString() + extension;
        
        String uploadUrl = String.format("%s/storage/v1/object/%s/%s", supabaseUrl, bucketName, uniqueFilename);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(supabaseKey);
        
        if (file.getContentType() != null) {
            headers.setContentType(MediaType.parseMediaType(file.getContentType()));
        } else {
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        }
        
        headers.set("apikey", supabaseKey);
        
        HttpEntity<byte[]> requestEntity = new HttpEntity<>(file.getBytes(), headers);
        
        ResponseEntity<String> response = restTemplate.postForEntity(uploadUrl, requestEntity, String.class);
        
        if (response.getStatusCode().is2xxSuccessful()) {
            return String.format("%s/storage/v1/object/public/%s/%s", supabaseUrl, bucketName, uniqueFilename);
        } else {
            throw new IOException("Error subiendo el archivo a Supabase: " + response.getBody());
        }
    }

    public void deleteFile(String fileUrl) {
        if (fileUrl == null || !fileUrl.contains("/storage/v1/object/public/")) return;
        try {
            String filename = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
            String deleteUrl = String.format("%s/storage/v1/object/%s/%s", supabaseUrl, bucketName, filename);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(supabaseKey);
            headers.set("apikey", supabaseKey);
            
            HttpEntity<?> requestEntity = new HttpEntity<>(headers);
            restTemplate.exchange(deleteUrl, org.springframework.http.HttpMethod.DELETE, requestEntity, String.class);
        } catch (Exception ignored) {
            System.err.println("Error deleting file from Supabase: " + ignored.getMessage());
        }
    }
}
