package de.zalando.address.domain.blacklist;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {"successfulImports", "failedImports", "comments"})
public class BlacklistCSVImportResponse {

    private List<String> successfulImports = new ArrayList<String>();

    private List<String> failedImports = new ArrayList<String>();

    private List<String> comments = new ArrayList<String>();

    @XmlElementWrapper(name = "successfulImports")
    @XmlElement(name = "data", nillable = false, required = true)
    public List<String> getSuccessfulImports() {
        return successfulImports;
    }

    public void addSuccessfulImport(final String importValue) {
        if (successfulImports == null) {
            successfulImports = new ArrayList<String>();
        }

        successfulImports.add(importValue);
    }

    public void setSuccessfulImports(final List<String> successfulImports) {
        this.successfulImports = successfulImports;
    }

    @XmlElementWrapper(name = "failedImports")
    @XmlElement(name = "data", nillable = false, required = true)
    public List<String> getFailedImports() {
        return failedImports;
    }

    public void addFailedImport(final String importValue) {
        if (failedImports == null) {
            failedImports = new ArrayList<String>();
        }

        failedImports.add(importValue);
    }

    public void setFailedImports(final List<String> failedImports) {
        this.failedImports = failedImports;
    }

    @XmlElementWrapper(name = "comments")
    @XmlElement(name = "comment", nillable = false, required = true)
    public List<String> getComments() {
        return comments;
    }

    public void addComment(final String comment) {
        if (comments == null) {
            comments = new ArrayList<String>();
        }

        comments.add(comment);
    }

    public void setComments(final List<String> comments) {
        this.comments = comments;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("BlacklistCSVImportResponse [successfulImports=");
        builder.append(successfulImports);
        builder.append(", failedImports=");
        builder.append(failedImports);
        builder.append(", comments=");
        builder.append(comments);
        builder.append("]");
        return builder.toString();
    }

}
