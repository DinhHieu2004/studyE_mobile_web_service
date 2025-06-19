package com.example.studyE.service.impl;

import com.example.studyE.dto.response.SubscriptionPlanResponse;
import com.example.studyE.entity.SubscriptionPlan;
import com.example.studyE.mapper.SubscriptionPlanMapper;
import com.example.studyE.repository.SubscriptionPlanRepository;
import com.example.studyE.service.SubscriptionPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubscriptionPlanServiceImpl implements SubscriptionPlanService {

    @Autowired
    private SubscriptionPlanRepository subscriptionPlanRepository;

    @Override
    public List<SubscriptionPlanResponse> getAllActivePlans() {
        List<SubscriptionPlan> plans = subscriptionPlanRepository.findByIsActiveTrue();
        return SubscriptionPlanMapper.toResponseList(plans);
    }
}
