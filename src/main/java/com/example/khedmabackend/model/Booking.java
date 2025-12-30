package com.example.khedmabackend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private User client; // FK client_id

    @ManyToOne
    @JoinColumn(name = "worker_id")
    private Worker worker; // FK worker_id

    @Column(name = "date_time") // Map 'date' entity → "date_time" table column (pas NULL)
    private LocalDateTime date;

    private String time;

    private String location;

    private Double price;

    private String status = "PENDING"; // String pour "ACCEPTEE" / "REFUSEE"

    @Transient
    @JsonProperty("clientId")
    private Long clientId;

    @Transient
    @JsonProperty("workerId")
    private Long workerId;

    // Constructors
    public Booking() {}

    public Booking(User client, Worker worker, LocalDateTime date, String time, String location, Double price) {
        this.client = client;
        this.worker = worker;
        this.date = date;
        this.time = time;
        this.location = location;
        this.price = price;
    }

    // Getters/Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getClient() { return client; }
    public void setClient(User client) { this.client = client; }

    public Worker getWorker() { return worker; }
    public void setWorker(Worker worker) { this.worker = worker; }

    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public String getStatus() { return status; } // String
    public void setStatus(String status) { this.status = status; }

    // Getters pour IDs
    public Long getClientId() { return client != null ? client.getId() : clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }

    public Long getWorkerId() { return worker != null ? worker.getId() : workerId; }
    public void setWorkerId(Long workerId) { this.workerId = workerId; }
}