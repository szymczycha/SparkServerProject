package controller;

import model.Photo;

import java.io.OutputStream;
import java.util.Map;
public interface PhotoService {
    Map<String, Photo> getPhotos();
    Photo getPhotoById(String id);
    Photo getPhotoByName(String name);
    boolean deletePhotoById(String id);
    OutputStream getPhotoStream();//??
    boolean renamePhotoById(String id, String newName);
}
