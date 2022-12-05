import static spark.Spark.*;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import static spark.Spark.externalStaticFileLocation;

import com.fasterxml.uuid.Generators;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import spark.Request;
import spark.Response;

public class App {
    public static void main(String[] args) {
        ArrayList<Car> cars = new ArrayList<>();
        externalStaticFileLocation("C:\\Users\\4pa\\Desktop\\Test\\src\\main\\resources\\public");
        post("/add", (req,res) -> add(req,res,cars));
        get("/json", (req,res) -> jsonCars(req,res,cars));
        post("/delete", (req,res) -> deleteCar(req,res,cars));
        post("/update", (req,res) -> updateCar(req,res,cars));
        post("/generateRandom", (req,res) -> generateCars(req,res,cars));
        post("/generateInvoice", (req, res) -> generateInvoice(req,res,cars));
        get("/downloadInvoice", (req,res) -> downloadInvoice(req,res,cars));
    }

    private static String downloadInvoice(Request req, Response res, ArrayList<Car> cars) throws IOException {
        Gson gson = new Gson();
        String uuid = req.queryParams("uuid");
        System.out.println(uuid);
        res.type("application/octet-stream"); //
        res.header("Content-Disposition", "attachment; filename="+uuid+".pdf"); // nagłówek

        OutputStream outputStream = null;
        try {
            outputStream = res.raw().getOutputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        outputStream.write(Files.readAllBytes(Path.of("invoices/" + uuid + ".pdf"))); // response pliku do przeglądarki
        return "";
    }

    private static String generateInvoice(Request req, Response res, ArrayList<Car> cars) {
        Gson gson = new Gson();
        UUID carUUID = gson.fromJson(req.body(), Car.class).getUuid();
        Car carInCars = null;

        for(Car car: cars){
            if(car.getUuid().equals(carUUID)){
                if(car.isHasInvoice()){
                    return "{invoice:\"exists\"}";
                }
                carInCars = car;
                break;
            }
        }
        Document document = new Document(); // dokument pdf
        String path = "invoices/"+carUUID.toString()+".pdf"; // lokalizacja zapisu
        try {
            PdfWriter.getInstance(document, new FileOutputStream(path));
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        document.open();
        try {
            Font bigFont = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
            Paragraph chunk = new Paragraph("FAKTURA dla: " + carUUID, bigFont); // akapit
            document.add(chunk);
            chunk = new Paragraph("Model: " + carInCars.getModel(), bigFont); // akapit
            document.add(chunk);
            chunk = new Paragraph("Kolor: " + carInCars.getColor(), bigFont); // akapit
            document.add(chunk);
            chunk = new Paragraph("rok: " + carInCars.getYear(), bigFont); // akapit
            document.add(chunk);
            for (Airbag airbag :
                    carInCars.getAirbags()) {
                Paragraph paragraph = new Paragraph("poduszka: "+airbag.getDescription()+" - "+airbag.isValue());
                document.add(paragraph);
            }

        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }



        document.close();
        carInCars.setHasInvoice(true);
        return "{success:\"tak\"}";
    }

    private static String generateCars(Request req, Response res, ArrayList<Car> cars){
        for (int i = 0; i<20; i++){
            cars.add(new Car(cars.size()+1));
        }
        return "{success:\"tak\"}";
    }
    private static String updateCar(Request req, Response res, ArrayList<Car> cars) {
        Gson gson = new Gson();
        System.out.println("update");
        System.out.println(req.body());
//        Car newCar = gson.fromJson(req.body(), Car.class);
        String newCarModel = gson.fromJson(req.body(), Car.class).getModel();
        UUID newCarUUID = gson.fromJson(req.body(), Car.class).getUuid();
        int newCarYear = gson.fromJson(req.body(), Car.class).getYear();
        System.out.println(newCarUUID);
        System.out.println(newCarModel);
        System.out.println(newCarYear);
        for(Car car : cars){
            System.out.println(car.getUuid());
            if(car.getUuid().equals(newCarUUID)){
                car.setModel(newCarModel);
                car.setYear(newCarYear);
            }
        }
        return "{pog: \"pog\"}";
    }
    private static String deleteCar(Request req, Response res, ArrayList<Car> cars) {
        Gson gson = new Gson();
        System.out.println(gson.fromJson(req.body(), Car.class));
        UUID uuidToDelete = gson.fromJson(req.body(), Car.class).getUuid();
//        System.out.println(uuidToDelete);
        for(Car car : cars){
//            System.out.println(car.getUuid());
//            System.out.println(uuidToDelete==car.getUuid());
//            System.out.println(uuidToDelete.equals(car.getUuid()));
            if(car.getUuid().equals(uuidToDelete)){
//                System.out.println(car);
                cars.remove(car);
                break;
            }
        }
        return "{deletedUUID:" + uuidToDelete+"}";
    }

    private static String jsonCars(Request req, Response res, ArrayList<Car> cars) {
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<Car>>(){}.getType();
        res.type("application/json");
        return gson.toJson(cars, listType);
    }

    private static String add(Request req, Response res, ArrayList<Car> cars) {
        Gson gson = new Gson();
        Car newCar = new Car(
                cars.size()+1,
                gson.fromJson(req.body(), Car.class).getModel(),
                gson.fromJson(req.body(), Car.class).getYear(),
                gson.fromJson(req.body(), Car.class).getColor(),
                gson.fromJson(req.body(), Car.class).getAirbags()
        );
        cars.add(newCar);
        System.out.println(newCar.toString());
        Type carType = new TypeToken<Car>(){}.getType();
        res.type("application/json");
        return gson.toJson(newCar, carType);
    }
}
class Car{
    private int id;
    private UUID uuid;
    private String model;
    private int year;
    private String color;
    private boolean hasInvoice;
    private ArrayList<Airbag> airbags;

    public int getId() {
        return id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getModel() {
        return model;
    }

    public int getYear() {
        return year;
    }

    public void setHasInvoice(boolean hasInvoice) {
        this.hasInvoice = hasInvoice;
    }

    public boolean isHasInvoice() {
        return hasInvoice;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setAirbags(ArrayList<Airbag> airbags) {
        this.airbags = airbags;
    }

    public String getColor() {
        return color;
    }

    public ArrayList<Airbag> getAirbags() {
        return airbags;
    }

    @Override
    public String toString() {
        return "Car{" +
                "id=" + id +
                ", uuid=" + uuid +
                ", model='" + model + '\'' +
                ", year=" + year +
                ", color='" + color + '\'' +
                ", airbags=" + airbags +
                '}';
    }
    public Car(int id){
        this.id = id;
        this.uuid = Generators.randomBasedGenerator().generate();
        ArrayList<String> models = new ArrayList<>();
        models.add("Fiat");
        models.add("BMW");
        models.add("Audi");
        this.model = models.get((int) Math.floor(Math.random()*3));
        this.airbags = new ArrayList<>();
        this.airbags.add(new Airbag("pasażer", false));
        this.airbags.add(new Airbag("kierowca", false));
        this.airbags.add(new Airbag("tylna kanapa", false));
        this.airbags.add(new Airbag("boczne z tylu", false));
        Random random = new Random();
        int nextInt = random.nextInt(0xffffff + 1);
        this.color = String.format("#%06x", nextInt);
        this.year = 1970 + random.nextInt(52);


    }
    public Car(int id, String model, int year, String color, ArrayList<Airbag> airbags) {
        this.id = id;
        UUID uuid = Generators.randomBasedGenerator().generate();
        this.uuid = uuid;
        this.model = model;
        this.year = year;
        this.color = color;
        this.airbags = airbags;
        this.hasInvoice = false;
    }

    public Car() {
    }
}
class Airbag{
    private String description;
    private boolean value;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public Airbag(String description, boolean value) {
        this.description = description;
        this.value = value;
    }

    @Override
    public String toString() {
        return "Airbag{" +
                "description='" + description + '\'' +
                ", value=" + value +
                '}';
    }
}