package com.example.studyE.service;

import com.example.studyE.dto.response.SubscriptionPlanResponse;

import java.util.List;

public interface SubscriptionPlanService {
    List<SubscriptionPlanResponse> getAllActivePlans();
}

