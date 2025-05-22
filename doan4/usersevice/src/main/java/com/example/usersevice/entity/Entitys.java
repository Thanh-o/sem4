package com.example.usersevice.entity;



import jakarta.persistence.*;

@MappedSuperclass
public abstract class Entitys<T> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private T id;

    public T getId() {
        return id;
    }

    public void setId(T id) {
        this.id = id;
    }
}