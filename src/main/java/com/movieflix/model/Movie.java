package com.movieflix.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Set;
@Document(collection = "movies")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Movie {
    @Id
    private String movieId;
    private String title;
    private String director;
    private String studio;
    private Set<String> movieCast;
    private Integer releaseYear;
    private String poster;
}
