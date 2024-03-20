package com.movieflix.service;

import com.movieflix.dto.MovieDto;
import com.movieflix.dto.MoviePageResponse;
import com.movieflix.model.Movie;
import com.movieflix.repositories.MoviesRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@Slf4j
public class MovieServiceImpl implements MovieService{
    private final MoviesRepository moviesRepository;
    private final ModelMapper modelMapper;
    private final FileService fileService;

    public MovieServiceImpl(MoviesRepository moviesRepository, ModelMapper modelMapper, FileService fileService) {
        this.moviesRepository = moviesRepository;
        this.modelMapper = modelMapper;
        this.fileService = fileService;
    }
    @Value("${project.poster}")
    private String path;
    @Value("${project.base.url}")
    private String baseUrl;

    @Override
    public MovieDto addMovie(MovieDto movieDto, MultipartFile file) throws IOException {
        // 1. upload file
        if (Files.exists(Paths.get(path + File.separator + file.getOriginalFilename()))) {
            throw new RuntimeException("File already exists! pleas enter another file name...!");
        }
         String uploadedFileName = fileService.uploadFile(path,file);
        // 2. save the value of field 'poster' as filename
        movieDto.setPoster(uploadedFileName);
        // 3. map dto to movie object
        Movie movie =  new Movie(
                null,
                movieDto.getTitle(),
                movieDto.getDirector(),
                movieDto.getStudio(),
                movieDto.getMovieCast(),
                movieDto.getReleaseYear(),
                movieDto.getPoster()
        ) ;

        // 4. save the movie object -> saved movie object
       Movie savedMovie  = moviesRepository.save(movie);
       log.info("save from db: {} ",savedMovie);
        // 5. generate  the posterUrl
        String posterUrl = baseUrl + "/file/" + uploadedFileName;
        // 6. map movie object to Dto object and return it
        return new MovieDto(
                savedMovie.getMovieId(),
                savedMovie.getTitle(),
                savedMovie.getDirector(),
                savedMovie.getStudio(),
                savedMovie.getMovieCast(),
                savedMovie.getReleaseYear(),
                savedMovie.getPoster(),posterUrl
        );
    }

    @Override
    public MovieDto getMovie(String movieId) {
        // 1. Check the data in DB nd if exists, fetch the data given ID
        Movie movie = moviesRepository.findById(movieId).orElseThrow(
                () -> new RuntimeException("movie not found in db"));
        // 2. Generate posterUrl
        String posterUrl = baseUrl + "/file/" + movie.getPoster();
        // 3. map to MovieDto Object and return  it
        return new MovieDto(
                movie.getMovieId(),
                movie.getTitle(),
                movie.getDirector(),
                movie.getStudio(),
                movie.getMovieCast(),
                movie.getReleaseYear(),
                movie.getPoster(),
                posterUrl
        );
    }

    @Override
    public List<MovieDto> getAllMovies() {
        // 1. Fetch all data from DB
        List<Movie> movies = moviesRepository.findAll();
        List<MovieDto> movieDtos = new ArrayList<>();
        // 2. iterate through the list, generate posterUrl for each movie object and map to MovieDto Obj
        for (Movie movie : movies) {
            String posterUrl = baseUrl + "/file/" + movie.getPoster();
            MovieDto movieDto = new MovieDto(
                    movie.getMovieId(),
                    movie.getTitle(),
                    movie.getDirector(),
                    movie.getStudio(),
                    movie.getMovieCast(),
                    movie.getReleaseYear(),
                    movie.getPoster(),
                    posterUrl
            );
            movieDtos.add(movieDto);
        }
        return movieDtos;
    }

    @Override

