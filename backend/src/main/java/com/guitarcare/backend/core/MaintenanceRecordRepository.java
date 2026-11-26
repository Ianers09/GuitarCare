package com.guitarcare.backend.core;
import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface MaintenanceRecordRepository extends JpaRepository<MaintenanceRecord,UUID>{List<MaintenanceRecord> findAllByUserIdAndGuitarIdOrderByActivityDateDesc(UUID userId,UUID guitarId); Optional<MaintenanceRecord> findByIdAndUserId(UUID id,UUID userId);}
