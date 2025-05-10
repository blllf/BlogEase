package com.blllf.blogease.service;

import java.util.Map;

public interface SystemMetricsService {
    // 获取 CPU 使用率
    double getCpuUsage();
    // 获取系统内存使用
    double getSystemMemoryUsage();
    // 获取磁盘信息
    Map<String, Object> getDiskInfo();
    // 其他指标（线程数、GC次数等）
    int getThreadCount();
}
