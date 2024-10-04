package com.base.services.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageListResponse {

    private Object listResponse;

    private Integer activePage;

    private Integer totalPages;

    private Long totalElements;

    private Integer numberOfElements;

    private Boolean isFirst;

    private Boolean isLast;

}
