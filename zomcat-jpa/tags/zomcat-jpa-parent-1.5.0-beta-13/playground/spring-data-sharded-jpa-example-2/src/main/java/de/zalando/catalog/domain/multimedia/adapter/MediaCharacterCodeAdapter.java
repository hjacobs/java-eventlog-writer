package de.zalando.catalog.domain.multimedia.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import de.zalando.catalog.domain.multimedia.MediaCharacterCode;

public class MediaCharacterCodeAdapter extends XmlAdapter<String, MediaCharacterCode> {

    @Override
    public String marshal(final MediaCharacterCode mediaCharacterCode) throws Exception {
        return mediaCharacterCode.getCode();
    }

    @Override
    public MediaCharacterCode unmarshal(final String str) throws Exception {
        return new MediaCharacterCode(str);
    }

}
