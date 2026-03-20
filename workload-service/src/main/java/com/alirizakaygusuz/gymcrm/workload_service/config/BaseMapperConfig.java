package com.alirizakaygusuz.gymcrm.workload_service.config;

import org.mapstruct.MapperConfig;
import org.mapstruct.ReportingPolicy;

@MapperConfig(
        componentModel = "spring" ,
        unmappedTargetPolicy = ReportingPolicy.WARN)
public interface BaseMapperConfig {
}
