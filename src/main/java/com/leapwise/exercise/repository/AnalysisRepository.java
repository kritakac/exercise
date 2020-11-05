package com.leapwise.exercise.repository;

import com.leapwise.exercise.domain.Analysis;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AnalysisRepository extends CrudRepository<Analysis,Long> {

    List<Analysis> findAllByUuid(String uuid);
}
