package com.contractor.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * dto for show contractor on display
 * and implementation pagination
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetPaginationDto {
    
    private String id;

    private String parentId;

    private String name;

    private String nameFull;

    private String inn;

    private String ogrn;

    private String country;

    private Integer industry;

    private Integer orgForm;

}
