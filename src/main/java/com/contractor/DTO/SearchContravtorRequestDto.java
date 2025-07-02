package com.contractor.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * dto for sending castoming variable to filter costumer
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchContravtorRequestDto {

    private String id;

    private String parentId;

    private String name;

    private String nameFull;

    private String inn;

    private String ogrn;

    private String country;

    private String industry;

    private String orgForm;

}
