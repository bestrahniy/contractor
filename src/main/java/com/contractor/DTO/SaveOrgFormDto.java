package com.contractor.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * dto for save org form
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaveOrgFormDto {
    
    private Integer id;

    private String name;

    private boolean isActive;
}
