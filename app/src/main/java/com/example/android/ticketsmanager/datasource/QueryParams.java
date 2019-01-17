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

        public QueryParamsBuilder setKeyword(String keyword){
            result.setKeyword(keyword);
            return this;
        }

        public QueryParams build(){ return result; }

        private QueryParams result;
    }

    private String countryCode;

    private String keyword;

    private QueryParams(){}

    public String getCountryCode() {
        return countryCode;
    }

    private void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getKeywordRaw(){
        return keyword;
    }

    public String getKeyword() {
        if(keyword.isEmpty()){
            return "%";
        }
        return "%" + keyword + "%";
    }

    private void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public boolean hasKeyword(){ return !keyword.isEmpty(); }

    @Override
    public int hashCode() {
        int hash = 29 * countryCode.hashCode();
        hash = 29 * hash + keyword.hashCode();
        return hash;
    }
}
