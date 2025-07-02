package com.contractor.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * dto for save industry
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaveIndustryDto {

    private Integer id;

    private String name;

    private boolean isActive;

}
