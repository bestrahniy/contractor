package com.contractor.mapper;

import org.springframework.stereotype.Component;

import com.contractor.dto.SaveOrgFormDto;
import com.contractor.model.OrgForm;

@Component
public class OrgFormSaveDtoMapper {

    public OrgForm saveOrgForm(SaveOrgFormDto saveOrgFormDto) {
        return OrgForm.builder()
            .id(saveOrgFormDto.getId())
            .name(saveOrgFormDto.getName())
            .isActive(saveOrgFormDto.isActive())
            .build();
    }

}
