import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import controller.PhotoServiceImpl;
import model.Photo;
import response.ResponseEntity;
import response.ResponseStatus;
import spark.Request;
import spark.Response;
import spark.Spark.*;

import static spark.Spark.*;


public class AppREST {
    private static PhotoServiceImpl photoService = new PhotoServiceImpl();
    public static void main(String[] args) {
        port(7777);
        get("/api/photos", (req,res) -> getPhotosJSON(req,res));
        get("/api/photos/:id", (req, res) -> getPhotoById(req, res));
        get("/api/photos/photo/:name", (req, res) -> getPhotoByName(req, res));
        delete("/api/photos/:id", (req,res) -> deletePhotoById(req,res));
        get("/api/photos/data/:id", (req,res) -> getPhotoJPEG(req,res));
        put("/api/photos/:id", (req,res) -> renamePhoto(req,res));
    }

    private static String renamePhoto(Request req, Response res) {
        return null;
    }

    private static String getPhotoJPEG(Request req, Response res) {
        return null;
    }

    private static String deletePhotoById(Request req, Response res) {
        return null;
    }

    private static String getPhotoByName(Request req, Response res) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        res.header("Access-Control-Allow-Origin", "*");
        res.type("application/json");
        Photo photo = photoService.getPhotoByName(req.params("name"));
        if(photo == null){
            return gson.toJson(new ResponseEntity(
                    ResponseStatus.NOT_FOUND,
                    "photo not found with this name = " + req.params("id")
            ));
        }
        return gson.toJson(new ResponseEntity(
                ResponseStatus.SUCCESS,
                "photo found",
                gson.toJsonTree(photo)
        ));
    }

    private static String getPhotosJSON(Request req, Response res) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        res.header("Access-Control-Allow-Origin", "*");
        res.type("application/json");
        return gson.toJson(new ResponseEntity(
                ResponseStatus.SUCCESS,
                "photo found",
                gson.toJsonTree(photoService.getPhotoMap())
        ));
    }

    private static String getPhotoById(Request req, Response res) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        res.header("Access-Control-Allow-Origin", "*");
        res.type("application/json");
        Photo photo = photoService.getPhotoById(req.params("id"));
        if(photo == null){
            return gson.toJson(new ResponseEntity(
                    ResponseStatus.NOT_FOUND,
                    "photo not found with id = " + req.params("id")
            ));
        }
        return gson.toJson(new ResponseEntity(
                ResponseStatus.SUCCESS,
                "photo found",
                gson.toJsonTree(photo)
        ));
    }
}
