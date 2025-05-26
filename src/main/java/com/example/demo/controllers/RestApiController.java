package com.example.demo.controllers;

import com.example.demo.models.*;
import com.example.demo.repositories.CategoryRepository;
import com.example.demo.repositories.CountryRepositories;
import com.example.demo.repositories.MovieRepository;
import com.example.demo.repositories.MovieTypeRepository;
import com.example.demo.services.CategoryService;
import com.example.demo.services.CountryService;
import com.example.demo.services.MovieService;
import com.example.demo.services.MovieTypeSevice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class RestApiController {
    @Autowired
    private MovieService movieService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private CountryService countryService;
    @Autowired
    private MovieTypeSevice movieTypeSevice;

    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private CountryRepositories countryRepository;
    @Autowired
    private MovieTypeRepository movieTypeRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    // rest api movies
//    @GetMapping("/movies")
//    public ResponseEntity<List<Movie>> movies() {
//        try {
//            List<Movie> movies = movieService.getAllMovies();
//            if (movies.isEmpty()) {
//                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//            }
//            return new ResponseEntity<>(movies, HttpStatus.OK);
//        } catch (Exception e) {
//            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

    @GetMapping("/movies/{movieId}/episodes")
    public ResponseEntity<List<String>> getEpisodeLinks(@PathVariable Long movieId){
//        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + movieId));
//        String episodeLinks = movie.getEpisodeLinks();
//        if (episodeLinks == null || episodeLinks.isEmpty()) {
//            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.NO_CONTENT);
//        }
//        List<String> links = new ArrayList<>();
//        for (String link : episodeLinks.split("\n")) {
//            links.add(link);
//        }
//        return new ResponseEntity<>(links, HttpStatus.OK);

        List<String> episodeLinks = movieService.getEpisodeLinks(movieId);
        return ResponseEntity.ok(episodeLinks);
    }


//    @GetMapping("/countries")
//    public ResponseEntity<List<Country>> countries() {
//        try {
//            List<Country> countries = countryService.getAllCountries();
//            if (countries.isEmpty()) {
//                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//            }
//            return new ResponseEntity<>(countries, HttpStatus.OK);
//        } catch (Exception e) {
//            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

//    @GetMapping("/types")
//    public ResponseEntity<List<MovieType>> movieTypes() {
//        try {
//            List<MovieType> movieTypes = movieTypeSevice.getAllMovieTypes();
//            if (movieTypes.isEmpty()) {
//                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//            }
//            return new ResponseEntity<>(movieTypes, HttpStatus.OK);
//        } catch (Exception e) {
//            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

    @GetMapping("/movies/{id}")
    public ResponseEntity<Movie> movieById(@PathVariable Long id) {
        try {
            Movie movie = movieService.getMovieById(id);
            if (movie == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(movie, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // get movie by category
    @GetMapping("/movies/category/{categoryName}")
    public ResponseEntity<List<Movie>> getMoviesByCategory(@PathVariable String categoryName) {
        List<Movie> movies = movieService.getMoviesByCategory(categoryName);
        if (movies.isEmpty()) {
            return ResponseEntity.noContent().build(); // trả về status 204 nếu không có phim
        }
        return ResponseEntity.ok(movies); // trả về danh sách phim
    }

    // rest api categories
//    @GetMapping("/categories")
//    public ResponseEntity<List<Category>> categories() {
//        try {
//            List<Category> categories = categoryService.getAllCategory();
//            if (categories.isEmpty()) {
//                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//            }
//            return new ResponseEntity<>(categories, HttpStatus.OK);
//        } catch (Exception e) {
//            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

    // Update the method to get movies by category ID
    @GetMapping("/movies/category/id/{categoryId}")
    public ResponseEntity<List<Movie>> getMoviesByCategoryId(@PathVariable Long categoryId) {
        List<Movie> movies = movieService.getMoviesByCategoryId(categoryId);
        if (movies.isEmpty()) {
            return ResponseEntity.noContent().build(); // trả về status 204 nếu không có phim
        }
        return ResponseEntity.ok(movies); // trả về danh sách phim
    }

    @GetMapping("/movies/category/count/{categoryId}")
    public ResponseEntity<Integer> countMoviesByCategoryId(@PathVariable Long categoryId) {
        int count = categoryService.countMoviesByCategoryId(categoryId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/movies/country/{countryName}/category/{categoryName}")
    public ResponseEntity<List<Movie>> getMoviesByCountryAndCategory(@PathVariable String countryName, @PathVariable String categoryName) {
        List<Movie> movies = movieService.findMoviesByCountryAndCategory(countryName, categoryName);
        if (movies.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(movies);
    }


//    @PutMapping("/movies/update/{id}")
//    public ResponseEntity<Movie> updateMovie(@PathVariable Long id, @RequestBody MovieDto updatedMovieDto) {
//        try {
//            // Log dữ liệu nhận được từ client (để kiểm tra)
//            System.out.println("ID from URL: " + id);
//            System.out.println("Data from client: \n" + updatedMovieDto);
//
//            // Lấy Movie hiện có từ database
//            Movie existingMovie = movieService.getMovieById(id);
//            if (existingMovie == null) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
//            }
//
//            // Cập nhật các trường trong Movie dựa trên MovieDto
//            existingMovie.setTitle(updatedMovieDto.getTitle());
//            existingMovie.setDescription(updatedMovieDto.getDescription());
//            existingMovie.setRelease_year(updatedMovieDto.getRelease_year());
//            existingMovie.setThumb_url(updatedMovieDto.getThumb_url());
//            existingMovie.setLink(updatedMovieDto.getLink());
//
//            // Xử lý country nếu cần
//            if (updatedMovieDto.getCountryId() != null) {
//                Country country = countryService.getCountryById(updatedMovieDto.getCountryId());
//                existingMovie.setCountry(country);
//            } else {
//                existingMovie.setCountry(null); // Nếu không có countryId, bỏ liên kết
//            }
//
//            // Cập nhật danh sách MovieTypes
//            if (updatedMovieDto.getMovieTypeIds() != null) {
//                List<MovieType> movieTypes = updatedMovieDto.getMovieTypeIds().stream() // Chuyển List<Long> thành List<MovieType>
//                        .map(movieTypeSevice::getMovieTypeById) // Gọi phương thức xử lý từng ID
//                        .collect(Collectors.toList());
//                existingMovie.setMovieTypes(new HashSet<>(movieTypes));
//            }
//
//
//            // Cập nhật danh sách Categories
//            if (updatedMovieDto.getCategoryIds() != null) {
//                List<Category> categories = categoryService.getCategoriesByIds(updatedMovieDto.getCategoryIds());
//                existingMovie.setMovieCategories(new HashSet<>(categories));
//            }
//
//            // Lưu movie đã cập nhật
//            Movie updatedMovie = movieService.saveMovie(existingMovie);
//
//            // Trả về Movie đã cập nhật
//            return ResponseEntity.ok(updatedMovie);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
//        }
//    }

    // path variable dùng de lay tham so tu url sau dau /
    // request param dùng de lay tham so tu url sau dau ?
    @GetMapping("/movies/search")
    public ResponseEntity<List<Movie>> searchMovie(@RequestParam(required = false) String keyword) {
        List<Movie> movies = (keyword == null || keyword.trim().isEmpty()) ?
                movieService.getAllMovies() : movieService.searchMovie(keyword);

//        return movies.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(movies);
        return ResponseEntity.ok(movies);
    }

    // get movie by hot = true
    @GetMapping("/movies/hot")
    public ResponseEntity<List<Movie>> getMovieByHot() {
        List<Movie> movies = movieService.getMovieByHot(true);
        if (movies.isEmpty()) {
            return ResponseEntity.noContent().build(); // trả về status 204 nếu không có phim
        }
        return ResponseEntity.ok(movies); // trả về danh sách phim
    }

}
