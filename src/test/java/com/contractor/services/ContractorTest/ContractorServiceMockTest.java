package com.contractor.services.ContractorTest;


import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.RowMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.jdbc.core.JdbcTemplate;

import com.contractor.dto.GetContactorByIdDto;
import com.contractor.dto.GetPaginationDto;
import com.contractor.dto.SaveContractorDto;
import com.contractor.mapper.ContractorSaveDtoMapper;
import com.contractor.mapper.GetContractorByIdMapper;
import com.contractor.model.Contractor;
import com.contractor.model.Country;
import com.contractor.model.Industry;
import com.contractor.model.OrgForm;
import com.contractor.repository.ContractorRepository;
import com.contractor.repository.CountryRepository;
import com.contractor.repository.IndustryRepositiry;
import com.contractor.repository.OrgFormRepository;
import com.contractor.services.ContractorServices;


@RunWith(MockitoJUnitRunner.class)
public class ContractorServiceMockTest {
    
    @Mock
    ContractorRepository contractorRepository;

    @Mock
    CountryRepository countryRepository;

    @Mock
    IndustryRepositiry industryRepositiry;

    @Mock
    OrgFormRepository orgFormRepository;

    @Mock
    ContractorSaveDtoMapper saveContractorDtoMapper;

    @Mock
    GetContractorByIdMapper getContractorByIdMapper;

    @Mock
    JdbcTemplate jdbcTemplate;

    @InjectMocks
    ContractorServices services;

    SaveContractorDto testContractor;

    Contractor contractor;

    @Before
    public void init(){
        testContractor = new SaveContractorDto();
        testContractor.setId("testId");
        testContractor.setParentId(null);
        testContractor.setName("ilya");
        testContractor.setNameFull("ilya bobkov");
        testContractor.setInn("12345");
        testContractor.setOgrn("123");
        testContractor.setCountry("ABH");
        testContractor.setIndustry(1);
        testContractor.setOrgForm(1);

        contractor = new Contractor();
        contractor.setId("testId");
        contractor.setParentId(null);
        contractor.setName("ilya");
        contractor.setNameFull("ilya bobkov");
        contractor.setInn("12345");
        contractor.setOgrn("123");
        contractor.setCountry("ABH");
        contractor.setIndustry(1);
        contractor.setOrgForm(1);
    }

    @Test
    public void createConractorTest(){
        when(contractorRepository.save(contractor))
            .thenReturn(contractor);

        when(contractorRepository.existsById(testContractor.getId()))
            .thenReturn(true);

        when(saveContractorDtoMapper.saveNewContractor(testContractor))
            .thenReturn(contractor);

        Contractor resultSave = services.saveContractor(testContractor);

        assertEquals("ABH", resultSave.getCountry());
    }

    @Test
    public void giveContractorTest(){

        Country country = new Country("ABH", "Абхазия", true);
        Industry industry = new Industry(1, "Авиастроение", true);
        OrgForm orgForm = new OrgForm(1, "-", true);

        when(contractorRepository.findById("testId"))
            .thenReturn(Optional.of(contractor));

        when(countryRepository.findById("ABH"))
            .thenReturn(Optional.of(country));

        when(industryRepositiry.findById(1))
            .thenReturn(Optional.of(industry));

        when(orgFormRepository.findById(1))
            .thenReturn(Optional.of(orgForm));

        when(getContractorByIdMapper.getContractorByIdMapping(contractor, industry, country, orgForm))
            .thenReturn(new GetContactorByIdDto(
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
            ));
        GetContactorByIdDto result = services.getContractorById("testId");

        assertEquals("ilya", result.getName());
        assertEquals("Авиастроение", result.getIndustry().getName());
    }


    @Test
    public void deleteContractor(){

        when(contractorRepository.findById("testId"))
            .thenReturn(Optional.of(contractor));

        services.deleteContractorById("testId");
        assertFalse("Contractor должен быть false", contractor.isActive());

        verify(contractorRepository).findById("testId");
        verify(contractorRepository).save(
            argThat(contractor -> "testId".equals(contractor.getId()) && !contractor.isActive())
        );
        verifyNoMoreInteractions(contractorRepository);
    }

    @Test
    public void getAllContractorPaginationTest(){
        int page = 1;
        int size = 5;
        List<GetPaginationDto> getPagination = List.of(
            new GetPaginationDto("id", "null", "ilya", "ILya Bobkov", 
            "inn", "ogrn", "ABH", 1, 1)
        );

        when(jdbcTemplate.query(
            anyString(),
            eq(new Object[]{size, page*size}),
            any(RowMapper.class)
        )).thenReturn(getPagination);

        List<GetPaginationDto> result = services.getAllContractorPagination(page, size);

        assertEquals(1, result.size());
    }

}
