package model;

public class Photo {
    private String id;
    private String name;

    public void setName(String name) {
        this.name = name;
    }

    private String path;

    @Override
    public String toString() {
        return "Photo{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", path='" + path + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public Photo(String id, String name, String path) {
        this.id = id;
        this.name = name;
        this.path = path;
    }
}
