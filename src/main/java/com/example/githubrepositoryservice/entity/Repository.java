package com.example.githubrepositoryservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "repository")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Repository {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "full_name", nullable = false)
    private String fullName;
    @Column(name = "description")
    private String description;
    @Column(name = "clone_url", nullable = false)
    private String cloneUrl;
    @Column(name = "stars", nullable = false)
    private int stars;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    @Column(name = "fetched_at", nullable = false)
    private Instant fetchedAt;
}