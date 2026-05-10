package com.UMarket.backend.service;

import com.UMarket.backend.model.Listing;
import com.UMarket.backend.model.User;
import com.UMarket.backend.repository.ListingRepository;
import com.UMarket.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ListingService {

    @Autowired
    private ListingRepository listingRepository;

    @Autowired
    private UserRepository userRepository;

    public Listing create(Listing listing, Long sellerId) {
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("Seller not found"));

        if (!seller.isVerified()) {
            throw new RuntimeException("You must verify your email before posting a listing");
        }

        if (listing.getPrice() <= 0) {
            throw new RuntimeException("Price must be greater than Q0.00");
        }

        listing.setSeller(seller);
        listing.setActive(true);
        listing.setPublishedAt(LocalDateTime.now());

        return listingRepository.save(listing);
    }

    public List<Listing> getFeed() {
        return listingRepository.findMainFeed();
    }

    public Optional<Listing> findById(Long id) {
        return listingRepository.findById(id).filter(Listing::isActive);
    }

    public List<Listing> findBySeller(Long sellerId) {
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("Seller not found"));
        return listingRepository.findBySellerAndActiveTrue(seller);
    }

    public List<Listing> search(String term) {
        return listingRepository.findByTitleContainingIgnoreCaseAndActiveTrue(term);
    }

    public void deactivate(Long id, Long sellerId) {
        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Listing not found"));

        if (!listing.getSeller().getId().equals(sellerId)) {
            throw new RuntimeException("You do not have permission to modify this listing");
        }

        listing.setActive(false);
        listingRepository.save(listing);
    }
}
