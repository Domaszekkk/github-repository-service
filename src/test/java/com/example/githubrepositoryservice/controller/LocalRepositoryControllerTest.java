package com.example.githubrepositoryservice.controller;

import com.example.githubrepositoryservice.dto.RepositoryResponse;
import com.example.githubrepositoryservice.exception.LocalRepositoryNotFoundException;
import com.example.githubrepositoryservice.service.GitHubRepositoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.Instant;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LocalRepositoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GitHubRepositoryService gitHubRepositoryService;

    @Test
    void getRepositoryFromLocal_DataCorrect_ReturnRepository() throws Exception {
        // given
        RepositoryResponse response = RepositoryResponse.builder()
                .fullName("octocat/Hello-World")
                .description("My first repository")
                .cloneUrl("https://github.com/octocat/Hello-World.git")
                .stars(1500)
                .createdAt(Instant.parse("2011-01-26T19:01:12Z"))
                .build();

        when(gitHubRepositoryService.getRepositoryFromLocal("octocat", "Hello-World")).thenReturn(response);

        // when
        ResultActions result = mockMvc.perform(get("/local/repositories/octocat/Hello-World"));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("octocat/Hello-World"))
                .andExpect(jsonPath("$.description").value("My first repository"))
                .andExpect(jsonPath("$.cloneUrl").value("https://github.com/octocat/Hello-World.git"))
                .andExpect(jsonPath("$.stars").value(1500))
                .andExpect(jsonPath("$.createdAt").value("2011-01-26T19:01:12Z"));
    }

    @Test
    void getRepositoryFromLocal_RepositoryNotFound_ReturnNotFound() throws Exception {
        // given
        when(gitHubRepositoryService.getRepositoryFromLocal("octocat", "missing"))
                .thenThrow(new LocalRepositoryNotFoundException("octocat", "missing"));

        // when
        ResultActions result = mockMvc.perform(get("/local/repositories/octocat/missing"));

        // then
        result.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Repository not found in local database: octocat/missing"));
    }
}