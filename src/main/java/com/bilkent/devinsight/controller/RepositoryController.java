package com.bilkent.devinsight.controller;

import com.bilkent.devinsight.entity.Repository;
import com.bilkent.devinsight.request.QAddRepository;
import com.bilkent.devinsight.response.RProjectHealthMetrics;
import com.bilkent.devinsight.response.struct.ApiResponse;
import com.bilkent.devinsight.service.RepositoryService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@AllArgsConstructor
@RestController
@RequestMapping("/repository")
public class RepositoryController {

    private final RepositoryService repositoryService;


    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path="")
    public ResponseEntity<ApiResponse<Repository>> addRepository(@Valid @RequestBody QAddRepository qAddRepository) {
        Repository repository = repositoryService.addRepository(qAddRepository);
        return ResponseEntity.ok(
                ApiResponse.<Repository>builder()
                        .data(repository)
                        .message("Repository added successfully")
                        .status(HttpStatus.OK.value())
                        .build()
        );
    }

    @GetMapping
    public ResponseEntity<RProjectHealthMetrics> getMetrics() {
//        return ResponseEntity.ok(service.getProjectHealthMetrics());
        return null;
    }

}
