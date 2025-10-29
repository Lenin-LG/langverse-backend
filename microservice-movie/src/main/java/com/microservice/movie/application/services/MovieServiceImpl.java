package com.microservice.movie.application.services;

import com.microservice.movie.application.mapper.MovieMapper;
import com.microservice.movie.application.ports.in.MovieServicePort;
import com.microservice.movie.application.ports.out.MoviePersistencePort;
import com.microservice.movie.domain.exceptions.MovieNotFoundException;
import com.microservice.movie.domain.model.Movie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class MovieServiceImpl implements MovieServicePort  {

    @Autowired
    private  MoviePersistencePort moviePersistencePort;
    @Autowired
    private  MovieMapper movieMapper;
    @Override
    public Movie findById(Long id) {
        return moviePersistencePort.findById(id)
                .orElseThrow(MovieNotFoundException ::new);
    }

    @Override
    public Movie save(Movie movie) {
        return moviePersistencePort.save(movie);
    }

    @Override
    public List<Movie> findAll() {
        return moviePersistencePort.findAll();
    }

    @Override
    public Movie findByCategory(Long idCategory) {
        return moviePersistencePort.findByCategory(idCategory)
                .orElseThrow();
    }

    @Override
    public void deleteById(Long id) {
        if(moviePersistencePort.findById(id).isEmpty()){
            throw new MovieNotFoundException();
        }
        moviePersistencePort.deleteById(id);
    }

    @Override
    public Movie updateById(Long id,Movie updateMovie) {
        return moviePersistencePort.findById(id)
                .map(savedMovie ->{
                    movieMapper.updateMovie(savedMovie,updateMovie);
                    return moviePersistencePort.save(savedMovie);
                })
                .orElseThrow(MovieNotFoundException::new);
    }

    @Override
    public Movie findByTitle(String title) {

        return moviePersistencePort.findByTitle(title)
                .filter(movie ->Boolean.TRUE.equals(movie.getEstate()))
                .orElseThrow(()-> new RuntimeException("Movie not found or disabled"));
    }

    @Override
    public List<Movie> searchByTitle(String title) {
        return moviePersistencePort.searchByTitle(title);
    }

    @Override
    public List<Movie> getEpisodesBySeriesTitle(String title) {
        return moviePersistencePort.findAllByTitle(title).stream()
                .filter(movie -> movie.getIdCategory() == 2) // Solo series
                .filter(movie -> Boolean.TRUE.equals(movie.getEstate()))
                .sorted(Comparator
                        .comparing(Movie::getSeasonNumber)
                        .thenComparing(Movie::getEpisodeNumber))
                .toList();
    }

    @Override
    public void setActiveStatus(Long id, boolean active) {
        Movie movie = findById(id);
        movie.setEstate(active);
        moviePersistencePort.save(movie);
    }

}
