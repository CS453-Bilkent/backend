package com.bilkent.devinsight.controller;

import com.bilkent.devinsight.dto.DeveloperEffectiveness;
import com.bilkent.devinsight.service.DeveloperService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/developer")
public class DeveloperController {

    private final DeveloperService developerService;


    @GetMapping("/effectiveness")
    public ResponseEntity<List<DeveloperEffectiveness>> getDeveloperEffectiveness() {
//        return ResponseEntity.ok(developerService.calculateDeveloperEffectiveness());
        return null;
    }

}
