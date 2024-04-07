package com.bilkent.devinsight.service;


import com.bilkent.devinsight.dto.DeveloperEffectiveness;
import com.bilkent.devinsight.entity.Contributor;
import com.bilkent.devinsight.repository.CommitRepository;
import com.bilkent.devinsight.repository.ContributorRepository;
import com.bilkent.devinsight.repository.IssueRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DeveloperService {
    private final ContributorRepository contributorRepository;
    private final CommitRepository commitRepository;
    private final IssueRepository issueRepository;

    public List<DeveloperEffectiveness> calculateDeveloperEffectiveness() {
        List<Contributor> contributors = contributorRepository.findAll();
        List<DeveloperEffectiveness> effectivenessList = new ArrayList<>();

        for (Contributor contributor : contributors) {
            long commitCount = commitRepository.countByContributor(contributor);
            long closedIssuesCount = issueRepository.countByClosedByAndClosedTrue(contributor);

            // Code stickiness and bug fixes would need custom logic to calculate
            long codeStickiness = 0; // Placeholder for actual calculation
            long bugsFixed = 0; // Placeholder for actual calculation

            DeveloperEffectiveness developerEffectiveness = DeveloperEffectiveness.builder()
                            .contributor(contributor)
                            .commitCount(commitCount)
                            .closedIssuesCount(closedIssuesCount)
                            .codeStickiness(codeStickiness)
                            .bugsFixed(bugsFixed)
                            .build();

            effectivenessList.add(developerEffectiveness);
        }

        return effectivenessList;
    }
}
