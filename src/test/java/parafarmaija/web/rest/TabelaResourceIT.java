package parafarmaija.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import parafarmaija.IntegrationTest;
import parafarmaija.domain.Tabela;
import parafarmaija.repository.TabelaRepository;

/**
 * Integration tests for the {@link TabelaResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TabelaResourceIT {

    private static final String DEFAULT_REGION = "AAAAAAAAAA";
    private static final String UPDATED_REGION = "BBBBBBBBBB";

    private static final Integer DEFAULT_PROMET = 1;
    private static final Integer UPDATED_PROMET = 2;

    private static final String ENTITY_API_URL = "/api/tabelas";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TabelaRepository tabelaRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTabelaMockMvc;

    private Tabela tabela;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tabela createEntity(EntityManager em) {
        Tabela tabela = new Tabela().region(DEFAULT_REGION).promet(DEFAULT_PROMET);
        return tabela;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tabela createUpdatedEntity(EntityManager em) {
        Tabela tabela = new Tabela().region(UPDATED_REGION).promet(UPDATED_PROMET);
        return tabela;
    }

    @BeforeEach
    public void initTest() {
        tabela = createEntity(em);
    }

    @Test
    @Transactional
    void createTabela() throws Exception {
        int databaseSizeBeforeCreate = tabelaRepository.findAll().size();
        // Create the Tabela
        restTabelaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tabela)))
            .andExpect(status().isCreated());

        // Validate the Tabela in the database
        List<Tabela> tabelaList = tabelaRepository.findAll();
        assertThat(tabelaList).hasSize(databaseSizeBeforeCreate + 1);
        Tabela testTabela = tabelaList.get(tabelaList.size() - 1);
        assertThat(testTabela.getRegion()).isEqualTo(DEFAULT_REGION);
        assertThat(testTabela.getPromet()).isEqualTo(DEFAULT_PROMET);
    }

    @Test
    @Transactional
    void createTabelaWithExistingId() throws Exception {
        // Create the Tabela with an existing ID
        tabela.setId(1L);

        int databaseSizeBeforeCreate = tabelaRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTabelaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tabela)))
            .andExpect(status().isBadRequest());

        // Validate the Tabela in the database
        List<Tabela> tabelaList = tabelaRepository.findAll();
        assertThat(tabelaList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllTabelas() throws Exception {
        // Initialize the database
        tabelaRepository.saveAndFlush(tabela);

        // Get all the tabelaList
        restTabelaMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tabela.getId().intValue())))
            .andExpect(jsonPath("$.[*].region").value(hasItem(DEFAULT_REGION)))
            .andExpect(jsonPath("$.[*].promet").value(hasItem(DEFAULT_PROMET)));
    }

    @Test
    @Transactional
    void getTabela() throws Exception {
        // Initialize the database
        tabelaRepository.saveAndFlush(tabela);

        // Get the tabela
        restTabelaMockMvc
            .perform(get(ENTITY_API_URL_ID, tabela.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(tabela.getId().intValue()))
            .andExpect(jsonPath("$.region").value(DEFAULT_REGION))
            .andExpect(jsonPath("$.promet").value(DEFAULT_PROMET));
    }

    @Test
    @Transactional
    void getNonExistingTabela() throws Exception {
        // Get the tabela
        restTabelaMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewTabela() throws Exception {
        // Initialize the database
        tabelaRepository.saveAndFlush(tabela);

        int databaseSizeBeforeUpdate = tabelaRepository.findAll().size();

        // Update the tabela
        Tabela updatedTabela = tabelaRepository.findById(tabela.getId()).get();
        // Disconnect from session so that the updates on updatedTabela are not directly saved in db
        em.detach(updatedTabela);
        updatedTabela.region(UPDATED_REGION).promet(UPDATED_PROMET);

        restTabelaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedTabela.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedTabela))
            )
            .andExpect(status().isOk());

        // Validate the Tabela in the database
        List<Tabela> tabelaList = tabelaRepository.findAll();
        assertThat(tabelaList).hasSize(databaseSizeBeforeUpdate);
        Tabela testTabela = tabelaList.get(tabelaList.size() - 1);
        assertThat(testTabela.getRegion()).isEqualTo(UPDATED_REGION);
        assertThat(testTabela.getPromet()).isEqualTo(UPDATED_PROMET);
    }

    @Test
    @Transactional
    void putNonExistingTabela() throws Exception {
        int databaseSizeBeforeUpdate = tabelaRepository.findAll().size();
        tabela.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTabelaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, tabela.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(tabela))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tabela in the database
        List<Tabela> tabelaList = tabelaRepository.findAll();
        assertThat(tabelaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTabela() throws Exception {
        int databaseSizeBeforeUpdate = tabelaRepository.findAll().size();
        tabela.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTabelaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(tabela))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tabela in the database
        List<Tabela> tabelaList = tabelaRepository.findAll();
        assertThat(tabelaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTabela() throws Exception {
        int databaseSizeBeforeUpdate = tabelaRepository.findAll().size();
        tabela.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTabelaMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tabela)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Tabela in the database
        List<Tabela> tabelaList = tabelaRepository.findAll();
        assertThat(tabelaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTabelaWithPatch() throws Exception {
        // Initialize the database
        tabelaRepository.saveAndFlush(tabela);

        int databaseSizeBeforeUpdate = tabelaRepository.findAll().size();

        // Update the tabela using partial update
        Tabela partialUpdatedTabela = new Tabela();
        partialUpdatedTabela.setId(tabela.getId());

        partialUpdatedTabela.region(UPDATED_REGION).promet(UPDATED_PROMET);

        restTabelaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTabela.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTabela))
            )
            .andExpect(status().isOk());

        // Validate the Tabela in the database
        List<Tabela> tabelaList = tabelaRepository.findAll();
        assertThat(tabelaList).hasSize(databaseSizeBeforeUpdate);
        Tabela testTabela = tabelaList.get(tabelaList.size() - 1);
        assertThat(testTabela.getRegion()).isEqualTo(UPDATED_REGION);
        assertThat(testTabela.getPromet()).isEqualTo(UPDATED_PROMET);
    }

    @Test
    @Transactional
    void fullUpdateTabelaWithPatch() throws Exception {
        // Initialize the database
        tabelaRepository.saveAndFlush(tabela);

        int databaseSizeBeforeUpdate = tabelaRepository.findAll().size();

        // Update the tabela using partial update
        Tabela partialUpdatedTabela = new Tabela();
        partialUpdatedTabela.setId(tabela.getId());

        partialUpdatedTabela.region(UPDATED_REGION).promet(UPDATED_PROMET);

        restTabelaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTabela.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTabela))
            )
            .andExpect(status().isOk());

        // Validate the Tabela in the database
        List<Tabela> tabelaList = tabelaRepository.findAll();
        assertThat(tabelaList).hasSize(databaseSizeBeforeUpdate);
        Tabela testTabela = tabelaList.get(tabelaList.size() - 1);
        assertThat(testTabela.getRegion()).isEqualTo(UPDATED_REGION);
        assertThat(testTabela.getPromet()).isEqualTo(UPDATED_PROMET);
    }

    @Test
    @Transactional
    void patchNonExistingTabela() throws Exception {
        int databaseSizeBeforeUpdate = tabelaRepository.findAll().size();
        tabela.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTabelaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, tabela.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(tabela))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tabela in the database
        List<Tabela> tabelaList = tabelaRepository.findAll();
        assertThat(tabelaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTabela() throws Exception {
        int databaseSizeBeforeUpdate = tabelaRepository.findAll().size();
        tabela.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTabelaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(tabela))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tabela in the database
        List<Tabela> tabelaList = tabelaRepository.findAll();
        assertThat(tabelaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTabela() throws Exception {
        int databaseSizeBeforeUpdate = tabelaRepository.findAll().size();
        tabela.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTabelaMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(tabela)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Tabela in the database
        List<Tabela> tabelaList = tabelaRepository.findAll();
        assertThat(tabelaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTabela() throws Exception {
        // Initialize the database
        tabelaRepository.saveAndFlush(tabela);

        int databaseSizeBeforeDelete = tabelaRepository.findAll().size();

        // Delete the tabela
        restTabelaMockMvc
            .perform(delete(ENTITY_API_URL_ID, tabela.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Tabela> tabelaList = tabelaRepository.findAll();
        assertThat(tabelaList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
