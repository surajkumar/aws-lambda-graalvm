package uk.gov.di.ipv.cri.address.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OrdnanceSurveyPostcodeError {

    @JsonProperty("error")
    private java.lang.Error error;

    public java.lang.Error getError() {
        return error;
    }

    public void setError(java.lang.Error error) {
        this.error = error;
    }
}
