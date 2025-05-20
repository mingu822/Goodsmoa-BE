package com.goodsmoa.goodsmoa_BE.category.Repository;

import com.goodsmoa.goodsmoa_BE.category.Entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    Optional<Category> findByName(String name);
}
