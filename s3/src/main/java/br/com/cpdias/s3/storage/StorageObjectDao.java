package br.com.cpdias.s3.storage;

import java.util.List;
import java.util.Optional;

interface StorageObjectDao {

    void addToIndex(StorageObject storageObject);

    void removeFromIndex(StorageObject storageObject);

    void updateIndex(StorageObject storageObject);

    List<StorageObject> getStorageObjects(String owner);

    Optional<StorageObject> getStorageObject(Long id);

}
