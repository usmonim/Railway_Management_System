package edu.ozyegin.cs.entity;

import com.google.common.base.Objects;

public class Stops {
    private int id;
    private String name;

    public Stops() {
    }

    public Stops id(int id) {
        this.id = id;
        return this;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Stops name(String name) {
        this.name = name;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Stops stops = (Stops) o;
        return getName() == stops.getName();
//                Objects.equal(getName(), stops.getName()) ;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getName());
    }
}



