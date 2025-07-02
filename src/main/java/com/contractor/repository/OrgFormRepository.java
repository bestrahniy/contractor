package com.contractor.repository;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import com.contractor.model.OrgForm;

public interface OrgFormRepository extends CrudRepository<OrgForm, Integer> {

    List<OrgForm> findAll();

}
