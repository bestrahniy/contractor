package com.contractor.services.IndustryTest;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.contractor.dto.SaveIndustryDto;
import com.contractor.mapper.IndustrySaveDtoMapper;

import com.contractor.model.Industry;
import com.contractor.repository.IndustryRepositiry;
import com.contractor.services.IndustryServices;


@RunWith(MockitoJUnitRunner.class)
public class IndustryServiceMockTest {
    
    @Mock
    IndustryRepositiry industryRepositiry;

    @InjectMocks
    IndustryServices industryServices;

    @Mock
    IndustrySaveDtoMapper saveIndustryDtoMapper;

    @Mock
    NamedParameterJdbcTemplate jdbcTemplate;

    SaveIndustryDto saveIndustryDto;
    
    Industry testIndustry;

    @Before
    public void init(){
        testIndustry = new Industry();
        testIndustry.setId(51);
        testIndustry.setName("Инжененрия");
        testIndustry.setActive(true);

        saveIndustryDto = new SaveIndustryDto();
        saveIndustryDto.setId(51);
        saveIndustryDto.setName("Ниженерия");
        saveIndustryDto.setActive(true);
    }

    @Test
    public void getAllIndustryTest(){
        List<Industry> mockList = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            Industry ind = new Industry();
            ind.setId(i);
            ind.setName("Name" + i);
            mockList.add(ind);
        }
        when(industryRepositiry.findAll()).thenReturn(mockList);
        List<Industry> result = industryRepositiry.findAll();
        assertEquals(50, result.size());
    }

    @Test
    public void deleteIndustryTest() {
        Industry testIndustry = new Industry(51, "Test Industry", true);
        industryRepositiry.save(testIndustry);

        when(jdbcTemplate.queryForObject(
            anyString(),
            any(Map.class),
            eq(Boolean.class)
        )).thenReturn(false);

        when(jdbcTemplate.update(
            anyString(),
            any(Map.class)
        )).thenReturn(1);

        industryServices.deleteIndustry(testIndustry.getId());

        Boolean isActive = jdbcTemplate.queryForObject(
            "SELECT is_active FROM industry WHERE id = :id",
            Map.of("id", testIndustry.getId()),
            Boolean.class
        );
        assertFalse(isActive);
    }

    @Test
    public void getIndustryByIdTest(){
        when(industryRepositiry.findById(1))
            .thenReturn(Optional.of(new Industry(1, "Авиастроение", true))
        );
        Industry industry = industryServices.getIndustryById(1);
        assertEquals("Авиастроение", industry.getName());
    }

    @Test
    public void saveIndustryTest(){
        when(industryRepositiry.save(testIndustry))
            .thenReturn(testIndustry);

        when(saveIndustryDtoMapper.saveIndustry(saveIndustryDto))
            .thenReturn(testIndustry);

        Industry industry = industryServices.saveIndustry(saveIndustryDto);
        assertEquals(testIndustry, industry);
    }
}
