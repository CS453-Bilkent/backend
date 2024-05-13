package com.bilkent.devinsight.response;


import com.bilkent.devinsight.entity.Commit;
import com.bilkent.devinsight.entity.Repository;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class RMultipleCommits {

    public Repository repository;

    public Set<Commit> commits;


}
