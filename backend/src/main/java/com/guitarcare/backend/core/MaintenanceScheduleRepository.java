package com.guitarcare.backend.core;
import org.springframework.data.jpa.repository.JpaRepository; import java.util.*; import java.time.LocalDate;
public interface MaintenanceScheduleRepository extends JpaRepository<MaintenanceSchedule,UUID>{List<MaintenanceSchedule> findAllByUserIdOrderByDueDateAsc(UUID userId); List<MaintenanceSchedule> findAllByUserIdAndDueDateBetweenAndStatusOrderByDueDateAsc(UUID userId,LocalDate from,LocalDate to,String status); Optional<MaintenanceSchedule> findByIdAndUserId(UUID id,UUID userId);}
