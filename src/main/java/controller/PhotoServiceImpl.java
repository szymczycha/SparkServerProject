package controller;

import model.Photo;
import org.eclipse.jetty.client.api.Response;

import java.io.File;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class PhotoServiceImpl implements PhotoService{
    Map<String, Photo> photoMap = new HashMap<>();

    public Map<String, Photo> getPhotoMap() {
        return photoMap;
    }

    int counter = 0;
    public PhotoServiceImpl() {
        File f = new File("./upload");
        File[] files = f.listFiles();
        for (File file :
                files) {
            photoMap.put(String.valueOf(counter), new Photo(
                    String.valueOf(counter),
                    file.getName(),
                    file.getPath()
            ));
            counter++;
        }
    }

    @Override
    public Map<String, Photo> getPhotos() {
        return photoMap;
    }

    @Override
    public Photo getPhotoById(String id) {
        if(!photoMap.containsKey(id)){
            System.out.println("no photo with id: "+id);
            return null;
        }
        Photo photo = photoMap.get(id);
        System.out.println(photo);
        return photo;
    }

    @Override
    public Photo getPhotoByName(String name) {
        Photo photo = null;
        for (String id :
                photoMap.keySet()) {
            if(photoMap.get(id).getName().equals(name)){
                photo = photoMap.get(id);
            }
        }
        System.out.println(photo);

        return photo;
    }

    @Override
    public boolean deletePhotoById(String id) {
        if(!photoMap.containsKey(id)){
            System.out.println("no photo with id: "+id);
            return false;
        }
        Photo photo = photoMap.get(id);
        File f = new File(photo.getPath());
        photoMap.remove(id);
        f.delete();
        return true;
    }

    @Override
    public OutputStream getPhotoStream() {
        return null;
    }


    @Override
    public boolean renamePhotoById(String id, String newName) {
        if(!photoMap.containsKey(id)){
            System.out.println("no photo with id: "+id);
            return false;
        }
        Photo photo = photoMap.get(id);
        photo.setName(newName);
        File f = new File(photo.getPath());
        File f2 = new File(photo.getPath().substring(0, photo.getPath().length()-photo.getName().length()));
        f.renameTo(f2);
        // to bedzie cud jak zadziala
        return true;
    }
}
