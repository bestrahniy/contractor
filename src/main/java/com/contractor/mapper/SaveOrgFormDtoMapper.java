package com.contractor.mapper;

import org.springframework.stereotype.Component;
import com.contractor.DTO.SaveOrgFormDto;
import com.contractor.model.OrgForm;

@Component
public class SaveOrgFormDtoMapper {

    public OrgForm saveOrgForm(SaveOrgFormDto saveOrgFormDto) {
        OrgForm orgForm = new OrgForm();
        orgForm.setId(saveOrgFormDto.getId());
        orgForm.setName(saveOrgFormDto.getName());
        orgForm.setActive(saveOrgFormDto.isActive());
        return orgForm;
    }

}
