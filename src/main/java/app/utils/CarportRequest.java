package app.utils;

public class CarportRequest {
    public int carportWidth;
    public int carportLength;
    public int shedWidth;
    public int shedLength;

    // Tom konstruktor kræves af Javalin ellers giver den åbenbbart fejl...
    public CarportRequest() {}
}
