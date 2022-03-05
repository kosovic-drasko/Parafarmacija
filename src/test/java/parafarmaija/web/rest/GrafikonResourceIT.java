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
import parafarmaija.domain.Grafikon;
import parafarmaija.repository.GrafikonRepository;

/**
 * Integration tests for the {@link GrafikonResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class GrafikonResourceIT {

    private static final String DEFAULT_REGION = "AAAAAAAAAA";
    private static final String UPDATED_REGION = "BBBBBBBBBB";

    private static final Integer DEFAULT_PROMET = 1;
    private static final Integer UPDATED_PROMET = 2;

    private static final String ENTITY_API_URL = "/api/grafikons";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private GrafikonRepository grafikonRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restGrafikonMockMvc;

    private Grafikon grafikon;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Grafikon createEntity(EntityManager em) {
        Grafikon grafikon = new Grafikon().region(DEFAULT_REGION).promet(DEFAULT_PROMET);
        return grafikon;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Grafikon createUpdatedEntity(EntityManager em) {
        Grafikon grafikon = new Grafikon().region(UPDATED_REGION).promet(UPDATED_PROMET);
        return grafikon;
    }

    @BeforeEach
    public void initTest() {
        grafikon = createEntity(em);
    }

    @Test
    @Transactional
    void createGrafikon() throws Exception {
        int databaseSizeBeforeCreate = grafikonRepository.findAll().size();
        // Create the Grafikon
        restGrafikonMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(grafikon)))
            .andExpect(status().isCreated());

        // Validate the Grafikon in the database
        List<Grafikon> grafikonList = grafikonRepository.findAll();
        assertThat(grafikonList).hasSize(databaseSizeBeforeCreate + 1);
        Grafikon testGrafikon = grafikonList.get(grafikonList.size() - 1);
        assertThat(testGrafikon.getRegion()).isEqualTo(DEFAULT_REGION);
        assertThat(testGrafikon.getPromet()).isEqualTo(DEFAULT_PROMET);
    }

    @Test
    @Transactional
    void createGrafikonWithExistingId() throws Exception {
        // Create the Grafikon with an existing ID
        grafikon.setId(1L);

        int databaseSizeBeforeCreate = grafikonRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restGrafikonMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(grafikon)))
            .andExpect(status().isBadRequest());

        // Validate the Grafikon in the database
        List<Grafikon> grafikonList = grafikonRepository.findAll();
        assertThat(grafikonList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllGrafikons() throws Exception {
        // Initialize the database
        grafikonRepository.saveAndFlush(grafikon);

        // Get all the grafikonList
        restGrafikonMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(grafikon.getId().intValue())))
            .andExpect(jsonPath("$.[*].region").value(hasItem(DEFAULT_REGION)))
            .andExpect(jsonPath("$.[*].promet").value(hasItem(DEFAULT_PROMET)));
    }

    @Test
    @Transactional
    void getGrafikon() throws Exception {
        // Initialize the database
        grafikonRepository.saveAndFlush(grafikon);

        // Get the grafikon
        restGrafikonMockMvc
            .perform(get(ENTITY_API_URL_ID, grafikon.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(grafikon.getId().intValue()))
            .andExpect(jsonPath("$.region").value(DEFAULT_REGION))
            .andExpect(jsonPath("$.promet").value(DEFAULT_PROMET));
    }

    @Test
    @Transactional
    void getNonExistingGrafikon() throws Exception {
        // Get the grafikon
        restGrafikonMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewGrafikon() throws Exception {
        // Initialize the database
        grafikonRepository.saveAndFlush(grafikon);

        int databaseSizeBeforeUpdate = grafikonRepository.findAll().size();

        // Update the grafikon
        Grafikon updatedGrafikon = grafikonRepository.findById(grafikon.getId()).get();
        // Disconnect from session so that the updates on updatedGrafikon are not directly saved in db
        em.detach(updatedGrafikon);
        updatedGrafikon.region(UPDATED_REGION).promet(UPDATED_PROMET);

        restGrafikonMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedGrafikon.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedGrafikon))
            )
            .andExpect(status().isOk());

        // Validate the Grafikon in the database
        List<Grafikon> grafikonList = grafikonRepository.findAll();
        assertThat(grafikonList).hasSize(databaseSizeBeforeUpdate);
        Grafikon testGrafikon = grafikonList.get(grafikonList.size() - 1);
        assertThat(testGrafikon.getRegion()).isEqualTo(UPDATED_REGION);
        assertThat(testGrafikon.getPromet()).isEqualTo(UPDATED_PROMET);
    }

    @Test
    @Transactional
    void putNonExistingGrafikon() throws Exception {
        int databaseSizeBeforeUpdate = grafikonRepository.findAll().size();
        grafikon.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGrafikonMockMvc
            .perform(
                put(ENTITY_API_URL_ID, grafikon.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(grafikon))
            )
            .andExpect(status().isBadRequest());

        // Validate the Grafikon in the database
        List<Grafikon> grafikonList = grafikonRepository.findAll();
        assertThat(grafikonList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchGrafikon() throws Exception {
        int databaseSizeBeforeUpdate = grafikonRepository.findAll().size();
        grafikon.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGrafikonMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(grafikon))
            )
            .andExpect(status().isBadRequest());

        // Validate the Grafikon in the database
        List<Grafikon> grafikonList = grafikonRepository.findAll();
        assertThat(grafikonList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamGrafikon() throws Exception {
        int databaseSizeBeforeUpdate = grafikonRepository.findAll().size();
        grafikon.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGrafikonMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(grafikon)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Grafikon in the database
        List<Grafikon> grafikonList = grafikonRepository.findAll();
        assertThat(grafikonList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateGrafikonWithPatch() throws Exception {
        // Initialize the database
        grafikonRepository.saveAndFlush(grafikon);

        int databaseSizeBeforeUpdate = grafikonRepository.findAll().size();

        // Update the grafikon using partial update
        Grafikon partialUpdatedGrafikon = new Grafikon();
        partialUpdatedGrafikon.setId(grafikon.getId());

        partialUpdatedGrafikon.region(UPDATED_REGION).promet(UPDATED_PROMET);

        restGrafikonMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedGrafikon.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedGrafikon))
            )
            .andExpect(status().isOk());

        // Validate the Grafikon in the database
        List<Grafikon> grafikonList = grafikonRepository.findAll();
        assertThat(grafikonList).hasSize(databaseSizeBeforeUpdate);
        Grafikon testGrafikon = grafikonList.get(grafikonList.size() - 1);
        assertThat(testGrafikon.getRegion()).isEqualTo(UPDATED_REGION);
        assertThat(testGrafikon.getPromet()).isEqualTo(UPDATED_PROMET);
    }

    @Test
    @Transactional
    void fullUpdateGrafikonWithPatch() throws Exception {
        // Initialize the database
        grafikonRepository.saveAndFlush(grafikon);

        int databaseSizeBeforeUpdate = grafikonRepository.findAll().size();

        // Update the grafikon using partial update
        Grafikon partialUpdatedGrafikon = new Grafikon();
        partialUpdatedGrafikon.setId(grafikon.getId());

        partialUpdatedGrafikon.region(UPDATED_REGION).promet(UPDATED_PROMET);

        restGrafikonMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedGrafikon.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedGrafikon))
            )
            .andExpect(status().isOk());

        // Validate the Grafikon in the database
        List<Grafikon> grafikonList = grafikonRepository.findAll();
        assertThat(grafikonList).hasSize(databaseSizeBeforeUpdate);
        Grafikon testGrafikon = grafikonList.get(grafikonList.size() - 1);
        assertThat(testGrafikon.getRegion()).isEqualTo(UPDATED_REGION);
        assertThat(testGrafikon.getPromet()).isEqualTo(UPDATED_PROMET);
    }

    @Test
    @Transactional
    void patchNonExistingGrafikon() throws Exception {
        int databaseSizeBeforeUpdate = grafikonRepository.findAll().size();
        grafikon.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGrafikonMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, grafikon.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(grafikon))
            )
            .andExpect(status().isBadRequest());

        // Validate the Grafikon in the database
        List<Grafikon> grafikonList = grafikonRepository.findAll();
        assertThat(grafikonList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchGrafikon() throws Exception {
        int databaseSizeBeforeUpdate = grafikonRepository.findAll().size();
        grafikon.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGrafikonMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(grafikon))
            )
            .andExpect(status().isBadRequest());

        // Validate the Grafikon in the database
        List<Grafikon> grafikonList = grafikonRepository.findAll();
        assertThat(grafikonList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamGrafikon() throws Exception {
        int databaseSizeBeforeUpdate = grafikonRepository.findAll().size();
        grafikon.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGrafikonMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(grafikon)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Grafikon in the database
        List<Grafikon> grafikonList = grafikonRepository.findAll();
        assertThat(grafikonList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteGrafikon() throws Exception {
        // Initialize the database
        grafikonRepository.saveAndFlush(grafikon);

        int databaseSizeBeforeDelete = grafikonRepository.findAll().size();

        // Delete the grafikon
        restGrafikonMockMvc
            .perform(delete(ENTITY_API_URL_ID, grafikon.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Grafikon> grafikonList = grafikonRepository.findAll();
        assertThat(grafikonList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
