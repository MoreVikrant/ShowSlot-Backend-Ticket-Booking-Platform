package com.BookMyShow.controller;

import com.BookMyShow.dto.MovieResponse;
import com.BookMyShow.dto.ShowResponse;
import com.BookMyShow.dto.TheatreResponse;
import com.BookMyShow.dto.admin.CreateMovieRequest;
import com.BookMyShow.dto.admin.CreateShowRequest;
import com.BookMyShow.dto.admin.CreateTheatreRequest;
import com.BookMyShow.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/admin")
// before allowing anyone to execute method in this controller check whether they have admin role
@PreAuthorize("hasRole('Admin')")  // locked for admin only  // need @EnableMethodSecurity need in securityConfig
public class AdminController {

    final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/movies")
    @ResponseStatus(HttpStatus.CREATED)
    public MovieResponse addMovie(@Valid @RequestBody CreateMovieRequest request){
        return adminService.addMovie(request);
    }

    @PostMapping("/theatres")
    @ResponseStatus(HttpStatus.CREATED)
    public TheatreResponse addTheatre(@Valid @RequestBody CreateTheatreRequest request){
        return adminService.addTheatre(request);
    }

    @PostMapping("/shows")
    @ResponseStatus(HttpStatus.CREATED)
    public ShowResponse addShow(@Valid @RequestBody CreateShowRequest request) {
        return adminService.addShow(request);
    }

    @PostMapping("/movies/{id}/deactivate")
    public void deactivateMovie(@PathVariable Long id){
        adminService.deactivateMovie(id);
    }


}
