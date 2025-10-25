package br.com.cpdias.s3.web;


import br.com.cpdias.s3.storage.StorageServiceIsilon;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/s3")
public class S3Controller {
    private final StorageServiceIsilon s3Service;

    public S3Controller(StorageServiceIsilon s3Service) {
        this.s3Service = s3Service;
    }


    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("O arquivo não pode estar vazio.");
        }

        // Exemplo: Salvar dentro da pasta 'imagens'
        String folderName = "imagens";

        try {
            String fileKey = s3Service.salvarArquivo(file, folderName);
            return ResponseEntity.status(HttpStatus.CREATED).body("Arquivo salvo com sucesso! Key: " + fileKey);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao processar o arquivo.", e);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, e.getMessage(), e);
        }
    }

    @PutMapping(value = "/{key}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updateFile(@PathVariable String key, @RequestParam("file") MultipartFile newFile) {
        if (newFile.isEmpty()) {
            return ResponseEntity.badRequest().body("O novo arquivo não pode estar vazio.");
        }
        try {
            String updatedKey = s3Service.atualizarArquivo(key, newFile);
            return ResponseEntity.ok("Arquivo atualizado com sucesso! Key: " + updatedKey);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao processar o novo arquivo.", e);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, e.getMessage(), e);
        }
    }

    @GetMapping("/{key}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String key) {
        byte[] data = s3Service.obterArquivo(key);

        if (data == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Arquivo não encontrado no S3: " + key);
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM) // Tipo binário genérico
                .header("Content-Disposition", "attachment; filename=\"" + key.substring(key.lastIndexOf("/") + 1) + "\"")
                .body(data);
    }

    @DeleteMapping("/{key}")
    public ResponseEntity<String> deleteFile(@PathVariable String key) {
        boolean deleted = s3Service.excluirArquivo(key);
        if (deleted) {
            return ResponseEntity.ok("Arquivo excluído com sucesso: " + key);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Falha ao excluir o arquivo: " + key);
        }
    }
    @GetMapping("/list")
    public ResponseEntity<List<String>> listFiles() {
        List<String> keys = s3Service.listarArquivos();
        return ResponseEntity.ok(keys);
    }
}
