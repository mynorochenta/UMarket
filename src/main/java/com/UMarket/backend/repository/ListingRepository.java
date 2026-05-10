package com.UMarket.backend.repository;

import com.UMarket.backend.model.Category;
import com.UMarket.backend.model.Listing;
import com.UMarket.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ListingRepository extends JpaRepository<Listing, Long> {

    List<Listing> findBySellerAndActiveTrue(User seller);

    List<Listing> findByCategoryAndActiveTrue(Category category);

    List<Listing> findByTitleContainingIgnoreCaseAndActiveTrue(String title);

    @Query("SELECT l FROM Listing l WHERE l.active = true ORDER BY l.publishedAt DESC")
    List<Listing> findMainFeed();
}
