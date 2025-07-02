package com.contractor.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("industry")
public class Industry {

    @Id
    @Column("id")
    private Integer id;

    @Column("name")
    private String name;

    @Column("is_active")
    private boolean isActive;

}
