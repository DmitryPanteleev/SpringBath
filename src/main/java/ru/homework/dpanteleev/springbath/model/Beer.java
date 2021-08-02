package ru.homework.dpanteleev.springbath.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Beer {

    @Id
    private Long id;
    private String name;
    private Double volume;

}
