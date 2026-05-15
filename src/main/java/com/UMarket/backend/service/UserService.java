package com.UMarket.backend.service;

import com.UMarket.backend.model.User;
import com.UMarket.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User register(User user) {
        if (!isEducationalEmail(user.getEmail())) {
            throw new RuntimeException("Only institutional email addresses (.edu) are allowed");
        }

        String domain = user.getEmail().toLowerCase().substring(user.getEmail().lastIndexOf("@") + 1);
user.setUniversity(DOMAIN_TO_UNIVERSITY.get(domain));

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("This email is already registered");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setVerified(false);
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());

        String token = UUID.randomUUID().toString();
        user.setVerificationToken(token);
        user.setTokenExpiration(LocalDateTime.now().plusHours(24));

        User savedUser = userRepository.save(user);
        emailService.sendVerificationEmail(user.getEmail(), token);

        return savedUser;
    }

    public String verifyEmail(String token) {
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (LocalDateTime.now().isAfter(user.getTokenExpiration())) {
            throw new RuntimeException("Token has expired");
        }

        user.setVerified(true);
        user.setVerificationToken(null);
        user.setTokenExpiration(null);
        userRepository.save(user);

        return "Account verified successfully! You can now log in to UMarket.";
    }

    private static final Set<String> ALLOWED_DOMAINS = Set.of(
            "usac.edu.gt",
            "galileo.edu",
            "uvg.edu.gt",
            "ufm.edu",
            "umg.edu.gt",
            "miumg.edu.gt",
            "url.edu.gt",
            "unis.edu.gt",
            "umes.edu.gt",
            "upana.edu.gt",
            "uni.edu.gt",
            "urural.edu.gt",
            "usanpablo.edu.gt",
            "udavinci.edu.gt",
            "udeo.edu.gt",
            "ureg.edu.gt"
    );

    private static final Map<String, String> DOMAIN_TO_UNIVERSITY = Map.ofEntries(
            Map.entry("usac.edu.gt",      "Universidad de San Carlos de Guatemala"),
            Map.entry("galileo.edu",      "Universidad Galileo"),
            Map.entry("uvg.edu.gt",       "Universidad del Valle de Guatemala"),
            Map.entry("ufm.edu",          "Universidad Francisco Marroquín"),
            Map.entry("umg.edu.gt",       "Universidad Mariano Gálvez"),
            Map.entry("miumg.edu.gt",     "Universidad Mariano Gálvez"),
            Map.entry("url.edu.gt",       "Universidad Rafael Landívar"),
            Map.entry("unis.edu.gt",      "Universidad del Istmo"),
            Map.entry("umes.edu.gt",      "Universidad Mesoamericana"),
            Map.entry("upana.edu.gt",     "Universidad Panamericana"),
            Map.entry("uni.edu.gt",       "Universidad InterNaciones"),
            Map.entry("urural.edu.gt",    "Universidad Rural"),
            Map.entry("usanpablo.edu.gt", "Universidad de San Pablo"),
            Map.entry("udavinci.edu.gt",  "Universidad Da Vinci"),
            Map.entry("udeo.edu.gt",      "Universidad de Occidente"),
            Map.entry("ureg.edu.gt",      "Universidad Regional")
    );

    private boolean isEducationalEmail(String email) {
        if (email == null || !email.contains("@")) return false;
        String domain = email.toLowerCase().substring(email.lastIndexOf("@") + 1);
        return ALLOWED_DOMAINS.contains(domain);
    }

    public List<User> findAll() {
        return userRepository.findAllByActiveTrue();
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User update(Long id, User updatedData) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setName(updatedData.getName());
        user.setUniversity(updatedData.getUniversity());

        return userRepository.save(user);
    }

    public void deactivate(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setActive(false);
        userRepository.save(user);
    }
}
