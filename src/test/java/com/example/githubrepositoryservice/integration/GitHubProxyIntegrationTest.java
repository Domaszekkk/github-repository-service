package com.example.githubrepositoryservice.integration;

import com.example.githubrepositoryservice.dto.GitHubRepositoryDto;
import com.example.githubrepositoryservice.entity.Repository;
import com.example.githubrepositoryservice.repository.RepositoryJpaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;

import java.time.Instant;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@EnableWireMock(@ConfigureWireMock(port = 8089))
class GithubProxyIntegrationTest {
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private RepositoryJpaRepository repositoryJpaRepository;

    @Test
    void getRepository_DataCorrect_ReturnRepository() throws Exception {
        // given
        GitHubRepositoryDto expectedDto = GitHubRepositoryDto.builder()
                .fullName("octocat/Hello-World")
                .description("My first repository")
                .cloneUrl("https://github.com/octocat/Hello-World.git")
                .stargazersCount(1500)
                .createdAt(Instant.parse("2011-01-26T19:01:12Z"))
                .build();

        String jsonResponseBody = objectMapper.writeValueAsString(expectedDto);
        stubFor(WireMock.get(urlEqualTo("/repos/octocat/Hello-World")).willReturn(okJson(jsonResponseBody)));

        RequestBuilder request = MockMvcRequestBuilders.get("/repositories/{owner}/{repository-name}", "octocat", "Hello-World")
                .accept(MediaType.APPLICATION_JSON);

        // when
        ResultActions result = mockMvc.perform(request);

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("octocat/Hello-World"))
                .andExpect(jsonPath("$.description").value("My first repository"))
                .andExpect(jsonPath("$.cloneUrl").value("https://github.com/octocat/Hello-World.git"))
                .andExpect(jsonPath("$.stars").value(1500))
                .andExpect(jsonPath("$.createdAt").value("2011-01-26T19:01:12Z"));
    }

    @Test
    void getRepository_RepositoryNotFoundOnGithub_ReturnNotFound() throws Exception {
        // given
        stubFor(WireMock.get(urlEqualTo("/repos/octocat/missing"))
                .willReturn(aResponse().withStatus(404)));

        RequestBuilder request = MockMvcRequestBuilders.get("/repositories/{owner}/{repository-name}", "octocat", "missing")
                .accept(MediaType.APPLICATION_JSON);

        // when
        ResultActions result = mockMvc.perform(request);

        // then
        result.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Repository not found: octocat/missing"));
    }

    @Test
    void getRepository_GithubServiceUnavailableThenSuccess_RetriesAndReturnsRepository() throws Exception {
        // given
        GitHubRepositoryDto expectedDto = GitHubRepositoryDto.builder()
                .fullName("octocat/Hello-World")
                .description("My first repository")
                .cloneUrl("https://github.com/octocat/Hello-World.git")
                .stargazersCount(1500)
                .createdAt(Instant.parse("2011-01-26T19:01:12Z"))
                .build();

        String jsonResponseBody = objectMapper.writeValueAsString(expectedDto);

        stubFor(WireMock.get(urlEqualTo("/repos/octocat/Hello-World"))
                .inScenario("controller-retry-scenario")
                .whenScenarioStateIs(STARTED)
                .willReturn(aResponse().withStatus(503))
                .willSetStateTo("SECOND_ATTEMPT"));

        stubFor(WireMock.get(urlEqualTo("/repos/octocat/Hello-World"))
                .inScenario("controller-retry-scenario")
                .whenScenarioStateIs("SECOND_ATTEMPT")
                .willReturn(okJson(jsonResponseBody)));

        RequestBuilder request = MockMvcRequestBuilders.get("/repositories/{owner}/{repository-name}", "octocat", "Hello-World")
                .accept(MediaType.APPLICATION_JSON);

        // when
        ResultActions result = mockMvc.perform(request);

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("octocat/Hello-World"))
                .andExpect(jsonPath("$.description").value("My first repository"))
                .andExpect(jsonPath("$.cloneUrl").value("https://github.com/octocat/Hello-World.git"))
                .andExpect(jsonPath("$.stars").value(1500))
                .andExpect(jsonPath("$.createdAt").value("2011-01-26T19:01:12Z"));
    }

    @Test
    void getRepository_GithubServiceUnavailablePersists_ReturnServiceUnavailable() throws Exception {
        // given
        stubFor(WireMock.get(urlEqualTo("/repos/octocat/Hello-World"))
                .willReturn(aResponse().withStatus(503)));

        RequestBuilder request = MockMvcRequestBuilders.get("/repositories/{owner}/{repository-name}", "octocat", "Hello-World")
                .accept(MediaType.APPLICATION_JSON);

        // when
        ResultActions result = mockMvc.perform(request);

        // then
        result.andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.message").value("GitHub API is currently unavailable, please try again later"));
    }

    @Test
    void saveRepository_DataCorrect_ReturnRepository() throws Exception {
        // given
        GitHubRepositoryDto expectedDto = GitHubRepositoryDto.builder()
                .fullName("octocat/Hello-World")
                .description("My first repository")
                .cloneUrl("https://github.com/octocat/Hello-World.git")
                .stargazersCount(1500)
                .createdAt(Instant.parse("2011-01-26T19:01:12Z"))
                .build();

        String jsonResponseBody = objectMapper.writeValueAsString(expectedDto);
        stubFor(WireMock.get(urlEqualTo("/repos/octocat/Hello-World")).willReturn(okJson(jsonResponseBody)));

        Repository saved = Repository.builder()
                .id(1L)
                .fullName("octocat/Hello-World")
                .description("My first repository")
                .cloneUrl("https://github.com/octocat/Hello-World.git")
                .stars(1500)
                .createdAt(Instant.parse("2011-01-26T19:01:12Z"))
                .fetchedAt(Instant.now())
                .build();

        when(repositoryJpaRepository.findByFullName("octocat/Hello-World")).thenReturn(Optional.empty());
        when(repositoryJpaRepository.save(any(Repository.class))).thenReturn(saved);

        RequestBuilder request = MockMvcRequestBuilders.post("/repositories/{owner}/{repository-name}", "octocat", "Hello-World")
                .accept(MediaType.APPLICATION_JSON);

        // when
        ResultActions result = mockMvc.perform(request);

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("octocat/Hello-World"))
                .andExpect(jsonPath("$.description").value("My first repository"))
                .andExpect(jsonPath("$.cloneUrl").value("https://github.com/octocat/Hello-World.git"))
                .andExpect(jsonPath("$.stars").value(1500))
                .andExpect(jsonPath("$.createdAt").value("2011-01-26T19:01:12Z"));
    }

    @Test
    void saveRepository_RepositoryAlreadyExists_ReturnConflict() throws Exception {
        // given
        Repository existing = Repository.builder()
                .id(1L)
                .fullName("octocat/Hello-World")
                .build();

        when(repositoryJpaRepository.findByFullName("octocat/Hello-World")).thenReturn(Optional.of(existing));

        RequestBuilder request = MockMvcRequestBuilders.post("/repositories/{owner}/{repository-name}", "octocat", "Hello-World")
                .accept(MediaType.APPLICATION_JSON);

        // when
        ResultActions result = mockMvc.perform(request);

        // then
        result.andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Repository already exists in local database: octocat/Hello-World"));
    }

    @Test
    void deleteRepository_DataCorrect_ReturnNoContent() throws Exception {
        // given
        Repository existing = Repository.builder()
                .id(1L)
                .fullName("octocat/Hello-World")
                .build();

        when(repositoryJpaRepository.findByFullName("octocat/Hello-World")).thenReturn(Optional.of(existing));
        RequestBuilder request = MockMvcRequestBuilders.delete("/repositories/{owner}/{repository-name}", "octocat", "Hello-World");

        // when
        ResultActions result = mockMvc.perform(request);

        // then
        result.andExpect(status().isNoContent());
    }

    @Test
    void deleteRepository_RepositoryNotFound_ReturnNotFound() throws Exception {
        // given
        when(repositoryJpaRepository.findByFullName("octocat/missing")).thenReturn(Optional.empty());
        RequestBuilder request = MockMvcRequestBuilders.delete("/repositories/{owner}/{repository-name}", "octocat", "missing");

        // when
        ResultActions result = mockMvc.perform(request);

        // then
        result.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Repository not found in local database: octocat/missing"));
    }
}