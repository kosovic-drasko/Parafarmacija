package parafarmaija.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import parafarmaija.web.rest.TestUtil;

class TabelaTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Tabela.class);
        Tabela tabela1 = new Tabela();
        tabela1.setId(1L);
        Tabela tabela2 = new Tabela();
        tabela2.setId(tabela1.getId());
        assertThat(tabela1).isEqualTo(tabela2);
        tabela2.setId(2L);
        assertThat(tabela1).isNotEqualTo(tabela2);
        tabela1.setId(null);
        assertThat(tabela1).isNotEqualTo(tabela2);
    }
}
