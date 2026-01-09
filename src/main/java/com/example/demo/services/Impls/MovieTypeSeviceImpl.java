package com.example.demo.services.Impls;

import com.example.demo.models.MovieType;
import com.example.demo.repositories.MovieTypeRepository;
import com.example.demo.services.MovieTypeSevice;
import com.example.demo.services.SlugService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class MovieTypeSeviceImpl implements MovieTypeSevice {
    @Autowired
    private MovieTypeRepository movieTypeRepository;

    @Autowired
    private SlugService slugService;
    @Override
    public List<MovieType> getAllMovieTypes() {
        return movieTypeRepository.findAll();
    }

    @Override
    public MovieType getMovieTypeById(UUID id) {
        return movieTypeRepository.findById(id).orElse(null);
    }

    @Override
    public MovieType getMovieTypeBySlug(String slug) {
        return movieTypeRepository.findBySlug(slug);
    }

    @Override
    public MovieType saveMovieType(MovieType movieType) {
        // Generate slug if not set
        if (movieType.getSlug() == null || movieType.getSlug().trim().isEmpty()) {
            slugService.generateMovieTypeSlug(movieType);
        }
        return movieTypeRepository.save(movieType);
    }

    @Override
    public void deleteMovieTypeById(UUID id) {
            MovieType movieType = movieTypeRepository.findById(id).orElse(null);
            movieTypeRepository.delete(movieType);
    }

    @Override
    public int countMoviesByMovieTypeId(UUID movieTypeId) {
        return movieTypeRepository.countMoviesByMovieTypeId(movieTypeId);
    }

    @Override
    public int countMovieTypes() {
        return (int) movieTypeRepository.count();
    }
}
