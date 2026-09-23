package com.example.githubrepositoryservice.controller;

import com.example.githubrepositoryservice.dto.RepositoryResponse;
import com.example.githubrepositoryservice.exception.LocalRepositoryNotFoundException;
import com.example.githubrepositoryservice.exception.RepositoryAlreadyExistsException;
import com.example.githubrepositoryservice.exception.RepositoryNotFoundException;
import com.example.githubrepositoryservice.service.GitHubRepositoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.Instant;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class GitHubRepositoryControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private GitHubRepositoryService gitHubRepositoryService;

    @Test
    void getRepository_DataCorrect_ReturnRepository() throws Exception {
        // given
        RepositoryResponse response = RepositoryResponse.builder()
                .fullName("octocat/Hello-World")
                .description("My first repository")
                .cloneUrl("https://github.com/octocat/Hello-World.git")
                .stars(1500)
                .createdAt(Instant.parse("2011-01-26T19:01:12Z"))
                .build();

        when(gitHubRepositoryService.getRepository("octocat", "Hello-World")).thenReturn(response);

        // when
        ResultActions result = mockMvc.perform(get("/repositories/octocat/Hello-World"));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("octocat/Hello-World"))
                .andExpect(jsonPath("$.description").value("My first repository"))
                .andExpect(jsonPath("$.cloneUrl").value("https://github.com/octocat/Hello-World.git"))
                .andExpect(jsonPath("$.stars").value(1500))
                .andExpect(jsonPath("$.createdAt").value("2011-01-26T19:01:12Z"));
    }

    @Test
    void getRepository_RepositoryNotFound_ReturnNotFound() throws Exception {
        // given
        when(gitHubRepositoryService.getRepository("octocat", "missing"))
                .thenThrow(new RepositoryNotFoundException("octocat", "missing"));

        // when
        ResultActions result = mockMvc.perform(get("/repositories/octocat/missing"));

        // then
        result.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Repository not found: octocat/missing"));
    }

    @Test
    void saveRepository_DataCorrect_ReturnRepository() throws Exception {
        // given
        RepositoryResponse response = RepositoryResponse.builder()
                .fullName("octocat/Hello-World")
                .description("My first repository")
                .cloneUrl("https://github.com/octocat/Hello-World.git")
                .stars(1500)
                .createdAt(Instant.parse("2011-01-26T19:01:12Z"))
                .build();

        when(gitHubRepositoryService.saveRepository("octocat", "Hello-World")).thenReturn(response);

        // when
        ResultActions result = mockMvc.perform(post("/repositories/octocat/Hello-World"));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("octocat/Hello-World"))
                .andExpect(jsonPath("$.description").value("My first repository"))
                .andExpect(jsonPath("$.cloneUrl").value("https://github.com/octocat/Hello-World.git"))
                .andExpect(jsonPath("$.stars").value(1500))
                .andExpect(jsonPath("$.createdAt").value("2011-01-26T19:01:12Z"));
    }

    @Test
    void saveRepository_RepositoryNotFound_ReturnNotFound() throws Exception {
        // given
        when(gitHubRepositoryService.saveRepository("octocat", "missing"))
                .thenThrow(new RepositoryNotFoundException("octocat", "missing"));

        // when
        ResultActions result = mockMvc.perform(post("/repositories/octocat/missing"));

        // then
        result.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Repository not found: octocat/missing"));
    }

    @Test
    void saveRepository_RepositoryAlreadyExists_ReturnConflict() throws Exception {
        // given
        when(gitHubRepositoryService.saveRepository("octocat", "Hello-World"))
                .thenThrow(new RepositoryAlreadyExistsException("octocat", "Hello-World"));

        // when
        ResultActions result = mockMvc.perform(post("/repositories/octocat/Hello-World"));

        // then
        result.andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Repository already exists in local database: octocat/Hello-World"));
    }

    @Test
    void updateRepository_DataCorrect_ReturnRepository() throws Exception {
        // given
        RepositoryResponse response = RepositoryResponse.builder()
                .fullName("octocat/Hello-World")
                .description("Updated description")
                .cloneUrl("https://github.com/octocat/Hello-World.git")
                .stars(1600)
                .createdAt(Instant.parse("2011-01-26T19:01:12Z"))
                .build();

        when(gitHubRepositoryService.updateRepository("octocat", "Hello-World")).thenReturn(response);

        // when
        ResultActions result = mockMvc.perform(put("/repositories/octocat/Hello-World"));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("octocat/Hello-World"))
                .andExpect(jsonPath("$.description").value("Updated description"))
                .andExpect(jsonPath("$.cloneUrl").value("https://github.com/octocat/Hello-World.git"))
                .andExpect(jsonPath("$.stars").value(1600))
                .andExpect(jsonPath("$.createdAt").value("2011-01-26T19:01:12Z"));
    }

    @Test
    void updateRepository_RepositoryNotFoundLocally_ReturnNotFound() throws Exception {
        // given
        when(gitHubRepositoryService.updateRepository("octocat", "missing"))
                .thenThrow(new LocalRepositoryNotFoundException("octocat", "missing"));

        // when
        ResultActions result = mockMvc.perform(put("/repositories/octocat/missing"));

        // then
        result.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Repository not found in local database: octocat/missing"));
    }

    @Test
    void deleteRepository_DataCorrect_ReturnNoContent() throws Exception {
        // given
        doNothing().when(gitHubRepositoryService).deleteRepository("octocat", "Hello-World");

        // when
        ResultActions result = mockMvc.perform(delete("/repositories/octocat/Hello-World"));

        // then
        result.andExpect(status().isNoContent());
    }

    @Test
    void deleteRepository_RepositoryNotFound_ReturnNotFound() throws Exception {
        // given
        doThrow(new LocalRepositoryNotFoundException("octocat", "missing"))
                .when(gitHubRepositoryService).deleteRepository("octocat", "missing");

        // when
        ResultActions result = mockMvc.perform(delete("/repositories/octocat/missing"));

        // then
        result.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Repository not found in local database: octocat/missing"));
    }
}