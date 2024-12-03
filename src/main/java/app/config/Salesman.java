package app.config;


public class Salesman {

    private String name;
    private Integer id;
    private String email;


    public String generate3DModel(Inquiry inquiry) {
        return null;
    }

    public boolean sendOfferEmail(Customer customer, String offer) {
        return false;
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

