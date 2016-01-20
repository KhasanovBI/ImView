package jat.imview.model;

/**
 * Created by bulat on 09.12.15.
 */
public class UserProfile {
    private int id;
    private String name;

    public UserProfile() {
    }

    public UserProfile(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


}
