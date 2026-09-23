package com.example.githubrepositoryservice;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Pageable {
    private Integer page;
    private Integer size;
}
