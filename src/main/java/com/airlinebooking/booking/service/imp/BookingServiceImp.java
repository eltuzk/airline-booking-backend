package com.airlinebooking.booking.service.imp;

import com.airlinebooking.booking.entity.*;
import com.airlinebooking.booking.exceptions.AppException;
import com.airlinebooking.booking.exceptions.ErrorCode;
import com.airlinebooking.booking.mapper.BookingMapper;
import com.airlinebooking.booking.payload.request.BookingRequest;
import com.airlinebooking.booking.payload.request.PassengerRequest;
import com.airlinebooking.booking.payload.response.BookingResponse;
import com.airlinebooking.booking.repository.*;
import com.airlinebooking.booking.service.BookingService;
import com.airlinebooking.booking.service.FlightService;
import com.airlinebooking.booking.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Service
public class BookingServiceImp implements BookingService {
    private static final long TIME_TO_EXTEND_SEAT_LOCK = 15;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private BaggageCatalogRepository baggageCatalogRepository;

    @Autowired
    private PassengerTicketRepository passengerTicketRepository;

    @Autowired
    private TicketBaggageRepository ticketBaggageRepository;

    @Autowired
    private RedisService redisService;

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PassengerRepository passengerRepository;

    @Autowired
    private BookingMapper bookingMapper;


    @Transactional
    @Override
    public BookingResponse createBooking(BookingRequest bookingRequest, Integer userId) {
        // gia hạn ghế trên redis
        extendSeatLock(bookingRequest);

        //lấy thông tin chuyến bay ra tuwf db lên có đuược base_price
        FlightEntity flightRunEntity = flightRepository.findById(bookingRequest.getRunFlightId()).get();
        FlightEntity flightReturnEntity = null;
        if(bookingRequest.getReturnFlightId() != null){
            flightReturnEntity = flightRepository.findById(bookingRequest.getReturnFlightId()).get();
        }

        // Tạo mẫu booking ban đầu trạng thái PENDING
        BookingEntity bookingEntity = initPendingBooking(bookingRequest, userId);

        //xử lý thông tin hành khách (lưu thông tin hành khách xuống db + tính tổng tiền tất cả)
        BigDecimal totalAmoutAllPassenger = processAllPassenger(bookingRequest, bookingEntity, flightRunEntity, flightReturnEntity);

        bookingEntity.setTotalAmount(totalAmoutAllPassenger);



        return bookingMapper.toResponse(bookingRepository.save(bookingEntity)) ;
    }

    // hàm gia hạn Redis, ở đây gia hạn 15p
    private void extendSeatLock(BookingRequest bookingRequest){

        for(PassengerRequest pRequest : bookingRequest.getPassengerRequestList()){
            if(!"INFANT".equals(pRequest.getPassengerType())){
                redisService.extendSeatLock(bookingRequest.getRunFlightId(), pRequest.getRunSeatNumber(), TIME_TO_EXTEND_SEAT_LOCK);
                // không cần check chuyến về, chiều đi có thì gia hạn là duwduwojc rồi
                if(bookingRequest.getReturnFlightId() != null){
                    redisService.extendSeatLock(bookingRequest.getReturnFlightId(), pRequest.getReturnSeatNumber(), TIME_TO_EXTEND_SEAT_LOCK);

                }
            }
        }

        return;

    }

