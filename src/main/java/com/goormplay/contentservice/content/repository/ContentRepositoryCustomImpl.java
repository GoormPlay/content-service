package com.goormplay.contentservice.content.repository;

import com.goormplay.contentservice.content.dto.VideoDTO;
import com.goormplay.contentservice.content.dto.VideoPreviewDTO;
import com.goormplay.contentservice.content.entity.Content;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AddFieldsOperation;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ContentRepositoryCustomImpl implements ContentRepositoryCustom {
    private final MongoTemplate mongoTemplate;

    @Override
    public Page<VideoPreviewDTO> findLatestContents(Pageable pageable) {
        Criteria criteria = Criteria.where("releaseDate").exists(true);

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.sort(pageable.getSort()),
                Aggregation.skip((long) pageable.getPageNumber() * pageable.getPageSize()),
                Aggregation.limit(pageable.getPageSize()),
                getPreviewProjection()
//                ,addLatestAttribute()
        );

        List<VideoPreviewDTO> results = mongoTemplate.aggregate(aggregation, "contents", VideoPreviewDTO.class)
                .getMappedResults();
        log.info("findLatestContents = {}", results);
        long total = mongoTemplate.count(Query.query(criteria), Content.class);

        return new PageImpl<>(results, pageable, total);
    }

    @Override
    public Page<VideoPreviewDTO> findRecommendedContents(List<String> recommendedIds, Pageable pageable) {
        if (recommendedIds == null || recommendedIds.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        Criteria criteria = Criteria.where("vidoId").in(recommendedIds);

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.sort(pageable.getSort()),
                Aggregation.skip((long) pageable.getPageNumber() * pageable.getPageSize()),
                Aggregation.limit(pageable.getPageSize()),
                getPreviewProjection()
//                ,addRecommendedAttribute()
        );

        List<VideoPreviewDTO> results = mongoTemplate.aggregate(aggregation, "contents", VideoPreviewDTO.class)
                .getMappedResults();

        long total = mongoTemplate.count(Query.query(criteria), Content.class);

        return new PageImpl<>(results, pageable, total);
    }

    @Override
    public List<VideoPreviewDTO> findLatestContentCards(int limit) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.sort(Sort.by(Sort.Direction.DESC, "releaseDate")),
                Aggregation.limit(limit),
                getPreviewProjection()
//                ,addLatestAttribute()
        );

        return mongoTemplate.aggregate(aggregation, "contents", VideoPreviewDTO.class)
                .getMappedResults();
    }

    @Override
    public List<VideoPreviewDTO> findContentCardsByVideoIds(List<String> videoIds) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("videoId").in(videoIds)),
                getPreviewProjection()
//                ,addRecommendedAttribute()
        );

        return mongoTemplate.aggregate(aggregation, "contents", VideoPreviewDTO.class)
                .getMappedResults();
    }

    @Override
    public Optional<VideoDTO> findContentDetailByVideoId(String videoId) {
        try {
            Criteria criteria = Criteria.where("videoId").is(videoId);

            Aggregation aggregation = Aggregation.newAggregation(
                    Aggregation.match(criteria),
                    getBaseProjection()
            );

            return Optional.ofNullable(mongoTemplate.aggregate(aggregation, "contents", VideoDTO.class)
                    .getUniqueMappedResult());
        } catch (IllegalArgumentException e) {
            log.error("Invalid videoId format: {}", videoId);
            return Optional.empty();
        }
    }

    @Override
    public List<VideoPreviewDTO> findAllWithBaseFields() {
        Aggregation aggregation = Aggregation.newAggregation(
                getPreviewProjection(),
                addDefaultAttributes()
        );

        return mongoTemplate.aggregate(aggregation, "contents", VideoPreviewDTO.class)
                .getMappedResults();
    }

    @Override
    public List<VideoPreviewDTO> findAllAsTrending() {
        Aggregation aggregation = Aggregation.newAggregation(
                getPreviewProjection()
                ,addTrendingAttribute()
        );

        return mongoTemplate.aggregate(aggregation, "contents", VideoPreviewDTO.class)
                .getMappedResults();
    }

    @Override
    public List<VideoPreviewDTO> findAllAsLatest() {
        Aggregation aggregation = Aggregation.newAggregation(
                getPreviewProjection(),
                addLatestAttribute()
        );

        return mongoTemplate.aggregate(aggregation, "contents", VideoPreviewDTO.class)
                .getMappedResults();
    }

    @Override
    public List<VideoPreviewDTO> findByIdsAsRecommended(List<String> videoIds) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("videoId").in(videoIds)),
                getPreviewProjection(),
                addRecommendedAttribute()
        );

        return mongoTemplate.aggregate(aggregation, "contents", VideoPreviewDTO.class)
                .getMappedResults();
    }

    @Override
    public List<VideoPreviewDTO> findAllLatestContentCards() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.sort(Sort.by(Sort.Direction.DESC, "releaseDate")),
                getPreviewProjection(),
                addLatestAttribute()
        );

        return mongoTemplate.aggregate(aggregation, "contents", VideoPreviewDTO.class)
                .getMappedResults();
    }

    private ProjectionOperation getPreviewProjection() {
        return Aggregation.project()
                .and("videoId").as("videoId")
                .and("title").as("title")
                .and("kind").as("kind")
                .and("genre").as("genre");
    }

    private ProjectionOperation getBaseProjection() {
        return Aggregation.project()
                .and("_id").as("id")
                .and("title").as("title")
                .and("kind").as("kind")
                .and("genre").as("genre")
                .and("videoId").as("videoId")
                .and("thumbnail").as("thumbnail")
                .and("synopsis").as("synopsis")
                .and("year").as("year")
                .and("KMRB").as("KMRB")
                .and("cast").as("cast")
                .and("provider").as("provider")
                .and("director").as("director")
                .and("releaseDate").as("releaseDate");
    }

    private AddFieldsOperation addDefaultAttributes() {
        return Aggregation.addFields()
                .addField("trending").withValue(false)
                .addField("latest").withValue(false)
                .addField("recommended").withValue(false)
                .build();
    }

    private AddFieldsOperation addTrendingAttribute() {
        return Aggregation.addFields()
                .addField("trending").withValue(true)
                .addField("latest").withValue(false)
                .addField("recommended").withValue(false)
                .build();
    }

    private AddFieldsOperation addLatestAttribute() {
        return Aggregation.addFields()
                .addField("trending").withValue(false)
                .addField("latest").withValue(true)
                .addField("recommended").withValue(false)
                .build();
    }

    private AddFieldsOperation addRecommendedAttribute() {
        return Aggregation.addFields()
                .addField("trending").withValue(false)
                .addField("latest").withValue(false)
                .addField("recommended").withValue(true)
                .build();
    }
}

