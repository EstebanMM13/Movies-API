package com.estebanmmk13.movies.dtoModels.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SystemStats {
    private long totalUsers;
    private long adminUsers;
    private long regularUsers;
}
