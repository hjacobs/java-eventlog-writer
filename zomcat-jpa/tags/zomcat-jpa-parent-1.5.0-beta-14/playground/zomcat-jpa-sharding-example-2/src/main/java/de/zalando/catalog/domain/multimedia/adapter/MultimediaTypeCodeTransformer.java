package de.zalando.catalog.domain.multimedia.adapter;

import de.zalando.catalog.domain.multimedia.MultimediaTypeCode;

import de.zalando.sprocwrapper.globalvaluetransformer.annotation.GlobalValueTransformer;

import de.zalando.typemapper.core.ValueTransformer;

@GlobalValueTransformer
public class MultimediaTypeCodeTransformer extends ValueTransformer<String, MultimediaTypeCode> {

    @Override
    public MultimediaTypeCode unmarshalFromDb(final String value) {
        if (value == null) {
            return null;
        }

        return new MultimediaTypeCode(value);
    }

    @Override
    public String marshalToDb(final MultimediaTypeCode multimediaTypeCode) {
        if (multimediaTypeCode == null) {
            return null;
        }

        return multimediaTypeCode.getCode();
    }

}
