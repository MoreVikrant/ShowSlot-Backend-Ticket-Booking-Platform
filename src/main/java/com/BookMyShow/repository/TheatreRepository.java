package com.BookMyShow.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.BookMyShow.entity.Theatre;

import java.util.List;
import java.util.Optional;

public interface TheatreRepository extends JpaRepository<Theatre,Long>{


    List<Theatre> findByCityIgnoreCaseOrderByName(String city);

    Optional<Theatre> findByNameAndCity(String name, String city);
}

// derived query methods are there no need to write the query it understands by the method name
