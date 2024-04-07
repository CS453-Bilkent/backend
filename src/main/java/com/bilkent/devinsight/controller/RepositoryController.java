package com.bilkent.devinsight.controller;

import com.bilkent.devinsight.dto.ProjectHealthMetrics;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@AllArgsConstructor
@RestController
@RequestMapping("/repository")
public class RepositoryController {

    @GetMapping
    public ResponseEntity<ProjectHealthMetrics> getMetrics() {
//        return ResponseEntity.ok(service.getProjectHealthMetrics());
        return null;
    }

}
