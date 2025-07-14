package com.contractor.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("country")
@Builder
public class Country {

    @Id
    @Column("id")
    private String id;

    @Column("name")
    private String name;

    @Column("is_active")
    private boolean isActive;

}
