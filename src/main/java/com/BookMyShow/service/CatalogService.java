package com.BookMyShow.service;

import com.BookMyShow.dto.MovieResponse;
import com.BookMyShow.dto.ShowResponse;
import com.BookMyShow.dto.TheatreResponse;
import com.BookMyShow.repository.MovieRepository;
import com.BookMyShow.repository.ShowRepository;
import com.BookMyShow.repository.ShowSeatRepository;
import com.BookMyShow.repository.TheatreRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

// This service will be responsible for showing the data
@Service
public class CatalogService {

    private final MovieRepository movieRepository;
    private final ShowRepository showRepository;
    private final ShowSeatRepository showSeatRepository;
    private final TheatreRepository theatreRepository;

    public CatalogService(MovieRepository movieRepository, ShowRepository showRepository, ShowSeatRepository showSeatRepository, TheatreRepository theatreRepository) {
        this.movieRepository = movieRepository;
        this.showRepository = showRepository;
        this.showSeatRepository = showSeatRepository;
        this.theatreRepository = theatreRepository;
    }

    // will show the movies which are active sort by title
    public List<MovieResponse> movies(){
        return movieRepository.findByActiveTrueOrderByTitle().stream().map(MovieResponse::from).toList();
                 // read the above method once in  movieRepository    // For every Movie, call MovieResponse.from(movie).
    }

    public List<TheatreResponse> theatres(String city){
        return theatreRepository.findByCityIgnoreCaseOrderByName(city).stream().map(TheatreResponse:: from).toList();
        // from is the method in the theatreResponse which will give the theatre details like city, address, name , id
    }
                                // The user wants to see shows in a particular city.
                               // LocalDate represents a date without time.
    public List<ShowResponse> shows(String city, LocalDate date){
         LocalDateTime from = date.atStartOfDay();   // converting LocalDate into LocalDateTime
         // this method adds time Start from midnight at the beginning of the selected date.
        // we are creating a time range because user is giving us only the date
        return showRepository.findActiveShows(city,from,from.plusDays(1))
   /*             // this is the timeline that
                // we have created day user given midnight to next day because we had this custom method in the showRepository
                // if input is city = Mumbai
                //date = 2026-09-20
                // the service passes city = Mumbai
                //from = 2026-09-20 00:00:00
                //to = 2026-09-21 00:00:00  At this point, we have found the shows. then continued                */
                .stream().map(show -> ShowResponse.from(show,showSeatRepository.findAvailableLabels(show.getId()))).toList();
 /*       For the given city and date, start at midnight,
        find all active shows occurring during that day,
        and for every show find its available seats.
        Then combine the show information and available seat information into a ShowResponse,
        collect all responses into a list, and return that list.*/
    }

}
