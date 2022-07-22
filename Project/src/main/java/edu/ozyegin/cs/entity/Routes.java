package edu.ozyegin.cs.entity;

import com.google.common.base.Objects;

public class Routes {
    private int id;
    private String name;

    public Routes() {
    }

    public Routes id(int id) {
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

    public Routes name(String name) {
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
        Routes routes = (Routes) o;
        return getName() == routes.getName();
//                Objects.equal(getName(), routes.getName()) ;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getName());
    }
}
