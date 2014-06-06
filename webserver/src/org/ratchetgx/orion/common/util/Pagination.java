/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ratchetgx.orion.common.util;

import org.ratchetgx.orion.common.SsfwConstants;

/**
 *
 * @author hrfan
 */
public class Pagination {
	
	public final static String RN = "sys_bz_rn";

    //每页记录数
    private int pageCount = 0;
    //记录总数
    private int total;
    //当前页
    private int currentPage;
    //上一页
    private int prevPage;
    //下一页
    private int nextPage;
    //总页数
    private int pages;
    //末页
    private int endPage;

    public Pagination() {
    }

    public Pagination(int pageCount) {
        this.pageCount = pageCount;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPrevPage() {
        return prevPage;
    }

    public void setPrevPage(int prevPage) {
        this.prevPage = prevPage;
    }

    public int getNextPage() {
        return nextPage;
    }

    public void setNextPage(int nextPage) {
        this.nextPage = nextPage;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public int getEndPage() {
        return endPage;
    }

    public void setEndPage(int endPage) {
        this.endPage = endPage;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public int getPageCount() {
        if (this.pageCount == 0) {
            this.pageCount = SsfwConstants.PAGE_COUNT;
        }
        return this.pageCount;
    }
}
