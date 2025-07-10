package edu.ifmg.hotelBAO.repository;

import edu.ifmg.hotelBAO.entities.Stay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface StayRepository extends JpaRepository<Stay, Long> {

    List<Stay> findByUserId(Long userId);

    @Query("SELECT s FROM Stay s WHERE s.room.id = :roomId AND " +
            "(:checkInDate < s.checkOutDate AND :checkOutDate > s.checkInDate)")
    List<Stay> findConflictingStays(@Param("roomId") Long roomId,
                                    @Param("checkInDate") LocalDate checkInDate,
                                    @Param("checkOutDate") LocalDate checkOutDate);

}
