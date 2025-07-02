package com.contractor.controllers;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.contractor.DTO.GetContactorByIdDto;
import com.contractor.DTO.GetPaginationDto;
import com.contractor.DTO.SaveContractorDto;
import com.contractor.DTO.SearchContravtorRequestDto;
import com.contractor.model.Contractor;
import com.contractor.services.ContractorServices;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * that is controller for contractors
 * this class defines endpoint and connect with ContractorsRepository
 */
@RestController()
@RequestMapping("/contactor")
@AllArgsConstructor
public class ContractorContollers {

    private final ContractorServices contractorServices;

    /**
     * connect to saveContractor method from srvice to
     * save new contructor
     * @param saveContractorDto dto for save new contractor
     * @return http status
     */
    @PutMapping("/save")
    public ResponseEntity<String> saveContructor(@RequestBody SaveContractorDto saveContractorDto) {
        Contractor contractor = new Contractor();
        contractor.setId(saveContractorDto.getId());
        contractor.setParentId(saveContractorDto.getParentId());
        contractor.setNameFull(saveContractorDto.getNameFull());
        contractor.setInn(saveContractorDto.getInn());
        contractor.setOgrn(saveContractorDto.getOgrn());
        contractor.setCountry(saveContractorDto.getCountry());
        contractor.setIndustry(saveContractorDto.getIndustry());
        contractor.setOrgForm(saveContractorDto.getOrgForm());
        contractor.setName(saveContractorDto.getName());

        contractorServices.saveContractor(contractor);

        return  ResponseEntity
                .status(HttpStatus.CREATED)
                .body(contractor.getId());
    }

    /**
     * connect to saveContractor method getContractorById
     * to get contuctor form db by id
     * @param id of contructor
     * @return http satatus
     */
    @GetMapping("/{id}")
    public ResponseEntity<GetContactorByIdDto> getContructorById(@PathVariable String id) {
        GetContactorByIdDto getContractor = contractorServices.getContractorById(id);
        return ResponseEntity.ok(getContractor);
    }

    /**
     * connect to deleteContractorById method to
     * do logical deletion contructor by id
     * @param id of contactor
     * @return http status
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteContractor(@PathVariable String id) {
        contractorServices.deleteContractorById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * connect to getAllContractorPagination method to
     * get all contractor from db
     * @param page number
     * @param size number
     * @return http status
     */
    @GetMapping("/all/{page}/{size}")
    public ResponseEntity<List<GetPaginationDto>> getAllContractor(
        @PathVariable Integer page, @PathVariable Integer size) {
        return ResponseEntity.ok(contractorServices.getAllContractorPagination(1, 10));
    }

    /**
     * post method to connect searchContractor method from
     * contractorService to get all contracotr by filter
     * @param searchContravtorRequestDto dto for search
     * @return https status
     */
    @PostMapping("/search")
    public ResponseEntity<List<GetPaginationDto>> postMethodName(
        @RequestBody SearchContravtorRequestDto searchContravtorRequestDto) {
        return ResponseEntity.ok(contractorServices.searchContractors(searchContravtorRequestDto, 10, 1));
    }

}
