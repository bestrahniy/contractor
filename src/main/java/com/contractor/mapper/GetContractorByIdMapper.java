package com.contractor.mapper;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import com.contractor.dto.GetContactorByIdDto;
import com.contractor.exeption.ContractorIsNotActive;
import com.contractor.exeption.CountryNotFoundException;
import com.contractor.exeption.IndustryNotFoundExeption;
import com.contractor.exeption.MissingUserRight;
import com.contractor.exeption.OrgFormNotFoundException;
import com.contractor.model.Contractor;
import com.contractor.model.Country;
import com.contractor.model.Industry;
import com.contractor.model.OrgForm;
import com.contractor.repository.CountryRepository;
import com.contractor.repository.IndustryRepositiry;
import com.contractor.repository.OrgFormRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;

@Component
@AllArgsConstructor
public class GetContractorByIdMapper {

    private final IndustryRepositiry industryRepositiry;

    private final CountryRepository countryRepository;

    private final OrgFormRepository orgFormRepository;

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

    public GetContactorByIdDto getMapping(Contractor contractor) {
        Industry industry = null;
        Country country = null;
        OrgForm orgForm = null;

        if (!contractor.isActive()) {
            throw new ContractorIsNotActive(contractor.getId());
        }

        if (contractor.getIndustry() != null) {
            industry = industryRepositiry.findById(contractor.getIndustry())
                .orElseThrow(() -> new IndustryNotFoundExeption(contractor.getIndustry()));
        }

        if (contractor.getCountry() != null) {
            country = countryRepository.findById(contractor.getCountry())
                .orElseThrow(() -> new CountryNotFoundException(contractor.getCountry()));
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        boolean isRus = auth.getAuthorities().stream()
            .anyMatch(role -> role.getAuthority().equals("ROLE_CONTRACTOR_RUS"));

        if (isRus) {
            if (!country.getId().equals("RUS")) {
                throw new MissingUserRight(auth.getPrincipal().toString());
            }
        }

        if (contractor.getOrgForm() != null) {
            orgForm = orgFormRepository.findById(contractor.getOrgForm())
                .orElseThrow(() -> new OrgFormNotFoundException(contractor.getOrgForm()));
        }
        return getContractorByIdMapping(contractor, industry, country, orgForm);
    }

}
