package de.zalando.address.domain.util.builder;

import de.zalando.domain.address.AddressWithDetails;
import de.zalando.domain.address.Similarity;

import de.zalando.utils.Range;

public class AddressWithDetailsImpl extends AddressImpl implements AddressWithDetails {

    private Similarity similarity;
    private Range houseNumberRange;

    @Override
    public Similarity getSimilarity() {
        return similarity;
    }

    public void setSimilarity(final Similarity similarity) {
        this.similarity = similarity;
    }

    @Override
    public Range getHouseNumberRange() {
        return houseNumberRange;
    }

    void setHouseNumberRange(final Range houseNumberRange) {
        this.houseNumberRange = houseNumberRange;
    }

    @Override
    public boolean isHouseNumberNeeded() {
        return houseNumberRange != null;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("AddressWithDetailsImpl [");
        builder.append(", address=");
        builder.append(super.toString());
        builder.append(", similarity=");
        if (similarity != null) {
            builder.append(similarity);
        }

        builder.append(", houseNumberRange=");
        if (houseNumberRange != null) {
            builder.append(houseNumberRange);
        }

        builder.append(']');
        return builder.toString();
    }

}
