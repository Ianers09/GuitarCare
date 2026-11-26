package com.guitarcare.backend.core;
import jakarta.persistence.*; import java.time.LocalDate; import java.time.OffsetDateTime; import java.util.UUID;
@Entity @Table(name="maintenance_schedules")
public class MaintenanceSchedule {
 @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
 @Column(nullable=false) private UUID userId; @Column(nullable=false) private UUID guitarId;
 @Column(nullable=false,length=80) private String activityType; @Column(nullable=false) private LocalDate dueDate;
 @Column(length=1000) private String notes; @Column(nullable=false,length=20) private String status="UPCOMING";
 @Column(nullable=false) private OffsetDateTime createdAt=OffsetDateTime.now();
 protected MaintenanceSchedule(){}
 public MaintenanceSchedule(UUID userId,UUID guitarId,String activityType,LocalDate dueDate,String notes){this.userId=userId;this.guitarId=guitarId;this.activityType=activityType;this.dueDate=dueDate;this.notes=notes;}
 public UUID getId(){return id;} public UUID getUserId(){return userId;} public UUID getGuitarId(){return guitarId;} public String getActivityType(){return activityType;} public LocalDate getDueDate(){return dueDate;} public String getNotes(){return notes;} public String getStatus(){return status;}
 public void update(String activityType,LocalDate dueDate,String notes,String status){this.activityType=activityType;this.dueDate=dueDate;this.notes=notes;this.status=status;}
}
