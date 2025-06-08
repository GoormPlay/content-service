package com.goormplay.contentservice.content.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoPreviewDTO {
    private String videoId;
    private String title;
    private String kind;
    private String[] genre;
}
