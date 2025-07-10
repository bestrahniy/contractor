package com.contractor.mapper;

import org.springframework.stereotype.Component;
import com.contractor.DTO.GetContactorByIdDto;
import com.contractor.model.Contractor;
import com.contractor.model.Country;
import com.contractor.model.Industry;
import com.contractor.model.OrgForm;

@Component
public class GetContractorByIdMapper {

    public GetContactorByIdDto getContractorByIdMapping(
        Contractor contractor, Industry industry,
        Country country, OrgForm orgForm) {
        return new GetContactorByIdDto(
            contractor.getId(),
            contractor.getParentId(),
            contractor.getName(),
            contractor.getNameFull(),
            contractor.getInn(),
            contractor.getOgrn(),
            new GetContactorByIdDto.GetIndustryDto(
                industry.getId(),
                industry.getName()
            ),
            new GetContactorByIdDto.GetCountryDto(
                country.getId(),
                country.getName()
            ),
            new GetContactorByIdDto.GetOrgFormDto(
                orgForm.getId(),
                orgForm.getName()
            )
        );
    }

}
