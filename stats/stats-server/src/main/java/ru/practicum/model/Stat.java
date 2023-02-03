package ru.practicum.model;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Data
@Getter
@Entity
@Table(name = "statistics")
@NoArgsConstructor
public class Stat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String app;
    private String ip;
    private String uri;
    private Timestamp stamp;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Stat)) return false;
        Stat stat = (Stat) o;
        return Objects.equals(ip, stat.ip) && Objects.equals(uri, stat.uri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, app, ip, uri, stamp);
    }
}


