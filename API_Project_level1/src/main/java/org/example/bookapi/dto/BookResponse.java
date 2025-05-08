package org.example.bookapi.dto;

import lombok.Builder;
import lombok.Data;
import java.io.Serializable;

@Data
@Builder
public class BookResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String title;
    private String author;
}
