package com.blllf.blogease.service.impl;

import com.blllf.blogease.service.SystemMetricsService;
import org.springframework.stereotype.Service;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.software.os.OSFileStore;

import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;

@Service
public class SystemMetricsServiceImpl implements SystemMetricsService {
    private final SystemInfo systemInfo = new SystemInfo();
    @Override
    public double getCpuUsage() {
        CentralProcessor processor = systemInfo.getHardware().getProcessor();
        long[] prevTicks = processor.getSystemCpuLoadTicks();
        try {
            Thread.sleep(1000); // 计算 1 秒内的 CPU 使用率
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return processor.getSystemCpuLoadBetweenTicks(prevTicks) * 100;
    }

    @Override
    public double getSystemMemoryUsage() {
        GlobalMemory memory = systemInfo.getHardware().getMemory();
        long totalMemory = memory.getTotal();
        long availableMemory = memory.getAvailable();
        long usedMemory = totalMemory - availableMemory; // 计算已使用的内存量
        // 计算内存使用率，结果是一个0到1之间的double值，表示占用比例
        double memoryUsageRatio = (double) usedMemory / totalMemory;
        return memoryUsageRatio * 100;// 转换为百分比形式
    }

    @Override
    public Map<String, Object> getDiskInfo() {
        OSFileStore fs = systemInfo.getOperatingSystem().getFileSystem().getFileStores().get(0); // 取第一个磁盘
        OSFileStore fs2 = systemInfo.getOperatingSystem().getFileSystem().getFileStores().get(1); // 取第二个磁盘
        Map<String, Object> map = new HashMap<>();
        map.put("totalC", fs.getTotalSpace());
        map.put("freeC", fs.getFreeSpace());
        map.put("usedC", fs.getTotalSpace() - fs.getFreeSpace());
        map.put("totalD", fs2.getTotalSpace());
        map.put("freeD", fs2.getFreeSpace());
        map.put("usedD", fs2.getTotalSpace() - fs2.getFreeSpace());
        return map;
    }

    @Override
    public int getThreadCount() {
        return ManagementFactory.getThreadMXBean().getThreadCount();
    }
}
