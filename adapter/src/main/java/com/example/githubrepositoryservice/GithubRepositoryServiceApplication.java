package com.example.githubrepositoryservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class GithubRepositoryServiceApplication {

	public static void main(String[] args)    {
		SpringApplication.run(GithubRepositoryServiceApplication.class, args);
	}

}
