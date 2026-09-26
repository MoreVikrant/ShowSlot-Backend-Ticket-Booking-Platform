package com.cfs.BookMyShow.repository;

import com.cfs.BookMyShow.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MovieRepository extends JpaRepository<Movie,Long> {

      List<Movie> findByActiveTrueOrderByTitle();
 //   Because True is already specified in the method name. that's why not parameter is passed
    // If the value is already written in the method name, you usually don't need a parameter.


    Optional<Movie> findByTitle(String title);
}
