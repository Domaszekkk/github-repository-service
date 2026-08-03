package com.example.githubrepositoryservice.client;

import com.example.githubrepositoryservice.dto.GitHubRepositoryDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import feign.FeignException;
import feign.RetryableException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;

import java.time.Instant;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@EnableWireMock(@ConfigureWireMock(port = 8089))
class GitHubApiClientTest {
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    @Autowired
    private GitHubApiClient gitHubApiClient;

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
        stubFor(com.github.tomakehurst.wiremock.client.WireMock.get(urlEqualTo("/repos/octocat/Hello-World"))
                .willReturn(okJson(jsonResponseBody)));

        // when
        GitHubRepositoryDto result = gitHubApiClient.getRepository("octocat", "Hello-World");

        // then
        assertAll(
                () -> assertEquals("octocat/Hello-World", result.getFullName()),
                () -> assertEquals("My first repository", result.getDescription()),
                () -> assertEquals("https://github.com/octocat/Hello-World.git", result.getCloneUrl()),
                () -> assertEquals(1500, result.getStargazersCount()),
                () -> assertEquals(Instant.parse("2011-01-26T19:01:12Z"), result.getCreatedAt())
        );
    }

    @Test
    void getRepository_RepositoryNotFound_ThrowsFeignNotFoundException() {
        // given
        stubFor(com.github.tomakehurst.wiremock.client.WireMock.get(urlEqualTo("/repos/octocat/missing"))
                .willReturn(aResponse().withStatus(404)));

        // when + then
        assertThrows(
                FeignException.NotFound.class, () -> gitHubApiClient.getRepository("octocat", "missing"));
    }

    @Test
    void getRepository_ServiceUnavailableThenSuccess_RetriesAndReturnsRepository() throws Exception {
        // given
        GitHubRepositoryDto expectedDto = GitHubRepositoryDto.builder()
                .fullName("octocat/Hello-World")
                .description("My first repository")
                .cloneUrl("https://github.com/octocat/Hello-World.git")
                .stargazersCount(1500)
                .createdAt(Instant.parse("2011-01-26T19:01:12Z"))
                .build();

        String jsonResponseBody = objectMapper.writeValueAsString(expectedDto);

        stubFor(com.github.tomakehurst.wiremock.client.WireMock.get(urlEqualTo("/repos/octocat/Hello-World"))
                .inScenario("retry-scenario")
                .whenScenarioStateIs(STARTED)
                .willReturn(aResponse().withStatus(503))
                .willSetStateTo("SECOND_ATTEMPT"));

        stubFor(com.github.tomakehurst.wiremock.client.WireMock.get(urlEqualTo("/repos/octocat/Hello-World"))
                .inScenario("retry-scenario")
                .whenScenarioStateIs("SECOND_ATTEMPT")
                .willReturn(okJson(jsonResponseBody)));

        // when
        GitHubRepositoryDto result = gitHubApiClient.getRepository("octocat", "Hello-World");

        // then
        assertAll(
                () -> assertEquals("octocat/Hello-World", result.getFullName()),
                () -> assertEquals("My first repository", result.getDescription()),
                () -> assertEquals("https://github.com/octocat/Hello-World.git", result.getCloneUrl()),
                () -> assertEquals(1500, result.getStargazersCount()),
                () -> assertEquals(Instant.parse("2011-01-26T19:01:12Z"), result.getCreatedAt())
        );
    }

    @Test
    void getRepository_ServiceUnavailablePersists_ThrowsRetryableException() {
        // given
        stubFor(com.github.tomakehurst.wiremock.client.WireMock.get(urlEqualTo("/repos/octocat/Hello-World"))
                .willReturn(aResponse().withStatus(503)));

        // when + then
        assertThrows(
                RetryableException.class, () -> gitHubApiClient.getRepository("octocat", "Hello-World"));
    }
}