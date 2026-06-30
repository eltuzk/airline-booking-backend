package com.airlinebooking.booking.repository;

import com.airlinebooking.booking.entity.BookingEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<BookingEntity, Integer> {


    // lệnh join luôn bảng vé không lazy nữa
    @Query("""
    SELECT b
    FROM BookingEntity b
    JOIN FETCH b.passengerTicketEntityList t
    JOIN FETCH t.seat s
    JOIN FETCH s.flight f
    WHERE b.bookingId = :id
    """)
    Optional<BookingEntity> findByIdWithTickets(@Param("id") Integer id);


    @Query("""
        SELECT b.contactEmail
        FROM BookingEntity b
        WHERE b.bookingId = :id
        """


    )
    Optional<String> getContactEmailByBookingId(@Param("id") Integer id);


    @Query(value = """

        select *
        from bookings b
        where b.user_id = :id
        order by b.created_at desc
    """, nativeQuery = true
    )
    List<BookingEntity> getBookingEntitiesByUserId(@Param("id") Integer userId);


    @Modifying
    @Transactional
    @Query( value =
            """

            update bookings b
            set b.status = "CANCELLED"
            where b.status = "PENDING"
            and b.created_at <= :expiredTime
            """, nativeQuery = true
    )
    int cancelExpiredBookings(@Param("expiredTime") LocalDateTime time);

}
