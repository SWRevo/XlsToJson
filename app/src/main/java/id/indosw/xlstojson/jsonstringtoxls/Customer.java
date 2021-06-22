package id.indosw.xlstojson.jsonstringtoxls;

import org.jetbrains.annotations.NotNull;

public class Customer {
    private String id;
    private String name;
    private String address;
    private int age;

    @SuppressWarnings("unused")
    public Customer() {
        super();
    }

    @SuppressWarnings("unused")
    public Customer(String id, String name, String address, int age) {
        super();
        this.id = id;
        this.name = name;
        this.address = address;
        this.age = age;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    @SuppressWarnings("unused")
    public void setAddress(String address) {
        this.address = address;
    }

    public int getAge() {
        return age;
    }

    @SuppressWarnings("unused")
    public void setAge(int age) {
        this.age = age;
    }

    @NotNull
    @Override
    public String toString() {
        return "Customer [id=" + id + ", name=" + name + ", address=" + address + ", age=" + age + "]";
    }
}
