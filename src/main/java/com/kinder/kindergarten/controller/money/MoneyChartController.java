package com.kinder.kindergarten.controller.money;

import com.sun.management.OperatingSystemMXBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;

@RestController
public class MoneyChartController {

    @GetMapping("/system/memoryData")
    public Map<String, Double> getMemoryData() {

        OperatingSystemMXBean mxBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);

        Map<String, Double> data = new HashMap<>();
        data.put("freeMemory", (double) mxBean.getFreeMemorySize() /1024/1024/1024);
        data.put("totalMemory", Math.ceil((double) mxBean.getTotalMemorySize() /1024/1024/1024));

        return data;
    }


}
