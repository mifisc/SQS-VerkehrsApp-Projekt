package de.th_ro.sqs_verkehrsapp.config;

import de.th_ro.sqs_verkehrsapp.dashboard.RouteWatch;
import de.th_ro.sqs_verkehrsapp.dashboard.RouteWatchRepository;
import de.th_ro.sqs_verkehrsapp.user.AppUser;
import de.th_ro.sqs_verkehrsapp.user.AppUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
public class DemoDataInitializer {

    @Bean
    CommandLineRunner loadDemoData(
            AppUserRepository userRepository,
            RouteWatchRepository routeWatchRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            if (userRepository.existsByUsername("demo_driver")) {
                return;
            }

            AppUser demoUser = new AppUser();
            demoUser.setUsername("demo_driver");
            demoUser.setDisplayName("Demo Fahrer");
            demoUser.setPasswordHash(passwordEncoder.encode("Demo123!"));
            demoUser.setDemoAccount(true);

            AppUser savedUser = userRepository.save(demoUser);

            for (RouteSeed seed : demoRoutes()) {
                RouteWatch routeWatch = new RouteWatch();
                routeWatch.setUser(savedUser);
                routeWatch.setName(seed.name());
                routeWatch.setRoadIds(seed.roadIds());
                routeWatch.setNotes(seed.notes());
                routeWatch.setDemoData(true);
                routeWatchRepository.save(routeWatch);
            }
        };
    }

    private List<RouteSeed> demoRoutes() {
        return List.of(
                new RouteSeed("Demo Pendelroute Nord", "A1,A7", "Demo-Daten für den täglichen Arbeitsweg."),
                new RouteSeed("Demo Ferienroute Süd", "A3,A8,A9", "Demo-Daten für Reiseverkehr und Baustellen."),
                new RouteSeed("Demo Ausweichroute West", "A61,A4", "Demo-Daten für alternative Route bei Sperrungen.")
        );
    }

    private record RouteSeed(String name, String roadIds, String notes) {
    }
}
