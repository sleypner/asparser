package dev.sleypner.asparser.service.core.image;

import dev.sleypner.asparser.domain.model.Image;
import dev.sleypner.asparser.service.parser.shared.RepositoryManager;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public class ImagePersistenceImpl implements RepositoryManager<Image> {

    @PersistenceContext
    private final EntityManager em;

    @Autowired
    public ImagePersistenceImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public EntityManager getEm() {
        return this.em;
    }

    @Override
    public Class<Image> getEntityClass() {
        return Image.class;
    }

    @Override
    public Optional<Image> getByName(String name) {
        return RepositoryManager.super.getByName(Image.UNIQUE_FIELD_NAME, name);
    }

    @Override
    public Image save(Image image) {
        return getByName(image.getExternalName())
                .orElseGet(() -> em.merge(image));
    }
}
