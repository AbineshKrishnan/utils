package com.base.services.support;

import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.TemporalAdjusters;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.base.services.dto.request.BasicRequest;
import com.base.services.dto.response.ApiGetResponse;
import com.base.services.dto.response.PageListResponse;

import reactor.core.publisher.Mono;

@Component
public class ServiceSupport {

    public Mono<ApiGetResponse> getResponse(Object result) {
        return Mono.just(new ApiGetResponse()).doOnNext(apiGetResponse -> apiGetResponse.setResult(result));
    }

    public Pageable getPageable(BasicRequest basicRequest) {
        return PageRequest.of(basicRequest.getPageNo(), basicRequest.getPageSize());
    }

    public LocalDate getStartDateOfMonth(int year, Month month) {
        return LocalDate.of(year, month, 1);
    }

    public LocalDate getLastDateOfMonth(int year, Month month) {
        return LocalDate.of(year, month, 1).with(TemporalAdjusters.lastDayOfMonth());
    }

    public PageListResponse pageToPageListResponse(Page<?> page) {
        PageListResponse pageListResponse = new PageListResponse();
        pageListResponse.setTotalPages(page.getTotalPages());
        pageListResponse.setTotalElements(page.getTotalElements());
        pageListResponse.setNumberOfElements(page.getNumberOfElements());
        pageListResponse.setActivePage(page.getPageable().getPageNumber() + 1);
        pageListResponse.setIsFirst(page.isFirst());
        pageListResponse.setIsLast(page.isLast());
        return pageListResponse;
    }

}
