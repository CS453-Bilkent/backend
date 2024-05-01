package com.bilkent.devinsight.controller;

import com.bilkent.devinsight.response.RProjectHealthMetrics;
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
    public ResponseEntity<RProjectHealthMetrics> getMetrics() {
//        return ResponseEntity.ok(service.getProjectHealthMetrics());
        return null;
    }

}
