package com.airlinebooking.booking.payload.response;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@Builder
public class PageResponse<T> {
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private List<T> items;


    public static <T> PageResponse<T> of (Page<?> pageData, List<T> listData){
        return PageResponse.<T>builder()
                .page(pageData.getNumber() + 1)
                .size(pageData.getSize())
                .totalElements(pageData.getTotalElements())
                .totalPages(pageData.getTotalPages())
                .items(listData)
                .build();
    }
}
