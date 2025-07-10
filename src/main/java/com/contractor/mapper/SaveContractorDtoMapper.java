package com.contractor.mapper;

import org.springframework.stereotype.Component;
import com.contractor.DTO.SaveContractorDto;
import com.contractor.model.Contractor;

@Component
public class SaveContractorDtoMapper {

    public Contractor saveNewContractor(SaveContractorDto saveContractorDto) {
        Contractor contractor = new Contractor();

        contractor.setId(saveContractorDto.getId());
        contractor.setParentId(saveContractorDto.getParentId());
        contractor.setNameFull(saveContractorDto.getNameFull());
        contractor.setInn(saveContractorDto.getInn());
        contractor.setOgrn(saveContractorDto.getOgrn());
        contractor.setCountry(saveContractorDto.getCountry());
        contractor.setIndustry(saveContractorDto.getIndustry());
        contractor.setOrgForm(saveContractorDto.getOrgForm());
        contractor.setName(saveContractorDto.getName());

        return contractor;
    }

}
