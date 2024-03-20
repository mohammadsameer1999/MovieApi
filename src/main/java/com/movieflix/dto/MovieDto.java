package com.movieflix.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.Set;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovieDto {
    @MongoId
    private String movieId;
    private String title;
    private String director;
    private String studio;
    private Set<String> movieCast;
    private Integer releaseYear;
    private String poster;
    private String posterUrl;

    // Constructor with parameters for all fields
    public MovieDto(String title, String director, String studio, Set<String> movieCast, Integer releaseYear, String poster, String posterUrl) {
        this.title = title;
        this.director = director;
        this.studio = studio;
        this.movieCast = movieCast;
        this.releaseYear = releaseYear;
        this.poster = poster;
        this.posterUrl = posterUrl;
    }
}
