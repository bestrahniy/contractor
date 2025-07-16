package com.contractor.mapper;

import org.springframework.stereotype.Component;

import com.contractor.dto.SaveCountryDto;
import com.contractor.model.Country;

@Component
public class CountrySaveDtoMapper {

    public Country saveNewCountry(SaveCountryDto saveCountryDto) {
        return Country.builder()
            .id(saveCountryDto.getId())
            .name(saveCountryDto.getName())
            .isActive(saveCountryDto.isActive())
            .build();
    }

}

