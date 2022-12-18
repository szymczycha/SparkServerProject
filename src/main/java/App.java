import static spark.Spark.*;

import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static spark.Spark.externalStaticFileLocation;

import com.fasterxml.uuid.Generators;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import spark.Request;
import spark.Response;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.http.Part;

public class App {
    public static void main(String[] args) {
        ArrayList<Invoice> allCarsInvoicesNames = new ArrayList<>();
        ArrayList<Invoice> byYearInvoicesNames = new ArrayList<>();
        ArrayList<Invoice> byPriceInvoiceNames = new ArrayList<>();
        ArrayList<String> imageNames = new ArrayList<>();

        ArrayList<Car> cars = new ArrayList<>();
//        externalStaticFileLocation("C:\\Users\\4pa\\Desktop\\Test\\src\\main\\resources\\public");
        externalStaticFileLocation("C:\\Users\\szymc\\OneDrive\\Pulpit\\SparkServerProject\\src\\main\\resources\\public");
        post("/add", (req,res) -> add(req,res,cars));
        get("/json", (req,res) -> jsonCars(req,res,cars));
        post("/delete", (req,res) -> deleteCar(req,res,cars));
        post("/update", (req,res) -> updateCar(req,res,cars));
        post("/generateRandom", (req,res) -> generateCars(req,res,cars));
        post("/generateInvoice", (req, res) -> generateInvoice(req,res,cars));
        post("/generateAllCarsInvoice", (req, res) -> generateAllCarsInvoice(req,res,cars, allCarsInvoicesNames));
        post("/generateCarsByYearInvoice", (req,res) -> generateCarsByYearInvoice(req,res,cars,byYearInvoicesNames));
        post("/generateCarsByPriceInvoice", (req,res) -> generateCarsByPriceInvoice(req,res,cars,byPriceInvoiceNames));
        post("/getAllCarsInvoices", (req, res) -> getAllCarsInvoices(req,res,allCarsInvoicesNames));
        post("/getCarsByYearInvoices", (req, res) -> getByYearInvoices(req,res,byYearInvoicesNames));
        post("/getCarsByPriceInvoices", (req, res) -> getCarsByPriceInvoices(req,res,byPriceInvoiceNames));
        get("/downloadInvoice", (req,res) -> downloadInvoice(req,res,cars));
        get("/downloadAllCarsInvoice", (req,res) -> downloadAllCarsInvoice(req,res,cars));
        get("/downloadByYearInvoice", (req,res) -> downloadByYearInvoice(req,res,cars));
        get("/downloadByPriceInvoice", (req,res) -> downloadByPriceInvoice(req,res,cars));
        post("/upload", (req,res) -> upload(req,res,imageNames));
        get("/getThumbnail", (req,res) -> getThumbnail(req,res,imageNames));
        post("/saveGalleryImages", (req,res) -> saveGalleryImages(req,res,cars));
        post("/getCarImages", (req,res) -> getCarImages(req,res,cars));
    }

    private static String getCarImages(Request req, Response res, ArrayList<Car> cars) {
        Gson gson = new Gson();
        UUID uuid = gson.fromJson(req.body(), Car.class).getUuid();
        System.out.println(uuid);
        ArrayList<String> imagesToSend = null;
        for (Car car :
                cars) {
            System.out.println(uuid);
            if(uuid.equals(car.getUuid())){
                imagesToSend = car.getImagesNames();
                break;
            }
        }
        System.out.println(imagesToSend);
        Type listType = new TypeToken<ArrayList<String>>(){}.getType();
        res.type("application/json");
        return gson.toJson(imagesToSend, listType);
    }

    private static String saveGalleryImages(Request req, Response res, ArrayList<Car> cars) {
        Gson gson = new Gson();
        UUID uuid = gson.fromJson(req.body(), Car.class).getUuid();
        ArrayList<String> imagesNames = gson.fromJson(req.body(), Car.class).getImagesNames();
        for (Car car :
                cars) {
            if(uuid.equals(car.getUuid())){

                for (String image : imagesNames) {
                    car.addImage(image);
                }
                break;
            }
        }
        return "{'status':'ok'}";
    }


