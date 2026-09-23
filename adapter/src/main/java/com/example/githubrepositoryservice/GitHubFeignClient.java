package com.example.githubrepositoryservice;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "github", url = "https://api.github.com")
public interface  GitHubFeignClient {

    @GetMapping("/repos/{owner}/{repo}")
    GitHubRepositoryResponse getRepository(@PathVariable("owner") String owner, @PathVariable("repo") String repo);
}
