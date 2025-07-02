package com.contractor.repository;

import org.springframework.data.repository.CrudRepository;
import com.contractor.model.Contractor;

public interface ContractorRepository extends CrudRepository<Contractor, String> {

    Contractor findById(Integer id);

}
