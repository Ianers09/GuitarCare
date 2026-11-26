package com.guitarcare.backend.core;
import jakarta.persistence.*; import java.time.LocalDate; import java.time.OffsetDateTime; import java.util.UUID;
@Entity @Table(name="maintenance_records")
public class MaintenanceRecord {
 @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
 @Column(nullable=false) private UUID userId; @Column(nullable=false) private UUID guitarId;
 @Column(nullable=false,length=80) private String activityType; @Column(nullable=false) private LocalDate activityDate;
 @Column(length=2000) private String notes; @Column(length=1000) private String photoUrl;
 @Column(nullable=false) private OffsetDateTime createdAt=OffsetDateTime.now();
 protected MaintenanceRecord(){}
 public MaintenanceRecord(UUID userId,UUID guitarId,String activityType,LocalDate activityDate,String notes,String photoUrl){this.userId=userId;this.guitarId=guitarId;this.activityType=activityType;this.activityDate=activityDate;this.notes=notes;this.photoUrl=photoUrl;}
 public UUID getId(){return id;} public UUID getUserId(){return userId;} public UUID getGuitarId(){return guitarId;} public String getActivityType(){return activityType;} public LocalDate getActivityDate(){return activityDate;} public String getNotes(){return notes;} public String getPhotoUrl(){return photoUrl;}
 public void update(String activityType,LocalDate activityDate,String notes,String photoUrl){this.activityType=activityType;this.activityDate=activityDate;this.notes=notes;this.photoUrl=photoUrl;}
}
