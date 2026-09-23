package com.tour.tour.security;

import com.tour.tour.model.Admin;
import com.tour.tour.model.Traveler;
import com.tour.tour.repository.AdminRepository;
import com.tour.tour.repository.TravelerRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TourUserDetailsService implements UserDetailsService {

    private final TravelerRepository travelerRepo;
    private final AdminRepository adminRepo;

    public TourUserDetailsService(TravelerRepository travelerRepo, AdminRepository adminRepo) {
        this.travelerRepo = travelerRepo;
        this.adminRepo = adminRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        if (username.startsWith("ADMIN_")) {
            String actualUsername = username.substring(6);
            Optional<Admin> adminOpt = adminRepo.findById(actualUsername);
            if (adminOpt.isPresent()) {
                Admin admin = adminOpt.get();
                return org.springframework.security.core.userdetails.User
                        .withUsername(username)
                        .password(admin.getPassword())
                        .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))
                        .build();
            }
        } else if (username.startsWith("USER_")) {
            String actualId = username.substring(5);
            try {
                Long travelerId = Long.valueOf(actualId);
                Traveler traveler = travelerRepo.findById(travelerId)
                        .orElseThrow(() -> new UsernameNotFoundException("Traveler not found"));

                return org.springframework.security.core.userdetails.User
                        .withUsername(username)
                        .password(traveler.getPassword())
                        .authorities(new SimpleGrantedAuthority("ROLE_USER"))
                        .build();
            } catch (NumberFormatException e) {
                throw new UsernameNotFoundException("Invalid user format");
            }
        }

        throw new UsernameNotFoundException("User not found");
    }
}