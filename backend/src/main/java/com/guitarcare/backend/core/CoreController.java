package com.guitarcare.backend.core;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api")
public class CoreController {
 private static final String SESSION_USER_ID="GUITARCARE_USER_ID";
 private final GuitarRepository guitars; private final MaintenanceRecordRepository records; private final MaintenanceScheduleRepository schedules;
 public CoreController(GuitarRepository guitars,MaintenanceRecordRepository records,MaintenanceScheduleRepository schedules){this.guitars=guitars;this.records=records;this.schedules=schedules;}
 private UUID owner(HttpServletRequest request){HttpSession s=request.getSession(false);if(s==null||s.getAttribute(SESSION_USER_ID)==null)throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Please sign in.");try{return UUID.fromString((String)s.getAttribute(SESSION_USER_ID));}catch(Exception e){throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid session.");}}
 private Guitar ownedGuitar(UUID id,UUID user){return guitars.findByIdAndUserId(id,user).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Guitar not found."));}
 public record GuitarInput(@NotBlank @Size(max=100) String brand,@NotBlank @Size(max=120) String model,@NotBlank @Size(max=40) String type,LocalDate purchaseDate,@Size(max=1000) String photoUrl){}
 public record RecordInput(@NotBlank @Size(max=80) String activityType,@NotNull LocalDate activityDate,@Size(max=2000) String notes,@Size(max=1000) String photoUrl){}
 public record ScheduleInput(@NotBlank @Size(max=80) String activityType,@NotNull LocalDate dueDate,@Size(max=1000) String notes,@Pattern(regexp="UPCOMING|COMPLETED|CANCELLED") String status){}
 @GetMapping("/guitars") public List<Guitar> listGuitars(HttpServletRequest r){return guitars.findAllByUserIdOrderByCreatedAtDesc(owner(r));}
 @PostMapping("/guitars") @ResponseStatus(HttpStatus.CREATED) public Guitar addGuitar(@Valid @RequestBody GuitarInput in,HttpServletRequest r){return guitars.save(new Guitar(owner(r),in.brand().trim(),in.model().trim(),in.type().trim(),in.purchaseDate(),in.photoUrl()));}
 @PutMapping("/guitars/{id}") public Guitar editGuitar(@PathVariable UUID id,@Valid @RequestBody GuitarInput in,HttpServletRequest r){UUID u=owner(r);Guitar g=ownedGuitar(id,u);g.update(in.brand().trim(),in.model().trim(),in.type().trim(),in.purchaseDate(),in.photoUrl());return guitars.save(g);}
 @DeleteMapping("/guitars/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void deleteGuitar(@PathVariable UUID id,HttpServletRequest r){UUID u=owner(r);ownedGuitar(id,u);records.findAllByUserIdAndGuitarIdOrderByActivityDateDesc(u,id).forEach(records::delete);schedules.findAllByUserIdOrderByDueDateAsc(u).stream().filter(x->x.getGuitarId().equals(id)).forEach(schedules::delete);guitars.deleteById(id);}
 @GetMapping("/guitars/{id}/maintenance") public List<MaintenanceRecord> history(@PathVariable UUID id,HttpServletRequest r){UUID u=owner(r);ownedGuitar(id,u);return records.findAllByUserIdAndGuitarIdOrderByActivityDateDesc(u,id);}
 @PostMapping("/guitars/{id}/maintenance") @ResponseStatus(HttpStatus.CREATED) public MaintenanceRecord addRecord(@PathVariable UUID id,@Valid @RequestBody RecordInput in,HttpServletRequest r){UUID u=owner(r);ownedGuitar(id,u);return records.save(new MaintenanceRecord(u,id,in.activityType().trim(),in.activityDate(),in.notes(),in.photoUrl()));}
 @PutMapping("/maintenance/{id}") public MaintenanceRecord editRecord(@PathVariable UUID id,@Valid @RequestBody RecordInput in,HttpServletRequest r){UUID u=owner(r);MaintenanceRecord m=records.findByIdAndUserId(id,u).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Maintenance record not found."));m.update(in.activityType().trim(),in.activityDate(),in.notes(),in.photoUrl());return records.save(m);}
 @DeleteMapping("/maintenance/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void deleteRecord(@PathVariable UUID id,HttpServletRequest r){UUID u=owner(r);MaintenanceRecord m=records.findByIdAndUserId(id,u).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Maintenance record not found."));records.delete(m);}
 @GetMapping("/schedules") public List<MaintenanceSchedule> listSchedules(HttpServletRequest r){return schedules.findAllByUserIdOrderByDueDateAsc(owner(r));}
 @PostMapping("/guitars/{id}/schedules") @ResponseStatus(HttpStatus.CREATED) public MaintenanceSchedule addSchedule(@PathVariable UUID id,@Valid @RequestBody ScheduleInput in,HttpServletRequest r){UUID u=owner(r);ownedGuitar(id,u);if(in.dueDate().isBefore(LocalDate.now()))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Due date must be today or later.");return schedules.save(new MaintenanceSchedule(u,id,in.activityType().trim(),in.dueDate(),in.notes()));}
 @PutMapping("/schedules/{id}") public MaintenanceSchedule editSchedule(@PathVariable UUID id,@Valid @RequestBody ScheduleInput in,HttpServletRequest r){UUID u=owner(r);MaintenanceSchedule m=schedules.findByIdAndUserId(id,u).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Schedule not found."));if(in.dueDate().isBefore(LocalDate.now()) && !"COMPLETED".equals(in.status()))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Due date must be today or later.");m.update(in.activityType().trim(),in.dueDate(),in.notes(),in.status()==null?m.getStatus():in.status());return schedules.save(m);}
 @DeleteMapping("/schedules/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void deleteSchedule(@PathVariable UUID id,HttpServletRequest r){UUID u=owner(r);MaintenanceSchedule m=schedules.findByIdAndUserId(id,u).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Schedule not found."));schedules.delete(m);}
 @GetMapping("/reminders") public List<MaintenanceSchedule> reminders(HttpServletRequest r){return schedules.findAllByUserIdAndDueDateBetweenAndStatusOrderByDueDateAsc(owner(r),LocalDate.now(),LocalDate.now().plusDays(30),"UPCOMING");}
}
