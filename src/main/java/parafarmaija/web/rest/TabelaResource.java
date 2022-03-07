package parafarmaija.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import parafarmaija.domain.Tabela;
import parafarmaija.repository.TabelaRepository;
import parafarmaija.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link parafarmaija.domain.Tabela}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class TabelaResource {

    private final Logger log = LoggerFactory.getLogger(TabelaResource.class);

    private static final String ENTITY_NAME = "tabela";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TabelaRepository tabelaRepository;

    public TabelaResource(TabelaRepository tabelaRepository) {
        this.tabelaRepository = tabelaRepository;
    }

    /**
     * {@code POST  /tabelas} : Create a new tabela.
     *
     * @param tabela the tabela to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new tabela, or with status {@code 400 (Bad Request)} if the tabela has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/tabelas")
    public ResponseEntity<Tabela> createTabela(@RequestBody Tabela tabela) throws URISyntaxException {
        log.debug("REST request to save Tabela : {}", tabela);
        if (tabela.getId() != null) {
            throw new BadRequestAlertException("A new tabela cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Tabela result = tabelaRepository.save(tabela);
        return ResponseEntity
            .created(new URI("/api/tabelas/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /tabelas/:id} : Updates an existing tabela.
     *
     * @param id the id of the tabela to save.
     * @param tabela the tabela to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tabela,
     * or with status {@code 400 (Bad Request)} if the tabela is not valid,
     * or with status {@code 500 (Internal Server Error)} if the tabela couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/tabelas/{id}")
    public ResponseEntity<Tabela> updateTabela(@PathVariable(value = "id", required = false) final Long id, @RequestBody Tabela tabela)
        throws URISyntaxException {
        log.debug("REST request to update Tabela : {}, {}", id, tabela);
        if (tabela.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tabela.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!tabelaRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Tabela result = tabelaRepository.save(tabela);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, tabela.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /tabelas/:id} : Partial updates given fields of an existing tabela, field will ignore if it is null
     *
     * @param id the id of the tabela to save.
     * @param tabela the tabela to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tabela,
     * or with status {@code 400 (Bad Request)} if the tabela is not valid,
     * or with status {@code 404 (Not Found)} if the tabela is not found,
     * or with status {@code 500 (Internal Server Error)} if the tabela couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/tabelas/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Tabela> partialUpdateTabela(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Tabela tabela
    ) throws URISyntaxException {
        log.debug("REST request to partial update Tabela partially : {}, {}", id, tabela);
        if (tabela.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tabela.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!tabelaRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Tabela> result = tabelaRepository
            .findById(tabela.getId())
            .map(existingTabela -> {
                if (tabela.getRegion() != null) {
                    existingTabela.setRegion(tabela.getRegion());
                }
                if (tabela.getPromet() != null) {
                    existingTabela.setPromet(tabela.getPromet());
                }

                return existingTabela;
            })
            .map(tabelaRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, tabela.getId().toString())
        );
    }

    /**
     * {@code GET  /tabelas} : get all the tabelas.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of tabelas in body.
     */
    @GetMapping("/tabelas")
    public ResponseEntity<List<Tabela>> getAllTabelas(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Tabelas");
        Page<Tabela> page = tabelaRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /tabelas/:id} : get the "id" tabela.
     *
     * @param id the id of the tabela to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the tabela, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/tabelas/{id}")
    public ResponseEntity<Tabela> getTabela(@PathVariable Long id) {
        log.debug("REST request to get Tabela : {}", id);
        Optional<Tabela> tabela = tabelaRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(tabela);
    }

    /**
     * {@code DELETE  /tabelas/:id} : delete the "id" tabela.
     *
     * @param id the id of the tabela to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/tabelas/{id}")
    public ResponseEntity<Void> deleteTabela(@PathVariable Long id) {
        log.debug("REST request to delete Tabela : {}", id);
        tabelaRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
