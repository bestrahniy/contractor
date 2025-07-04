package com.contractor.services;

import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
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

    private final JdbcTemplate jdbcTemplate;

    /**
     * find all industry
     * @return list of industry object
     */
    public List<Industry> getAllIndictry() {
        return industryRepositiry.findAll();
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
    public Industry saveIndustry(Industry industry) {
        return industryRepositiry.save(industry);
    }

    /**
     * verify is_active industry become false
     * and verify is_actieve related contractors become false
     * @param id of industry
     */
    public void deleteIndustry(Integer id) {

        jdbcTemplate.update("UPDATE industry SET is_active = false WHERE id = ?", id);

        jdbcTemplate.update("UPDATE contractor SET is_active = false WHERE industry = ?", id);

    }

}
