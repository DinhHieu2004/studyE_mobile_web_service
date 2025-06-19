package com.example.studyE.mapper;

import com.example.studyE.dto.response.SubscriptionPlanResponse;
import com.example.studyE.entity.SubscriptionPlan;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SubscriptionPlanMapper {
    public static SubscriptionPlanResponse toResponse(SubscriptionPlan plan) {
        SubscriptionPlanResponse response = new SubscriptionPlanResponse();
        response.setId(plan.getId());
        response.setName(plan.getName());
        response.setPrice(plan.getPrice());
        response.setDurationDays(plan.getDurationDays());

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<String> features = objectMapper.readValue(plan.getFeaturesJson(), new TypeReference<List<String>>() {
            });
            response.setFeatures(features);
        } catch (Exception e) {
            response.setFeatures(Collections.emptyList());
        }

        return response;
    }

    public static List<SubscriptionPlanResponse> toResponseList(List<SubscriptionPlan> plans) {
        return plans.stream()
                .map(SubscriptionPlanMapper::toResponse)
                .collect(Collectors.toList());
    }
}
