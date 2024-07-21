package com.everycare.backend.domain.medicinerecord.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class DetailedDrugApiResponse {
    private Header header;
    private Body body;

    @Data
    public static class Header {
        @JsonProperty("resultCode")
        private String resultCode;
        @JsonProperty("resultMsg")
        private String resultMsg;
    }

    @Data
    public static class Body {
        @JsonProperty("numOfRows")
        private int numOfRows;
        @JsonProperty("pageNo")
        private int pageNo;
        @JsonProperty("totalCount")
        private int totalCount;
        @JsonProperty("items")
        private List<Item> items;
    }

    @Data
    public static class Item {
        @JsonProperty("ITEM_SEQ")
        private String itemSeq;
        @JsonProperty("ITEM_NAME")
        private String itemName;
        @JsonProperty("ENTP_NAME")
        private String entpName;
        @JsonProperty("ETC_OTC_CODE")
        private String etcOtcCode;
        @JsonProperty("CHART")
        private String chart;
        @JsonProperty("MATERIAL_NAME")
        private String materialName;
        @JsonProperty("STORAGE_METHOD")
        private String storageMethod;
        @JsonProperty("VALID_TERM")
        private String validTerm;
        @JsonProperty("MAKE_MATERIAL_FLAG")
        private String makeMaterialFlag;
        @JsonProperty("GBN_NAME")
        private String gbnName;
        @JsonProperty("TOTAL_CONTENT")
        private String totalContent;
        @JsonProperty("EE_DOC_DATA")
        private String eeDocData;
        @JsonProperty("UD_DOC_DATA")
        private String udDocData;
        @JsonProperty("NB_DOC_DATA")
        private String nbDocData;
        @JsonProperty("PN_DOC_DATA")
        private String pnDocData;
        @JsonProperty("MAIN_ITEM_INGR")
        private String mainItemIngr;
        @JsonProperty("INGR_NAME")
        private String ingrName;
    }

    public boolean isSuccessful() {
        return header != null && "00".equals(header.getResultCode());
    }
}
