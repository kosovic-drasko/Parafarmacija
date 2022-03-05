package parafarmaija.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import parafarmaija.domain.Grafikon;

/**
 * Spring Data SQL repository for the Grafikon entity.
 */
@SuppressWarnings("unused")
@Repository
public interface GrafikonRepository extends JpaRepository<Grafikon, Long> {}
