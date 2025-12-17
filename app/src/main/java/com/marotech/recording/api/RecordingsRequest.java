package com.marotech.recording.api;

public class RecordingsRequest extends BaseRequest {

    private Page page;

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }
}