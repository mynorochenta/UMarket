package com.UMarket.backend.repository;

import com.UMarket.backend.model.Review;
import com.UMarket.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findBySeller(User seller);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.seller = :seller")
    Double calculateAverageRating(@Param("seller") User seller);
}
