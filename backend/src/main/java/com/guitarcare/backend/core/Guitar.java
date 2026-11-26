package com.guitarcare.backend.core;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name="guitars")
public class Guitar {
 @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
 @Column(name="user_id", nullable=false) private UUID userId;
 @Column(nullable=false,length=100) private String brand;
 @Column(nullable=false,length=120) private String model;
 @Column(nullable=false,length=40) private String type;
 private LocalDate purchaseDate;
 @Column(length=1000) private String photoUrl;
 @Column(nullable=false) private OffsetDateTime createdAt=OffsetDateTime.now();
 protected Guitar() {}
 public Guitar(UUID userId,String brand,String model,String type,LocalDate purchaseDate,String photoUrl){this.userId=userId;this.brand=brand;this.model=model;this.type=type;this.purchaseDate=purchaseDate;this.photoUrl=photoUrl;}
 public UUID getId(){return id;} public UUID getUserId(){return userId;} public String getBrand(){return brand;} public String getModel(){return model;} public String getType(){return type;} public LocalDate getPurchaseDate(){return purchaseDate;} public String getPhotoUrl(){return photoUrl;}
 public void update(String brand,String model,String type,LocalDate purchaseDate,String photoUrl){this.brand=brand;this.model=model;this.type=type;this.purchaseDate=purchaseDate;this.photoUrl=photoUrl;}
}
