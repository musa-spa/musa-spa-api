package spa.musa.send.mensage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import spa.musa.send.mensage.entity.Appointment;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByDate(LocalDate date);

    List<Appointment> findByDateBetween(LocalDate startDate, LocalDate endDate);

    List<Appointment> findByClientId(Long clientId);

    List<Appointment> findByStatus(Appointment.AppointmentStatus status);

    @Query("SELECT a FROM Appointment a WHERE a.date = :date ORDER BY a.time")
    List<Appointment> findByDateOrderByTime(@Param("date") LocalDate date);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.date = :date")
    Long countByDate(@Param("date") LocalDate date);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.date = :date AND a.status = 'CONFIRMADO'")
    Long countConfirmedByDate(@Param("date") LocalDate date);
    
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.date = :date AND a.status = 'REALIZADO'")
    Long countCompletedByDate(@Param("date") LocalDate date);
    
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.date = :date AND a.status = 'PENDENTE'")
    Long countPendingByDate(@Param("date") LocalDate date);

    @Query("SELECT a FROM Appointment a WHERE a.date BETWEEN :startDate AND :endDate ORDER BY a.date, a.time")
    List<Appointment> findByDateRangeOrderByDateTime(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
