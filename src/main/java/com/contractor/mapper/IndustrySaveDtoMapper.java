package com.contractor.mapper;

import org.springframework.stereotype.Component;

import com.contractor.dto.SaveIndustryDto;
import com.contractor.model.Industry;

@Component
public class IndustrySaveDtoMapper {

    public Industry saveIndustry(SaveIndustryDto saveIndustryDto) {
        return Industry.builder()
            .id(saveIndustryDto.getId())
            .name(saveIndustryDto.getName())
            .isActive(saveIndustryDto.isActive())
            .build();
    }

}
