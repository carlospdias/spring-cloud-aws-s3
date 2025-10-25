package br.com.cpdias.s3.storage;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;

import java.util.List;

@Service
public class StorageService {

    private final Logger log = LoggerFactory.getLogger(StorageService.class);

    private Storage storage;

    public StorageService(Storage storage) {
        this.storage = storage;
    }


    public String salvarArquivo(MultipartFile file, String folderName) throws IOException {
        return storage.write(file, folderName);
    }

    public String atualizarArquivo(String key, MultipartFile newFile) throws IOException {
        return storage.rewrite(key, newFile);
    }
    public byte[] obterArquivo(String key) {
        return storage.restore(key);
    }

    public boolean excluirArquivo(String key) {
        return storage.delete(key);
    }

    public List<String> listarArquivos() {
        return storage.getAllKeys();
    }
}
