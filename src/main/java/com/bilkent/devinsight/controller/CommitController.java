package com.bilkent.devinsight.controller;

import com.bilkent.devinsight.dto.FileCommitRank;
import com.bilkent.devinsight.service.CommitService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/commit")
public class CommitController {

    private final CommitService commitService;

    @GetMapping(path="/file-rankings")
    public ResponseEntity<List<FileCommitRank>> getFileCommitRankings() {
        return null;
    }
}
