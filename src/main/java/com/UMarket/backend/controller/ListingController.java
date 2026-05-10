package com.UMarket.backend.controller;

import com.UMarket.backend.model.Listing;
import com.UMarket.backend.service.ListingService;
import com.UMarket.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/listings")
public class ListingController {

    @Autowired
    private ListingService listingService;

    @Autowired
    private UserService userService;

    // POST /api/listings
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Listing listing) {
        try {
            Long sellerId = getAuthenticatedId();
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(listingService.create(listing, sellerId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // GET /api/listings
    @GetMapping
    public ResponseEntity<List<Listing>> getFeed() {
        return ResponseEntity.ok(listingService.getFeed());
    }

    // GET /api/listings/1
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        return listingService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/listings/seller/1
    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<List<Listing>> findBySeller(@PathVariable Long sellerId) {
        return ResponseEntity.ok(listingService.findBySeller(sellerId));
    }

    // GET /api/listings/search?term=calculus
    @GetMapping("/search")
    public ResponseEntity<List<Listing>> search(@RequestParam String term) {
        return ResponseEntity.ok(listingService.search(term));
    }

    // DELETE /api/listings/1
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deactivate(@PathVariable Long id) {
        try {
            Long sellerId = getAuthenticatedId();
            listingService.deactivate(id, sellerId);
            return ResponseEntity.ok("Listing deactivated successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private Long getAuthenticatedId() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"))
                .getId();
    }
}
