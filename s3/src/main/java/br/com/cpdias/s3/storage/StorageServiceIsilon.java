package br.com.cpdias.s3.storage;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import java.util.List;

@Service
public class StorageServiceIsilon {

    private final Logger log = LoggerFactory.getLogger(StorageServiceIsilon.class);

    private Storage storage;
    private StorageObjectDao storageObjectDao;

    public StorageServiceIsilon(Storage storage, StorageObjectDao storageObjectDao) {
        this.storage = storage;
        this.storageObjectDao = storageObjectDao;
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
