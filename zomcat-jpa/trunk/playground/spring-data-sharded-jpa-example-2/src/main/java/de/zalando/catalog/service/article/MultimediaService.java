package de.zalando.catalog.service.article;

import java.util.List;

import de.zalando.catalog.domain.ShardedId;
import de.zalando.catalog.domain.multimedia.MediaCharacter;
import de.zalando.catalog.domain.multimedia.Multimedia;
import de.zalando.catalog.domain.multimedia.MultimediaType;
import de.zalando.catalog.domain.sku.Sku;

public interface MultimediaService {

    List<Multimedia> getMultimedia(Sku sku);

    ShardedId createMultimedia(Multimedia multimedia);

    void updateMultimedia(Multimedia multimedia);

    List<MediaCharacter> getMediaCharacters(final boolean includeInactive);

    List<MultimediaType> getMultimediaTypes(final boolean includeInactive);

    void createOrUpdateMultimediaType(MultimediaType multimediaType);

    void createOrUpdateMediaCharacter(MediaCharacter mediaCharacter);

}
