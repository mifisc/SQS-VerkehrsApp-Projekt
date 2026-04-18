package de.th_ro.sqs_verkehrsapp.dashboard;

import de.th_ro.sqs_verkehrsapp.user.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RouteWatchRepository extends JpaRepository<RouteWatch, Long> {
    List<RouteWatch> findAllByUserOrderByCreatedAtAsc(AppUser user);
    Optional<RouteWatch> findByIdAndUser(Long id, AppUser user);
}
