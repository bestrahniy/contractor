package com.contractor.services;

import java.util.ArrayList;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.contractor.DTO.GetContactorByIdDto;
import com.contractor.DTO.GetPaginationDto;
import com.contractor.DTO.SearchContravtorRequestDto;
import com.contractor.model.Contractor;
import com.contractor.model.Country;
import com.contractor.model.Industry;
import com.contractor.model.OrgForm;
import com.contractor.repository.ContractorRepository;
import com.contractor.repository.CountryRepository;
import com.contractor.repository.IndustryRepositiry;
import com.contractor.repository.OrgFormRepository;
import lombok.RequiredArgsConstructor;

/**
 * service for manage contructor db
 */
@Service
@RequiredArgsConstructor
public class ContractorServices {

    private final ContractorRepository contractorRepository;

    private final CountryRepository countryRepository;

    private final IndustryRepositiry industryRepositiry;

    private final OrgFormRepository orgFormRepository;

    private final JdbcTemplate jdbcTemplate;

    /**
     * checkeng all related entity if
     * at least one entity is missing object will not save
     * @param contractor object
     * @return save contractor
     */
    @Transactional
    public Contractor saveContractor(Contractor contractor) {
        countryRepository.findById(contractor.getCountry())
            .orElseThrow(() -> new IllegalArgumentException("Country not found"));

        industryRepositiry.findById(contractor.getIndustry())
            .orElseThrow(() -> new IllegalArgumentException("Industry not foud"));

        orgFormRepository.findById(contractor.getOrgForm())
            .orElseThrow(() -> new IllegalArgumentException("OrgForm not found"));

        if (contractor.getParentId() != null) {
            contractorRepository.findById(contractor.getParentId())
                .orElseThrow(() -> new IllegalArgumentException("Parent contractor not found"));
        }

        return contractorRepository.save(contractor);
    }

    /**
     * getting contractor by id and checking all
     * related etity
     * @param id of contractor
     * @return dto for showing contractor
     */
    @Transactional
    public GetContactorByIdDto getContractorById(String id) {
        Contractor contractor = contractorRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Contructor not found"));

        Country country = countryRepository.findById(contractor.getCountry())
            .orElseThrow(() -> new IllegalArgumentException("Country not found"));

        Industry industry = industryRepositiry.findById(contractor.getIndustry())
            .orElseThrow(() -> new IllegalArgumentException("Industry not found"));

        OrgForm orgForm = orgFormRepository.findById(contractor.getOrgForm())
            .orElseThrow(() -> new IllegalArgumentException("org form not found"));

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

    /**
     * variable is_active become false
     * @param id of contractor
     */
    @Transactional
    public void deleteContractorById(String id) {
        Contractor contractor = contractorRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("contractor not found"));

        contractor.setActive(false);
        contractorRepository.save(contractor);
    }

    /**
     * using pagination for show object to screen by
     * use LIMIT OFFSET in sql response
     * @param page count
     * @param size count object on screen
     * @return List of entity
     */
    @Transactional
    public List<GetPaginationDto> getAllContractorPagination(int page, int size) {

        int lasElem = page * size;

        String sql = "SELECT id, parent_id, name, name_full, inn, ogrn, country, industry, org_form "
            + " FROM contractor ORDER BY id LIMIT ? OFFSET ?";

        return jdbcTemplate.query(sql, new Object[]{size, lasElem}, (result, strNumber) -> {
            GetPaginationDto contractor = new GetPaginationDto();
            contractor.setId(result.getString("id"));
            contractor.setParentId(result.getString("parent_id"));
            contractor.setName(result.getString("name"));
            contractor.setNameFull(result.getString("name_full"));
            contractor.setInn(result.getString("inn"));
            contractor.setOgrn(result.getString("ogrn"));
            contractor.setCountry(result.getString("country"));
            contractor.setIndustry(result.getInt("industry"));
            contractor.setOrgForm(result.getInt("org_form"));
            return contractor;
        });
    }


    /**
     * this method create a sql query for searching Object
     * by custom filter, it add custom filter into main sql and
     * ready sql is execute, getting data show on screen using pagination
     * @param searchContravtorRequestDto dto with params for searching
     * @param size max count object on screen
     * @param page count page
     * @return list of entity
     */
    @Transactional
    public List<GetPaginationDto> searchContractors(
            SearchContravtorRequestDto searchContravtorRequestDto,
            Integer size,
            Integer page) {

        StringBuilder sql = new StringBuilder("SELECT * FROM contractor " +
                                        "FULL JOIN country ON contractor.country = country.id " +
                                        "FULL JOIN industry ON contractor.industry = industry.id " +
                                        "FULL JOIN org_form ON contractor.org_form = org_form.id " +
                                        "WHERE country.is_active = true");
        List<Object> param = new ArrayList<>();

        if (searchContravtorRequestDto.getId() != null) {
            sql.append(" AND contractor.id = ?");
            param.add(searchContravtorRequestDto.getId());
        }
        if (searchContravtorRequestDto.getParentId() != null) {
            sql.append(" AND contractor.parent_id = ?");
            param.add(searchContravtorRequestDto.getParentId());
        }
        if (searchContravtorRequestDto.getName() != null ||
            searchContravtorRequestDto.getInn() != null ||
            searchContravtorRequestDto.getNameFull() != null ||
            searchContravtorRequestDto.getOgrn() != null) {
            sql.append(" AND (LOWER(contractor.name) LIKE ?" +
                        " OR LOWER(contractor.name_full) LIKE ?" +
                        " OR LOWER(contractor.inn) LIKE ?" +
                        " OR LOWER(contractor.ogrn) LIKE ?");
            String termName = "%" + (searchContravtorRequestDto.getName()).toString().toLowerCase() + "%";
            String termInn = "%" + (searchContravtorRequestDto.getInn()).toString().toLowerCase() + "%";
            String termNameFull = "%" + (searchContravtorRequestDto.getNameFull()).toString().toLowerCase() + "%";
            String termOgrn = "%" + (searchContravtorRequestDto.getOgrn()).toString().toLowerCase() + "%";
            param.add(termName);
            param.add(termNameFull);
            param.add(termInn);
            param.add(termOgrn);
        }
        if (searchContravtorRequestDto.getCountry() != null) {
            sql.append(" AND LOWER(country.name) LIKE ?");
            param.add("%" + searchContravtorRequestDto.getCountry().toLowerCase() + "%");
        }

        if (searchContravtorRequestDto.getIndustry() != null) {
            sql.append(" AND industry.name = ?");
            param.add(searchContravtorRequestDto.getIndustry());
        }

        if (searchContravtorRequestDto.getOrgForm() != null) {
            sql.append(" AND LOWER(org_form.name) LIKE ?");
            param.add("%" + searchContravtorRequestDto.getOrgForm().toLowerCase() + "%");
        }

        sql.append(" ORDER BY contractor.id LIMIT ? OFFSET ?");
        int offset = page * size;
        param.add(size);
        param.add(offset);
        List<GetPaginationDto> list = jdbcTemplate.query(sql.toString(), param.toArray(), (result, row) ->
            new GetPaginationDto(
                result.getString("id"),
                result.getString("parent_id"),
                result.getString("name"),
                result.getString("name_full"),
                result.getString("inn"),
                result.getString("ogrn"),
                result.getString("country"),
                result.getInt("industry"),
                result.getInt("org_form")
            )
        );

        return list;
    }

}
