package entities;

public enum Category {

    FOOD(1),
    ELECTRICITY(2),
    RESTAURANT(3),
    VACATION(4);

    private long id;

    Category(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
