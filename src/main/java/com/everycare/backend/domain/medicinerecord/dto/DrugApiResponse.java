package com.everycare.backend.domain.medicinerecord.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

import java.util.List;

@Data
public class DrugApiResponse {
    private Header header;
    private Body body;

    @Data
    public static class Header {
        @JacksonXmlProperty(localName = "resultCode")
        private String resultCode;
        @JacksonXmlProperty(localName = "resultMsg")
        private String resultMsg;
    }

    @Data
    public static class Body {
        private int numOfRows;
        private int pageNo;
        private int totalCount;
        @JacksonXmlProperty(localName = "items")
        private Items items;
    }

    @Data
    public static class Items {
        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "item")
        private List<Item> itemList;
    }

    @Data
    public static class Item {
        @JacksonXmlProperty(localName = "ITEM_NAME")
        private String itemName;
        @JacksonXmlProperty(localName = "ENTP_NAME")
        private String entpName;
    }

    public boolean isSuccessful() {
        return header != null && "00".equals(header.getResultCode());
    }
}
