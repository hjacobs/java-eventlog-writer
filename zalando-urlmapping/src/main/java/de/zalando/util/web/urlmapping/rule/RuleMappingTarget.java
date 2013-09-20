package de.zalando.util.web.urlmapping.rule;

import static com.google.common.base.Preconditions.checkState;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.google.common.base.Objects;
import com.google.common.base.Strings;

/**
 * Created with IntelliJ IDEA. User: abaresel Date: 9/18/13 Time: 4:30 PM To change this template use File | Settings |
 * File Templates.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_DEFAULT)
public class RuleMappingTarget {

    @JsonProperty
    private String targetUrl;

    /**
     * This specifies the target-type.
     */
    @JsonProperty
    private ForwardMappingRule.TargetType targetType;

    /**
     * This specifies the request-method for which this rule should be applied: GET, POST, or PUT. Same as the value of
     * the CGI variable REQUEST_METHOD.<br />
     * NOTE: this is a string type like the corresponding attribute in HttpServletRequest.
     */
    @JsonProperty
    private String requestMethod;

    public RuleMappingTarget() { }

    ForwardMappingRule.TargetType getTargetType() {
        return targetType;
    }

    String getRequestMethod() {
        return requestMethod;
    }

    public void setTargetType(final ForwardMappingRule.TargetType targetType) {
        this.targetType = targetType;
    }

    private void setRequestMethod(final String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public void checkIntegrity(final String ruleId) {
        checkState(!Strings.isNullOrEmpty(targetUrl), "No target URL defined in rule " + ruleId);
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(final String targetUrl) {
        this.targetUrl = targetUrl;
    }

    @Override
    public String toString() {
        return "RuleMappingTarget{" + ", targetUrl='" + targetUrl + '\'' + ", targetType=" + targetType
                + ", requestMethod='" + requestMethod + '\'' + '}';
    }

    @Override
    public boolean equals(final Object obj) {

        if (obj instanceof RuleMappingTarget) {
            final RuleMappingTarget other = (RuleMappingTarget) obj;
            return Objects.equal(targetUrl, other.targetUrl) && Objects.equal(targetType, other.targetType)
                    && Objects.equal(requestMethod, other.requestMethod);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(targetUrl, targetType, requestMethod);
    }
}