        public MovieDto updateMovie(String movieId, MovieDto movieDto, MultipartFile file) throws IOException {
        // 1. check if movie object exists with given movieID
        Movie mv = moviesRepository.findById(movieId).orElseThrow(() -> new RuntimeException("movies not found...!"));
        // 2. if file is null, do nothing
        //  if file not null , then delete existing file associated with the record and upload the new file
        String fileName = mv.getPoster();
        if (file != null) {

            Files.deleteIfExists(Paths.get(path + File.separator + fileName));
            fileName = fileService.uploadFile(path, file);
            log.info("uplod the file: {}",fileName);
        }
        // 3. set the movieDto  poster value according to step2

        movieDto.setPoster(fileName);

        // 4. map it movie object

        Movie movie = new Movie(
                mv.getMovieId(),
                movieDto.getTitle(),
                movieDto.getDirector(),
                movieDto.getStudio(),
                movieDto.getMovieCast(),
                movieDto.getReleaseYear(),
                movieDto.getPoster()
        );
        // 5. save the movie object -> return saved movie object

        Movie updatedMovies = moviesRepository.save(movie);
        log.info("save the updated movies: {}", updatedMovies);

        //6. generate posterUrl for it

        String posterUrl = baseUrl + "/file/" + fileName;

        // 7. map to movieDto and return it
        return new MovieDto(
                movie.getMovieId(),
                movie.getTitle(),
                movie.getDirector(),
                movie.getStudio(),
                movie.getMovieCast(),
                movie.getReleaseYear(),
                movie.getPoster(),
                posterUrl);
    }
    @Override
    public String deleteMovie(String movieId) throws IOException {
        // 1.  check if movie object   exists in db
        Movie mv  = moviesRepository.findById(movieId).orElseThrow(()-> new RuntimeException("Movies not found...!"));
        String id = mv.getMovieId();
        // 2. delete the file associated  with this  object
        Files.deleteIfExists(Paths.get(path + File.separator+ mv.getPoster()));
        // 3. delete the movie object
         moviesRepository.delete(mv);
         return "movie deleted with id = " + id;
    }

    @Override
    public MoviePageResponse getAllMoviesWithPagination(Integer pageNumber, Integer pageSize) {
        Pageable pageable  = PageRequest.of(pageNumber,pageSize);
      Page<Movie> moviePage = moviesRepository.findAll(pageable);
      List<Movie> movies = moviePage.getContent();
        List<MovieDto> movieDtos = new ArrayList<>();
        // 2. iterate through the list, generate posterUrl for each movie object and map to MovieDto Obj
        for (Movie movie : movies) {
            String posterUrl = baseUrl + "/file/" + movie.getPoster();
            MovieDto movieDto = new MovieDto(
                    movie.getMovieId(),
                    movie.getTitle(),
                    movie.getDirector(),
                    movie.getStudio(),
                    movie.getMovieCast(),
                    movie.getReleaseYear(),
                    movie.getPoster(),
                    posterUrl
            );
            movieDtos.add(movieDto);
        }
        return new MoviePageResponse(movieDtos,pageNumber,pageSize,
                moviePage.getTotalPages(),
                moviePage.getTotalElements(),
                moviePage.isLast());
    }

    @Override
    public MoviePageResponse getAllMoviesWithPaginationAndSorting(Integer pageNumber, Integer pageSize, String sortBy, String dir) {
        Sort sort = dir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable  = PageRequest.of(pageNumber,pageSize);
        Page<Movie> moviePage = moviesRepository.findAll(pageable);
        List<Movie> movies = moviePage.getContent();
        List<MovieDto> movieDtos = new ArrayList<>();
        // 2. iterate through the list, generate posterUrl for each movie object and map to MovieDto Obj
        for (Movie movie : movies) {
            String posterUrl = baseUrl + "/file/" + movie.getPoster();
            MovieDto movieDto = new MovieDto(
                    movie.getMovieId(),
                    movie.getTitle(),
                    movie.getDirector(),
                    movie.getStudio(),
                    movie.getMovieCast(),
                    movie.getReleaseYear(),
                    movie.getPoster(),
                    posterUrl
            );
            movieDtos.add(movieDto);
        }
        return new MoviePageResponse(movieDtos,pageNumber,pageSize,
                moviePage.getTotalPages(),
                moviePage.getTotalElements(),
                moviePage.isLast());    }

}
