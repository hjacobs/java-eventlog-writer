package de.zalando.zomcat.cxf;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * simple DTO with human readable information about a particular web service with all it's operations.
 *
 * @author  hjacobs
 */
public class WebServiceInfo {

    public static class OperationParameter {
        private String name;
        private String type;

        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(final String type) {
            this.type = type;
        }

    }

    public static class OperationInfo {
        private String name;
        private String documentation;
        private List<OperationParameter> parameters = Lists.newLinkedList();
        private String returnType;
        private String restPath;

        public String getDocumentation() {
            return documentation;
        }

        public void setDocumentation(final String documentation) {
            this.documentation = documentation;
        }

        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }

        public List<OperationParameter> getParameters() {
            return parameters;
        }

        public void setParameters(final List<OperationParameter> parameters) {
            this.parameters = parameters;
        }

        public String getReturnType() {
            return returnType;
        }

        public void setReturnType(final String returnType) {
            this.returnType = returnType;
        }

        public String getRestPath() {
            return restPath;
        }

        public void setRestPath(final String restPath) {
            this.restPath = restPath;
        }
    }

    private String name;
    private String address;
    private String documentation;
    private List<OperationInfo> operations = Lists.newLinkedList();
    private boolean rest;

    public String getAddress() {
        return address;
    }

    public void setAddress(final String address) {
        this.address = address;
    }

    public String getDocumentation() {
        return documentation;
    }

    public void setDocumentation(final String documentation) {
        this.documentation = documentation;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public List<OperationInfo> getOperations() {
        return operations;
    }

    public void setOperations(final List<OperationInfo> operations) {
        this.operations = operations;
    }

    public boolean isRest() {
        return rest;
    }

    public void setRest(final boolean rest) {
        this.rest = rest;
    }
}
