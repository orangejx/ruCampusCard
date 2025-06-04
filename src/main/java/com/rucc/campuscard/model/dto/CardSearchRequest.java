package com.rucc.campuscard.model.dto;

import lombok.Data;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

@Data
public class CardSearchRequest {
    private Integer page;
    private Integer size;
    private String studentId;
    private String studentName;

    public Pageable getPageable() {
        return Pageable.ofSize(size != null ? size : 10)
                .withPage(page != null ? page : 0);
    }
}
