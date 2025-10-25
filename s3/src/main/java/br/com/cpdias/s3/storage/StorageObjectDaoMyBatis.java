package br.com.cpdias.s3.storage;

import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;


@Repository
class StorageObjectDaoMyBatis implements StorageObjectDao {

    private Session session;

    @Override
    public void addToIndex(StorageObject storageObject) {
        try{
            int result = session.insert("", storageObject);

            if (result < 1) {
                throw new RuntimeException( "");
            }
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeFromIndex(StorageObject storageObject) {
        try{
            int result = session.delete("", storageObject.getId());

            if (result < 1) {
                throw new RuntimeException( "");
            }
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateIndex(StorageObject storageObject) {
        try{
            int result = session.update("", storageObject);

            if (result < 1) {
                throw new RuntimeException( "");
            }
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<StorageObject> getStorageObjects(String owner) {
        try{
            List<StorageObject> objects = session.update("", owner);
            if (objects != null) {
                return Collections.unmodifiableList(objects);
            }
            return Collections.emptyList();
        }catch(Exception e){
            throw new RuntimeException(e);
        }

    }

    @Override
    public Optional<StorageObject> getStorageObject(Long id) {
        StorageObject storageObj =  session.selectOne("sadf", id);

        if (storageObj != null) {
            return Optional.of(storageObj);
        }
        return Optional.empty();
    }
}
