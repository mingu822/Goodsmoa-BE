package com.goodsmoa.goodsmoa_BE.category.Repository;

import com.goodsmoa.goodsmoa_BE.category.Entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
}
