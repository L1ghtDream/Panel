package dev.lightdream.originalpanel.dto.data;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

public class ComplainData {

    public enum ComplainStatus {
        OPENED_AWAITING_TARGET_RESPONSE("Awaiting target response"),
        OPEN_AWAITING_STAFF_APPROVAL("Awaiting staff approval"),
        CLOSED("Closed");

        public String message;

        ComplainStatus(String message){
            this.message=message;
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    public static class ComplainDataRequest {

        public String cookie;
        public String target;
        public String section;
        public String dateAndTime;
        public String description;
        public String proof;
        public ComplainStatus status;
        public String targetResponse;
        public Long timestamp;

        public ComplainDataResponse respond(String response) {
            return new ComplainDataResponse(cookie, target, section, dateAndTime, description, proof, status, targetResponse, timestamp, response);
        }

    }

    @AllArgsConstructor
    @NoArgsConstructor
    public static class ComplainDataResponse {

        public String cookie;
        public String target;
        public String section;
        public String dateAndTime;
        public String description;
        public String proof;
        public ComplainStatus status;
        public String targetResponse;
        public Long timestamp;
        public String response;

        public static ComplainDataResponse error(String error) {
            ComplainDataResponse output = new ComplainDataResponse();
            output.response = error;
            return output;
        }
    }

}
