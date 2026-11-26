package com.guitarcare.backend.core;
import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface GuitarRepository extends JpaRepository<Guitar,UUID>{List<Guitar> findAllByUserIdOrderByCreatedAtDesc(UUID userId); Optional<Guitar> findByIdAndUserId(UUID id,UUID userId);}
