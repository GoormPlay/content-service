package com.goormplay.contentservice.content.controller;

import com.goormplay.contentservice.content.dto.ContentDetailRequest;
import com.goormplay.contentservice.content.dto.VideoIdsRequest;
import com.goormplay.contentservice.content.dto.VideoPreviewDTO;
import com.goormplay.contentservice.content.dto.response.ContentDetailResponse;
import com.goormplay.contentservice.content.service.ContentService;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/contents")
@RequiredArgsConstructor
@Slf4j
public class ContentController {
    private final ContentService contentService;

    @PostMapping("/bulk-ids")
    public List<VideoPreviewDTO> getContentCardsByVideoIds(@RequestBody VideoIdsRequest request) {
        log.info("Received request for contents with ids: {}", request.getVideoIds());
        List<VideoPreviewDTO> results = contentService.getContentCardsByVideoIds(request.getVideoIds());
        log.info("Found {} contents", results.size());
        return results;
    }

    @GetMapping("/latest")
    public ResponseEntity<Map<String, Object>> getLatestContents(@RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("releaseDate").descending());
        return ResponseEntity.ok(contentService.getLatestContentsWithMeta(pageable));
    }

    // getRecommendedVideos

    @GetMapping("/recommended")
    public ResponseEntity<Map<String, Object>> getRecommendedVideos(@RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "10") int size, Authentication authentication) {
        @SuppressWarnings("unchecked")
        Map<String, String> principal = (Map<String, String>) authentication.getPrincipal();
        String memberId = principal.get("memberId");
        Pageable pageable = PageRequest.of(page, size, Sort.by("releaseDate").descending());
        return ResponseEntity.ok(contentService.getRecommendedVideos(memberId,pageable));
    }
    @GetMapping("/detail")
    public ResponseEntity<ContentDetailResponse> getContentDetail(@RequestParam String videoId,
                                                                  @Nullable Authentication authentication) {
        try {
            String userId = Optional.ofNullable(authentication)
                    .map(Authentication::getName)
                    .orElse(null);

            ContentDetailResponse response = contentService.getContentDetailById(videoId, userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting content detail", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }




}