    //khi trên client gửi request thì sẽ tạo mẫu booking ban đầu lưu tạm xuống db
    //nhưng lúc này chwua biết tổng số tiền, và trạng thái booking là pending
    private BookingEntity initPendingBooking(BookingRequest  bookingRequest, Integer userId){
        BookingEntity bookingEntity = new BookingEntity();

        bookingEntity.setBookingCode(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        bookingEntity.setUserId(userId);
        bookingEntity.setContactName(bookingRequest.getContactInfoRequest().getFullName());
        bookingEntity.setContactEmail(bookingRequest.getContactInfoRequest().getEmail());
        bookingEntity.setContactPhone(bookingRequest.getContactInfoRequest().getPhoneNumber());
        bookingEntity.setStatus("PENDING");
        bookingEntity.setTotalAmount(BigDecimal.ZERO);

        return bookingRepository.save(bookingEntity);   // lưu lúc này n trả lại booking id ã có bookingId theo thứ tự dưới database
    }

    //xử lý tính tổng + lưu thông tin passenger
    private BigDecimal processAllPassenger(BookingRequest request, BookingEntity bookingEntity, FlightEntity runFlightEntity, FlightEntity returnFlightEntity){
        BigDecimal totalAmount = BigDecimal.ZERO;

        for(PassengerRequest pRequest : request.getPassengerRequestList()){
            // lưu thông tin của passenger
            PassengerEntity passengerEntity = saveInfoPassenger(pRequest);

            // chheck nếu là INFANT thì bỏ qua
            if("INFANT".equals(pRequest.getPassengerType())){
                continue;
            }

            //tính tổng tiền của 1 người cả chiềuddisi và chiều về(nếu có) và cộng tổng vào
            totalAmount = totalAmount.add(calculateTotalTicketPasssenger(bookingEntity, runFlightEntity, returnFlightEntity, pRequest,passengerEntity));



        }

        return totalAmount;
    }

    //Lưu thông tin từng khách hàng
    private PassengerEntity saveInfoPassenger(PassengerRequest request){
        PassengerEntity passengerEntity = new PassengerEntity();

        passengerEntity.setFullName(request.getFullName());
        passengerEntity.setDateOfBirth(request.getDateOfBirth());
        passengerEntity.setGender(request.getGender());
        passengerEntity.setPassengerType(request.getPassengerType());


        return passengerRepository.save(passengerEntity);

    }

    private BigDecimal calculateTotalTicketPasssenger(BookingEntity booking, FlightEntity runFlightEntity, FlightEntity returnFlightEntity, PassengerRequest passengerRequest, PassengerEntity passenger){
        BigDecimal totalPricePassenger = BigDecimal.ZERO;

        //tính tiền chiều đi
        totalPricePassenger = totalPricePassenger.add(calculateTicket(booking, runFlightEntity, passengerRequest.getRunSeatNumber(), passengerRequest.getRunBaggageId(), passenger));

        //tính tiền chiều về nễu có
        if(returnFlightEntity != null){
            totalPricePassenger = totalPricePassenger.add(calculateTicket(booking, returnFlightEntity, passengerRequest.getReturnSeatNumber(), passengerRequest.getReturnBaggageId(), passenger));

        }

        return totalPricePassenger;


    }


    private BigDecimal calculateTicket(BookingEntity booking, FlightEntity flight, String seatNumber, Integer baggageId, PassengerEntity passenger) {

        SeatEntity seat = new SeatEntity();

        BigDecimal ticketPrice = flight.getBasePrice();

        // th1: khách chủ động chọn ghế thì sẽ có giá khác
        if(seatNumber != null && !seatNumber.trim().isEmpty()){ // ở đây check truyền vào có null và có phải toàn dấu cách không
            Optional<SeatEntity> seatEntity = seatRepository.findByFlightIdAndSeatNumber(flight.getFlightId(), seatNumber);

            if(!seatEntity.isPresent()){
                throw new AppException(ErrorCode.SEAT_NOT_FOUND);
            }

            //tính giá vé thật
            ticketPrice = ticketPrice.multiply(seatEntity.get().getPriceMultiplier());

            seat = seatEntity.get();


        } else {// th2: là bỏ qua và không chọn ghế thì sẽ lấy giá Mặc định chuyens bay đó và vị trí ghees random theo hạng ghế thấp đến cao
            Optional<SeatEntity> seatRandomEntity = seatRepository.findRandomSeat(flight.getFlightId());

            if(!seatRandomEntity.isPresent()){
                throw new AppException(ErrorCode.SEAT_NOT_FOUND);
            }

            seat = seatRandomEntity.get();


        }

        // đến bước coi hành lý giá bao hiêu để cộng gi tiền vào
        BaggageCatalogEntity baggageCatalog = null;
        if(baggageId != null){
            Optional<BaggageCatalogEntity> baggageCatalogEntity = baggageCatalogRepository.findById(baggageId);

            if(!baggageCatalogEntity.isPresent()){
                throw new AppException(ErrorCode.BAGGAGE_NOT_FOUND);
            }

            baggageCatalog = baggageCatalogEntity.get();

            ticketPrice = ticketPrice.add(baggageCatalog.getPrice());
        }


        // lưu các phần dữ liệu người đó vào db PassengerTicket
        PassengerTicketEntity passengerTicket = new PassengerTicketEntity();

        passengerTicket.setBooking(booking);
        passengerTicket.setPassenger(passenger);
        passengerTicket.setSeat(seat);
        passengerTicket.setTicketPrice(ticketPrice);
        passengerTicket = passengerTicketRepository.save(passengerTicket);

        // lưu dữ liệu vào bảng ticketBaggage để biết vé đó đi kém với hành lý nào
        if(baggageId != null){
            TicketBaggageEntity ticketBaggageEntity = new TicketBaggageEntity();
            ticketBaggageEntity.setTicket(passengerTicket);
            ticketBaggageEntity.setBaggageCatalog(baggageCatalog);
            ticketBaggageEntity = ticketBaggageRepository.save(ticketBaggageEntity);
        }


        return ticketPrice;
    }


}
