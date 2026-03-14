package com.example.demo.controllers;

import com.example.demo.dto.response.ApiResponse;
import com.example.demo.models.*;
import com.example.demo.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class RestApiController {
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "createdAt", "updatedAt", "title", "release_year", "views", "duration", "hot"
    );

    @Autowired
    private MovieService movieService;
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/movies/{movieId}/episodes")
    public ResponseEntity<ApiResponse<List<String>>> getEpisodeLinks(@PathVariable UUID movieId) {
        try {
            List<String> episodeLinks = movieService.getEpisodeLinks(movieId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Episode links retrieved successfully", episodeLinks));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "An error occurred while retrieving episode links", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/movies/category/{categoryName}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMoviesByCategory(
            @PathVariable String categoryName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Page<Movie> moviePage = movieService.getMoviesByCategory(categoryName, pageRequest(page, size, sortBy, sortDir));
            if (moviePage.isEmpty()) {
                return ResponseEntity.ok(new ApiResponse<>(true, "No movies found for the category", null));
            }
            return ResponseEntity.ok(new ApiResponse<>(true, "Movies retrieved successfully", toPagedPayload(moviePage)));
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new ApiResponse<>(false, e.getMessage(), null), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "An error occurred while retrieving movies", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/movies/category/id/{categoryId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMoviesByCategoryId(
            @PathVariable UUID categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Page<Movie> moviePage = movieService.getMoviesByCategoryId(categoryId, pageRequest(page, size, sortBy, sortDir));
            if (moviePage.isEmpty()) {
                return ResponseEntity.ok(new ApiResponse<>(true, "No movies found for the category ID", null));
            }
            return ResponseEntity.ok(new ApiResponse<>(true, "Movies retrieved successfully", toPagedPayload(moviePage)));
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new ApiResponse<>(false, e.getMessage(), null), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "An error occurred while retrieving movies", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/movies/country/{countryId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMoviesByCountryId(
            @PathVariable UUID countryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Page<Movie> moviePage = movieService.getMoviesByCountryId(countryId, pageRequest(page, size, sortBy, sortDir));
            if (moviePage.isEmpty()) {
                return ResponseEntity.ok(new ApiResponse<>(true, "No movies found for the country ID", null));
            }
            return ResponseEntity.ok(new ApiResponse<>(true, "Movies retrieved successfully", toPagedPayload(moviePage)));
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new ApiResponse<>(false, e.getMessage(), null), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "An error occurred while retrieving movies", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/movies/category/count/{categoryId}")
    public ResponseEntity<ApiResponse<Integer>> countMoviesByCategoryId(@PathVariable UUID categoryId) {
        try {
            int count = categoryService.countMoviesByCategoryId(categoryId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Movie count retrieved successfully", count));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "An error occurred while counting movies", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/movies/country/{countryName}/category/{categoryName}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMoviesByCountryAndCategory(
            @PathVariable String countryName,
            @PathVariable String categoryName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Page<Movie> moviePage = movieService.findMoviesByCountryAndCategory(countryName, categoryName, pageRequest(page, size, sortBy, sortDir));
            if (moviePage.isEmpty()) {
                return ResponseEntity.ok(new ApiResponse<>(true, "No movies found for the country and category", null));
            }
            return ResponseEntity.ok(new ApiResponse<>(true, "Movies retrieved successfully", toPagedPayload(moviePage)));
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new ApiResponse<>(false, e.getMessage(), null), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "An error occurred while retrieving movies", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/movies/search")
    public ResponseEntity<ApiResponse<Map<String, Object>>> searchMovie(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Pageable pageable = pageRequest(page, size, sortBy, sortDir);
            Page<Movie> moviePage = (keyword == null || keyword.trim().isEmpty())
                    ? movieService.getAllMovies(pageable)
                    : movieService.searchMovie(keyword, pageable);
            return ResponseEntity.ok(new ApiResponse<>(true, "Movies retrieved successfully", toPagedPayload(moviePage)));
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new ApiResponse<>(false, e.getMessage(), null), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "An error occurred while searching for movies", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/movies/hot")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMovieByHot(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Page<Movie> moviePage = movieService.getMovieByHot(true, pageRequest(page, size, sortBy, sortDir));
            if (moviePage.isEmpty()) {
                return ResponseEntity.ok(new ApiResponse<>(true, "No hot movies found", null));
            }
            return ResponseEntity.ok(new ApiResponse<>(true, "Hot movies retrieved successfully", toPagedPayload(moviePage)));
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new ApiResponse<>(false, e.getMessage(), null), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "An error occurred while retrieving hot movies", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/movies/type/{movieTypeName}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMoviesByMovieType(
            @PathVariable String movieTypeName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Page<Movie> moviePage = movieService.getMoviesByMovieType(movieTypeName, pageRequest(page, size, sortBy, sortDir));
            if (moviePage.isEmpty()) {
                return ResponseEntity.ok(new ApiResponse<>(true, "No movies found for the movie type", null));
            }
            return ResponseEntity.ok(new ApiResponse<>(true, "Movies retrieved successfully", toPagedPayload(moviePage)));
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new ApiResponse<>(false, e.getMessage(), null), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "An error occurred while retrieving movies", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/movies/type/id/{movieTypeId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMoviesByMovieTypeId(
            @PathVariable UUID movieTypeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Page<Movie> moviePage = movieService.getMoviesByMovieTypeId(movieTypeId, pageRequest(page, size, sortBy, sortDir));
            if (moviePage.isEmpty()) {
                return ResponseEntity.ok(new ApiResponse<>(true, "No movies found for the movie type ID", null));
            }
            return ResponseEntity.ok(new ApiResponse<>(true, "Movies retrieved successfully", toPagedPayload(moviePage)));
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new ApiResponse<>(false, e.getMessage(), null), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "An error occurred while retrieving movies", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Pageable pageRequest(int page, int size, String sortBy, String sortDir) {
        Sort.Direction direction = "asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;
        String safeSortBy = (sortBy == null || sortBy.isBlank()) ? "createdAt" : sortBy;

        if (!ALLOWED_SORT_FIELDS.contains(safeSortBy)) {
            throw new IllegalArgumentException("Invalid sortBy. Allowed values: " + ALLOWED_SORT_FIELDS);
        }

        return PageRequest.of(Math.max(page, 0), Math.max(size, 1), Sort.by(direction, safeSortBy));
    }

    private Map<String, Object> toPagedPayload(Page<Movie> page) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("items", page.getContent());
        payload.put("page", page.getNumber());
        payload.put("size", page.getSize());
        payload.put("totalItems", page.getTotalElements());
        payload.put("totalPages", page.getTotalPages());
        payload.put("hasNext", page.hasNext());
        payload.put("hasPrevious", page.hasPrevious());
        return payload;
    }
}