package com.goormplay.contentservice.content.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoIdsRequest {
    private List<String> videoIds;
}
