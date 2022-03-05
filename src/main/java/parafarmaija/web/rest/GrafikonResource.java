package parafarmaija.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import parafarmaija.domain.Grafikon;
import parafarmaija.repository.GrafikonRepository;
import parafarmaija.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link parafarmaija.domain.Grafikon}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class GrafikonResource {

    private final Logger log = LoggerFactory.getLogger(GrafikonResource.class);

    private static final String ENTITY_NAME = "grafikon";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final GrafikonRepository grafikonRepository;

    public GrafikonResource(GrafikonRepository grafikonRepository) {
        this.grafikonRepository = grafikonRepository;
    }

    /**
     * {@code POST  /grafikons} : Create a new grafikon.
     *
     * @param grafikon the grafikon to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new grafikon, or with status {@code 400 (Bad Request)} if the grafikon has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/grafikons")
    public ResponseEntity<Grafikon> createGrafikon(@RequestBody Grafikon grafikon) throws URISyntaxException {
        log.debug("REST request to save Grafikon : {}", grafikon);
        if (grafikon.getId() != null) {
            throw new BadRequestAlertException("A new grafikon cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Grafikon result = grafikonRepository.save(grafikon);
        return ResponseEntity
            .created(new URI("/api/grafikons/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /grafikons/:id} : Updates an existing grafikon.
     *
     * @param id the id of the grafikon to save.
     * @param grafikon the grafikon to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated grafikon,
     * or with status {@code 400 (Bad Request)} if the grafikon is not valid,
     * or with status {@code 500 (Internal Server Error)} if the grafikon couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/grafikons/{id}")
    public ResponseEntity<Grafikon> updateGrafikon(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Grafikon grafikon
    ) throws URISyntaxException {
        log.debug("REST request to update Grafikon : {}, {}", id, grafikon);
        if (grafikon.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, grafikon.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!grafikonRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Grafikon result = grafikonRepository.save(grafikon);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, grafikon.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /grafikons/:id} : Partial updates given fields of an existing grafikon, field will ignore if it is null
     *
     * @param id the id of the grafikon to save.
     * @param grafikon the grafikon to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated grafikon,
     * or with status {@code 400 (Bad Request)} if the grafikon is not valid,
     * or with status {@code 404 (Not Found)} if the grafikon is not found,
     * or with status {@code 500 (Internal Server Error)} if the grafikon couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/grafikons/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Grafikon> partialUpdateGrafikon(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Grafikon grafikon
    ) throws URISyntaxException {
        log.debug("REST request to partial update Grafikon partially : {}, {}", id, grafikon);
        if (grafikon.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, grafikon.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!grafikonRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Grafikon> result = grafikonRepository
            .findById(grafikon.getId())
            .map(existingGrafikon -> {
                if (grafikon.getRegion() != null) {
                    existingGrafikon.setRegion(grafikon.getRegion());
                }
                if (grafikon.getPromet() != null) {
                    existingGrafikon.setPromet(grafikon.getPromet());
                }

                return existingGrafikon;
            })
            .map(grafikonRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, grafikon.getId().toString())
        );
    }

    /**
     * {@code GET  /grafikons} : get all the grafikons.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of grafikons in body.
     */
    @GetMapping("/grafikons")
    public List<Grafikon> getAllGrafikons() {
        log.debug("REST request to get all Grafikons");
        return grafikonRepository.findAll();
    }

    /**
     * {@code GET  /grafikons/:id} : get the "id" grafikon.
     *
     * @param id the id of the grafikon to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the grafikon, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/grafikons/{id}")
    public ResponseEntity<Grafikon> getGrafikon(@PathVariable Long id) {
        log.debug("REST request to get Grafikon : {}", id);
        Optional<Grafikon> grafikon = grafikonRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(grafikon);
    }

    /**
     * {@code DELETE  /grafikons/:id} : delete the "id" grafikon.
     *
     * @param id the id of the grafikon to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/grafikons/{id}")
    public ResponseEntity<Void> deleteGrafikon(@PathVariable Long id) {
        log.debug("REST request to delete Grafikon : {}", id);
        grafikonRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
