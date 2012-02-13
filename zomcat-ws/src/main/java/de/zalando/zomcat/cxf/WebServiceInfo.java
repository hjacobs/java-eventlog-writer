package de.zalando.zomcat.cxf;

import java.util.List;

/**
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
        private List<OperationParameter> parameters;
        private String returnType;

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

    }

    private String name;
    private String address;
    private String documentation;
    private List<OperationInfo> operations;

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

}
