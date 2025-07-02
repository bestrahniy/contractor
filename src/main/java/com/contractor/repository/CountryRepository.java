package com.contractor.repository;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import com.contractor.model.Country;

public interface CountryRepository extends CrudRepository<Country, String> {

    List<Country> findAll();

    List<Country> findAllById(String id);

}
