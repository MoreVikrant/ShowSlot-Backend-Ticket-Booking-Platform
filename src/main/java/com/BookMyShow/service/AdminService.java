package com.BookMyShow.service;

import com.BookMyShow.dto.MovieResponse;
import com.BookMyShow.dto.ShowResponse;
import com.BookMyShow.dto.TheatreResponse;
import com.BookMyShow.dto.admin.CreateMovieRequest;
import com.BookMyShow.dto.admin.CreateShowRequest;
import com.BookMyShow.dto.admin.CreateTheatreRequest;
import com.BookMyShow.entity.Movie;
import com.BookMyShow.entity.Show;
import com.BookMyShow.entity.ShowSeat;
import com.BookMyShow.entity.Theatre;
import com.BookMyShow.repository.MovieRepository;
import com.BookMyShow.repository.ShowRepository;
import com.BookMyShow.repository.ShowSeatRepository;
import com.BookMyShow.repository.TheatreRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AdminService {

    private final MovieRepository movieRepository;
    private final ShowRepository showRepository;
    private final TheatreRepository theatreRepository;
    private final ShowSeatRepository showSeatRepository;


    public AdminService(MovieRepository movieRepository, ShowRepository showRepository, TheatreRepository theatreRepository, ShowSeatRepository showSeatRepository) {
        this.movieRepository = movieRepository;
        this.showRepository = showRepository;
        this.theatreRepository = theatreRepository;
        this.showSeatRepository = showSeatRepository;
    }

    public MovieResponse addMovie(CreateMovieRequest create){

        String title = create.title().trim();
        if(movieRepository.findByTitle(title).isPresent()){
            throw new IllegalArgumentException("Movie already exists");
        }
        Movie movie = new Movie(title, create.language().trim(), create.genre().trim(),
                create.durationInMin(), create.description(), create.certificate(),
                create.posterUrl(), create.trailerUrl());
         return MovieResponse.from(movieRepository.save(movie));
    }

    public TheatreResponse addTheatre(CreateTheatreRequest r) {
        if (theatreRepository.findByNameAndCity(r.name().trim(), r.city().trim()).isPresent()) {
            throw new IllegalArgumentException("Theatre already exists in this city");
        }
        return TheatreResponse.from(theatreRepository.save(
                new Theatre(r.name().trim(), r.city().trim(), r.address().trim())));
    }

    public ShowResponse addShow(CreateShowRequest request){

        // Find the movie
        Movie movie = movieRepository.findById(request.movieId())
                .orElseThrow(()-> new ResourceNotFoundException("Movie not found"));

        // Find the theatre
        Theatre theatre = theatreRepository.findById(request.theatreId())
                .orElseThrow(()-> new ResourceNotFoundException("Theatre not found"));

        // Check for Duplicate show
        // So we are asking is the show exists for this movie in this theatre at this exact starting time ?
        if(showRepository.existsByMovieIdAndTheatreIdAndStartsAt(movie.getId(),
                theatre.getId(), request.startsAt())) {
            throw new IllegalArgumentException("This show already exists");
        }

        // Checking the movie Runtime
        if(movie.getDurationInMin() == null){
            throw new IllegalArgumentException("Movie has no duration set ");
        }

        // Calculate the ending time // local time  +   movie duration
        LocalDateTime endsAt = request.startsAt().plusMinutes(movie.getDurationInMin());

        // total seats
        int totalSeats = request.rows() * request.seatsPerRow();

        // Create and save the show
        Show show = showRepository.save(
                new Show(movie,theatre,request.startsAt(),endsAt,totalSeats,request.ticketPrice()));


        List<ShowSeat> seats = new ArrayList<>();
        for (int row = 0; row < request.rows(); row++) {
            for(int n =1; n <= request.seatsPerRow(); n++){
                seats.add(new ShowSeat(show, String.valueOf( (char) ('A'+ row)) + n));
            }                             // caste to character and  A + 1 = B  this concept is used
        }

        showSeatRepository.saveAll(seats);        // show + seats saved together

  return  ShowResponse.from(show,seats.stream().map(ShowSeat :: getSeatLabel).toList());
    }

    // Deactivate the show
    @Transactional
    public void deactivateMovie(Long id){
      Movie movie = movieRepository.findById(id)
              .orElseThrow(()-> new ResourceNotFoundException("Movie not found"));
      movie.setActive(false);
    }
}
