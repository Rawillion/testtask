package com.fogstream.testtask.repository;

import com.fogstream.testtask.model.News;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.util.List;

@Repository
@RepositoryRestResource
public interface NewsRepository extends JpaRepository<News, Long>
{
	@Query("select n from News n where n.newsCategory.id in (?1)")
	Page<News> findAllByNewsCategories(@Param("categories") List<Long> categories, Pageable pageable);
}
