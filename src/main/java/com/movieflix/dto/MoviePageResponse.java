package com.movieflix.dto;

import java.util.List;

public record MoviePageResponse(List<MovieDto>movieDtos,
                               Integer pageNumer,
                                Integer pageSize,
                                int totalElements,
                                long totalPages,
                                boolean isLast) {
}
