package com.fogstream.testtask.repository;

import com.fogstream.testtask.model.NewsCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

@Repository
@RepositoryRestResource
public interface NewsCategoryRepository extends JpaRepository<NewsCategory, Long>
{
}
