package id.indosw.xlstojson.xlstojsonstring;

import org.jetbrains.annotations.NotNull;

public class Customer {

    private String id;
    private String name;
    private String address;
    private int age;

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

    @SuppressWarnings("unused")
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @SuppressWarnings("unused")
    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @NotNull
    @Override
    public String toString() {
        return "Customer [id=" + id + ", name=" + name + ", address=" + address + ", age=" + age + "]";
    }


}