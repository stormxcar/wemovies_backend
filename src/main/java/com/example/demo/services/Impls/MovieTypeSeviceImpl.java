package com.example.demo.services.Impls;

import com.example.demo.models.MovieType;
import com.example.demo.repositories.MovieTypeRepository;
import com.example.demo.services.MovieTypeSevice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieTypeSeviceImpl implements MovieTypeSevice {
    @Autowired
    private MovieTypeRepository movieTypeRepository;
    @Override
    public List<MovieType> getAllMovieTypes() {
        return movieTypeRepository.findAll();
    }

    @Override
    public MovieType getMovieTypeById(Long id) {
        return movieTypeRepository.findById(id).orElse(null);
    }

    @Override
    public MovieType saveMovieType(MovieType movieType) {
        return movieTypeRepository.save(movieType);
    }

    @Override
    public void deleteMovieTypeById(Long id) {
            MovieType movieType = movieTypeRepository.findById(id).orElse(null);
            movieTypeRepository.delete(movieType);
    }

    @Override
    public int countMoviesByMovieTypeId(Long movieTypeId) {
        return movieTypeRepository.countMoviesByMovieTypeId(movieTypeId);
    }

    @Override
    public int countMovieTypes() {
        return (int) movieTypeRepository.count();
    }
}
