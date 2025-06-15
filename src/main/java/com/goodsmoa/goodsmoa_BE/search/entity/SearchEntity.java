package com.goodsmoa.goodsmoa_BE.search.entity;

import com.goodsmoa.goodsmoa_BE.category.Entity.Category;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;

import java.time.LocalDateTime;

public interface SearchEntity {
    Long getId();
    String getTitle();
    String getDescription();
    String getImageUrl();
    String getHashtag();
    Long getViews();
    Category getCategory();
    LocalDateTime getStartTime();
    LocalDateTime getEndTime();
    UserEntity getUser();

    void setDescription(String s);
}
