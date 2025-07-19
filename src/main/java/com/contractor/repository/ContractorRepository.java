package com.contractor.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import com.contractor.model.Contractor;

public interface ContractorRepository extends CrudRepository<Contractor, String> {

    Contractor findById(Integer id);

    List<Contractor> findAllByCountry(String id);

    List<Contractor> findAll();

}
