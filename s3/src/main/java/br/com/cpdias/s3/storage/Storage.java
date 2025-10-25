package br.com.cpdias.s3.storage;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

interface Storage {

    String write(MultipartFile file, String folderName) ;

    String rewrite(String key, MultipartFile newFile);

    byte[] restore(String key);

    boolean delete(String key);
    List<String> getAllKeys();


}
