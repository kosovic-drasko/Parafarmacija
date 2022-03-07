package parafarmaija.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import parafarmaija.domain.Tabela;

/**
 * Spring Data SQL repository for the Tabela entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TabelaRepository extends JpaRepository<Tabela, Long> {}
