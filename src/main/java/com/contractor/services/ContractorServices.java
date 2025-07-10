package com.contractor.services;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.contractor.DTO.GetContactorByIdDto;
import com.contractor.DTO.GetPaginationDto;
import com.contractor.DTO.SaveContractorDto;
import com.contractor.DTO.SearchContractorRequestDto;
import com.contractor.mapper.GetContractorByIdMapper;
import com.contractor.mapper.SaveContractorDtoMapper;
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

    private final SaveContractorDtoMapper saveContractorDtoMapper;

    private final JdbcAggregateTemplate jdbcAggregateTemplate;

    private final GetContractorByIdMapper getContractorByIdMapper;
    /**
     * checkeng all related entity if
     * at least one entity is missing object will not save
     * @param contractor object
     * @return save contractor
     */
    @Transactional
    public Contractor saveContractor(SaveContractorDto saveContractorDto) {
        Contractor contractor = saveContractorDtoMapper.saveNewContractor(saveContractorDto);
        boolean exist = contractorRepository.existsById(contractor.getId());
        if (exist) {
            return contractorRepository.save(contractor);
        } else {
            return jdbcAggregateTemplate.insert(contractor);
        }

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

        Country country = null;
        Industry industry = null;
        OrgForm orgForm = null;
        if (contractor.getCountry() != null) {
            country = countryRepository.findById(contractor.getCountry())
                .orElseThrow(() -> new IllegalArgumentException("Country not found"));
        }

        if (contractor.getIndustry() != null) {
            industry = industryRepositiry.findById(contractor.getIndustry())
                .orElseThrow(() -> new IllegalArgumentException("Industry not found"));
        }

        if (contractor.getOrgForm() != null) {
            orgForm = orgFormRepository.findById(contractor.getOrgForm())
                .orElseThrow(() -> new IllegalArgumentException("org form not found"));
        }

        if (!contractor.isActive()) {
            throw new IllegalArgumentException("contractor is not active");
        }

        return getContractorByIdMapper.getContractorByIdMapping(contractor, industry, country, orgForm);
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
            + " FROM contractor id LIMIT ? OFFSET ?";

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
    public List<GetPaginationDto> searchContractors(
            SearchContractorRequestDto searchContractorRequestDto,
            Integer size,
            Integer page) {

        StringBuilder sql = new StringBuilder("SELECT * FROM contractor " +
                                        "FULL JOIN country ON contractor.country = country.id " +
                                        "FULL JOIN industry ON contractor.industry = industry.id " +
                                        "FULL JOIN org_form ON contractor.org_form = org_form.id " +
                                        "WHERE country.is_active = true");
        List<Object> param = new ArrayList<>();

        if (searchContractorRequestDto.getId() != null) {
            sql.append(" AND contractor.id = ?");
            param.add(searchContractorRequestDto.getId());
        }
        if (searchContractorRequestDto.getParentId() != null) {
            sql.append(" AND contractor.parent_id = ?");
            param.add(searchContractorRequestDto.getParentId());
        }
        if (searchContractorRequestDto.getName() != null) {
            sql.append(" AND LOWER(contractor.name) LIKE ?");
            String term = "%" + searchContractorRequestDto.getName().toLowerCase() + "%";
            param.add(term);
        }
        if (searchContractorRequestDto.getNameFull() != null) {
            sql.append(" AND LOWER(contractor.name_full) LIKE ?");
            String term = "%" + searchContractorRequestDto.getNameFull().toLowerCase() + "%";
            param.add(term);
        }
        if (searchContractorRequestDto.getInn() != null) {
            sql.append(" AND contractor.inn LIKE ?");
            String term = "%" + searchContractorRequestDto.getInn().toLowerCase() + "%";
            param.add(term);
        }
        if (searchContractorRequestDto.getOgrn() != null) {
            sql.append(" AND contractor.ogrn LIKE ?");
            String term = "%" + searchContractorRequestDto.getOgrn().toLowerCase() + "%";
            param.add(term);
        }
        if (searchContractorRequestDto.getCountry() != null) {
            sql.append(" AND LOWER(country.name) LIKE ?");
            param.add("%" + searchContractorRequestDto.getCountry().toLowerCase() + "%");
        }

        if (searchContractorRequestDto.getIndustry() != null) {
            sql.append(" AND industry.name = ?");
            param.add(searchContractorRequestDto.getIndustry());
        }

        if (searchContractorRequestDto.getOrgForm() != null) {
            sql.append(" AND LOWER(org_form.name) LIKE ?");
            param.add("%" + searchContractorRequestDto.getOrgForm().toLowerCase() + "%");
        }

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
