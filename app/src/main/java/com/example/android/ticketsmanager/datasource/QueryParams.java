package com.example.android.ticketsmanager.datasource;

public class QueryParams {

    public static class QueryParamsBuilder {

        public QueryParamsBuilder(){
            result = new QueryParams();
        }

        public QueryParamsBuilder setCountryCode(String countryCode){
            result.setCountryCode(countryCode);
            return this;
        }

        public QueryParams build(){ return result; }

        private QueryParams result;
    }

    private String countryCode;

    private QueryParams(){}

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    @Override
    public int hashCode() {
        return countryCode.hashCode();
    }
}
