package com.shah.volopay.Assignment;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "software_purchase")
public class SoftwarePurchase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date")
    private Date date;
    @Column(name = "_user")
    private String user;
    @Column(name = "department")
    private String department;
    @Column(name = "software")
    private String software;
    @Column(name = "seats")
    private int seats;
    @Column(name = "amount")
    private Double amount;

    public SoftwarePurchase() {
    }

    public SoftwarePurchase(int id, Date date, String user, String department, String software, int seats, Double amount) {
        this.id = id;
        this.date = date;
        this.user = user;
        this.department = department;
        this.software = software;
        this.seats = seats;
        this.amount = amount;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getSoftware() {
        return software;
    }

    public void setSoftware(String software) {
        this.software = software;
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
