package com.BookMyShow.dto;

import com.BookMyShow.entity.Movie;

public record MovieResponse(
        Long id, String title, String language, String genre, Integer durationMinutes,
        String certificate, String description, String posterUrl, String trailerUrl
) {

    public static MovieResponse from(Movie movie){
        return new  MovieResponse(
                movie.getId(),
                movie.getTitle(),
                movie.getLanguage(),
                movie.getGenre(),
                movie.getDurationInMin(),
                movie.getCertificate(),
                movie.getDescription(),
                movie.getPosterUrl(),
                movie.getTrailerUrl());
    }
}
