package com.airlinebooking.booking.job;


import com.airlinebooking.booking.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingCleanupJob {
    private final BookingRepository bookingRepository;


    @Scheduled(cron = "0 * * * * *")
    public void cleanUpExpiredPendingBookings(){
        LocalDateTime expriredTime = LocalDateTime.now().minusMinutes(15);

        int count = bookingRepository.cancelExpiredBookings(expriredTime);

        if(count > 0){
            log.info("CRON JOB CỌ VỆ SINH: Đã quét và tự động chuyển trạng thái CANCELLED cho {} đơn hàng PENDING quá 15 phút.", count);
        }
    }

}
