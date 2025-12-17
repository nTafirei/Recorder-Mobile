package com.marotech.recording.api;

import java.util.ArrayList;
import java.util.List;

public class Page {

    private int currPage = 1;
    private int itemsPerPage = 10;
    private long totalItemsFound = 0L;
    private long numItemsShowing;

    public int getFirstResultIndex() {
        if (this.currPage == 0) {
            this.currPage = 1;
        }

        if (this.itemsPerPage == 0) {
            this.itemsPerPage = 1;
        }

        return (this.currPage - 1) * this.itemsPerPage;
    }

    public long getTotalPages() {
        if (this.itemsPerPage == 0) {
            this.itemsPerPage = 1;
        }

        return (this.totalItemsFound + (long) this.itemsPerPage - 1L) / (long) this.itemsPerPage;
    }

    public List<Integer> getPageNumbers() {
        List<Integer> list = new ArrayList();
        long totalPages = this.getTotalPages();

        for (int i = 1; (long) i <= totalPages; ++i) {
            list.add(i);
        }

        return list;
    }


    public Page() {
    }


    public int getCurrPage() {
        return this.currPage;
    }


    public int getItemsPerPage() {
        return this.itemsPerPage;
    }


    public long getTotalItemsFound() {
        return this.totalItemsFound;
    }


    public long getNumItemsShowing() {
        return this.numItemsShowing;
    }


    public void setCurrPage(int currPage) {
        this.currPage = currPage;
    }


    public void setItemsPerPage(int itemsPerPage) {
        this.itemsPerPage = itemsPerPage;
    }


    public void setTotalItemsFound(long totalItemsFound) {
        this.totalItemsFound = totalItemsFound;
    }


    public void setNumItemsShowing(long numItemsShowing) {
        this.numItemsShowing = numItemsShowing;
    }


    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof Page)) {
            return false;
        } else {
            Page other = (Page) o;
            if (!other.canEqual(this)) {
                return false;
            } else if (this.getCurrPage() != other.getCurrPage()) {
                return false;
            } else if (this.getItemsPerPage() != other.getItemsPerPage()) {
                return false;
            } else if (this.getTotalItemsFound() != other.getTotalItemsFound()) {
                return false;
            } else {
                return this.getNumItemsShowing() == other.getNumItemsShowing();
            }
        }
    }


    protected boolean canEqual(Object other) {
        return other instanceof Page;
    }


    public int hashCode() {
        int result = 1;
        result = result * 59 + this.getCurrPage();
        result = result * 59 + this.getItemsPerPage();
        long $totalItemsFound = this.getTotalItemsFound();
        result = result * 59 + (int) ($totalItemsFound >>> 32 ^ $totalItemsFound);
        long $numItemsShowing = this.getNumItemsShowing();
        result = result * 59 + (int) ($numItemsShowing >>> 32 ^ $numItemsShowing);
        return result;
    }


    public String toString() {
        int var10000 = this.getCurrPage();
        return "Page(currPage=" + var10000 + ", itemsPerPage=" + this.getItemsPerPage() + ", totalItemsFound=" + this.getTotalItemsFound() + ", numItemsShowing=" + this.getNumItemsShowing() + ")";
    }
}
