package com.tour.tour.security;

import com.tour.tour.model.Traveler;
import com.tour.tour.repository.TravelerRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class TourUserDetailsService implements UserDetailsService {

    private final TravelerRepository travelerRepo;

    public TourUserDetailsService(TravelerRepository travelerRepo) {
        this.travelerRepo = travelerRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        Traveler traveler = travelerRepo.findById(Long.valueOf(username))
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "Traveler not found"
                        ));

        return org.springframework.security.core.userdetails.User
                .withUsername(String.valueOf(traveler.getId()))
                .password(traveler.getPassword())
                .authorities(
                        new SimpleGrantedAuthority(
                                "ROLE_" + traveler.getRole().name()
                        )
                )
                .build();

    }
}