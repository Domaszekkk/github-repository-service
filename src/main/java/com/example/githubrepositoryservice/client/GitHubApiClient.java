package com.example.githubrepositoryservice.client;

import com.example.githubrepositoryservice.dto.GitHubRepositoryDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "github-api", url = "${github.api.url}")
public interface GitHubApiClient {
    @GetMapping("/repos/{owner}/{repo}")
    GitHubRepositoryDto getRepository(@PathVariable("owner") String owner, @PathVariable("repo") String repo);
}