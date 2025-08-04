/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.estebanmmk13.movies.repositories;

import com.estebanmmk13.movies.models.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 *
 * @author esteb
 */
@Repository
public interface MovieRepository extends JpaRepository<Movie,Long>{

    Optional<Movie> findMovieByTitleIgnoreCase(String title);

    @Query("SELECT m FROM Movie m JOIN m.genres g WHERE LOWER(g.name) = LOWER(:name)")
    List<Movie> findAllByGenreNameIgnoreCase(@Param("name") String name);
}
