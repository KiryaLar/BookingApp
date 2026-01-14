package ru.larkin.hotelmanagementservice.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.larkin.hotelmanagementservice.entity.Room;

import java.time.LocalDate;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {

    @Query("""
            select r from Room r
            where not exists (
                select 1 from RoomHold h
                where h.room = r
                  and h.expiresAt > CURRENT_TIMESTAMP
                  and h.dateFrom < :toDate
                  and h.dateTo > :fromDate
            )
            """)
    List<Room> findAvailableRooms(@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);

    @Query("""
            select r from Room r
            where not exists (
                select 1 from RoomHold h
                where h.room = r
                  and h.expiresAt > CURRENT_TIMESTAMP
                  and h.dateFrom < :toDate
                  and h.dateTo > :fromDate
            )
            order by r.timesBooked asc
            """)
    List<Room> findRecommendedAvailableRooms(@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);
}

