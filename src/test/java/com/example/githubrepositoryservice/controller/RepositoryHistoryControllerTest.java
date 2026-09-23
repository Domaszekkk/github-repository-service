package com.example.githubrepositoryservice.controller;

import com.example.githubrepositoryservice.dto.RepositoryResponse;
import com.example.githubrepositoryservice.dto.PageResponse;
import com.example.githubrepositoryservice.service.GitHubRepositoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RepositoryHistoryControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private GitHubRepositoryService gitHubRepositoryService;

    @Test
    void getRepositoryHistory_DataCorrect_ReturnHistory() throws Exception {
        // given
        RepositoryResponse repositoryResponse = RepositoryResponse.builder()
                .fullName("octocat/Hello-World")
                .description("My first repository")
                .cloneUrl("https://github.com/octocat/Hello-World.git")
                .stars(1500)
                .createdAt(Instant.parse("2011-01-26T19:01:12Z"))
                .build();

        PageResponse<RepositoryResponse> pageResponse = PageResponse.<RepositoryResponse>builder()
                .content(List.of(repositoryResponse))
                .pageNumber(0)
                .pageSize(10)
                .totalElements(1)
                .totalPages(1)
                .last(true)
                .build();

        when(gitHubRepositoryService.getRepositoryHistory(any(Pageable.class))).thenReturn(pageResponse);

        // when
        ResultActions result = mockMvc.perform(get("/repositories?page=0&size=10"));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].fullName").value("octocat/Hello-World"))
                .andExpect(jsonPath("$.content[0].description").value("My first repository"))
                .andExpect(jsonPath("$.content[0].cloneUrl").value("https://github.com/octocat/Hello-World.git"))
                .andExpect(jsonPath("$.content[0].stars").value(1500))
                .andExpect(jsonPath("$.content[0].createdAt").value("2011-01-26T19:01:12Z"))
                .andExpect(jsonPath("$.pageNumber").value(0))
                .andExpect(jsonPath("$.pageSize").value(10))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.last").value(true));
    }
}