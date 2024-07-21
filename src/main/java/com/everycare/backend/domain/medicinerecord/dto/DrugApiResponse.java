package com.everycare.backend.domain.medicinerecord.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class DrugApiResponse {
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
        @JsonProperty("ITEM_ENG_NAME")
        private String itemEngName;
        @JsonProperty("ENTP_NAME")
        private String entpName;
        @JsonProperty("ENTP_ENG_NAME")
        private String entpEngName;
        @JsonProperty("ENTP_SEQ")
        private String entpSeq;
        @JsonProperty("ENTP_NO")
        private String entpNo;
        @JsonProperty("ITEM_PERMIT_DATE")
        private String itemPermitDate;
        @JsonProperty("INDUTY")
        private String induty;
        @JsonProperty("PRDLST_STDR_CODE")
        private String prdlstStdrCode;
        @JsonProperty("SPCLTY_PBLC")
        private String spcltyPblc;
        @JsonProperty("PRDUCT_TYPE")
        private String prductType;
        @JsonProperty("PRDUCT_PRMISN_NO")
        private String prductPrmisnNo;
        @JsonProperty("ITEM_INGR_NAME")
        private String itemIngrName;
        @JsonProperty("ITEM_INGR_CNT")
        private String itemIngrCnt;
        @JsonProperty("BIG_PRDT_IMG_URL")
        private String bigPrdtImgUrl;
        @JsonProperty("PERMIT_KIND_CODE")
        private String permitKindCode;
        @JsonProperty("CANCEL_DATE")
        private String cancelDate;
        @JsonProperty("CANCEL_NAME")
        private String cancelName;
        @JsonProperty("EDI_CODE")
        private String ediCode;
        @JsonProperty("BIZRNO")
        private String bizrno;
    }

    public boolean isSuccessful() {
        return header != null && "00".equals(header.getResultCode());
    }
}
