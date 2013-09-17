package de.zalando.catalog.domain.multimedia.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import de.zalando.catalog.domain.multimedia.MultimediaTypeCode;

public class MultimediaTypeCodeAdapter extends XmlAdapter<String, MultimediaTypeCode> {

    @Override
    public String marshal(final MultimediaTypeCode multimediaTypeCode) throws Exception {
        return multimediaTypeCode.getCode();
    }

    @Override
    public MultimediaTypeCode unmarshal(final String str) throws Exception {
        return new MultimediaTypeCode(str);
    }

}
