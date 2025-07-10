package com.contractor.mapper;

import org.springframework.stereotype.Component;
import com.contractor.DTO.SaveIndustryDto;
import com.contractor.model.Industry;

@Component
public class SaveIndustryDtoMapper {

    public Industry saveIndustry(SaveIndustryDto saveIndustryDto) {
        Industry industry = new Industry();
        industry.setId(saveIndustryDto.getId());
        industry.setName(saveIndustryDto.getName());
        industry.setActive(saveIndustryDto.isActive());
        return industry;
    }

}
