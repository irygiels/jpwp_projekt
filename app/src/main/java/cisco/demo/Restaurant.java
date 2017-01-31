package cisco.demo;

/**
 * Created by irygiels on 15.03.16.
 */
public class Restaurant {

    private String name; //nazwa restauracji
    private String address; //adres
    private String image; //obrazek

    public Restaurant(String name, String address, String image) {
        super();
        this.name = name;
        this.address = address;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String nameText) {
        name = nameText;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}