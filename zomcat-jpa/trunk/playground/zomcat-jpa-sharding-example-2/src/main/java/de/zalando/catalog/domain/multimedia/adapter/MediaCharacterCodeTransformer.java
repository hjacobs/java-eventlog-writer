package de.zalando.catalog.domain.multimedia.adapter;

import de.zalando.catalog.domain.multimedia.MediaCharacterCode;

import de.zalando.sprocwrapper.globalvaluetransformer.annotation.GlobalValueTransformer;

import de.zalando.typemapper.core.ValueTransformer;

@GlobalValueTransformer
public class MediaCharacterCodeTransformer extends ValueTransformer<String, MediaCharacterCode> {

    @Override
    public MediaCharacterCode unmarshalFromDb(final String value) {
        if (value == null) {
            return null;
        }

        return new MediaCharacterCode(value);
    }

    @Override
    public String marshalToDb(final MediaCharacterCode mediaCharacterCode) {
        if (mediaCharacterCode == null) {
            return null;
        }

        return mediaCharacterCode.getCode();
    }

}
