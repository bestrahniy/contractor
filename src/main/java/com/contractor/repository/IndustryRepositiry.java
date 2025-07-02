package com.contractor.repository;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import com.contractor.model.Industry;
public interface IndustryRepositiry extends CrudRepository<Industry, Integer>{

    List<Industry> findAll();
    
}