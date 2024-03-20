package com.movieflix.repositories;

import com.movieflix.model.Movie;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MoviesRepository extends MongoRepository<Movie,String> {
}
