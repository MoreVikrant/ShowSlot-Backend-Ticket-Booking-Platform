package com.cfs.BookMyShow.entity;

import jakarta.persistence.*;

@Entity       // can search movie by the title also
@Table(name = "movies",uniqueConstraints = @UniqueConstraint(name = "uk_movie_title", columnNames = {"title"}) )
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String language;

    private String genre;

    private Integer durationInMin;

    private String description;

    private String certificate;

    private String posterUrl ;

    private String trailerUrl;

    private boolean active = true;


    public Movie() {

    }

    public Movie(String title,String language, String genre, Integer durationInMin, String description, String certificate, String posterUrl, String trailerUrl) {
        this.title = title;
        this.language = language;
        this.genre = genre;
        this.durationInMin = durationInMin;
        this.description = description;
        this.certificate = certificate;
        this.posterUrl = posterUrl;
        this.trailerUrl = trailerUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Integer getDurationInMin() {
        return durationInMin;
    }

    public void setDurationInMin(Integer durationInMin) {
        this.durationInMin = durationInMin;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public String getTrailerUrl() {
        return trailerUrl;
    }

    public void setTrailerUrl(String trailerUrl) {
        this.trailerUrl = trailerUrl;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
