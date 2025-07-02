package com.contractor.services;

import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import com.contractor.model.OrgForm;
import com.contractor.repository.OrgFormRepository;
import lombok.AllArgsConstructor;


@Service
@AllArgsConstructor
public class OrgFormService {
    
    private final OrgFormRepository orgFormRepository;

    private final JdbcTemplate jdbcType;

    public List<OrgForm> giveAllOrgForm(){
        return orgFormRepository.findAll();
    }

    public OrgForm giveOrgFormById(Integer id){
        return orgFormRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("org form not found"));
    }

    public void deleteOrgForm(Integer id){
        jdbcType.update("UPDATE org_form SET is_active = false WHERE id = ?", id);
        jdbcType.update("UPDATE  contractor SET is_active = false WHERE org_form = ?", id);
    }


    public OrgForm saveOrgForm(OrgForm orgForm){
        return orgFormRepository.save(orgForm);
    }
}
