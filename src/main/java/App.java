import static spark.Spark.*;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.UUID;

import static spark.Spark.externalStaticFileLocation;

import com.fasterxml.uuid.Generators;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import spark.Request;
import spark.Response;

public class App {
    public static void main(String[] args) {
        ArrayList<Car> cars = new ArrayList<>();
        externalStaticFileLocation("C:\\Users\\4pa\\Desktop\\Test\\src\\main\\resources\\public");
        post("/add", (req,res) -> add(req,res,cars));
        get("/json", (req,res) -> jsonCars(req,res,cars));
        post("/delete", (req,res) -> deleteCar(req,res,cars));
//        post("/update", ...)
    }

    private static String deleteCar(Request req, Response res, ArrayList<Car> cars) {
        Gson gson = new Gson();
        UUID uuidToDelete = gson.fromJson(req.body(), UUID.class);
        for(Car car : cars){
            if(car.getUuid() == uuidToDelete){
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

    public Car(int id, String model, int year, String color, ArrayList<Airbag> airbags) {
        this.id = id;
        UUID uuid = Generators.randomBasedGenerator().generate();
        this.uuid = uuid;
        this.model = model;
        this.year = year;
        this.color = color;
        this.airbags = airbags;
    }

    public Car() {
    }
}
class Airbag{
    private String description;
    private boolean value;

    @Override
    public String toString() {
        return "Airbag{" +
                "description='" + description + '\'' +
                ", value=" + value +
                '}';
    }
}