package com.smartbiz.seeder;

import com.smartbiz.entity.Admin;
import com.smartbiz.entity.Subscription;
import com.smartbiz.repository.AdminRepository;
import com.smartbiz.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final AdminRepository adminRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        seedAdmins();
        seedSubscriptions();
    }

    private void seedAdmins() {
        if (adminRepository.count() == 0) {
            log.info("========================================");
            log.info("SEEDING ADMIN ACCOUNTS");
            log.info("========================================");

            // Admin 1 - Super Admin
            // Raw password: admin@123
            String rawPassword1 = "admin@123";
            String encryptedPassword1 = passwordEncoder.encode(rawPassword1);
            Admin superAdmin = Admin.builder()
                    .name("Super Admin")
                    .email("admin@smartbiz.com")
                    .password(encryptedPassword1)
                    .build();
            adminRepository.save(superAdmin);

            log.info("Admin 1 Created:");
            log.info("  Name     : Super Admin");
            log.info("  Email    : admin@smartbiz.com");
            log.info("  Password (RAW)      : {}", rawPassword1);
            log.info("  Password (ENCRYPTED): {}", encryptedPassword1);

            // Admin 2 - Support Admin
            // Raw password: support@456
            String rawPassword2 = "support@456";
            String encryptedPassword2 = passwordEncoder.encode(rawPassword2);
            Admin supportAdmin = Admin.builder()
                    .name("Support Admin")
                    .email("support@smartbiz.com")
                    .password(encryptedPassword2)
                    .build();
            adminRepository.save(supportAdmin);

            log.info("Admin 2 Created:");
            log.info("  Name     : Support Admin");
            log.info("  Email    : support@smartbiz.com");
            log.info("  Password (RAW)      : {}", rawPassword2);
            log.info("  Password (ENCRYPTED): {}", encryptedPassword2);

            log.info("========================================");
            log.info("Admin seeding completed.");
            log.info("========================================");
        } else {
            log.info("Admins already exist. Skipping admin seeding.");
        }
    }

    private void seedSubscriptions() {
        if (subscriptionRepository.count() == 0) {
            log.info("Seeding Subscription Plans...");

            Subscription free = Subscription.builder()
                    .planName("Free")
                    .price(BigDecimal.ZERO)
                    .durationDays(30)
                    .build();
            subscriptionRepository.save(free);

            Subscription starter = Subscription.builder()
                    .planName("Starter")
                    .price(new BigDecimal("9.99"))
                    .durationDays(30)
                    .build();
            subscriptionRepository.save(starter);

            Subscription professional = Subscription.builder()
                    .planName("Professional")
                    .price(new BigDecimal("29.99"))
                    .durationDays(30)
                    .build();
            subscriptionRepository.save(professional);

            Subscription enterprise = Subscription.builder()
                    .planName("Enterprise")
                    .price(new BigDecimal("99.99"))
                    .durationDays(365)
                    .build();
            subscriptionRepository.save(enterprise);

            log.info("Seeded 4 subscription plans: Free, Starter, Professional, Enterprise");
        } else {
            log.info("Subscriptions already exist. Skipping subscription seeding.");
        }
    }
}
