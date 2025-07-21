package com.contractor.services;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.contractor.dto.GetContactorByIdDto;
import com.contractor.dto.GetPaginationDto;
import com.contractor.dto.SaveContractorDto;
import com.contractor.dto.SearchContractorRequestDto;
import com.contractor.dto.UserRolesAddDto;
import com.contractor.exeption.MissingUserRight;
import com.contractor.mapper.ContractorSaveDtoMapper;
import com.contractor.mapper.GetContractorByIdMapper;
import com.contractor.model.Contractor;
import com.contractor.repository.ContractorRepository;
import lombok.RequiredArgsConstructor;

/**
 * service for manage contructor db
 */
@Service
@RequiredArgsConstructor
public class ContractorServices {

    private final ContractorRepository contractorRepository;

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private final ContractorSaveDtoMapper saveContractorDtoMapper;

    private final JdbcAggregateTemplate jdbcAggregateTemplate;

    private final GetContractorByIdMapper getContractorByIdMapper;

    private List<GetContactorByIdDto> availableContractorByRole = new ArrayList<>();

    private final ConnectAuthService connectAuthService;

    /**
     * checkeng all related entity if
     * at least one entity is missing object will not save
     * @param contractor object
     * @return save contractor
     */
    @Transactional
    public Contractor saveContractor(SaveContractorDto saveContractorDto) {
        Contractor contractor = saveContractorDtoMapper.saveNewContractor(saveContractorDto);
        contractor.setCreateDate(Instant.now());
        contractor.setActive(true);
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

        if (!contractor.isActive()) {
            throw new IllegalArgumentException("contractor is not active");
        }

        return getContractorByIdMapper.getMapping(contractor);
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
    @PostFilter("!hasRole('CONTRACTOR_RUS') or filterObject.country == 'RUS'")
    public List<GetPaginationDto> getAllContractorPagination(int page, int size) {

        int offset = page * size;

        String sql = "SELECT id, parent_id, name, name_full, inn, ogrn, country, industry, org_form "
                + " FROM contractor LIMIT :limit OFFSET :offset";

        Map<String, Object> params = Map.of(
        "limit", size,
        "offset", offset
        );

        return jdbcTemplate.query(sql, params, (result, strNumber) -> {
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
     * ready sql is execute
     * @param searchContravtorRequestDto dto with params for searching
     * @param size max count object on screen
     * @param page count page
     * @return list of entity
     */
    @PostFilter("!hasRole('CONTRACTOR_RUS') or filterObject.country == 'RUS'")
    public List<GetPaginationDto> searchContractors(
            SearchContractorRequestDto searchContractorRequestDto) {

        StringBuilder sql = new StringBuilder("SELECT * FROM contractor " +
                                            "FULL JOIN country ON contractor.country = country.id " +
                                            "FULL JOIN industry ON contractor.industry = industry.id " +
                                            "FULL JOIN org_form ON contractor.org_form = org_form.id " +
                                            "WHERE country.is_active = true");
        Map<String, Object> param = new HashMap<>();

        if (searchContractorRequestDto.getId() != null) {
            sql.append(" AND contractor.id = :id");
            param.put("id", searchContractorRequestDto.getId());
        }
        if (searchContractorRequestDto.getParentId() != null) {
            sql.append(" AND contractor.parent_id = :parentId");
            param.put("parentId", searchContractorRequestDto.getParentId());
        }
        if (searchContractorRequestDto.getName() != null) {
            sql.append(" AND LOWER(contractor.name) LIKE :name");
            String term = "%" + searchContractorRequestDto.getName().toLowerCase() + "%";
            param.put("name", term);
        }
        if (searchContractorRequestDto.getNameFull() != null) {
            sql.append(" AND LOWER(contractor.name_full) LIKE :nameFull");
            String term = "%" + searchContractorRequestDto.getNameFull().toLowerCase() + "%";
            param.put("name_full", term);
        }
        if (searchContractorRequestDto.getInn() != null) {
            sql.append(" AND contractor.inn LIKE :inn");
            String term = "%" + searchContractorRequestDto.getInn().toLowerCase() + "%";
            param.put("inn", term);
        }
        if (searchContractorRequestDto.getOgrn() != null) {
            sql.append(" AND contractor.ogrn LIKE :ogrn");
            String term = "%" + searchContractorRequestDto.getOgrn().toLowerCase() + "%";
            param.put("ogrn", term);
        }
        if (searchContractorRequestDto.getCountry() != null) {
            sql.append(" AND LOWER(country.name) LIKE :country");
            param.put("country", "%" + searchContractorRequestDto.getCountry().toLowerCase() + "%");
        }

        if (searchContractorRequestDto.getIndustry() != null) {
            sql.append(" AND industry.name = :industry");
            param.put("industry", searchContractorRequestDto.getIndustry());
        }

        if (searchContractorRequestDto.getOrgForm() != null) {
            sql.append(" AND LOWER(org_form.name) LIKE :orgForm");
            param.put("orgForm", "%" + searchContractorRequestDto.getOrgForm().toLowerCase() + "%");
        }

        List<GetPaginationDto> list = jdbcTemplate.query(sql.toString(), param, (result, row) ->
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

    /**
     *
     * @param roles
     * @return
     */
    public List<GetContactorByIdDto> getAllDeals(Collection<? extends GrantedAuthority> roles) {
        if (hasRoles(roles, "ROLE_CONTRACTOR_RUS")) {
            availableContractorByRole = contractorRepository.findAllByCountry("RUS").stream()
                .map(getContractorByIdMapper::getMapping)
                .collect(Collectors.toList());
            return availableContractorByRole;
        }
        availableContractorByRole = contractorRepository.findAll().stream()
            .map(getContractorByIdMapper::getMapping)
            .collect(Collectors.toList());
        return availableContractorByRole;
    }

    /**
     * save new roles, connect with auth service and getting new jwt
     * @param userRolesAddDto dto with login and set user roles
     * @param adminJwt admin token
     * @return connect to auth service
     */
    public String saveNewRoleUser(UserRolesAddDto userRolesAddDto, String adminJwt) {
        return connectAuthService.connectAuthService(adminJwt, userRolesAddDto);
    }

    /**
     * geting all roles from token and collect in set
     * @param login user
     * @return set with roles
     */
    public Set<String> checkRolesUser(String login) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (!login.equals(auth.getName())) {
            throw new MissingUserRight(login);
        }
        return auth.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .map(role -> role.startsWith("ROLE_")
                ? role.substring("ROLE_".length())
                : role)
            .collect(Collectors.toSet());
    }

    /**
     * check that unique role contains in token
     * @param roles set roles from token
     * @param role need roles
     * @return true, if roles contains role
     */
    public boolean hasRoles(Collection<? extends GrantedAuthority> roles, String role) {
        return roles.stream()
            .anyMatch(getRole -> getRole.getAuthority().equals(role));
    }

}
