package app.config;


public class Salesman {

    private String name;
    private Integer id;
    private String email;

    //Konstruktør
    public Salesman(Integer id, String name, String email) {
        this.name = name;
        this.id = id;
        this.email = email;
    }


    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Integer getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

