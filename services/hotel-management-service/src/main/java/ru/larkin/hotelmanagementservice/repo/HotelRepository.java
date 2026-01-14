package ru.larkin.hotelmanagementservice.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.larkin.hotelmanagementservice.entity.Hotel;

import java.util.UUID;

public interface HotelRepository extends JpaRepository<Hotel, UUID> {
}

