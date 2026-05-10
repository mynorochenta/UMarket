package com.UMarket.backend.controller;

import com.UMarket.backend.model.Listing;
import com.UMarket.backend.model.ListingImage;
import com.UMarket.backend.repository.ListingImageRepository;
import com.UMarket.backend.repository.ListingRepository;
import com.UMarket.backend.service.CloudinaryService;
import com.UMarket.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private ListingImageRepository listingImageRepository;

    @Autowired
    private ListingRepository listingRepository;

    @Autowired
    private UserService userService;

    // POST /api/images/listing/{id}
    @PostMapping("/listing/{listingId}")
    public ResponseEntity<?> uploadImages(
            @PathVariable Long listingId,
            @RequestParam("files") List<MultipartFile> files) {
        try {
            Listing listing = listingRepository.findById(listingId)
                    .orElseThrow(() -> new RuntimeException("Listing not found"));

            Long authenticatedId = getAuthenticatedId();
            if (!listing.getSeller().getId().equals(authenticatedId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("You do not have permission to upload images to this listing");
            }

            if (files.size() > 5) {
                return ResponseEntity.badRequest().body("Maximum 5 images per listing");
            }

            List<ListingImage> savedImages = new ArrayList<>();

            for (int i = 0; i < files.size(); i++) {
                String url = cloudinaryService.uploadImage(files.get(i));

                ListingImage image = new ListingImage();
                image.setUrl(url);
                image.setSortOrder(i + 1);
                image.setListing(listing);

                savedImages.add(listingImageRepository.save(image));
            }

            return ResponseEntity.ok(savedImages);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error uploading images: " + e.getMessage());
        }
    }

    private Long getAuthenticatedId() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"))
                .getId();
    }
}
