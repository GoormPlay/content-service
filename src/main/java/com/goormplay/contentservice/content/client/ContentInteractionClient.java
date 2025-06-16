package com.goormplay.contentservice.content.client;

import com.goormplay.contentservice.Security.FeignHeaderConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "ui-service",
        url = "http://ui-service.default.svc.cluster.local:8086",
        configuration = FeignHeaderConfig.class)
public interface ContentInteractionClient {
    @GetMapping("/ui/content/liked")
    boolean isContentLikedByUser(@RequestParam String videoId, @RequestParam String userId);
}
