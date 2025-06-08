package com.goormplay.contentservice.content.repository;

import com.goormplay.contentservice.content.dto.VideoDTO;
import com.goormplay.contentservice.content.dto.VideoPreviewDTO;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface ContentRepositoryCustom {
    // DTO 직접 반환 메서드

    List<VideoPreviewDTO> findLatestContentCards(int limit);
    List<VideoPreviewDTO> findContentCardsByVideoIds(List<String> videoIds);
    Optional<VideoDTO> findContentDetailByVideoId(String videoId);
    List<VideoPreviewDTO> findAllLatestContentCards();
    Page<VideoPreviewDTO> findLatestContents(Pageable pageable);
    List<VideoPreviewDTO> findAllWithBaseFields();
    List<VideoPreviewDTO> findAllAsTrending();
    List<VideoPreviewDTO> findAllAsLatest();
    Page<VideoPreviewDTO> findRecommendedContents(List<String> recommendedIds, Pageable pageable);
    List<VideoPreviewDTO> findByIdsAsRecommended(List<ObjectId> videoIds);
}
