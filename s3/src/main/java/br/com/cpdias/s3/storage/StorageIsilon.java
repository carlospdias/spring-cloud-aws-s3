package br.com.cpdias.s3.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@Component
class StorageIsilon implements Storage {

    private final Logger log = LoggerFactory.getLogger(StorageIsilon.class);
    private final S3Client s3Client;



    @Value("${app.aws.s3.bucket-name}")
    private String bucketName;

    public StorageIsilon(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @Override
    public String write(MultipartFile file, String folderName) {
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename != null && originalFilename.contains(".") ?
                originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
        String key = (folderName != null ? folderName + "/" : "") +
                UUID.randomUUID() + fileExtension;

        log.info("Iniciando upload para S3. Bucket: {}, Key: {}", bucketName, key);

        try (InputStream is = file.getInputStream()) {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(is, file.getSize()));

            log.info("Upload de arquivo S3 concluído com sucesso. Key: {}", key);
            return key;
        } catch (S3Exception | IOException e) {
            log.error("Erro no S3 ao salvar arquivo: {}", e.getMessage());
            throw new RuntimeException("Falha ao salvar arquivo no S3", e);
        }
    }

    @Override
    public String rewrite(String key, MultipartFile newFile) {
        log.info("Atualizando arquivo S3. Bucket: {}, Key: {}", bucketName, key);
        return write(newFile, null); // Reutilizamos o método salvarArquivo, assumindo que a chave completa está na 'key'
    }

    @Override
    public byte[] restore(String key) {
        log.info("Obtendo arquivo do S3. Bucket: {}, Key: {}", bucketName, key);
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            return s3Client.getObjectAsBytes(getObjectRequest).asByteArray();
        } catch (S3Exception e) {
            if (e.statusCode() == 404) {
                log.warn("Arquivo não encontrado: {}", key);
                return null; // Retorna null se o arquivo não existir
            }
            log.error("Erro no S3 ao obter arquivo: {}", e.getMessage());
            throw new RuntimeException("Falha ao obter arquivo do S3", e);
        }
    }

    @Override
    public boolean delete(String key) {
        log.warn("Excluindo arquivo do S3. Bucket: {}, Key: {}", bucketName, key);
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
            log.info("Arquivo S3 excluído com sucesso. Key: {}", key);
            return true;
        } catch (S3Exception e) {
            log.error("Erro no S3 ao excluir arquivo: {}", e.getMessage());
            throw new RuntimeException("Falha ao excluir arquivo do S3", e);
        }
    }

    @Override
    public List<String> getAllKeys() {
        log.info("Listando arquivos no S3. Bucket: {}", bucketName);
        try {
            ListObjectsV2Request listObjectsV2Request = ListObjectsV2Request.builder()
                    .bucket(bucketName)
                    .build();

            ListObjectsV2Response listObjectsV2Response = s3Client.listObjectsV2(listObjectsV2Request);

            return listObjectsV2Response.contents().stream()
                    .map(S3Object::key)
                    .collect(java.util.stream.Collectors.toList());
        } catch (S3Exception e) {
            log.error("Erro no S3 ao listar arquivos: {}", e.getMessage());
            throw new RuntimeException("Falha ao listar arquivos do S3", e);
        }
    }
}
