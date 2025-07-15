package com.contractor.services;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import com.contractor.dto.SaveIndustryDto;
import com.contractor.mapper.IndustrySaveDtoMapper;
import com.contractor.model.Industry;
import com.contractor.repository.IndustryRepositiry;
import lombok.AllArgsConstructor;

/**
 * this is service to manage db
 */
@Service
@AllArgsConstructor
public class IndustryServices {

    private final IndustryRepositiry industryRepositiry;

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private final IndustrySaveDtoMapper saveIndustryDtoMapper;

    /**
     * find all industry
     * @return list of industry object
     */
    public List<SaveIndustryDto> getAllIndustry() {
        return industryRepositiry.findAll().stream()
            .map(industry -> {
                SaveIndustryDto saveIndustryDto = new SaveIndustryDto();
                saveIndustryDto.setId(industry.getId());
                saveIndustryDto.setName(industry.getName());
                saveIndustryDto.setActive(industry.isActive());
                return saveIndustryDto;
            })
            .collect(Collectors.toList());
    }

    /**
     * find indestry by id
     * @param id of industry
     * @return industry object
     */
    public Industry getIndustryById(Integer id) {
        Industry industry = industryRepositiry.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Industry not found"));

        if (!industry.isActive()) {
            throw new IllegalArgumentException("Industry is not active");
        }

        return industry;
    }

    /**
     * save new industry
     * @param industry object
     * @return save industry
     */
    public Industry saveIndustry(SaveIndustryDto saveIndustryDto) {
        Industry industry = saveIndustryDtoMapper.saveIndustry(saveIndustryDto);
        return industryRepositiry.save(industry);
    }

    /**
     * verify is_active industry become false
     * and verify is_actieve related contractors become false
     * @param id of industry
     */
    public void deleteIndustry(Integer id) {
        String sql1 = "UPDATE industry SET is_active = false WHERE id = :id";
        String sql2 = "UPDATE contractor SET is_active = false WHERE industry = :id";
        Map<String, Object> param = Map.of("id", id);
        jdbcTemplate.update(sql1, param);
        jdbcTemplate.update(sql2, param);
    }

}
