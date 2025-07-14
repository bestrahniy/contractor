package com.contractor.mapper;

import org.springframework.stereotype.Component;

import com.contractor.dto.SaveContractorDto;
import com.contractor.model.Contractor;

@Component
public class ContractorSaveDtoMapper {

    public Contractor saveNewContractor(SaveContractorDto saveContractorDto) {
        return Contractor.builder()
            .id(saveContractorDto.getId())
            .parentId(saveContractorDto.getParentId())
            .name(saveContractorDto.getName())
            .nameFull(saveContractorDto.getNameFull())
            .ogrn(saveContractorDto.getOgrn())
            .inn(saveContractorDto.getInn())
            .country(saveContractorDto.getCountry())
            .industry(saveContractorDto.getIndustry())
            .orgForm(saveContractorDto.getOrgForm())
            .build();
    }

}
