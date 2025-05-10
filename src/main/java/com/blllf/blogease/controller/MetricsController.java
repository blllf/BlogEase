package com.blllf.blogease.controller;

import com.blllf.blogease.pojo.Result;
import com.blllf.blogease.service.SystemMetricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/system")
public class MetricsController {

    @Autowired
    private SystemMetricsService metricsService;

    @GetMapping("/metrics")
    public Result<Map<String, Object>> getMetrics(){
        return Result.success(Map.of(
                "cpu", metricsService.getCpuUsage(),
                "systemMemory", metricsService.getSystemMemoryUsage(),
                "disk", metricsService.getDiskInfo(),
                "threadCount", metricsService.getThreadCount(),
                "jvmMemory", Map.of(
                        "max", Runtime.getRuntime().maxMemory(),
                        "total", Runtime.getRuntime().totalMemory(),
                        "free", Runtime.getRuntime().freeMemory()
                )
        ));
    }
}
