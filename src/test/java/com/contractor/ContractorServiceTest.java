package com.contractor;


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
import com.contractor.DTO.GetContactorByIdDto;
import com.contractor.DTO.GetPaginationDto;
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
public class ContractorServiceTest {
    
    @Mock
    ContractorRepository contractorRepository;

    @Mock
    CountryRepository countryRepository;

    @Mock
    IndustryRepositiry industryRepositiry;

    @Mock
    OrgFormRepository orgFormRepository;

    @Mock
    JdbcTemplate jdbcTemplate;

    @InjectMocks
    ContractorServices services;

    Contractor tesContractor;

    @Before
    public void init(){
        tesContractor = new Contractor();
        tesContractor.setId("C1");
        tesContractor.setName("Ilya");
        tesContractor.setOgrn("ogrn");
        tesContractor.setCountry("ABH");
        tesContractor.setIndustry(1);
        tesContractor.setOrgForm(1);
        tesContractor.setNameFull("Ilya Bobokiv");
        tesContractor.setCreateUserId("user");
        tesContractor.setCrateDate(Instant.now());
    }

    @Test
    public void createConractorTest(){
        when(countryRepository.findById("ABH"))
            .thenReturn(Optional.of(new Country("ABH","Абхазия",true))
        );

        when(industryRepositiry.findById(1))
            .thenReturn(Optional.of(new Industry(1, "Авиастроение", true))
        );

        when(orgFormRepository.findById(1))
            .thenReturn(Optional.of(new OrgForm(1, "-", true))
        );

        when(contractorRepository.save(tesContractor))
            .thenReturn(tesContractor);

        Contractor resultSave = services.saveContractor(tesContractor);

        assertEquals("ABH", resultSave.getCountry());
    }

    @Test
    public void giveContractorTest(){

        when(countryRepository.findById("ABH"))
            .thenReturn(Optional.of(new Country("ABH","Абхазия",true))
        );

        when(industryRepositiry.findById(1))
            .thenReturn(Optional.of(new Industry(1, "Авиастроение", true))
        );

        when(orgFormRepository.findById(1))
            .thenReturn(Optional.of(new OrgForm(1, "-", true))
        );

        when(contractorRepository.findById("C1"))
            .thenReturn(Optional.of(tesContractor));

        services.saveContractor(tesContractor);

        GetContactorByIdDto contractor = services.getContractorById("C1");

        assertEquals("Ilya", contractor.getName());
        assertEquals("Авиастроение", contractor.getIndustry().getName());
    }

    @Test
    public void deleteContractor(){

        when(contractorRepository.findById("C1"))
            .thenReturn(Optional.of(tesContractor));

        services.deleteContractorById("C1");
        assertFalse("Contractor должен быть false", tesContractor.isActive());

        verify(contractorRepository).save(
            argThat(c ->
                "C1".equals(c.getId())&&
                Boolean.FALSE.equals(c.isActive())
        ));

        verify(contractorRepository).findById("C1");
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
