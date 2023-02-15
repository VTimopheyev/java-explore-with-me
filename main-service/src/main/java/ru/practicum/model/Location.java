package ru.practicum.model;

import lombok.*;

import javax.persistence.*;

@Data
@Entity
@Table(name = "locations")
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @org.springframework.data.annotation.Id
    private Long id;
    @Column(name = "lon")
    private Float lon;
    @Column(name = "lat")
    private Float lat;
}
