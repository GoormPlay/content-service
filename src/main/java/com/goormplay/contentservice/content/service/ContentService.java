package com.goormplay.contentservice.content.service;
import com.goormplay.contentservice.content.client.ContentInteractionClient;
import com.goormplay.contentservice.content.client.ContentReviewClient;
import com.goormplay.contentservice.content.dto.*;
import com.goormplay.contentservice.content.dto.response.ContentDetailResponse;
import com.goormplay.contentservice.content.repository.ContentRepository;
import jakarta.annotation.Nullable;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContentService {
    private final ContentRepository contentRepository;
    private final ContentInteractionClient contentInteractionClient;
    private final ContentReviewClient contentReviewClient;
    private final PythonClient pythonClient;

    // 상세 페이지 조회
    public ContentDetailResponse getContentDetailById(String videoId, @Nullable String userId) {
        VideoDTO content = contentRepository.findContentDetailByVideoId(videoId)
                .orElseThrow(() -> new NotFoundException("Content not found"));

        // 리뷰 목록 조회
        List<ReviewDTO> reviews = contentReviewClient.getReviews(videoId);

        // 사용자별 좋아요 상태 확인 (비로그인 사용자는 false)
        boolean isLiked = userId != null && checkIsLiked(userId, videoId);
        Double averageRating = 2.0; //임시

        return ContentDetailResponse.builder()
                .content(content)
                .isLiked(isLiked)
                .reviews(reviews)
                .averageRating(averageRating)
                .build();
    }

    private boolean checkIsLiked(String userId, String videoId) {
        return Optional.ofNullable(userId)
                .map(id -> contentInteractionClient.isContentLikedByUser(videoId, id))
                .orElse(false);
    }

    // 컨텐츠 ID 목록으로 카드 조회
    public List<VideoPreviewDTO> getContentCardsByVideoIds(List<String> videoIds) {

        return contentRepository.findContentCardsByVideoIds(videoIds);
    }

    // 사용자별 추천 컨텐츠 조회
    public Map<String, Object> getRecommendedVideos(String userId, Pageable pageable) {
        RecommendationResponse recommendation = pythonClient.fetchRecommendation(userId);
        log.info("Recommendation: {}", recommendation);
        List<String> recommendedIds = recommendation.getVideoIds();
        Page<VideoPreviewDTO> page = contentRepository.findRecommendedContents(recommendedIds, pageable);
        Map<String, Object> response = new HashMap<>();
        response.put("contents", page.getContent());
        response.put("page", page.getNumber());
        response.put("size", page.getSize());
        response.put("totalElements", page.getTotalElements());
        response.put("totalPages", page.getTotalPages());
        response.put("isLast", page.isLast());
        return response;
    }

    // 일반적인 트렌딩/최신 컨텐츠 조회
    public List<VideoPreviewDTO> getTrendingContents() {
        return contentRepository.findAllAsTrending();
    }

    // 최신 컨텐츠 카드 조회 (페이징)
    public Map<String, Object> getLatestContentsWithMeta(Pageable pageable) {
        Page<VideoPreviewDTO> page = contentRepository.findLatestContents(pageable);
        Map<String, Object> response = new HashMap<>();
        log.info("Page: {}", page);
        response.put("contents", page.getContent());
        response.put("page", page.getNumber());
        response.put("size", page.getSize());
        response.put("totalElements", page.getTotalElements());
        response.put("totalPages", page.getTotalPages());
        response.put("isLast", page.isLast());
        log.info("Response: {}", response);
        return response;
    }

    // 최신 컨텐츠 카드 조회 (리스트)
    public List<VideoPreviewDTO> getLatestContentCards(int limit) {
        return contentRepository.findLatestContentCards(limit);
    }
}