    private static String getThumbnail(Request req, Response res, ArrayList<String> imageNames) {
        String imageName = req.queryParams("name");
        System.out.println(imageName);
        String path = "images/"+imageName;
        File file = new File(path);
        res.type("image/jpeg");

        OutputStream outputStream = null;
        try {
            outputStream = res.raw().getOutputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            outputStream.write(Files.readAllBytes(file.toPath()));
            outputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private static String upload(Request req, Response res, ArrayList<String> imageNames) {
        ArrayList<String> sentImagesNames = new ArrayList<>();
        try {
            req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/images"));
            for(Part p : req.raw().getParts()){
                System.out.println(p);
                System.out.println(p.getInputStream());
                InputStream inputStream = p.getInputStream();
                // inputstream to byte
                byte[] bytes = inputStream.readAllBytes();
                UUID uuid = Generators.randomBasedGenerator().generate();
                String fileName = uuid.toString()+".jpg";
                sentImagesNames.add(fileName);
                FileOutputStream fos = new FileOutputStream("images/" + fileName);
                fos.write(bytes);
                fos.close();
                // dodaj do Arraylist z nazwami aut do odesłania do przeglądarki
                imageNames.add(fileName);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<String>>(){}.getType();
        res.type("application/json");
        return gson.toJson(sentImagesNames, listType);
    }

    private static String getCarsByPriceInvoices(Request req, Response res, ArrayList<Invoice> byPriceInvoiceNames) {
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<Invoice>>(){}.getType();
        res.type("application/json");
        return gson.toJson(byPriceInvoiceNames, listType);
    }

    private static String generateCarsByPriceInvoice(Request req, Response res, ArrayList<Car> cars, ArrayList<Invoice> byPriceInvoiceNames) {
        Gson gson = new Gson();
        int min = gson.fromJson(req.body(), Invoice.class).getMin();
        int max = gson.fromJson(req.body(), Invoice.class).getMax();
        ArrayList<Car> carsByPrice =  new ArrayList(cars.stream().filter(car-> car.getPrice()<=max && car.getPrice()>=min).toList());
        Invoice invoice = new Invoice(System.currentTimeMillis(), "faktura za ceny "+min+"-"+max, "","", carsByPrice, min,max);
        byPriceInvoiceNames.add(invoice);
        invoice.generateCarsByPriceInvoice(carsByPrice);
        return "{success:\"tak\"}";
    }

    private static String downloadByPriceInvoice(Request req, Response res, ArrayList<Car> cars) {
        Gson gson = new Gson();
        String time = req.queryParams("time");
        System.out.println(time);
        res.type("application/octet-stream"); //
        res.header("Content-Disposition", "attachment; filename=invoice_all_cars_by_price_"+time+".pdf"); // nagłówek

        OutputStream outputStream = null;
        try {
            outputStream = res.raw().getOutputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            outputStream.write(Files.readAllBytes(Path.of("invoices/invoice_all_cars_by_price_"+time+".pdf"))); // response pliku do przeglądarki
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "";
    }

    private static String downloadByYearInvoice(Request req, Response res, ArrayList<Car> cars) {
        Gson gson = new Gson();
        String time = req.queryParams("time");
        System.out.println(time);
        res.type("application/octet-stream"); //
        res.header("Content-Disposition", "attachment; filename=invoice_all_cars_by_year_"+time+".pdf"); // nagłówek

        OutputStream outputStream = null;
        try {
            outputStream = res.raw().getOutputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            outputStream.write(Files.readAllBytes(Path.of("invoices/invoice_all_cars_by_year_"+time+".pdf"))); // response pliku do przeglądarki
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "";
    }

    private static Object getByYearInvoices(Request req, Response res, ArrayList<Invoice> byYearInvoicesNames) {
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<Invoice>>(){}.getType();
        res.type("application/json");
        return gson.toJson(byYearInvoicesNames, listType);
    }

    private static String generateCarsByYearInvoice(Request req, Response res, ArrayList<Car> cars, ArrayList<Invoice> byYearInvoicesNames) {
        Gson gson = new Gson();
        int year = gson.fromJson(req.body(), Car.class).getYear();
        ArrayList<Car> carsByYear =  new ArrayList(cars.stream().filter(car-> car.getYear() == year).toList());
        Invoice invoice = new Invoice(System.currentTimeMillis(), "faktura za rocznik "+year, "","", carsByYear, year);
        byYearInvoicesNames.add(invoice);
        invoice.generateCarsByYearInvoice(carsByYear);
        return "{success:\"tak\"}";
    }

    private static String downloadAllCarsInvoice(Request req, Response res, ArrayList<Car> cars) throws IOException {
        Gson gson = new Gson();
        String time = req.queryParams("time");
        System.out.println(time);
        res.type("application/octet-stream"); //
        res.header("Content-Disposition", "attachment; filename=invoice_all_cars_"+time+".pdf"); // nagłówek

        OutputStream outputStream = null;
        try {
            outputStream = res.raw().getOutputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        outputStream.write(Files.readAllBytes(Path.of("invoices/invoice_all_cars_"+time+".pdf"))); // response pliku do przeglądarki
        return "";
    }
    private static String getAllCarsInvoices(Request req, Response res, ArrayList<Invoice> allCarsInvoicesNames) {
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<Invoice>>(){}.getType();
        res.type("application/json");
        return gson.toJson(allCarsInvoicesNames, listType);
    }

    private static String generateAllCarsInvoice(Request req, Response res, ArrayList<Car> cars, ArrayList<Invoice> allCarsInvoicesNames) {
        Invoice invoice = new Invoice(System.currentTimeMillis(), "faktura za wszystkie auta", "","", cars);
        allCarsInvoicesNames.add(invoice);
        invoice.generateAllCarsInvoice(cars);
        return "{success:\"tak\"}";
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
        Invoice.generateInvoice(carInCars);
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
    public int getVat() {
        return vat;
    }

    public void setVat(int vat) {
        this.vat = vat;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public CustomDate getDate() {
        return date;
    }

    public void setDate(CustomDate date) {
        this.date = date;
    }

    private int vat;
    private int id;
    private UUID uuid;
    private String model;
    private int price;
    private int year;
    private String color;
    private boolean hasInvoice;
    private CustomDate date;
    private ArrayList<Airbag> airbags;

    public ArrayList<String> getImagesNames() {
        return imagesNames;
    }

    private ArrayList<String> imagesNames;
    public void addImage(String imageName){
        imagesNames.add(imageName);
    }

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
        double vatRoll = Math.random();
        if(vatRoll <= 0.5){
            this.vat = 22;
        }else if(vatRoll <= 0.9){
            this.vat = 7;
        }else{
            this.vat = 0;
        }
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
        this.date = new CustomDate();
        this.price = 10000 + random.nextInt(20000);
        this.imagesNames = new ArrayList<>();
    }
    public Car(int id, String model, int year, String color, ArrayList<Airbag> airbags) {
        double vatRoll = Math.random();
        if(vatRoll <= 0.5){
            this.vat = 22;
        }else if(vatRoll <= 0.9){
            this.vat = 7;
        }else{
            this.vat = 0;
        }
        this.id = id;
        UUID uuid = Generators.randomBasedGenerator().generate();
        this.uuid = uuid;
        this.model = model;
        this.year = year;
        this.color = color;
        this.airbags = airbags;
        this.hasInvoice = false;
        this.imagesNames = new ArrayList<>();
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