package com.goormplay.contentservice.content.repository;

import com.goormplay.contentservice.content.entity.Content;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ContentRepository extends MongoRepository<Content, String>, ContentRepositoryCustom {

    // 단순 ID 조회
    Optional<Content> findById(String id);
}
