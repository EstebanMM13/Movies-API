/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.estebanmmk13.movies.repositories;

import com.estebanmmk13.movies.models.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 *
 * @author esteb
 */
public interface MovieRepository extends JpaRepository<Movie,Long>{

    Optional<Movie> findByTitleIgnoreCase(String title);
}
