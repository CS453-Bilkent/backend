package com.bilkent.devinsight.controller;

import com.bilkent.devinsight.entity.Contributor;
import com.bilkent.devinsight.request.QGetRepository;
import com.bilkent.devinsight.response.RDeveloperEffectiveness;
import com.bilkent.devinsight.response.struct.ApiResponse;
import com.bilkent.devinsight.service.DeveloperService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@AllArgsConstructor
@RestController
@RequestMapping("/developer")
public class DeveloperController {

    private final DeveloperService developerService;

    @GetMapping("/effectiveness")
    public ResponseEntity<List<RDeveloperEffectiveness>> getDeveloperEffectiveness() {
//        return ResponseEntity.ok(developerService.calculateDeveloperEffectiveness());
        return null;
    }

}
