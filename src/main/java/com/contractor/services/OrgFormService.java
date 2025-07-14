package com.contractor.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.contractor.dto.SaveOrgFormDto;
import com.contractor.mapper.OrgFormSaveDtoMapper;
import com.contractor.model.OrgForm;
import com.contractor.repository.OrgFormRepository;
import lombok.AllArgsConstructor;

/**
 * this is service to manage org_form db
 */
@Service
@AllArgsConstructor
public class OrgFormService {

    private final OrgFormRepository orgFormRepository;

    private final JdbcTemplate jdbcType;

    private final OrgFormSaveDtoMapper saveOrgFormDtoMapper;
    /**
     * get all org form
     * @return list of org form object
     */
    public List<SaveOrgFormDto> giveAllOrgForm() {
        return orgFormRepository.findAll().stream()
            .map(orgForm -> {
                SaveOrgFormDto saveOrgFormDto = new SaveOrgFormDto();
                saveOrgFormDto.setId(orgForm.getId());
                saveOrgFormDto.setName(orgForm.getName());
                saveOrgFormDto.setActive(orgForm.isActive());
                return saveOrgFormDto;
                })
            .collect(Collectors.toList());
    }

    /**
     * find org form by id
     * @param id org form
     * @return org form object
     */
    public OrgForm giveOrgFormById(Integer id) {
        OrgForm orgForm = orgFormRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("org form not found"));

        if (!orgForm.isActive()) {
            throw new IllegalArgumentException("org form is not active");
        }

        return orgForm;
    }

    /**
     * verify is_active org form become false
     * and verify is_actieve related contractors become false
     * @param id of orf form
     */
    public void deleteOrgForm(Integer id) {
        jdbcType.update("UPDATE org_form SET is_active = false WHERE id = ?", id);
        jdbcType.update("UPDATE  contractor SET is_active = false WHERE org_form = ?", id);
    }

    /**
     * save new org form
     * @param orgForm object
     * @return save org form
     */
    public OrgForm saveOrgForm(SaveOrgFormDto saveOrgFormDto) {
        OrgForm orgForm = saveOrgFormDtoMapper.saveOrgForm(saveOrgFormDto);
        return orgFormRepository.save(orgForm);
    }

}
