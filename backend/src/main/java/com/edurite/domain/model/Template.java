package com.edurite.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name="templates")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Template {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    private String name;
    @Column(columnDefinition="TEXT") private String content;
    private String channel;
}
